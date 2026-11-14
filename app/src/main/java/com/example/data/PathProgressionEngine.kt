package com.example.data

import com.example.model.CourseSection
import com.example.model.LessonNode
import com.example.model.LessonNodeStatus
import com.example.model.LessonNodeType
import com.example.model.NodeProgressRecord
import com.example.model.PathAttemptOutcome
import com.example.model.PathUnit

object PathProgressionEngine {

    const val DEFAULT_PASSING_SCORE_PERCENT = 70

    fun initialProgressMap(section: CourseSection): Map<String, NodeProgressRecord> {
        val firstNode = section.units.firstOrNull()?.nodes?.firstOrNull() ?: return emptyMap()
        return mapOf(
            firstNode.id to NodeProgressRecord(
                id = firstNode.id,
                nodeId = firstNode.id,
                unitId = firstNode.unitId,
                status = LessonNodeStatus.AVAILABLE.name,
                passingScorePercent = firstNode.passingScorePercent
            )
        )
    }

    fun isFinished(status: LessonNodeStatus): Boolean =
        status == LessonNodeStatus.COMPLETED || status == LessonNodeStatus.PERFECT

    fun mergeRecords(
        primary: Map<String, NodeProgressRecord>,
        secondary: Map<String, NodeProgressRecord>
    ): Map<String, NodeProgressRecord> {
        val merged = primary.toMutableMap()
        secondary.forEach { (nodeId, incoming) ->
            val current = merged[nodeId]
            if (current == null) {
                merged[nodeId] = incoming
            } else {
                val bestStatus = if (incoming.resolvedStatus().rank >= current.resolvedStatus().rank) {
                    incoming.resolvedStatus()
                } else {
                    current.resolvedStatus()
                }
                merged[nodeId] = current.copy(
                    status = bestStatus.name,
                    bestScorePercent = maxOf(current.bestScorePercent, incoming.bestScorePercent),
                    lastScorePercent = if (incoming.attemptsCount >= current.attemptsCount) {
                        incoming.lastScorePercent
                    } else {
                        current.lastScorePercent
                    },
                    starsEarned = maxOf(current.starsEarned, incoming.starsEarned),
                    attemptsCount = maxOf(current.attemptsCount, incoming.attemptsCount),
                    xpAwarded = current.xpAwarded || incoming.xpAwarded,
                    passingScorePercent = current.passingScorePercent
                )
            }
        }
        return merged
    }

    fun resolveUnits(
        section: CourseSection,
        records: Map<String, NodeProgressRecord>
    ): List<PathUnit> {
        val rawStatusMap = mutableMapOf<String, LessonNodeStatus>()
        val allNodes = section.units.flatMap { it.nodes }

        // First pass: capture recorded statuses (never downgrade initial node below AVAILABLE)
        allNodes.forEach { node ->
            val saved = records[node.id]?.resolvedStatus()
            val baseStatus = when {
                saved != null -> saved
                node.unlockRequirementId == null -> LessonNodeStatus.AVAILABLE
                else -> node.status
            }
            rawStatusMap[node.id] = baseStatus
        }

        // Second pass: recompute unlock eligibility cleanly from prerequisites & unit rules
        section.units.forEach { unit ->
            unit.nodes.sortedBy { it.orderInUnit }.forEach { node ->
                val current = rawStatusMap[node.id] ?: LessonNodeStatus.LOCKED
                if (current == LessonNodeStatus.LOCKED && isNodeEligibleToUnlock(node, unit, rawStatusMap)) {
                    rawStatusMap[node.id] = LessonNodeStatus.AVAILABLE
                }
            }
        }

        return section.units.map { unit ->
            unit.copy(
                nodes = unit.nodes.map { node ->
                    val record = records[node.id]
                    val resolved = rawStatusMap[node.id] ?: node.status
                    node.copy(
                        status = resolved,
                        starsEarned = record?.starsEarned ?: if (resolved == LessonNodeStatus.PERFECT) 3 else 0,
                        bestScorePercent = record?.bestScorePercent ?: 0,
                        attemptsCount = record?.attemptsCount ?: 0,
                        xpAwarded = record?.xpAwarded ?: false
                    )
                }
            )
        }
    }

