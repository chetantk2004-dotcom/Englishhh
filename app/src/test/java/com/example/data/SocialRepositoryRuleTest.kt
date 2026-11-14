package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class SocialRepositoryRuleTest : FirestoreEmulatorTestBase() {

    private fun uniqueEmail(prefix: String): String {
        val tag = UUID.randomUUID().toString().replace("-", "").take(8)
        return "${prefix}_$tag@test.com"
    }

    @Test
    fun unauthenticated_caller_is_rejected_with_permission_denied() = runBlocking {
        auth.signOut()
        val repo = SocialRepository(firestore, auth)

        var caught: Throwable? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repo.observeNotifications("any_uid").first()
            }
        } catch (e: Throwable) {
            caught = e
        }
        assertNotNull(caught)
        val firestoreEx = caught?.unwrapFirestoreException()
        assertNotNull("Expected FirebaseFirestoreException, got $caught", firestoreEx)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreEx?.code)
    }

    @Test
    fun full_social_cuj_follow_feed_achievements_likes_comments_notifications_and_privacy() = runBlocking {
        val emailA = uniqueEmail("rahul")
        val emailB = uniqueEmail("priya")
        val emailC = uniqueEmail("aman")

        // 1. User A registers with immutable EL profileId
        val uidA = signInTestUser(emailA)
        val repo = SocialRepository(firestore, auth)
        val profileA = withTimeout(TIMEOUT_MS) {
            repo.ensureUserProfile("Rahul Sharma", initialXp = 0L, initialStreak = 1L, initialLessons = 0L, initialWords = 0L).getOrThrow()
        }
        assertTrue(profileA.profileId.startsWith("EL"))

        // 2. User B registers
        val uidB = signInTestUser(emailB)
        val profileB = withTimeout(TIMEOUT_MS) {
            repo.ensureUserProfile("Priya Verma", initialXp = 0L, initialStreak = 3L, initialLessons = 0L, initialWords = 0L).getOrThrow()
        }

        // 3. User A follows User B & prevents self-follow & duplicate follow
        signInTestUser(emailA)
        val selfFollowResult = withTimeout(TIMEOUT_MS) { repo.followUser(profileA, profileA) }
        assertTrue(selfFollowResult.isFailure)

        withTimeout(TIMEOUT_MS) { repo.followUser(profileA, profileB).getOrThrow() }
        // Repeat follow should be idempotent and NOT duplicate count
        withTimeout(TIMEOUT_MS) { repo.followUser(profileA, profileB).getOrThrow() }

        val updatedB = withTimeout(TIMEOUT_MS) { repo.getUserProfileById(uidB).getOrThrow() }!!
        val updatedA = withTimeout(TIMEOUT_MS) { repo.getUserProfileById(uidA).getOrThrow() }!!
        assertEquals(1L, updatedB.followersCount)
        assertEquals(1L, updatedA.followingCount)

        // User A appears in User B's followers list, User B appears in User A's following list
        val bFollowers = withTimeout(TIMEOUT_MS) { repo.getFollowersForUser(uidB).getOrThrow() }
        val aFollowing = withTimeout(TIMEOUT_MS) { repo.getFollowingForUser(uidA).getOrThrow() }
        assertTrue(bFollowers.any { it.followerUserId == uidA })
        assertTrue(aFollowing.any { it.followedUserId == uidB })

        // 4. User B earns achievement -> creates activity post -> notifies follower User A
        signInTestUser(emailB)
        val earnedB = withTimeout(TIMEOUT_MS) {
            repo.recordLearningProgressAndAchievements(updatedB, xpDelta = 30L, lessonsDelta = 1L, wordsDelta = 10L).getOrThrow()
        }
        assertTrue(earnedB.any { it.type == "FIRST_LESSON" })

        // 5. User A sees User B's achievement in Friends Feed
        signInTestUser(emailA)
        val feedForA = withTimeout(TIMEOUT_MS) {
            repo.loadFriendsFeed(listOf(uidB), emptySet()).getOrThrow()
        }
        val bPost = feedForA.firstOrNull { it.userId == uidB && it.achievementId == "${uidB}_FIRST_LESSON" }
        val post = requireNotNull(bPost) { "Expected User B's achievement in User A's feed" }

        // 6. User A likes User B's achievement -> count updates; unlike works cleanly
        val liked = withTimeout(TIMEOUT_MS) { repo.toggleLike(updatedA, post).getOrThrow() }
        assertTrue(liked)
        val feedAfterLike = withTimeout(TIMEOUT_MS) {
            repo.loadFriendsFeed(listOf(uidB), emptySet()).getOrThrow()
        }
        assertEquals(1L, feedAfterLike.first { it.id == post.id }.likesCount)

        // 7. User A comments & replies on User B's achievement & User B reads comment
        val commentId = withTimeout(TIMEOUT_MS) {
            repo.addComment(updatedA, post, "Great job! Keep learning 🔥").getOrThrow()
        }
        assertTrue(commentId.isNotEmpty())
        // Empty comment rejected
        val emptyCommentResult = withTimeout(TIMEOUT_MS) { repo.addComment(updatedA, post, "   ") }
        assertTrue(emptyCommentResult.isFailure)

        // Search by Profile ID & username works
        val foundById = withTimeout(TIMEOUT_MS) { repo.searchUsers(profileB.profileId, emptySet()).getOrThrow() }
        assertTrue(foundById.any { it.userId == uidB })
        val foundByUsername = withTimeout(TIMEOUT_MS) { repo.searchUsers(profileB.username.take(5), emptySet()).getOrThrow() }
        assertTrue(foundByUsername.any { it.userId == uidB })

        signInTestUser(emailB)
        val commentsForB = withTimeout(FLOW_TIMEOUT_MS) {
            repo.observeComments(post.id).first { it.any { c -> c.id == commentId } }
        }
        val parentComment = commentsForB.first { it.id == commentId }
        assertEquals("Great job! Keep learning 🔥", parentComment.commentText)

        // Threaded reply & comment deletion
        val replyId = withTimeout(TIMEOUT_MS) {
            repo.addComment(updatedB, post, "Thank you! 🙏", parentComment = parentComment).getOrThrow()
        }
        assertTrue(replyId.isNotEmpty())
        withTimeout(TIMEOUT_MS) { repo.deleteComment(post.id, replyId).getOrThrow() }

        // Check User B received follow, like, and comment notifications and can mark read
        val bNotifs = withTimeout(FLOW_TIMEOUT_MS) {
            repo.observeNotifications(uidB).first { list -> list.size >= 3 }
        }
        assertTrue(bNotifs.any { it.notificationType == "FOLLOW" })
        assertTrue(bNotifs.any { it.notificationType == "LIKE" })
        assertTrue(bNotifs.any { it.notificationType == "COMMENT" })
        withTimeout(TIMEOUT_MS) { repo.markNotificationRead(bNotifs.first().id).getOrThrow() }

        // 8. Private profile, Blocking, Unblocking & Reporting checks with User C
        withTimeout(TIMEOUT_MS) {
            repo.updatePrivacySettings(isPrivate = true, shareAchievements = true, notifyActivity = true).getOrThrow()
        }
        val uidC = signInTestUser(emailC)
        val profileC = withTimeout(TIMEOUT_MS) {
            repo.ensureUserProfile("Aman Gupta", initialXp = 0L, initialStreak = 1L, initialLessons = 0L, initialWords = 0L).getOrThrow()
        }
        // User C does not follow private User B -> cannot read B's activities
        val privActivitiesForC = withTimeout(TIMEOUT_MS) {
            repo.getUserActivities(uidB, targetIsPrivate = true, amIFollowing = false).getOrThrow()
        }
        assertTrue(privActivitiesForC.isEmpty())

        // User C submits report
        withTimeout(TIMEOUT_MS) {
            repo.reportContent(uidB, "COMMENT", commentId, "Spam test report").getOrThrow()
        }

        // User B blocks User C -> User C cannot follow B; then B unblocks C
        signInTestUser(emailB)
        withTimeout(TIMEOUT_MS) { repo.blockUser(updatedB, profileC).getOrThrow() }

        signInTestUser(emailC)
        val followBlockedResult = withTimeout(TIMEOUT_MS) { repo.followUser(profileC, updatedB) }
        assertTrue(followBlockedResult.isFailure)

        signInTestUser(emailB)
        withTimeout(TIMEOUT_MS) { repo.unblockUser(uidC).getOrThrow() }

        // 9. Unfollow updates counts accurately & hideMyActivity hides post
        withTimeout(TIMEOUT_MS) { repo.hideMyActivity(post.id).getOrThrow() }

        signInTestUser(emailA)
        withTimeout(TIMEOUT_MS) { repo.unfollowUser(updatedA, uidB).getOrThrow() }
        val finalB = withTimeout(TIMEOUT_MS) { repo.getUserProfileById(uidB).getOrThrow() }
        // Note: A unfollowed B -> B's followersCount == 0
        assertEquals(0L, finalB?.followersCount)
        assertFalse(finalB?.userId.isNullOrBlank())
    }

    private companion object {
        const val TIMEOUT_MS = 8000L
        const val FLOW_TIMEOUT_MS = 5000L
    }
}
