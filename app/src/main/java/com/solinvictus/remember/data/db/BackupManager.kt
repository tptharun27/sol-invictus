package com.solinvictus.remember.data.db

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.solinvictus.remember.data.model.BackupPayload

data class ImportResult(
    val success: Boolean,
    val tasksImported: Int,
    val memoriesImported: Int,
    val message: String
)

class BackupManager(
    private val taskDao: TaskDao,
    private val memoryDao: MemoryDao
) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    fun exportToJson(): String {
        val tasks = taskDao.getAll()
        val memories = memoryDao.getAll()
        val payload = BackupPayload(
            tasks = tasks,
            memories = memories
        )
        return gson.toJson(payload)
    }

    fun importFromJson(jsonString: String): ImportResult {
        return try {
            val payload = gson.fromJson(jsonString, BackupPayload::class.java)
                ?: return ImportResult(false, 0, 0, "Invalid JSON data.")

            var tasksCount = 0
            var memoriesCount = 0

            payload.tasks.forEach { task ->
                taskDao.insert(task)
                tasksCount++
            }

            payload.memories.forEach { memory ->
                memoryDao.insert(memory)
                memoriesCount++
            }

            ImportResult(
                success = true,
                tasksImported = tasksCount,
                memoriesImported = memoriesCount,
                message = "Successfully restored $tasksCount tasks and $memoriesCount forever memories!"
            )
        } catch (e: Exception) {
            ImportResult(false, 0, 0, "Import failed: ${e.localizedMessage}")
        }
    }
}
