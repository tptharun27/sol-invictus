package com.solinvictus.remember.data.db

import android.content.ContentValues
import android.database.Cursor
import com.solinvictus.remember.data.model.ForeverMemory
import com.solinvictus.remember.data.model.MemoryCategory

data class MemoryStats(
    val totalCount: Int,
    val pinnedCount: Int,
    val onThisDayCount: Int,
    val categoriesCount: Map<String, Int>
)

class MemoryDao(private val dbHelper: DatabaseHelper) {

    fun insert(memory: ForeverMemory): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id", memory.id)
            put("title", memory.title)
            put("story", memory.story)
            put("date", memory.date)
            put("category", memory.category.name)
            put("mood", memory.mood)
            put("isPinned", if (memory.isPinned) 1 else 0)
            put("repeatYearly", if (memory.repeatYearly) 1 else 0)
            put("colorHex", memory.colorHex)
            put("createdAt", memory.createdAt)
        }
        return db.insertWithOnConflict(DatabaseHelper.TABLE_MEMORIES, null, values, android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun update(memory: ForeverMemory): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", memory.title)
            put("story", memory.story)
            put("date", memory.date)
            put("category", memory.category.name)
            put("mood", memory.mood)
            put("isPinned", if (memory.isPinned) 1 else 0)
            put("repeatYearly", if (memory.repeatYearly) 1 else 0)
            put("colorHex", memory.colorHex)
        }
        return db.update(DatabaseHelper.TABLE_MEMORIES, values, "id = ?", arrayOf(memory.id))
    }

    fun togglePinned(id: String, isPinned: Boolean): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("isPinned", if (isPinned) 1 else 0)
        }
        return db.update(DatabaseHelper.TABLE_MEMORIES, values, "id = ?", arrayOf(id))
    }

    fun delete(id: String): Int {
        val db = dbHelper.writableDatabase
        return db.delete(DatabaseHelper.TABLE_MEMORIES, "id = ?", arrayOf(id))
    }

    fun getById(id: String): ForeverMemory? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_MEMORIES,
            null,
            "id = ?",
            arrayOf(id),
            null,
            null,
            null
        )
        return cursor.use {
            if (it.moveToFirst()) cursorToMemory(it) else null
        }
    }

    fun getAll(): List<ForeverMemory> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_MEMORIES,
            null,
            null,
            null,
            null,
            null,
            "isPinned DESC, date DESC, createdAt DESC"
        )
        return cursorToList(cursor)
    }

    fun getByDate(date: String): List<ForeverMemory> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_MEMORIES,
            null,
            "date = ?",
            arrayOf(date),
            null,
            null,
            "isPinned DESC, createdAt DESC"
        )
        return cursorToList(cursor)
    }

    fun getByMonth(yearMonth: String): List<ForeverMemory> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_MEMORIES,
            null,
            "date LIKE ?",
            arrayOf("$yearMonth%"),
            null,
            null,
            "date ASC"
        )
        return cursorToList(cursor)
    }

    /**
     * Finds memories that occurred on the same month and day in any year.
     * @param monthDay formatted as "-MM-DD" e.g. "-11-10"
     */
    fun getOnThisDay(monthDay: String): List<ForeverMemory> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_MEMORIES,
            null,
            "date LIKE ?",
            arrayOf("%$monthDay"),
            null,
            null,
            "date DESC"
        )
        return cursorToList(cursor)
    }

    fun search(query: String): List<ForeverMemory> {
        val db = dbHelper.readableDatabase
        val wildQuery = "%$query%"
        val cursor = db.query(
            DatabaseHelper.TABLE_MEMORIES,
            null,
            "title LIKE ? OR story LIKE ? OR category LIKE ?",
            arrayOf(wildQuery, wildQuery, wildQuery),
            null,
            null,
            "date DESC"
        )
        return cursorToList(cursor)
    }

    fun getStats(todayMonthDay: String): MemoryStats {
        val all = getAll()
        val pinned = all.count { it.isPinned }
        val onThisDay = all.count { it.date.endsWith(todayMonthDay) }
        val categoryCounts = all.groupingBy { it.category.label }.eachCount()
        return MemoryStats(all.size, pinned, onThisDay, categoryCounts)
    }

    private fun cursorToList(cursor: Cursor): List<ForeverMemory> {
        val list = mutableListOf<ForeverMemory>()
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToMemory(it))
            }
        }
        return list
    }

    private fun cursorToMemory(cursor: Cursor): ForeverMemory {
        return ForeverMemory(
            id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
            title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
            story = cursor.getString(cursor.getColumnIndexOrThrow("story")) ?: "",
            date = cursor.getString(cursor.getColumnIndexOrThrow("date")),
            category = MemoryCategory.fromString(cursor.getString(cursor.getColumnIndexOrThrow("category"))),
            mood = cursor.getString(cursor.getColumnIndexOrThrow("mood")) ?: "🌟",
            isPinned = cursor.getInt(cursor.getColumnIndexOrThrow("isPinned")) == 1,
            repeatYearly = cursor.getInt(cursor.getColumnIndexOrThrow("repeatYearly")) == 1,
            colorHex = cursor.getString(cursor.getColumnIndexOrThrow("colorHex")) ?: "#FFB800",
            createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("createdAt"))
        )
    }
}
