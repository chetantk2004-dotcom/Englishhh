package com.example.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val profileId: String = "",
    val username: String = "",
    val usernameLower: String = "",
    val displayName: String = "",
    val avatarUrl: String = AvatarPresets.SPARROW,
    val bio: String = "",
    val totalXp: Long = 0L,
    val streak: Long = 0L,
    val lessonsCompleted: Long = 0L,
    val wordsLearned: Long = 0L,
    val achievementsCount: Long = 0L,
    val followersCount: Long = 0L,
    val followingCount: Long = 0L,
    val isPrivate: Boolean = false,
    val shareAchievements: Boolean = true,
    val notifyActivity: Boolean = true,
    val interests: List<String> = listOf("Daily Conversation", "Basic Grammar"),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FollowRelation(
    @DocumentId val id: String = "",
    val followerUserId: String = "",
    val followedUserId: String = "",
    val followerDisplayName: String = "",
    val followerUsername: String = "",
    val followerAvatarUrl: String = AvatarPresets.SPARROW,
    val followedDisplayName: String = "",
    val followedUsername: String = "",
    val followedAvatarUrl: String = AvatarPresets.SPARROW,
    val isPublicFollow: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class UserAchievement(
    @DocumentId val id: String = "",
    val userId: String = "",
    val achievementType: String = "",
    val achievementTitle: String = "",
    val achievementTitleHi: String = "",
    val descriptionHi: String = "",
    val xpBonus: Long = 0L,
    val isPublic: Boolean = true,
    @ServerTimestamp val earnedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ActivityFeedItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val username: String = "",
    val displayName: String = "",
    val avatarUrl: String = AvatarPresets.SPARROW,
    val profileId: String = "",
    val activityType: String = "ACHIEVEMENT",
    val achievementId: String = "",
    val achievementTitle: String = "",
    val description: String = "",
    val descriptionHi: String = "",
    val visibility: String = "PUBLIC",
    val likesCount: Long = 0L,
    val commentsCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PostLike(
    @DocumentId val id: String = "",
    val activityId: String = "",
    val userId: String = "",
    val postOwnerId: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PostComment(
    @DocumentId val id: String = "",
    val activityId: String = "",
    val postOwnerId: String = "",
    val userId: String = "",
    val username: String = "",
    val displayName: String = "",
    val avatarUrl: String = AvatarPresets.SPARROW,
    val commentText: String = "",
    val parentCommentId: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AppNotification(
    @DocumentId val id: String = "",
    val recipientUserId: String = "",
    val actorUserId: String = "",
    val actorUsername: String = "",
    val actorDisplayName: String = "",
    val actorAvatarUrl: String = AvatarPresets.SPARROW,
    val notificationType: String = "FOLLOW",
    val message: String = "",
    val messageHi: String = "",
    val activityId: String = "",
    val commentId: String = "",
    val isRead: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class BlockedUser(
    @DocumentId val id: String = "",
    val blockerUserId: String = "",
    val blockedUserId: String = "",
    val blockedUsername: String = "",
    val blockedDisplayName: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ReportedContent(
    @DocumentId val id: String = "",
    val reporterUserId: String = "",
    val reportedUserId: String = "",
    val contentType: String = "COMMENT",
    val contentId: String = "",
    val reason: String = "",
    val status: String = "PENDING",
    @ServerTimestamp val createdAt: Timestamp? = null
)

enum class FollowButtonState {
    FOLLOW,
    FOLLOWING,
    FOLLOW_BACK
}

enum class FriendsSubTab(val labelHi: String, val labelEn: String) {
    FEED("फ़ीड", "Feed"),
    FIND_FRIENDS("मित्र खोजें", "Find Friends"),
    NOTIFICATIONS("सूचनाएँ", "Alerts")
}

object AvatarPresets {
    const val SPARROW = "preset:sparrow"
    const val OWL = "preset:owl"
    const val LOTUS = "preset:lotus"
    const val BOOK = "preset:book"
    const val STAR = "preset:star"
    const val TROPHY = "preset:trophy"

    val allPresets = listOf(SPARROW, OWL, LOTUS, BOOK, STAR, TROPHY)
}

data class AchievementDefinition(
    val type: String,
    val titleEn: String,
    val titleHi: String,
    val descriptionHi: String,
    val feedDescriptionTemplateEn: String,
    val feedDescriptionTemplateHi: String,
    val xpBonus: Long
)

object AchievementCatalog {
    val all: List<AchievementDefinition> = listOf(
        AchievementDefinition(
            type = "FIRST_LESSON",
            titleEn = "First Lesson Completed",
            titleHi = "पहला पाठ पूरा",
            descriptionHi = "अपना पहला अंग्रेज़ी पाठ सफलतापूर्वक पूरा किया",
            feedDescriptionTemplateEn = "%s completed their first English lesson!",
            feedDescriptionTemplateHi = "%s ने अपना पहला अंग्रेज़ी पाठ पूरा किया!",
            xpBonus = 25L
        ),
        AchievementDefinition(
            type = "XP_100",
            titleEn = "First 100 XP",
            titleHi = "पहले 100 XP",
            descriptionHi = "100 XP अंक अर्जित किए",
            feedDescriptionTemplateEn = "%s earned 100 XP!",
            feedDescriptionTemplateHi = "%s ने 100 XP अर्जित किए!",
            xpBonus = 20L
        ),
        AchievementDefinition(
            type = "STREAK_3",
            titleEn = "3-Day Streak",
            titleHi = "3-दिन की स्ट्रीक",
            descriptionHi = "लगातार 3 दिन अंग्रेज़ी का अभ्यास किया",
            feedDescriptionTemplateEn = "%s earned the 3-Day Streak Badge!",
            feedDescriptionTemplateHi = "%s ने 3-दिन की स्ट्रीक बैज जीती!",
            xpBonus = 30L
        ),
        AchievementDefinition(
            type = "STREAK_7",
            titleEn = "7-Day Streak",
            titleHi = "7-दिन की स्ट्रीक",
            descriptionHi = "लगातार 7 दिन अंग्रेज़ी सीखी",
            feedDescriptionTemplateEn = "%s earned the 7-Day Streak Badge!",
            feedDescriptionTemplateHi = "%s ने 7-दिन की स्ट्रीक बैज हासिल की!",
            xpBonus = 50L
        ),
        AchievementDefinition(
            type = "STREAK_30",
            titleEn = "30-Day Streak",
            titleHi = "30-दिन की स्ट्रीक",
            descriptionHi = "लगातार 30 दिन सीखने की अद्भुत आदत",
            feedDescriptionTemplateEn = "%s completed a 30-Day Learning Streak!",
            feedDescriptionTemplateHi = "%s ने 30-दिन की स्ट्रीक पूरी की!",
            xpBonus = 150L
        ),
        AchievementDefinition(
            type = "WORDS_100",
            titleEn = "100 Words Learned",
            titleHi = "100 शब्द सीखे",
            descriptionHi = "100 नए अंग्रेज़ी शब्द याद किए",
            feedDescriptionTemplateEn = "%s learned 100 new English words!",
            feedDescriptionTemplateHi = "%s ने 100 नए अंग्रेज़ी शब्द सीखे!",
            xpBonus = 40L
        ),
        AchievementDefinition(
            type = "WORDS_500",
            titleEn = "500 Words Learned",
            titleHi = "500 शब्द सीखे",
            descriptionHi = "500 अंग्रेज़ी शब्दों का मज़बूत संग्रह",
            feedDescriptionTemplateEn = "%s learned 500 new English words!",
            feedDescriptionTemplateHi = "%s ने 500 नए अंग्रेज़ी शब्द सीखे!",
            xpBonus = 120L
        ),
        AchievementDefinition(
            type = "BEGINNER_COMPLETE",
            titleEn = "Beginner Course Completed",
            titleHi = "बेसिक कोर्स पूर्ण",
            descriptionHi = "Level 1 Absolute Beginner पूरा किया",
            feedDescriptionTemplateEn = "%s completed the Beginner Level!",
            feedDescriptionTemplateHi = "%s ने शुरुआती स्तर (Beginner Level) पूरा किया!",
            xpBonus = 100L
        ),
        AchievementDefinition(
            type = "GRAMMAR_MASTER",
            titleEn = "Grammar Master",
            titleHi = "व्याकरण मास्टर",
            descriptionHi = "सरल हिंदी व्याकरण के मुख्य नियम पूरे किए",
            feedDescriptionTemplateEn = "%s unlocked the Grammar Master badge!",
            feedDescriptionTemplateHi = "%s ने ग्रामर मास्टर बैज जीता!",
            xpBonus = 80L
        ),
        AchievementDefinition(
            type = "CHAMPION",
            titleEn = "English Learning Champion",
            titleHi = "अंग्रेज़ी चैंपियन",
            descriptionHi = "1000+ XP के साथ नियमित अंग्रेज़ी संवाद अभ्यास",
            feedDescriptionTemplateEn = "%s became an English Learning Champion!",
            feedDescriptionTemplateHi = "%s अंग्रेज़ी लर्निंग चैंपियन बन गए!",
            xpBonus = 200L
        )
    )
}