    fun findNodeById(section: CourseSection, records: Map<String, NodeProgressRecord>, nodeId: String): LessonNode? {
        return resolveUnits(section, records)
            .flatMap { it.nodes }
            .firstOrNull { it.id == nodeId }
    }

    fun isNodeEligibleToUnlock(
        node: LessonNode,
        parentUnit: PathUnit,
        statusByNodeId: Map<String, LessonNodeStatus>
    ): Boolean {
        val reqId = node.unlockRequirementId
        val reqSatisfied = reqId == null || isFinished(statusByNodeId[reqId] ?: LessonNodeStatus.LOCKED)
        if (!reqSatisfied) return false

        // Unit tests require every preceding lesson in that unit to be COMPLETED or PERFECT
        if (node.type == LessonNodeType.UNIT_TEST) {
            val allPriorUnitLessonsDone = parentUnit.nodes
                .filter { it.id != node.id && it.orderInUnit < node.orderInUnit }
                .all { isFinished(statusByNodeId[it.id] ?: LessonNodeStatus.LOCKED) }
            if (!allPriorUnitLessonsDone) return false
        }

        return true
    }

    fun markInProgress(
        section: CourseSection,
        records: Map<String, NodeProgressRecord>,
        nodeId: String
    ): Pair<Map<String, NodeProgressRecord>, NodeProgressRecord?> {
        val resolvedUnits = resolveUnits(section, records)
        val target = resolvedUnits.flatMap { it.nodes }.firstOrNull { it.id == nodeId }
            ?: return records to null

        if (target.status == LessonNodeStatus.LOCKED) return records to null
        if (isFinished(target.status)) return records to records[nodeId]

        val existing = records[nodeId]
        val updatedRecord = NodeProgressRecord(
            id = nodeId,
            nodeId = nodeId,
            unitId = target.unitId,
            status = LessonNodeStatus.IN_PROGRESS.name,
            bestScorePercent = existing?.bestScorePercent ?: 0,
            lastScorePercent = existing?.lastScorePercent ?: 0,
            starsEarned = existing?.starsEarned ?: 0,
            attemptsCount = existing?.attemptsCount ?: 0,
            xpAwarded = existing?.xpAwarded ?: false,
            passingScorePercent = target.passingScorePercent,
            createdAt = existing?.createdAt
        )
        return (records + (nodeId to updatedRecord)) to updatedRecord
    }

