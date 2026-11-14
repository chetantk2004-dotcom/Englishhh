package com.example.data

import android.content.Context
import com.example.model.LessonNodeStatus
import com.example.model.NodeProgressRecord

class CourseProgressLocalStore(context: Context) {

    private val appContext = context.applicationContext

    fun loadProgress(userId: String): Map<String, NodeProgressRecord> {
        if (userId.isBlank()) return emptyMap()
        val prefs = appContext.getSharedPreferences(prefsName(userId), Context.MODE_PRIVATE)
        val result = mutableMapOf<String, NodeProgressRecord>()
        prefs.all.forEach { (key, rawValue) ->
            val encoded = rawValue as? String ?: return@forEach
            decode(key, encoded)?.let { record ->
                result[key] = record
            }
        }
        return result
    }

    fun saveRecord(userId: String, record: NodeProgressRecord) {
        if (userId.isBlank() || record.nodeId.isBlank()) return
        val prefs = appContext.getSharedPreferences(prefsName(userId), Context.MODE_PRIVATE)
        prefs.edit().putString(record.nodeId, encode(record)).apply()
    }

    fun saveAll(userId: String, records: Map<String, NodeProgressRecord>) {
        if (userId.isBlank()) return
        val prefs = appContext.getSharedPreferences(prefsName(userId), Context.MODE_PRIVATE)
        val editor = prefs.edit()
        records.values.forEach { record ->
            if (record.nodeId.isNotBlank()) {
                editor.putString(record.nodeId, encode(record))
            }
        }
        editor.apply()
    }

    private fun prefsName(userId: String): String =
        "course_progress_${userId.replace(Regex("[^a-zA-Z0-9_-]"), "_")}"

    private fun encode(r: NodeProgressRecord): String =
        listOf(
            r.unitId,
            r.status,
            r.bestScorePercent.toString(),
            r.lastScorePercent.toString(),
            r.starsEarned.toString(),
            r.attemptsCount.toString(),
            r.xpAwarded.toString(),
            r.passingScorePercent.toString()
        ).joinToString("|")

    private fun decode(nodeId: String, encoded: String): NodeProgressRecord? {
        val parts = encoded.split("|")
        if (parts.size < 8) return null
        val unitId = parts[0]
        val status = LessonNodeStatus.entries.firstOrNull { it.name == parts[1] }?.name
            ?: LessonNodeStatus.LOCKED.name
        val bestScore = parts[2].toIntOrNull() ?: 0
        val lastScore = parts[3].toIntOrNull() ?: 0
        val stars = parts[4].toIntOrNull() ?: 0
        val attempts = parts[5].toIntOrNull() ?: 0
        val xpAwarded = parts[6].toBooleanStrictOrNull() ?: false
        val passing = parts[7].toIntOrNull() ?: PathProgressionEngine.DEFAULT_PASSING_SCORE_PERCENT
        return NodeProgressRecord(
            id = nodeId,
            nodeId = nodeId,
            unitId = unitId,
            status = status,
            bestScorePercent = bestScore.coerceIn(0, 100),
            lastScorePercent = lastScore.coerceIn(0, 100),
            starsEarned = stars.coerceIn(0, 3),
            attemptsCount = attempts.coerceAtLeast(0),
            xpAwarded = xpAwarded,
            passingScorePercent = passing.coerceIn(50, 100)
        )
    }
}
