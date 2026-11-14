package com.example.data

import android.content.Context
import com.example.R
import com.example.model.NodeProgressRecord
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class CourseProgressRepository(
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
            ?: throw IllegalStateException("User must be signed in with Google before saving course progress.")
    }

    fun observeCourseProgress(userId: String): Flow<Map<String, NodeProgressRecord>> =
        db.collection("users")
            .document(userId)
            .collection("course_progress")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(
                        NodeProgressRecord::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )?.let { rec ->
                        val effectiveNodeId = rec.nodeId.ifBlank { doc.id }
                        effectiveNodeId to rec.copy(id = effectiveNodeId, nodeId = effectiveNodeId)
                    }
                }.toMap()
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val ex = firestoreError ?: (error as? Exception)
                if (ex != null && auth.currentUser != null) {
                    handleFirestoreError(ex, OperationType.LIST, "users/$userId/course_progress")
                }
                throw error
            }

    suspend fun saveNodeProgress(record: NodeProgressRecord): Result<Unit> = runCatching {
        val uid = requireUserId()
        val cleanNodeId = record.nodeId.ifBlank { record.id }
        require(cleanNodeId.isNotBlank() && record.unitId.isNotBlank()) {
            "NodeProgressRecord requires valid nodeId and unitId"
        }

        val docRef = db.collection("users")
            .document(uid)
            .collection("course_progress")
            .document(cleanNodeId)

        val existingSnap = docRef.get().await()
        if (!existingSnap.exists()) {
            docRef.set(
                mapOf(
                    "nodeId" to cleanNodeId,
                    "unitId" to record.unitId,
                    "status" to record.status,
                    "bestScorePercent" to record.bestScorePercent.coerceIn(0, 100),
                    "lastScorePercent" to record.lastScorePercent.coerceIn(0, 100),
                    "starsEarned" to record.starsEarned.coerceIn(0, 3),
                    "attemptsCount" to record.attemptsCount.coerceAtLeast(0),
                    "xpAwarded" to record.xpAwarded,
                    "passingScorePercent" to record.passingScorePercent.coerceIn(50, 100),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } else {
            docRef.update(
                mapOf(
                    "status" to record.status,
                    "bestScorePercent" to record.bestScorePercent.coerceIn(0, 100),
                    "lastScorePercent" to record.lastScorePercent.coerceIn(0, 100),
                    "starsEarned" to record.starsEarned.coerceIn(0, 3),
                    "attemptsCount" to record.attemptsCount.coerceAtLeast(0),
                    "xpAwarded" to record.xpAwarded,
                    "passingScorePercent" to record.passingScorePercent.coerceIn(50, 100),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
        Unit
    }.onFailure { error ->
        if (error !is CancellationException && auth.currentUser != null) {
            val uid = auth.currentUser?.uid ?: "unknown"
            handleFirestoreError(error, OperationType.WRITE, "users/$uid/course_progress/${record.nodeId}")
        }
    }

    suspend fun saveMultipleProgress(records: List<NodeProgressRecord>): Result<Unit> = runCatching {
        for (record in records) {
            saveNodeProgress(record).getOrThrow()
        }
        Unit
    }
}
