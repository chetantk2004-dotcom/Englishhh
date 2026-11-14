package com.example.data

import android.content.Context
import com.example.R
import com.example.model.AchievementCatalog
import com.example.model.AchievementDefinition
import com.example.model.ActivityFeedItem
import com.example.model.AppNotification
import com.example.model.AvatarPresets
import com.example.model.BlockedUser
import com.example.model.FollowRelation
import com.example.model.PostComment
import com.example.model.UserAchievement
import com.example.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlin.math.abs

class SocialRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeUserProfile(userId: String): Flow<UserProfile?> =
        db.collection("users").document(userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObject(
                    UserProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
            .catch { error ->
                logIfNotCancelled(error, OperationType.GET, "users/$userId")
                throw error
            }

    fun observeFollowingRelations(userId: String): Flow<List<FollowRelation>> =
        db.collection("follows")
            .whereEqualTo("followerUserId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    FollowRelation::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
            .catch { error ->
                logIfNotCancelled(error, OperationType.LIST, "follows")
                throw error
            }

    fun observeFollowerRelations(userId: String): Flow<List<FollowRelation>> =
        db.collection("follows")
            .whereEqualTo("followedUserId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    FollowRelation::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
            .catch { error ->
                logIfNotCancelled(error, OperationType.LIST, "follows")
                throw error
            }

    fun observeBlockedUsers(userId: String): Flow<List<BlockedUser>> =
        db.collection("blocked_users")
            .whereEqualTo("blockerUserId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    BlockedUser::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
            .catch { error ->
                logIfNotCancelled(error, OperationType.LIST, "blocked_users")
                throw error
            }

    fun observeNotifications(userId: String): Flow<List<AppNotification>> =
        db.collection("notifications")
            .whereEqualTo("recipientUserId", userId)
            .limit(40)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    AppNotification::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }
            .catch { error ->
                logIfNotCancelled(error, OperationType.LIST, "notifications")
                throw error
            }

    fun observeComments(activityId: String, limit: Long = 30L): Flow<List<PostComment>> =
        db.collection("comments")
            .whereEqualTo("activityId", activityId)
            .limit(limit)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    PostComment::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedBy { it.createdAt ?: Timestamp(0, 0) }
            }
            .catch { error ->
                logIfNotCancelled(error, OperationType.LIST, "comments")
                throw error
            }

    fun observeMyLikedActivityIds(userId: String): Flow<Set<String>> =
        db.collection("post_likes")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.getString("activityId") }.toSet()
            }
            .catch { error ->
                logIfNotCancelled(error, OperationType.LIST, "post_likes")
                throw error
            }

    suspend fun ensureUserProfile(
        preferredDisplayName: String?,
        initialXp: Long = 240L,
        initialStreak: Long = 5L,
        initialLessons: Long = 9L,
        initialWords: Long = 38L
    ): Result<UserProfile> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val existingSnap = docRef.get().await()
        if (existingSnap.exists()) {
            val existing = existingSnap.toObject(
                UserProfile::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
            if (existing != null) return@runCatching existing
        }

        val cleanDisplay = (preferredDisplayName?.trim()?.takeIf { it.isNotBlank() } ?: "Learner")
            .take(40)
        val rawHandle = cleanDisplay.lowercase()
            .replace(Regex("[^a-z0-9_]"), "")
            .take(14)
            .ifBlank { "learner" }
        val suffix = abs(uid.hashCode() % 9000) + 1000
        val username = "${rawHandle}_$suffix".take(24)
        val profileId = generateUniqueProfileId(uid)

        val payload = mapOf(
            "userId" to uid,
            "profileId" to profileId,
            "username" to username,
            "usernameLower" to username.lowercase(),
            "displayName" to cleanDisplay,
            "avatarUrl" to AvatarPresets.SPARROW,
            "bio" to "हिंदी से अंग्रेज़ी सीख रहा हूँ! 🌱",
            "totalXp" to initialXp,
            "streak" to initialStreak,
            "lessonsCompleted" to initialLessons,
            "wordsLearned" to initialWords,
            "achievementsCount" to 0L,
            "followersCount" to 0L,
            "followingCount" to 0L,
            "isPrivate" to false,
            "shareAchievements" to true,
            "notifyActivity" to true,
            "interests" to listOf("Daily Conversation", "Basic Grammar"),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
        val created = docRef.get().await().toObject(
            UserProfile::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
        )
        created ?: error("Profile creation readback returned null")
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.CREATE, "users")
        }
    }

    private suspend fun generateUniqueProfileId(uid: String): String {
        val baseNum = (abs(uid.hashCode()) % 89999) + 10000
        var candidate = "EL$baseNum"
        for (attempt in 0 until 5) {
            val existing = db.collection("users")
                .whereEqualTo("profileId", candidate)
                .limit(1)
                .get()
                .await()
            if (existing.isEmpty) return candidate
            candidate = "EL${((baseNum + attempt * 7919) % 89999) + 10000}"
        }
        return "EL${UUID.randomUUID().toString().replace("-", "").uppercase().take(7)}"
    }

    suspend fun updateProfile(
        displayName: String,
        usernameInput: String,
        bio: String,
        avatarUrl: String,
        interests: List<String>
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val cleanName = displayName.trim().take(40)
        require(cleanName.isNotEmpty()) { "नाम खाली नहीं हो सकता।" }

        val cleanUsername = usernameInput.trim().removePrefix("@")
        require(cleanUsername.length in 3..24 && cleanUsername.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            "यूज़रनेम 3–24 अक्षरों का होना चाहिए (केवल अक्षर, अंक और _)"
        }
        val lower = cleanUsername.lowercase()

        // Ensure username uniqueness across other users
        val conflict = db.collection("users")
            .whereEqualTo("usernameLower", lower)
            .limit(2)
            .get()
            .await()
        val takenByOther = conflict.documents.any { it.id != uid }
        require(!takenByOther) { "यह यूज़रनेम (@$cleanUsername) पहले से उपयोग में है।" }

        val payload = mapOf(
            "displayName" to cleanName,
            "username" to cleanUsername,
            "usernameLower" to lower,
            "bio" to bio.trim().take(160),
            "avatarUrl" to avatarUrl.take(35000),
            "interests" to interests.take(5),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        db.collection("users").document(uid).update(payload).await()
        Unit
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.UPDATE, "users")
        }
    }

    suspend fun updatePrivacySettings(
        isPrivate: Boolean,
        shareAchievements: Boolean,
        notifyActivity: Boolean
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        db.collection("users").document(uid).update(
            mapOf(
                "isPrivate" to isPrivate,
                "shareAchievements" to shareAchievements,
                "notifyActivity" to notifyActivity,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
        Unit
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.UPDATE, "users")
        }
    }

    suspend fun getUserProfileById(targetUserId: String): Result<UserProfile?> = runCatching {
        requireUserId()
        val snap = db.collection("users").document(targetUserId).get().await()
        snap.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.GET, "users/$targetUserId")
        }
    }

    suspend fun searchUsers(query: String, blockedUserIds: Set<String>): Result<List<UserProfile>> = runCatching {
        val myUid = requireUserId()
        val trimmed = query.trim().removePrefix("@")
        if (trimmed.isEmpty()) {
            return@runCatching getSuggestedUsers(blockedUserIds).getOrThrow()
        }

        val upper = trimmed.uppercase()
        val lower = trimmed.lowercase()

        val results = LinkedHashMap<String, UserProfile>()

        // 1. Exact Profile ID match (e.g., EL10245)
        if (upper.startsWith("EL")) {
            val byProfileId = db.collection("users")
                .whereEqualTo("profileId", upper)
                .limit(10)
                .get()
                .await()
            byProfileId.toObjects(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                .forEach { results[it.userId] = it }
        }

        // 2. Case-insensitive username prefix search
        val byUsername = db.collection("users")
            .orderBy("usernameLower")
            .startAt(lower)
            .endAt(lower + "\uf8ff")
            .limit(20)
            .get()
            .await()
        byUsername.toObjects(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            .forEach { results[it.userId] = it }

        results.values
            .filter { it.userId != myUid && it.userId !in blockedUserIds }
            .toList()
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.LIST, "users")
        }
    }

    suspend fun getSuggestedUsers(blockedUserIds: Set<String>): Result<List<UserProfile>> = runCatching {
        val myUid = requireUserId()
        val snap = db.collection("users")
            .orderBy("totalXp", Query.Direction.DESCENDING)
            .limit(25)
            .get()
            .await()
        snap.toObjects(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            .filter { it.userId != myUid && it.userId !in blockedUserIds }
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.LIST, "users")
        }
    }

    suspend fun followUser(me: UserProfile, target: UserProfile): Result<Unit> = runCatching {
        val myUid = requireUserId()
        require(myUid == me.userId && target.userId != myUid) { "आप स्वयं को फ़ॉलो नहीं कर सकते।" }

        val followId = "${myUid}_${target.userId}"
        val followRef = db.collection("follows").document(followId)
        if (followRef.get().await().exists()) {
            return@runCatching
        }

        val myRef = db.collection("users").document(myUid)
        val targetRef = db.collection("users").document(target.userId)
        val myFresh = myRef.get().await().toObject(UserProfile::class.java) ?: me
        val targetFresh = targetRef.get().await().toObject(UserProfile::class.java) ?: target

        val batch = db.batch()
        batch.set(
            followRef,
            mapOf(
                "followerUserId" to myUid,
                "followedUserId" to target.userId,
                "followerDisplayName" to myFresh.displayName,
                "followerUsername" to myFresh.username,
                "followerAvatarUrl" to myFresh.avatarUrl,
                "followedDisplayName" to targetFresh.displayName,
                "followedUsername" to targetFresh.username,
                "followedAvatarUrl" to targetFresh.avatarUrl,
                "isPublicFollow" to !targetFresh.isPrivate,
                "createdAt" to FieldValue.serverTimestamp()
            )
        )
        batch.update(
            myRef,
            mapOf(
                "followingCount" to myFresh.followingCount + 1L,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        )
        batch.update(
            targetRef,
            mapOf(
                "followersCount" to targetFresh.followersCount + 1L,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        )
        batch.commit().await()

        // Create deterministic FOLLOW notification (no duplicates if toggled rapidly)
        val notifId = "follow_${myUid}_${target.userId}"
        val notifRef = db.collection("notifications").document(notifId)
        if (!notifRef.get().await().exists()) {
            notifRef.set(
                mapOf(
                    "recipientUserId" to target.userId,
                    "actorUserId" to myUid,
                    "actorUsername" to myFresh.username,
                    "actorDisplayName" to myFresh.displayName,
                    "actorAvatarUrl" to myFresh.avatarUrl,
                    "notificationType" to "FOLLOW",
                    "message" to "${myFresh.displayName} started following you.",
                    "messageHi" to "${myFresh.displayName} ने आपको फ़ॉलो किया।",
                    "activityId" to "",
                    "commentId" to "",
                    "isRead" to false,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
        Unit
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.WRITE, "follows")
        }
    }

    suspend fun unfollowUser(me: UserProfile, targetUserId: String): Result<Unit> = runCatching {
        val myUid = requireUserId()
        require(myUid == me.userId && targetUserId != myUid)
        val followId = "${myUid}_${targetUserId}"
        val followRef = db.collection("follows").document(followId)
        if (!followRef.get().await().exists()) {
            return@runCatching
        }

        val myRef = db.collection("users").document(myUid)
        val targetRef = db.collection("users").document(targetUserId)
        val myFresh = myRef.get().await().toObject(UserProfile::class.java) ?: me
        val targetFresh = targetRef.get().await().toObject(UserProfile::class.java)

        val batch = db.batch()
        batch.delete(followRef)
        batch.update(
            myRef,
            mapOf(
                "followingCount" to (myFresh.followingCount - 1L).coerceAtLeast(0L),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        )
        if (targetFresh != null && targetFresh.followersCount > 0L) {
            batch.update(
                targetRef,
                mapOf(
                    "followersCount" to (targetFresh.followersCount - 1L).coerceAtLeast(0L),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
        }
        batch.commit().await()
        Unit
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.DELETE, "follows/$targetUserId")
        }
    }

    suspend fun getFollowersForUser(targetUserId: String): Result<List<FollowRelation>> = runCatching {
        requireUserId()
        db.collection("follows")
            .whereEqualTo("followedUserId", targetUserId)
            .limit(50)
            .get()
            .await()
            .toObjects(FollowRelation::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.LIST, "follows")
        }
    }

    suspend fun getFollowingForUser(targetUserId: String): Result<List<FollowRelation>> = runCatching {
        requireUserId()
        db.collection("follows")
            .whereEqualTo("followerUserId", targetUserId)
            .limit(50)
            .get()
            .await()
            .toObjects(FollowRelation::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.LIST, "follows")
        }
    }

    suspend fun getUserAchievements(targetUserId: String, amIFollowing: Boolean): Result<List<UserAchievement>> = runCatching {
        val myUid = requireUserId()
        val snap = if (myUid == targetUserId) {
            db.collection("achievements")
                .whereEqualTo("userId", targetUserId)
                .get()
                .await()
        } else {
            db.collection("achievements")
                .whereEqualTo("userId", targetUserId)
                .whereEqualTo("isPublic", true)
                .get()
                .await()
        }
        snap.toObjects(UserAchievement::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            .sortedByDescending { it.earnedAt ?: Timestamp(0, 0) }
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.LIST, "achievements")
        }
    }

    suspend fun getUserActivities(
        targetUserId: String,
        targetIsPrivate: Boolean,
        amIFollowing: Boolean,
        limit: Long = 20L
    ): Result<List<ActivityFeedItem>> = runCatching {
        val myUid = requireUserId()
        if (targetUserId != myUid && targetIsPrivate && !amIFollowing) {
            return@runCatching emptyList()
        }

        val items = if (targetUserId == myUid) {
            db.collection("activity_feed")
                .whereEqualTo("userId", targetUserId)
                .limit(limit)
                .get()
                .await()
                .toObjects(ActivityFeedItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
        } else if (targetIsPrivate) {
            db.collection("users").document(targetUserId)
                .collection("follower_activities")
                .limit(limit)
                .get()
                .await()
                .toObjects(ActivityFeedItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
        } else {
            db.collection("activity_feed")
                .whereEqualTo("userId", targetUserId)
                .whereEqualTo("visibility", "PUBLIC")
                .limit(limit)
                .get()
                .await()
                .toObjects(ActivityFeedItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
        }
        items.filter { it.visibility != "HIDDEN" || it.userId == myUid }
            .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.LIST, "activity_feed")
        }
    }

    suspend fun loadFriendsFeed(
        followingUserIds: List<String>,
        blockedUserIds: Set<String>,
        limitPerChunk: Long = 20L
    ): Result<List<ActivityFeedItem>> = runCatching {
        val myUid = requireUserId()
        val allowedTargets = followingUserIds.filter { it !in blockedUserIds }.distinct()
        val feedMap = LinkedHashMap<String, ActivityFeedItem>()

        // Always include my own non-hidden recent activities
        val mySnap = db.collection("activity_feed")
            .whereEqualTo("userId", myUid)
            .limit(limitPerChunk)
            .get()
            .await()
        mySnap.toObjects(ActivityFeedItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            .filter { it.visibility != "HIDDEN" }
            .forEach { feedMap[it.id] = it }

        // Public activities from followed users (chunked by 10 for Firestore whereIn limit)
        allowedTargets.chunked(10).forEach { chunk ->
            val publicSnap = db.collection("activity_feed")
                .whereEqualTo("visibility", "PUBLIC")
                .whereIn("userId", chunk)
                .limit(limitPerChunk)
                .get()
                .await()
            publicSnap.toObjects(ActivityFeedItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                .forEach { feedMap[it.id] = it }
        }

        // Private-profile activities from followed users (authorized via follows doc)
        allowedTargets.take(15).forEach { followedUid ->
            try {
                val privSnap = db.collection("users").document(followedUid)
                    .collection("follower_activities")
                    .limit(10)
                    .get()
                    .await()
                privSnap.toObjects(ActivityFeedItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .filter { it.visibility != "HIDDEN" }
                    .forEach { feedMap[it.id] = it }
            } catch (_: Exception) {
                // If not present or blocked, skip gracefully
            }
        }

        feedMap.values
            .filter { it.userId !in blockedUserIds }
            .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.LIST, "activity_feed")
        }
    }

    suspend fun recordLearningProgressAndAchievements(
        me: UserProfile,
        xpDelta: Long,
        lessonsDelta: Long,
        wordsDelta: Long
    ): Result<List<AchievementDefinition>> = runCatching {
        val myUid = requireUserId()
        require(myUid == me.userId)
        val userRef = db.collection("users").document(myUid)
        val fresh = userRef.get().await().toObject(UserProfile::class.java) ?: me

        val newXp = fresh.totalXp + xpDelta
        val newLessons = fresh.lessonsCompleted + lessonsDelta
        val newWords = fresh.wordsLearned + wordsDelta

        // Determine newly qualified achievements
        val qualifyingTypes = buildList {
            if (newLessons >= 1L) add("FIRST_LESSON")
            if (newXp >= 100L) add("XP_100")
            if (fresh.streak >= 3L) add("STREAK_3")
            if (fresh.streak >= 7L) add("STREAK_7")
            if (fresh.streak >= 30L) add("STREAK_30")
            if (newWords >= 100L) add("WORDS_100")
            if (newWords >= 500L) add("WORDS_500")
            if (newLessons >= 10L) add("BEGINNER_COMPLETE")
            if (newXp >= 1000L) add("CHAMPION")
        }

        val newlyEarned = mutableListOf<AchievementDefinition>()
        for (type in qualifyingTypes) {
            val def = AchievementCatalog.all.firstOrNull { it.type == type } ?: continue
            val achId = "${myUid}_${type}"
            val achRef = db.collection("achievements").document(achId)
            if (!achRef.get().await().exists()) {
                newlyEarned.add(def)
            }
        }

        val bonusXp = newlyEarned.sumOf { it.xpBonus }
        userRef.update(
            mapOf(
                "totalXp" to (newXp + bonusXp),
                "lessonsCompleted" to newLessons,
                "wordsLearned" to newWords,
                "achievementsCount" to (fresh.achievementsCount + newlyEarned.size),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()

        val followerRelations = if (fresh.shareAchievements && newlyEarned.isNotEmpty()) {
            db.collection("follows")
                .whereEqualTo("followedUserId", myUid)
                .limit(25)
                .get()
                .await()
                .toObjects(FollowRelation::class.java)
        } else emptyList()

        for (def in newlyEarned) {
            val achId = "${myUid}_${def.type}"
            val activityId = "ach_${myUid}_${def.type}"
            val visibility = when {
                !fresh.shareAchievements -> "HIDDEN"
                fresh.isPrivate -> "FOLLOWERS"
                else -> "PUBLIC"
            }

            db.collection("achievements").document(achId).set(
                mapOf(
                    "userId" to myUid,
                    "achievementType" to def.type,
                    "achievementTitle" to def.titleEn,
                    "achievementTitleHi" to def.titleHi,
                    "descriptionHi" to def.descriptionHi,
                    "xpBonus" to def.xpBonus,
                    "isPublic" to !fresh.isPrivate,
                    "earnedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            val activityPayload = mapOf(
                "userId" to myUid,
                "username" to fresh.username,
                "displayName" to fresh.displayName,
                "avatarUrl" to fresh.avatarUrl,
                "profileId" to fresh.profileId,
                "activityType" to "ACHIEVEMENT",
                "achievementId" to achId,
                "achievementTitle" to def.titleEn,
                "description" to String.format(def.feedDescriptionTemplateEn, fresh.displayName),
                "descriptionHi" to String.format(def.feedDescriptionTemplateHi, fresh.displayName),
                "visibility" to visibility,
                "likesCount" to 0L,
                "commentsCount" to 0L,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            // Deterministic activity ID prevents duplicate posts for same achievement
            val actRef = db.collection("activity_feed").document(activityId)
            if (!actRef.get().await().exists()) {
                actRef.set(activityPayload).await()
                if (fresh.isPrivate && fresh.shareAchievements) {
                    db.collection("users").document(myUid)
                        .collection("follower_activities").document(activityId)
                        .set(activityPayload).await()
                }
            }

            // Notify followers who enabled activity notifications
            for (rel in followerRelations) {
                try {
                    val followerSnap = db.collection("users").document(rel.followerUserId).get().await()
                    val notifyEnabled = followerSnap.getBoolean("notifyActivity") ?: true
                    if (notifyEnabled) {
                        val notifId = "ach_${activityId}_${rel.followerUserId}"
                        db.collection("notifications").document(notifId).set(
                            mapOf(
                                "recipientUserId" to rel.followerUserId,
                                "actorUserId" to myUid,
                                "actorUsername" to fresh.username,
                                "actorDisplayName" to fresh.displayName,
                                "actorAvatarUrl" to fresh.avatarUrl,
                                "notificationType" to "FRIEND_ACHIEVEMENT",
                                "message" to String.format(def.feedDescriptionTemplateEn, fresh.displayName),
                                "messageHi" to String.format(def.feedDescriptionTemplateHi, fresh.displayName),
                                "activityId" to activityId,
                                "commentId" to "",
                                "isRead" to false,
                                "createdAt" to FieldValue.serverTimestamp()
                            )
                        ).await()
                    }
                } catch (_: Exception) {
                    // Skip notification if blocked
                }
            }
        }
        newlyEarned
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.WRITE, "achievements")
        }
    }

    suspend fun toggleLike(me: UserProfile, activity: ActivityFeedItem): Result<Boolean> = runCatching {
        val myUid = requireUserId()
        val likeId = "${activity.id}_${myUid}"
        val likeRef = db.collection("post_likes").document(likeId)
        val actRef = db.collection("activity_feed").document(activity.id)

        val alreadyLiked = likeRef.get().await().exists()
        val freshAct = actRef.get().await().toObject(ActivityFeedItem::class.java) ?: activity

        val batch = db.batch()
        if (alreadyLiked) {
            batch.delete(likeRef)
            batch.update(
                actRef,
                mapOf(
                    "likesCount" to (freshAct.likesCount - 1L).coerceAtLeast(0L),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.commit().await()
            false
        } else {
            batch.set(
                likeRef,
                mapOf(
                    "activityId" to activity.id,
                    "userId" to myUid,
                    "postOwnerId" to activity.userId,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
            batch.update(
                actRef,
                mapOf(
                    "likesCount" to freshAct.likesCount + 1L,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.commit().await()

            if (activity.userId != myUid) {
                val notifId = "like_${activity.id}_${myUid}"
                val notifRef = db.collection("notifications").document(notifId)
                if (!notifRef.get().await().exists()) {
                    notifRef.set(
                        mapOf(
                            "recipientUserId" to activity.userId,
                            "actorUserId" to myUid,
                            "actorUsername" to me.username,
                            "actorDisplayName" to me.displayName,
                            "actorAvatarUrl" to me.avatarUrl,
                            "notificationType" to "LIKE",
                            "message" to "${me.displayName} liked your ${activity.achievementTitle} achievement.",
                            "messageHi" to "${me.displayName} ने आपकी '${activity.achievementTitle}' उपलब्धि को पसंद किया।",
                            "activityId" to activity.id,
                            "commentId" to "",
                            "isRead" to false,
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }
            true
        }
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.WRITE, "post_likes")
        }
    }

    suspend fun addComment(
        me: UserProfile,
        activity: ActivityFeedItem,
        commentText: String,
        parentComment: PostComment? = null
    ): Result<String> = runCatching {
        val myUid = requireUserId()
        val trimmed = commentText.trim()
        require(trimmed.isNotEmpty()) { "खाली टिप्पणी नहीं भेजी जा सकती।" }
        require(trimmed.length <= 500) { "टिप्पणी 500 अक्षरों से कम होनी चाहिए।" }

        val commentRef = db.collection("comments").document()
        val actRef = db.collection("activity_feed").document(activity.id)
        val freshAct = actRef.get().await().toObject(ActivityFeedItem::class.java) ?: activity

        val batch = db.batch()
        batch.set(
            commentRef,
            mapOf(
                "activityId" to activity.id,
                "postOwnerId" to activity.userId,
                "userId" to myUid,
                "username" to me.username,
                "displayName" to me.displayName,
                "avatarUrl" to me.avatarUrl,
                "commentText" to trimmed,
                "parentCommentId" to (parentComment?.id ?: ""),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
        )
        batch.update(
            actRef,
            mapOf(
                "commentsCount" to freshAct.commentsCount + 1L,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        )
        batch.commit().await()

        // Notify post owner (or parent comment author for reply)
        val recipientId = if (parentComment != null && parentComment.userId != myUid) {
            parentComment.userId
        } else if (activity.userId != myUid) {
            activity.userId
        } else {
            null
        }
        if (recipientId != null) {
            val isReply = parentComment != null
            db.collection("notifications").document().set(
                mapOf(
                    "recipientUserId" to recipientId,
                    "actorUserId" to myUid,
                    "actorUsername" to me.username,
                    "actorDisplayName" to me.displayName,
                    "actorAvatarUrl" to me.avatarUrl,
                    "notificationType" to if (isReply) "REPLY" else "COMMENT",
                    "message" to if (isReply) {
                        "${me.displayName} replied: $trimmed"
                    } else {
                        "${me.displayName} commented: $trimmed"
                    }.take(180),
                    "messageHi" to if (isReply) {
                        "${me.displayName} ने जवाब दिया: $trimmed"
                    } else {
                        "${me.displayName} ने टिप्पणी की: $trimmed"
                    }.take(220),
                    "activityId" to activity.id,
                    "commentId" to commentRef.id,
                    "isRead" to false,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
        commentRef.id
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.CREATE, "comments")
        }
    }

    suspend fun deleteComment(activityId: String, commentId: String): Result<Unit> = runCatching {
        requireUserId()
        val commentRef = db.collection("comments").document(commentId)
        val actRef = db.collection("activity_feed").document(activityId)
        val freshAct = actRef.get().await().toObject(ActivityFeedItem::class.java)

        val batch = db.batch()
        batch.delete(commentRef)
        if (freshAct != null && freshAct.commentsCount > 0L) {
            batch.update(
                actRef,
                mapOf(
                    "commentsCount" to (freshAct.commentsCount - 1L).coerceAtLeast(0L),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
        }
        batch.commit().await()
        Unit
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.DELETE, "comments/$commentId")
        }
    }

    suspend fun hideMyActivity(activityId: String): Result<Unit> = runCatching {
        requireUserId()
        db.collection("activity_feed").document(activityId).update(
            mapOf(
                "visibility" to "HIDDEN",
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
        Unit
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.UPDATE, "activity_feed/$activityId")
        }
    }

    suspend fun markNotificationRead(notificationId: String): Result<Unit> = runCatching {
        requireUserId()
        db.collection("notifications").document(notificationId)
            .update("isRead", true)
            .await()
        Unit
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.UPDATE, "notifications/$notificationId")
        }
    }

    suspend fun markAllNotificationsRead(notifications: List<AppNotification>): Result<Unit> = runCatching {
        requireUserId()
        val unread = notifications.filter { !it.isRead }
        if (unread.isEmpty()) return@runCatching
        val batch = db.batch()
        unread.take(40).forEach { notif ->
            batch.update(db.collection("notifications").document(notif.id), "isRead", true)
        }
        batch.commit().await()
        Unit
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.UPDATE, "notifications")
        }
    }

    suspend fun blockUser(me: UserProfile, target: UserProfile): Result<Unit> = runCatching {
        val myUid = requireUserId()
        require(myUid == me.userId && target.userId != myUid)
        val blockId = "${myUid}_${target.userId}"
        db.collection("blocked_users").document(blockId).set(
            mapOf(
                "blockerUserId" to myUid,
                "blockedUserId" to target.userId,
                "blockedUsername" to target.username,
                "blockedDisplayName" to target.displayName,
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
        // Also clean up outgoing follow if present
        unfollowUser(me, target.userId)
        Unit
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.CREATE, "blocked_users")
        }
    }

    suspend fun unblockUser(targetUserId: String): Result<Unit> = runCatching {
        val myUid = requireUserId()
        db.collection("blocked_users").document("${myUid}_${targetUserId}").delete().await()
        Unit
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.DELETE, "blocked_users")
        }
    }

    suspend fun reportContent(
        reportedUserId: String,
        contentType: String,
        contentId: String,
        reason: String
    ): Result<Unit> = runCatching {
        val myUid = requireUserId()
        val cleanReason = reason.trim().take(300)
        require(cleanReason.length >= 3) { "कृपया रिपोर्ट का कारण लिखें।" }
        db.collection("reported_content").document().set(
            mapOf(
                "reporterUserId" to myUid,
                "reportedUserId" to reportedUserId,
                "contentType" to contentType,
                "contentId" to contentId,
                "reason" to cleanReason,
                "status" to "PENDING",
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).await()
        Unit
    }.onFailure { e ->
        if (e !is CancellationException) {
            handleFirestoreError(e, OperationType.CREATE, "reported_content")
        }
    }

    private fun logIfNotCancelled(error: Throwable, op: OperationType, path: String) {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) return
        val ex = firestoreError ?: (error as? Exception)
        if (ex != null && auth.currentUser != null) {
            handleFirestoreError(ex, op, path)
        }
    }
}
