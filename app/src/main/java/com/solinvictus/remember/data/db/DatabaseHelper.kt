package com.solinvictus.remember.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_TASKS (
                id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                description TEXT,
                date TEXT NOT NULL,
                time TEXT,
                timeMillis INTEGER NOT NULL,
                priority TEXT NOT NULL,
                isCompleted INTEGER NOT NULL,
                completedAtMillis INTEGER,
                hasAlarm INTEGER NOT NULL,
                isDailyAlarm INTEGER NOT NULL,
                category TEXT NOT NULL,
                subtasksJson TEXT,
                createdAt INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_MEMORIES (
                id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                story TEXT,
                date TEXT NOT NULL,
                category TEXT NOT NULL,
                mood TEXT NOT NULL,
                isPinned INTEGER NOT NULL,
                repeatYearly INTEGER NOT NULL,
                colorHex TEXT NOT NULL,
                createdAt INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL("CREATE INDEX idx_tasks_date ON $TABLE_TASKS(date)")
        db.execSQL("CREATE INDEX idx_tasks_alarm ON $TABLE_TASKS(hasAlarm, isCompleted)")
        db.execSQL("CREATE INDEX idx_memories_date ON $TABLE_MEMORIES(date)")
        db.execSQL("CREATE INDEX idx_memories_pinned ON $TABLE_MEMORIES(isPinned)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Future migrations if needed
    }

    companion object {
        const val DATABASE_NAME = "sol_invictus.db"
        const val DATABASE_VERSION = 1

        const val TABLE_TASKS = "tasks"
        const val TABLE_MEMORIES = "memories"

        @Volatile
        private var INSTANCE: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
