package com.solinvictus.remember.data.model

data class TaskItem(
    val id: String,
    val title: String,
    val description: String = "",
    val date: String, // ISO YYYY-MM-DD
    val time: String = "", // Display string e.g. "09:30 AM"
    val timeMillis: Long = 0L, // Epoch millis for sorting and alarms
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val completedAtMillis: Long? = null,
    val hasAlarm: Boolean = false,
    val isDailyAlarm: Boolean = false,
    val category: String = "General",
    val subtasks: List<SubTask> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
