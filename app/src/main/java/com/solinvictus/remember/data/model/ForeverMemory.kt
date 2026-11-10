package com.solinvictus.remember.data.model

data class ForeverMemory(
    val id: String,
    val title: String,
    val story: String = "",
    val date: String, // ISO YYYY-MM-DD
    val category: MemoryCategory = MemoryCategory.MILESTONE,
    val mood: String = "🌟",
    val isPinned: Boolean = false,
    val repeatYearly: Boolean = true,
    val colorHex: String = "#FFB800",
    val createdAt: Long = System.currentTimeMillis()
)
