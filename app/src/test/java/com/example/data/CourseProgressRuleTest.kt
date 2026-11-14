package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.example.model.LessonNodeStatus
import com.example.model.NodeProgressRecord
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class CourseProgressRuleTest : FirestoreEmulatorTestBase() {

    private fun uniqueEmail(prefix: String): String {
        val tag = UUID.randomUUID().toString().replace("-", "").take(8)
        return "${prefix}_$tag@test.com"
    }

    @Test
    fun unauthenticated_course_progress_read_is_rejected() = runBlocking {
        auth.signOut()
        val repo = CourseProgressRepository(firestore, auth)

        var caught: Throwable? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repo.observeCourseProgress("any_uid").first()
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
    fun owner_can_persist_and_update_course_progress_while_other_users_are_denied() = runBlocking {
        val uidA = signInTestUser(uniqueEmail("learner_a"))
        val repoA = CourseProgressRepository(firestore, auth)

        val initialRecord = NodeProgressRecord(
            id = "s1_u1_n1",
            nodeId = "s1_u1_n1",
            unitId = "s1_u1",
            status = LessonNodeStatus.IN_PROGRESS.name,
            bestScorePercent = 50,
            lastScorePercent = 50,
            starsEarned = 0,
            attemptsCount = 1,
            xpAwarded = false,
            passingScorePercent = 70
        )
        withTimeout(TIMEOUT_MS) {
            repoA.saveNodeProgress(initialRecord).getOrThrow()
        }

        val updatedRecord = initialRecord.copy(
            status = LessonNodeStatus.PERFECT.name,
            bestScorePercent = 100,
            lastScorePercent = 100,
            starsEarned = 3,
            attemptsCount = 2,
            xpAwarded = true
        )
        withTimeout(TIMEOUT_MS) {
            repoA.saveNodeProgress(updatedRecord).getOrThrow()
        }

        val observed = withTimeout(FLOW_TIMEOUT_MS) {
            repoA.observeCourseProgress(uidA).first { map ->
                map["s1_u1_n1"]?.status == LessonNodeStatus.PERFECT.name
            }
        }
        assertEquals(100, observed["s1_u1_n1"]?.bestScorePercent)
        assertTrue(observed["s1_u1_n1"]?.xpAwarded == true)

        // Another authenticated user cannot read or overwrite user A's course_progress
        signInTestUser(uniqueEmail("learner_b"))
        val repoB = CourseProgressRepository(firestore, auth)
        var crossUserReadError: Throwable? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repoB.observeCourseProgress(uidA).first()
            }
        } catch (e: Throwable) {
            crossUserReadError = e
        }
        assertNotNull(crossUserReadError)
        assertEquals(
            FirebaseFirestoreException.Code.PERMISSION_DENIED,
            crossUserReadError?.unwrapFirestoreException()?.code
        )
    }

    private companion object {
        const val TIMEOUT_MS = 8000L
        const val FLOW_TIMEOUT_MS = 5000L
    }
}
