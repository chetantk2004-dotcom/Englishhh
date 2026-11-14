package com.example.data

import com.example.model.LessonNodeStatus
import com.example.model.NodeProgressRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PathProgressionEngineTest {

    private val section = LearningPathRepository.phase1Section

    @Test
    fun `initial state unlocks only the first node`() {
        val initial = PathProgressionEngine.initialProgressMap(section)
        val units = PathProgressionEngine.resolveUnits(section, initial)
        val allNodes = units.flatMap { it.nodes }

        assertEquals(LessonNodeStatus.AVAILABLE, allNodes.first().status)
        assertEquals("s1_u1_n1", allNodes.first().id)
        allNodes.drop(1).forEach { node ->
            assertEquals("Expected ${node.id} to be LOCKED initially", LessonNodeStatus.LOCKED, node.status)
        }
    }

    @Test
    fun `early exit marks available node IN_PROGRESS without downgrading finished nodes`() {
        val initial = PathProgressionEngine.initialProgressMap(section)
        val (inProgressMap, rec) = PathProgressionEngine.markInProgress(section, initial, "s1_u1_n1")
        assertEquals(LessonNodeStatus.IN_PROGRESS.name, rec?.status)

        val resolved = PathProgressionEngine.resolveUnits(section, inProgressMap)
        assertEquals(LessonNodeStatus.IN_PROGRESS, resolved.first().nodes.first().status)

        // Complete s1_u1_n1, then call markInProgress again (simulating replay early exit)
        val (completedMap, _, _) = PathProgressionEngine.recordAttempt(section, inProgressMap, "s1_u1_n1", 100)
        val (afterReplayExit, _) = PathProgressionEngine.markInProgress(section, completedMap, "s1_u1_n1")
        val afterResolved = PathProgressionEngine.resolveUnits(section, afterReplayExit)
        assertEquals(LessonNodeStatus.PERFECT, afterResolved.first().nodes.first().status)
    }

    @Test
    fun `failing score below 70 keeps node IN_PROGRESS and keeps next node LOCKED`() {
        val initial = PathProgressionEngine.initialProgressMap(section)
        val (updated, outcome, _) = PathProgressionEngine.recordAttempt(
            section = section,
            records = initial,
            nodeId = "s1_u1_n1",
            scorePercent = 50
        )

        assertFalse(outcome.passed)
        assertEquals(LessonNodeStatus.IN_PROGRESS, outcome.resultingStatus)
        assertEquals(0, outcome.xpGranted)
        assertTrue(outcome.newlyUnlockedNodeIds.isEmpty())

        val nodes = PathProgressionEngine.resolveUnits(section, updated).first().nodes
        assertEquals(LessonNodeStatus.IN_PROGRESS, nodes[0].status)
        assertEquals(LessonNodeStatus.LOCKED, nodes[1].status)
    }

    @Test
    fun `passing score unlocks next node and replay awards zero duplicate XP`() {
        val initial = PathProgressionEngine.initialProgressMap(section)
        val (afterPass, passOutcome, _) = PathProgressionEngine.recordAttempt(
            section = section,
            records = initial,
            nodeId = "s1_u1_n1",
            scorePercent = 75
        )

        assertTrue(passOutcome.passed)
        assertEquals(LessonNodeStatus.COMPLETED, passOutcome.resultingStatus)
        assertEquals(20, passOutcome.xpGranted)
        assertFalse(passOutcome.isReplay)
        assertEquals(listOf("s1_u1_n2"), passOutcome.newlyUnlockedNodeIds)

        val nodesAfterPass = PathProgressionEngine.resolveUnits(section, afterPass).first().nodes
        assertEquals(LessonNodeStatus.COMPLETED, nodesAfterPass[0].status)
        assertEquals(LessonNodeStatus.AVAILABLE, nodesAfterPass[1].status)

        // Replay at 100% upgrades to PERFECT but awards 0 duplicate XP
        val (afterReplay, replayOutcome, _) = PathProgressionEngine.recordAttempt(
            section = section,
            records = afterPass,
            nodeId = "s1_u1_n1",
            scorePercent = 100
        )

        assertTrue(replayOutcome.isReplay)
        assertEquals(0, replayOutcome.xpGranted)
        assertEquals(LessonNodeStatus.PERFECT, replayOutcome.resultingStatus)
        assertEquals(3, PathProgressionEngine.resolveUnits(section, afterReplay).first().nodes[0].starsEarned)
    }

    @Test
    fun `unit test stays locked until all prior unit lessons complete and passing unlocks next unit`() {
        var records = PathProgressionEngine.initialProgressMap(section)
        val unit1 = section.units[0]
        val priorLessons = unit1.nodes.dropLast(1) // n1..n7
        val unit1Test = unit1.nodes.last() // n8

        // Even if someone bypassed n1..n6 with an artificial completed record for n7 alone,
        // unit test remains gated unless all prior unit lessons are done
        val skippedMap = mapOf(
            "s1_u1_n7" to NodeProgressRecord(
                id = "s1_u1_n7",
                nodeId = "s1_u1_n7",
                unitId = "s1_u1",
                status = LessonNodeStatus.COMPLETED.name,
                bestScorePercent = 80,
                xpAwarded = true
            )
        )
        val skippedResolved = PathProgressionEngine.resolveUnits(section, skippedMap)
        assertEquals(LessonNodeStatus.LOCKED, skippedResolved[0].nodes.last().status)

        // Complete n1..n7 sequentially
        priorLessons.forEach { lesson ->
            val (nextMap, _, _) = PathProgressionEngine.recordAttempt(section, records, lesson.id, 85)
            records = nextMap
        }

        val readyUnits = PathProgressionEngine.resolveUnits(section, records)
        assertEquals(LessonNodeStatus.AVAILABLE, readyUnits[0].nodes.last().status)
        assertEquals(LessonNodeStatus.LOCKED, readyUnits[1].nodes.first().status)

        // Pass Unit 1 Test -> unlocks Unit 2 first node
        val (afterTestMap, testOutcome, _) = PathProgressionEngine.recordAttempt(section, records, unit1Test.id, 80)
        assertTrue(testOutcome.passed)
        assertTrue(testOutcome.unlockedNextUnit)
        assertTrue(testOutcome.newlyUnlockedNodeIds.contains("s1_u2_n1"))

        val finalUnits = PathProgressionEngine.resolveUnits(section, afterTestMap)
        assertEquals(LessonNodeStatus.AVAILABLE, finalUnits[1].nodes.first().status)
    }
}
