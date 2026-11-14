package com.example.model

enum class AppTab(
    val route: String,
    val labelHi: String,
    val labelEn: String
) {
    HOME("home", "होम", "Home"),
    LEARN("learn", "सीखें", "Learn"),
    PRACTICE("practice", "अभ्यास", "Practice"),
    FRIENDS("friends", "मित्र", "Friends"),
    PROFILE("profile", "प्रोफ़ाइल", "Profile")
}

const val ROUTE_PROGRESS = "progress"
const val ROUTE_LESSON_PREFIX = "lesson"
const val ROUTE_LESSON_ARG_NODE_ID = "nodeId"
const val ROUTE_LESSON = "$ROUTE_LESSON_PREFIX/{$ROUTE_LESSON_ARG_NODE_ID}"

fun lessonRoute(nodeId: String): String = "$ROUTE_LESSON_PREFIX/$nodeId"

enum class UnitStatus {
    COMPLETED,
    ACTIVE,
    UPCOMING
}

data class SampleLessonPreview(
    val titleEn: String,
    val titleHi: String,
    val explanationHi: String,
    val englishPhrase: String,
    val hindiMeaning: String,
    val pronunciationHi: String,
    val realLifeExampleEn: String,
    val realLifeExampleHi: String,
    val quizQuestionHi: String,
    val quizOptions: List<String>,
    val correctOptionIndex: Int,
    val xpReward: Int = 20
)

data class LearningUnit(
    val id: String,
    val levelNumber: Int,
    val orderNumber: Int,
    val titleEn: String,
    val titleHi: String,
    val lessonsCount: Int,
    val completedLessons: Int,
    val estimatedMinutes: Int,
    val xpReward: Int,
    val status: UnitStatus,
    val sampleLesson: SampleLessonPreview
)

data class LearningLevel(
    val levelNumber: Int,
    val titleEn: String,
    val titleHi: String,
    val subtitleHi: String,
    val badgeText: String,
    val units: List<LearningUnit>
)

data class DailyWord(
    val id: String,
    val english: String,
    val hindi: String,
    val pronunciationHi: String,
    val partOfSpeechHi: String,
    val exampleEn: String,
    val exampleHi: String,
    val categoryHi: String,
    val isSaved: Boolean = false
)

data class VocabCategory(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val wordCount: Int,
    val learnedCount: Int
)

data class GrammarPreview(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val summaryHi: String,
    val structurePattern: String,
    val positiveExampleEn: String,
    val positiveExampleHi: String,
    val commonMistakeHi: String
)

data class DayProgress(
    val dayEn: String,
    val dayHi: String,
    val xpEarned: Int,
    val goalMet: Boolean
)

data class AchievementBadge(
    val id: String,
    val titleHi: String,
    val titleEn: String,
    val descriptionHi: String,
    val isUnlocked: Boolean,
    val progressText: String
)

data class TopicStrength(
    val topicHi: String,
    val topicEn: String,
    val masteryPercent: Float,
    val needsRevision: Boolean
)

data class PhaseRoadmapItem(
    val phaseNumber: Int,
    val titleHi: String,
    val titleEn: String,
    val statusHi: String,
    val isCurrent: Boolean
)
