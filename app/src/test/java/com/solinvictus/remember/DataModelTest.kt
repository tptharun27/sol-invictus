package com.solinvictus.remember

import com.google.gson.Gson
import com.solinvictus.remember.data.model.BackupPayload
import com.solinvictus.remember.data.model.ForeverMemory
import com.solinvictus.remember.data.model.MemoryCategory
import com.solinvictus.remember.data.model.Priority
import com.solinvictus.remember.data.model.SubTask
import com.solinvictus.remember.data.model.TaskItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DataModelTest {

    @Test
    fun testPriorityEnum() {
        assertEquals(Priority.URGENT, Priority.fromString("URGENT"))
        assertEquals(Priority.LOW, Priority.fromString("low"))
        assertEquals(Priority.MEDIUM, Priority.fromString("unknown_value"))
        assertTrue(Priority.URGENT.level > Priority.HIGH.level)
        assertTrue(Priority.HIGH.level > Priority.MEDIUM.level)
        assertTrue(Priority.MEDIUM.level > Priority.LOW.level)
    }

    @Test
    fun testMemoryCategoryEnum() {
        assertEquals(MemoryCategory.MILESTONE, MemoryCategory.fromString("MILESTONE"))
        assertEquals(MemoryCategory.ANNIVERSARY, MemoryCategory.fromString("anniversary"))
        assertEquals("🏆", MemoryCategory.MILESTONE.iconEmoji)
        assertEquals("💍", MemoryCategory.ANNIVERSARY.iconEmoji)
    }

    @Test
    fun testTaskItemSerialization() {
        val subtask = SubTask(id = "sub-1", title = "Write unit tests", isDone = true)
        val task = TaskItem(
            id = "task-1",
            title = "Launch Sol Invictus",
            description = "Complete testing and compile APK",
            date = "2026-11-10",
            time = "04:00 PM",
            timeMillis = 1794306600000L,
            priority = Priority.URGENT,
            isCompleted = false,
            hasAlarm = true,
            isDailyAlarm = true,
            category = "Work",
            subtasks = listOf(subtask)
        )

        val gson = Gson()
        val json = gson.toJson(task)
        val restored = gson.fromJson(json, TaskItem::class.java)

        assertEquals(task.id, restored.id)
        assertEquals(task.title, restored.title)
        assertEquals(Priority.URGENT, restored.priority)
        assertTrue(restored.hasAlarm)
        assertTrue(restored.isDailyAlarm)
        assertEquals(1, restored.subtasks.size)
        assertTrue(restored.subtasks[0].isDone)
    }

    @Test
    fun testBackupPayloadSerialization() {
        val memory = ForeverMemory(
            id = "mem-1",
            title = "First Line of Sol Invictus",
            story = "Started building the personalized mobile app.",
            date = "2026-11-10",
            category = MemoryCategory.MILESTONE,
            mood = "🌟",
            isPinned = true,
            repeatYearly = true
        )
        val task = TaskItem(
            id = "task-1",
            title = "Verify backup",
            date = "2026-11-10"
        )
        val payload = BackupPayload(
            tasks = listOf(task),
            memories = listOf(memory)
        )

        val gson = Gson()
        val json = gson.toJson(payload)
        val restored = gson.fromJson(json, BackupPayload::class.java)

        assertNotNull(restored)
        assertEquals(1, restored.tasks.size)
        assertEquals(1, restored.memories.size)
        assertEquals("First Line of Sol Invictus", restored.memories[0].title)
        assertTrue(restored.memories[0].isPinned)
    }
}
