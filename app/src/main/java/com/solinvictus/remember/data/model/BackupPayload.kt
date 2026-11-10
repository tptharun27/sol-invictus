package com.solinvictus.remember.data.model

data class BackupPayload(
    val exportVersion: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val app: String = "Sol Invictus",
    val tasks: List<TaskItem>,
    val memories: List<ForeverMemory>
)
