package com.solinvictus.remember.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {

    val ISO_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)
    val MONTH_YEAR_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)
    val DISPLAY_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.US)
    val SHORT_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.US)
    val TIME_12H_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
    val TIME_24H_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US)

    fun todayIso(): String = LocalDate.now().format(ISO_DATE_FORMATTER)

    fun currentMonthIso(): String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM", Locale.US))

    fun currentMonthDayIso(): String = LocalDate.now().format(DateTimeFormatter.ofPattern("-MM-dd", Locale.US))

    fun parseLocalDate(isoDate: String): LocalDate {
        return try {
            LocalDate.parse(isoDate, ISO_DATE_FORMATTER)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }

    fun formatDisplayDate(isoDate: String): String {
        return try {
            val date = parseLocalDate(isoDate)
            val today = LocalDate.now()
            when (date) {
                today -> "Today"
                today.plusDays(1) -> "Tomorrow"
                today.minusDays(1) -> "Yesterday"
                else -> date.format(DISPLAY_DATE_FORMATTER)
            }
        } catch (e: Exception) {
            isoDate
        }
    }

    fun formatTime(hour: Int, minute: Int, is24Hour: Boolean = false): String {
        val localTime = LocalTime.of(hour, minute)
        return if (is24Hour) {
            localTime.format(TIME_24H_FORMATTER)
        } else {
            localTime.format(TIME_12H_FORMATTER)
        }
    }

    /**
     * Calculates the exact epoch timestamp in millis for a given date ("YYYY-MM-DD") and time ("HH:mm" or "hh:mm a").
     */
    fun calculateEpochMillis(dateIso: String, hour: Int, minute: Int): Long {
        return try {
            val date = parseLocalDate(dateIso)
            val time = LocalTime.of(hour.coerceIn(0, 23), minute.coerceIn(0, 59))
            val ldt = LocalDateTime.of(date, time)
            ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    /**
     * For recurring daily alarm: calculate the next occurrence timestamp.
     * If the specified time today is in the future, return today's timestamp.
     * Otherwise, return tomorrow's timestamp.
     */
    fun calculateNextDailyAlarmMillis(hour: Int, minute: Int): Long {
        val now = LocalDateTime.now()
        var target = LocalDateTime.of(now.toLocalDate(), LocalTime.of(hour.coerceIn(0, 23), minute.coerceIn(0, 59)))
        if (!target.isAfter(now)) {
            target = target.plusDays(1)
        }
        return target.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    /**
     * Calculates time elapsed or time remaining relative to a memory date.
     */
    fun getMemoryTimeDescription(isoDate: String): String {
        return try {
            val date = parseLocalDate(isoDate)
            val today = LocalDate.now()
            val days = ChronoUnit.DAYS.between(date, today)

            when {
                days == 0L -> "Today! ✨"
                days == 1L -> "Yesterday"
                days in 2..30 -> "$days days ago"
                days in 31..364 -> "${days / 30} months ago"
                days >= 365 -> {
                    val years = days / 365
                    val remMonths = (days % 365) / 30
                    if (remMonths > 0) "$years yr $remMonths mo ago" else "$years years ago"
                }
                days == -1L -> "Tomorrow"
                days < -1L -> "In ${-days} days"
                else -> date.format(SHORT_DATE_FORMATTER)
            }
        } catch (e: Exception) {
            isoDate
        }
    }

    /**
     * Returns true if the memory occurred on today's month and day.
     */
    fun isAnniversaryToday(isoDate: String): Boolean {
        return try {
            val date = parseLocalDate(isoDate)
            val today = LocalDate.now()
            date.month == today.month && date.dayOfMonth == today.dayOfMonth
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Dynamic friendly greeting based on current local hour.
     */
    fun getGreeting(name: String): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..21 -> "Good Evening"
            else -> "Good Night"
        }
        return if (name.isNotBlank()) "$greeting, $name!" else "$greeting!"
    }
}
