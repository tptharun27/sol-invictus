package com.solinvictus.remember

import com.solinvictus.remember.util.DateTimeUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DateTimeUtilsTest {

    @Test
    fun testDateFormatting() {
        val todayIso = DateTimeUtils.todayIso()
        assertNotNull(todayIso)
        assertTrue(todayIso.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))

        assertEquals("Today", DateTimeUtils.formatDisplayDate(todayIso))

        val tomorrowIso = LocalDate.now().plusDays(1).format(DateTimeUtils.ISO_DATE_FORMATTER)
        assertEquals("Tomorrow", DateTimeUtils.formatDisplayDate(tomorrowIso))

        val yesterdayIso = LocalDate.now().minusDays(1).format(DateTimeUtils.ISO_DATE_FORMATTER)
        assertEquals("Yesterday", DateTimeUtils.formatDisplayDate(yesterdayIso))
    }

    @Test
    fun testTimeFormatting() {
        val time12h = DateTimeUtils.formatTime(9, 30, is24Hour = false)
        assertEquals("09:30 AM", time12h)

        val time24h = DateTimeUtils.formatTime(14, 45, is24Hour = true)
        assertEquals("14:45", time24h)
    }

    @Test
    fun testEpochCalculation() {
        val today = DateTimeUtils.todayIso()
        val epochMillis = DateTimeUtils.calculateEpochMillis(today, 12, 0)
        assertTrue(epochMillis > 0L)
    }

    @Test
    fun testNextDailyAlarmCalculation() {
        val now = java.time.LocalTime.now()
        // Pick an hour clearly in the future or past
        val futureHour = (now.hour + 2) % 24
        val millis = DateTimeUtils.calculateNextDailyAlarmMillis(futureHour, 0)
        assertTrue(millis > System.currentTimeMillis())
    }

    @Test
    fun testGreeting() {
        val greeting = DateTimeUtils.getGreeting("Tharun")
        assertTrue(greeting.contains("Tharun"))
        assertTrue(
            greeting.startsWith("Good Morning") ||
            greeting.startsWith("Good Afternoon") ||
            greeting.startsWith("Good Evening") ||
            greeting.startsWith("Good Night")
        )
    }

    @Test
    fun testAnniversaryMatching() {
        val today = LocalDate.now()
        val pastAnniversaryIso = LocalDate.of(2020, today.monthValue, today.dayOfMonth)
            .format(DateTimeUtils.ISO_DATE_FORMATTER)

        assertTrue(DateTimeUtils.isAnniversaryToday(pastAnniversaryIso))

        val nonAnniversaryIso = LocalDate.of(2020, today.monthValue, if (today.dayOfMonth == 1) 2 else 1)
            .format(DateTimeUtils.ISO_DATE_FORMATTER)
        assertFalse(DateTimeUtils.isAnniversaryToday(nonAnniversaryIso))
    }
}
