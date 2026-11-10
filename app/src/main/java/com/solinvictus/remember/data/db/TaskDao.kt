package com.solinvictus.remember.data.db

import android.content.ContentValues
import android.database.Cursor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.solinvictus.remember.data.model.Priority
import com.solinvictus.remember.data.model.SubTask
import com.solinvictus.remember.data.model.TaskItem

data class TaskStats(
    val totalCount: Int,
    val completedCount: Int,
    val pendingCount: Int,
    val urgentCount: Int,
    val completionRate: Float
)

class TaskDao(private val dbHelper: DatabaseHelper) {

    private val gson = Gson()
    private val subtaskType = object : TypeToken<List<SubTask>>() {}.type

    fun insert(task: TaskItem): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id", task.id)
            put("title", task.title)
            put("description", task.description)
            put("date", task.date)
            put("time", task.time)
            put("timeMillis", task.timeMillis)
            put("priority", task.priority.name)
            put("isCompleted", if (task.isCompleted) 1 else 0)
            put("completedAtMillis", task.completedAtMillis)
            put("hasAlarm", if (task.hasAlarm) 1 else 0)
            put("isDailyAlarm", if (task.isDailyAlarm) 1 else 0)
            put("category", task.category)
            put("subtasksJson", gson.toJson(task.subtasks))
            put("createdAt", task.createdAt)
        }
        return db.insertWithOnConflict(DatabaseHelper.TABLE_TASKS, null, values, android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun update(task: TaskItem): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", task.title)
            put("description", task.description)
            put("date", task.date)
            put("time", task.time)
            put("timeMillis", task.timeMillis)
            put("priority", task.priority.name)
            put("isCompleted", if (task.isCompleted) 1 else 0)
            put("completedAtMillis", task.completedAtMillis)
            put("hasAlarm", if (task.hasAlarm) 1 else 0)
            put("isDailyAlarm", if (task.isDailyAlarm) 1 else 0)
            put("category", task.category)
            put("subtasksJson", gson.toJson(task.subtasks))
        }
        return db.update(DatabaseHelper.TABLE_TASKS, values, "id = ?", arrayOf(task.id))
    }

    fun toggleCompleted(id: String, isCompleted: Boolean): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("isCompleted", if (isCompleted) 1 else 0)
            put("completedAtMillis", if (isCompleted) System.currentTimeMillis() else null)
        }
        return db.update(DatabaseHelper.TABLE_TASKS, values, "id = ?", arrayOf(id))
    }

    fun delete(id: String): Int {
        val db = dbHelper.writableDatabase
        return db.delete(DatabaseHelper.TABLE_TASKS, "id = ?", arrayOf(id))
    }

    fun getById(id: String): TaskItem? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_TASKS,
            null,
            "id = ?",
            arrayOf(id),
            null,
            null,
            null
        )
        return cursor.use {
            if (it.moveToFirst()) cursorToTask(it) else null
        }
    }

    fun getAll(): List<TaskItem> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_TASKS,
            null,
            null,
            null,
            null,
            null,
            "date ASC, timeMillis ASC, createdAt DESC"
        )
        return cursorToList(cursor)
    }

    fun getByDate(date: String): List<TaskItem> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_TASKS,
            null,
            "date = ?",
            arrayOf(date),
            null,
            null,
            "isCompleted ASC, timeMillis ASC, createdAt ASC"
        )
        return cursorToList(cursor)
    }

    fun getByMonth(yearMonth: String): List<TaskItem> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_TASKS,
            null,
            "date LIKE ?",
            arrayOf("$yearMonth%"),
            null,
            null,
            "date ASC, timeMillis ASC"
        )
        return cursorToList(cursor)
    }

    fun getActiveAlarms(): List<TaskItem> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_TASKS,
            null,
            "hasAlarm = 1 AND isCompleted = 0",
            null,
            null,
            null,
            "timeMillis ASC"
        )
        return cursorToList(cursor)
    }

    fun search(query: String): List<TaskItem> {
        val db = dbHelper.readableDatabase
        val wildQuery = "%$query%"
        val cursor = db.query(
            DatabaseHelper.TABLE_TASKS,
            null,
            "title LIKE ? OR description LIKE ? OR category LIKE ?",
            arrayOf(wildQuery, wildQuery, wildQuery),
            null,
            null,
            "date DESC, createdAt DESC"
        )
        return cursorToList(cursor)
    }

    fun getStats(): TaskStats {
        val db = dbHelper.readableDatabase
        var total = 0
        var completed = 0
        var urgent = 0

        val cursor = db.rawQuery(
            "SELECT isCompleted, priority FROM ${DatabaseHelper.TABLE_TASKS}",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                total++
                val isComp = it.getInt(0) == 1
                if (isComp) completed++
                val prio = it.getString(1)
                if (prio == Priority.URGENT.name && !isComp) urgent++
            }
        }
        val pending = total - completed
        val rate = if (total > 0) (completed.toFloat() / total.toFloat()) * 100f else 0f
        return TaskStats(total, completed, pending, urgent, rate)
    }

    private fun cursorToList(cursor: Cursor): List<TaskItem> {
        val list = mutableListOf<TaskItem>()
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToTask(it))
            }
        }
        return list
    }

    private fun cursorToTask(cursor: Cursor): TaskItem {
        val subtasksJson = cursor.getString(cursor.getColumnIndexOrThrow("subtasksJson"))
        val subtasks: List<SubTask> = if (!subtasksJson.isNullOrBlank()) {
            try {
                gson.fromJson(subtasksJson, subtaskType) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }

        val completedAt = if (cursor.isNull(cursor.getColumnIndexOrThrow("completedAtMillis"))) {
            null
        } else {
            cursor.getLong(cursor.getColumnIndexOrThrow("completedAtMillis"))
        }

        return TaskItem(
            id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
            title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
            description = cursor.getString(cursor.getColumnIndexOrThrow("description")) ?: "",
            date = cursor.getString(cursor.getColumnIndexOrThrow("date")),
            time = cursor.getString(cursor.getColumnIndexOrThrow("time")) ?: "",
            timeMillis = cursor.getLong(cursor.getColumnIndexOrThrow("timeMillis")),
            priority = Priority.fromString(cursor.getString(cursor.getColumnIndexOrThrow("priority"))),
            isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("isCompleted")) == 1,
            completedAtMillis = completedAt,
            hasAlarm = cursor.getInt(cursor.getColumnIndexOrThrow("hasAlarm")) == 1,
            isDailyAlarm = cursor.getInt(cursor.getColumnIndexOrThrow("isDailyAlarm")) == 1,
            category = cursor.getString(cursor.getColumnIndexOrThrow("category")) ?: "General",
            subtasks = subtasks,
            createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("createdAt"))
        )
    }
}
