package com.solinvictus.remember.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.solinvictus.remember.data.model.AccentTheme
import com.solinvictus.remember.data.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _themeModeFlow = MutableStateFlow(getThemeMode())
    val themeModeFlow: StateFlow<ThemeMode> = _themeModeFlow.asStateFlow()

    private val _accentThemeFlow = MutableStateFlow(getAccentTheme())
    val accentThemeFlow: StateFlow<AccentTheme> = _accentThemeFlow.asStateFlow()

    private val _is24HourFlow = MutableStateFlow(is24Hour())
    val is24HourFlow: StateFlow<Boolean> = _is24HourFlow.asStateFlow()

    fun getThemeMode(): ThemeMode {
        val name = prefs.getString(KEY_THEME_MODE, ThemeMode.DARK_AMOLED.name)
        return try {
            ThemeMode.valueOf(name ?: ThemeMode.DARK_AMOLED.name)
        } catch (e: Exception) {
            ThemeMode.DARK_AMOLED
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeModeFlow.value = mode
    }

    fun getAccentTheme(): AccentTheme {
        val name = prefs.getString(KEY_ACCENT_THEME, AccentTheme.SOLAR_GOLD.name)
        return try {
            AccentTheme.valueOf(name ?: AccentTheme.SOLAR_GOLD.name)
        } catch (e: Exception) {
            AccentTheme.SOLAR_GOLD
        }
    }

    fun setAccentTheme(theme: AccentTheme) {
        prefs.edit().putString(KEY_ACCENT_THEME, theme.name).apply()
        _accentThemeFlow.value = theme
    }

    fun is24Hour(): Boolean = prefs.getBoolean(KEY_IS_24_HOUR, false)
    fun set24Hour(value: Boolean) {
        prefs.edit().putBoolean(KEY_IS_24_HOUR, value).apply()
        _is24HourFlow.value = value
    }

    fun weekStartsOnMonday(): Boolean = prefs.getBoolean(KEY_WEEK_STARTS_MONDAY, false)
    fun setWeekStartsOnMonday(value: Boolean) = prefs.edit().putBoolean(KEY_WEEK_STARTS_MONDAY, value).apply()

    fun isSoundEnabled(): Boolean = prefs.getBoolean(KEY_SOUND_ENABLED, true)
    fun setSoundEnabled(value: Boolean) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    fun isVibrationEnabled(): Boolean = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
    fun setVibrationEnabled(value: Boolean) = prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()

    fun isDailyAlarmEnabled(): Boolean = prefs.getBoolean(KEY_DAILY_ALARM_ENABLED, false)
    fun setDailyAlarmEnabled(value: Boolean) = prefs.edit().putBoolean(KEY_DAILY_ALARM_ENABLED, value).apply()

    fun getDailyAlarmHour(): Int = prefs.getInt(KEY_DAILY_ALARM_HOUR, 8)
    fun getDailyAlarmMinute(): Int = prefs.getInt(KEY_DAILY_ALARM_MINUTE, 0)
    fun setDailyAlarmTime(hour: Int, minute: Int) {
        prefs.edit()
            .putInt(KEY_DAILY_ALARM_HOUR, hour)
            .putInt(KEY_DAILY_ALARM_MINUTE, minute)
            .apply()
    }

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "Tharun") ?: "Tharun"
    fun setUserName(name: String) = prefs.edit().putString(KEY_USER_NAME, name).apply()

    companion object {
        private const val PREFS_NAME = "sol_invictus_prefs"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_ACCENT_THEME = "accent_theme"
        private const val KEY_IS_24_HOUR = "is_24_hour"
        private const val KEY_WEEK_STARTS_MONDAY = "week_starts_monday"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_DAILY_ALARM_ENABLED = "daily_alarm_enabled"
        private const val KEY_DAILY_ALARM_HOUR = "daily_alarm_hour"
        private const val KEY_DAILY_ALARM_MINUTE = "daily_alarm_minute"
        private const val KEY_USER_NAME = "user_name"
    }
}