    fun recordAttempt(
        section: CourseSection,
        records: Map<String, NodeProgressRecord>,
        nodeId: String,
        scorePercent: Int,
        passingScoreOverride: Int? = null
    ): Triple<Map<String, NodeProgressRecord>, PathAttemptOutcome, List<NodeProgressRecord>> {
        val beforeUnits = resolveUnits(section, records)
        val beforeStatuses = beforeUnits.flatMap { it.nodes }.associate { it.id to it.status }
        val node = beforeUnits.flatMap { it.nodes }.firstOrNull { it.id == nodeId }
            ?: throw IllegalArgumentException("Unknown learning path node: $nodeId")

        val clampedScore = scorePercent.coerceIn(0, 100)
        val threshold = (passingScoreOverride ?: node.passingScorePercent).coerceIn(50, 100)
        val passed = clampedScore >= threshold
        val existing = records[nodeId]
        val wasFinished = isFinished(beforeStatuses[nodeId] ?: LessonNodeStatus.LOCKED)
        val isReplay = wasFinished || (existing?.xpAwarded == true)

        val attemptStars = when {
            !passed -> 0
            clampedScore >= 100 -> 3
            clampedScore >= 85 -> 2
            else -> 1
        }

        val resultingStatus = when {
            beforeStatuses[nodeId] == LessonNodeStatus.PERFECT -> LessonNodeStatus.PERFECT
            wasFinished && !passed -> beforeStatuses[nodeId] ?: LessonNodeStatus.COMPLETED
            !passed -> LessonNodeStatus.IN_PROGRESS
            clampedScore >= 100 -> LessonNodeStatus.PERFECT
            else -> LessonNodeStatus.COMPLETED
        }

        val xpGranted = if (passed && !isReplay) node.xpReward else 0
        val updatedAttempts = (existing?.attemptsCount ?: 0) + 1
        val bestScore = maxOf(existing?.bestScorePercent ?: 0, clampedScore)
        val bestStars = maxOf(existing?.starsEarned ?: 0, attemptStars)

        val updatedNodeRecord = NodeProgressRecord(
            id = nodeId,
            nodeId = nodeId,
            unitId = node.unitId,
            status = resultingStatus.name,
            bestScorePercent = bestScore,
            lastScorePercent = clampedScore,
            starsEarned = bestStars,
            attemptsCount = updatedAttempts,
            xpAwarded = (existing?.xpAwarded == true) || (passed && xpGranted > 0),
            passingScorePercent = threshold,
            createdAt = existing?.createdAt
        )

        val intermediateRecords = records.toMutableMap().apply {
            put(nodeId, updatedNodeRecord)
        }

        val afterUnits = resolveUnits(section, intermediateRecords)
        val allAfterNodes = afterUnits.flatMap { it.nodes }
        val newlyUnlockedNodes = allAfterNodes.filter { candidate ->
            beforeStatuses[candidate.id] == LessonNodeStatus.LOCKED &&
                candidate.status == LessonNodeStatus.AVAILABLE
        }

        val changedRecords = mutableListOf(updatedNodeRecord)
        newlyUnlockedNodes.forEach { unlocked ->
            val unlockedRec = NodeProgressRecord(
                id = unlocked.id,
                nodeId = unlocked.id,
                unitId = unlocked.unitId,
                status = LessonNodeStatus.AVAILABLE.name,
                bestScorePercent = intermediateRecords[unlocked.id]?.bestScorePercent ?: 0,
                lastScorePercent = intermediateRecords[unlocked.id]?.lastScorePercent ?: 0,
                starsEarned = intermediateRecords[unlocked.id]?.starsEarned ?: 0,
                attemptsCount = intermediateRecords[unlocked.id]?.attemptsCount ?: 0,
                xpAwarded = intermediateRecords[unlocked.id]?.xpAwarded ?: false,
                passingScorePercent = unlocked.passingScorePercent,
                createdAt = intermediateRecords[unlocked.id]?.createdAt
            )
            intermediateRecords[unlocked.id] = unlockedRec
            changedRecords.add(unlockedRec)
        }

        val unlockedNextUnit = node.type == LessonNodeType.UNIT_TEST &&
            newlyUnlockedNodes.any { it.unitId != node.unitId }

        val currentIndex = allAfterNodes.indexOfFirst { it.id == nodeId }
        val nextPlayable = if (currentIndex >= 0) {
            allAfterNodes.drop(currentIndex + 1).firstOrNull {
                it.status == LessonNodeStatus.AVAILABLE || it.status == LessonNodeStatus.IN_PROGRESS
            } ?: allAfterNodes.firstOrNull {
                it.status == LessonNodeStatus.AVAILABLE || it.status == LessonNodeStatus.IN_PROGRESS
            }
        } else null

        val outcome = PathAttemptOutcome(
            nodeId = nodeId,
            passed = passed,
            scorePercent = clampedScore,
            passingScorePercent = threshold,
            starsEarned = attemptStars,
            resultingStatus = resultingStatus,
            xpGranted = xpGranted,
            isReplay = isReplay,
            unlockedNextUnit = unlockedNextUnit,
            newlyUnlockedNodeIds = newlyUnlockedNodes.map { it.id },
            nextPlayableNodeId = nextPlayable?.id
        )

        return Triple(intermediateRecords, outcome, changedRecords)
    }
}
