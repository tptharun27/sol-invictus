package com.solinvictus.remember.data.model

enum class MemoryCategory(val label: String, val iconEmoji: String) {
    MILESTONE("Milestone", "🏆"),
    ANNIVERSARY("Anniversary", "💍"),
    BIRTHDAY("Birthday", "🎂"),
    MEMORABLE_DAY("Memorable Day", "🌟"),
    LIFE_LESSON("Life Lesson", "💡"),
    PROMISE("Promise", "🤝"),
    PERSONAL("Personal", "📖");

    companion object {
        fun fromString(value: String?): MemoryCategory {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: MILESTONE
        }
    }
}
