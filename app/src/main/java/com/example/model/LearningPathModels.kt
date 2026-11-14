package com.example.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

enum class CefrStage(val code: String, val labelHi: String) {
    PRE_A1("Pre-A1", "बिल्कुल शुरुआत"),
    A1("A1", "बेसिक (Beginner)"),
    A2("A2", "एलिमेंट्री (Elementary)"),
    B1("B1", "इंटरमीडिएट (Intermediate)")
}

enum class PathUnitTheme {
    EMERALD,
    INDIGO,
    SAFFRON,
    TEAL
}

enum class LessonNodeType(
    val labelHi: String,
    val labelEn: String
) {
    NEW_LESSON("नया पाठ", "New Lesson"),
    VOCABULARY("शब्दावली", "Vocabulary"),
    GRAMMAR("व्याकरण", "Grammar"),
    LISTENING("सुनकर समझें", "Listening"),
    SPEAKING("बोलने का अभ्यास", "Speaking"),
    REVIEW("दोहराव अभ्यास", "Review"),
    STORY("कहानी पाठ", "Story"),
    UNIT_TEST("इकाई परीक्षा", "Unit Test"),
    CHALLENGE("चुनौती पाठ", "Challenge")
}

enum class LessonNodeStatus(
    val labelHi: String,
    val rank: Int
) {
    LOCKED("लॉक है", 0),
    AVAILABLE("शुरू करें", 1),
    IN_PROGRESS("जारी है", 2),
    COMPLETED("पूर्ण", 3),
    PERFECT("परफ़ेक्ट ★", 4)
}

data class GuidebookPattern(
    val patternEn: String,
    val meaningHi: String,
    val pronunciationHi: String
)

data class NodeProgressRecord(
    @DocumentId val id: String = "",
    val nodeId: String = "",
    val unitId: String = "",
    val status: String = LessonNodeStatus.LOCKED.name,
    val bestScorePercent: Int = 0,
    val lastScorePercent: Int = 0,
    val starsEarned: Int = 0,
    val attemptsCount: Int = 0,
    val xpAwarded: Boolean = false,
    val passingScorePercent: Int = 70,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun resolvedStatus(): LessonNodeStatus =
        LessonNodeStatus.entries.firstOrNull { it.name == status } ?: LessonNodeStatus.LOCKED
}

data class PathAttemptOutcome(
    val nodeId: String,
    val passed: Boolean,
    val scorePercent: Int,
    val passingScorePercent: Int,
    val starsEarned: Int,
    val resultingStatus: LessonNodeStatus,
    val xpGranted: Int,
    val isReplay: Boolean,
    val unlockedNextUnit: Boolean,
    val newlyUnlockedNodeIds: List<String>,
    val nextPlayableNodeId: String?
)

data class LessonNode(
    val id: String,
    val unitId: String,
    val orderInUnit: Int,
    val titleEn: String,
    val titleHi: String,
    val type: LessonNodeType,
    val status: LessonNodeStatus,
    val xpReward: Int,
    val estimatedMinutes: Int,
    val unlockRequirementId: String?,
    val learningObjectiveHi: String,
    val passingScorePercent: Int = 70,
    val starsEarned: Int = 0,
    val bestScorePercent: Int = 0,
    val attemptsCount: Int = 0,
    val xpAwarded: Boolean = false,
    val samplePreview: SampleLessonPreview
)

data class PathUnit(
    val id: String,
    val sectionId: String,
    val orderNumber: Int,
    val titleEn: String,
    val titleHi: String,
    val descriptionHi: String,
    val learningObjectivesHi: List<String>,
    val guidebookSummaryHi: String,
    val guidebookPatterns: List<GuidebookPattern>,
    val theme: PathUnitTheme,
    val nodes: List<LessonNode>
)

data class CourseSection(
    val id: String,
    val orderNumber: Int,
    val titleEn: String,
    val titleHi: String,
    val subtitleHi: String,
    val cefrStage: CefrStage,
    val isAvailableInPhase1: Boolean,
    val units: List<PathUnit>
)

data class CourseSectionOutline(
    val sectionNumber: Int,
    val titleEn: String,
    val titleHi: String,
    val cefrStage: CefrStage,
    val unitRangeLabel: String,
    val isActive: Boolean
)
