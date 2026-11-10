package com.solinvictus.remember.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.solinvictus.remember.alarm.AlarmScheduler
import com.solinvictus.remember.data.db.BackupManager
import com.solinvictus.remember.data.db.DatabaseHelper
import com.solinvictus.remember.data.db.ImportResult
import com.solinvictus.remember.data.db.MemoryDao
import com.solinvictus.remember.data.db.MemoryStats
import com.solinvictus.remember.data.db.TaskDao
import com.solinvictus.remember.data.db.TaskStats
import com.solinvictus.remember.data.model.AccentTheme
import com.solinvictus.remember.data.model.ForeverMemory
import com.solinvictus.remember.data.model.Priority
import com.solinvictus.remember.data.model.SubTask
import com.solinvictus.remember.data.model.TaskItem
import com.solinvictus.remember.data.model.ThemeMode
import com.solinvictus.remember.data.preferences.UserPreferences
import com.solinvictus.remember.util.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

enum class NavTab {
    CALENDAR,
    TASKS,
    MEMORIES,
    STATS,
    SETTINGS
}

enum class TaskFilter {
    ALL,
    TODAY,
    UPCOMING,
    COMPLETED,
    URGENT
}

data class MainUiState(
    val currentTab: NavTab = NavTab.CALENDAR,
    val selectedDate: LocalDate = LocalDate.now(),
    val currentYearMonth: YearMonth = YearMonth.now(),
    val taskFilter: TaskFilter = TaskFilter.ALL,
    val tasksForSelectedDate: List<TaskItem> = emptyList(),
    val memoriesForSelectedDate: List<ForeverMemory> = emptyList(),
    val allTasks: List<TaskItem> = emptyList(),
    val allMemories: List<ForeverMemory> = emptyList(),
    val onThisDayMemories: List<ForeverMemory> = emptyList(),
    val monthTaskDates: Set<String> = emptySet(),
    val monthAllCompletedTaskDates: Set<String> = emptySet(),
    val monthMemoryDates: Set<String> = emptySet(),
    val taskStats: TaskStats = TaskStats(0, 0, 0, 0, 0f),
    val memoryStats: MemoryStats = MemoryStats(0, 0, 0, emptyMap()),
    val searchQuery: String = "",
    val searchTasks: List<TaskItem> = emptyList(),
    val searchMemories: List<ForeverMemory> = emptyList(),
    val themeMode: ThemeMode = ThemeMode.DARK_AMOLED,
    val accentTheme: AccentTheme = AccentTheme.SOLAR_GOLD,
    val is24Hour: Boolean = false,
    val weekStartsOnMonday: Boolean = false,
    val userName: String = "Tharun",
    val dailyAlarmEnabled: Boolean = false,
    val dailyAlarmHour: Int = 8,
    val dailyAlarmMinute: Int = 0,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val toastMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper.getInstance(application)
    private val taskDao = TaskDao(dbHelper)
    private val memoryDao = MemoryDao(dbHelper)
    private val prefs = UserPreferences(application)
    private val alarmScheduler = AlarmScheduler(application)
    private val backupManager = BackupManager(taskDao, memoryDao)

    private val _uiState = MutableStateFlow(
        MainUiState(
            themeMode = prefs.getThemeMode(),
            accentTheme = prefs.getAccentTheme(),
            is24Hour = prefs.is24Hour(),
            weekStartsOnMonday = prefs.weekStartsOnMonday(),
            userName = prefs.getUserName(),
            dailyAlarmEnabled = prefs.isDailyAlarmEnabled(),
            dailyAlarmHour = prefs.getDailyAlarmHour(),
            dailyAlarmMinute = prefs.getDailyAlarmMinute(),
            soundEnabled = prefs.isSoundEnabled(),
            vibrationEnabled = prefs.isVibrationEnabled()
        )
    )
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun selectTab(tab: NavTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun selectDate(date: LocalDate) {
        _uiState.update {
            it.copy(
                selectedDate = date,
                currentYearMonth = YearMonth.of(date.year, date.month)
            )
        }
        loadSelectedDateData()
    }

    fun jumpToToday() {
        val today = LocalDate.now()
        selectDate(today)
    }

    fun nextMonth() {
        _uiState.update { it.copy(currentYearMonth = it.currentYearMonth.plusMonths(1)) }
        loadMonthIndicatorData()
    }

    fun previousMonth() {
        _uiState.update { it.copy(currentYearMonth = it.currentYearMonth.minusMonths(1)) }
        loadMonthIndicatorData()
    }

    fun setTaskFilter(filter: TaskFilter) {
        _uiState.update { it.copy(taskFilter = filter) }
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val allT = taskDao.getAll()
            val allM = memoryDao.getAll()
            val tStats = taskDao.getStats()
            val todayMonthDay = DateTimeUtils.currentMonthDayIso()
            val mStats = memoryDao.getStats(todayMonthDay)
            val onThisDay = memoryDao.getOnThisDay(todayMonthDay)

            _uiState.update {
                it.copy(
                    allTasks = allT,
                    allMemories = allM,
                    taskStats = tStats,
                    memoryStats = mStats,
                    onThisDayMemories = onThisDay
                )
            }
            loadSelectedDateData()
            loadMonthIndicatorData()
        }
    }

    private fun loadSelectedDateData() {
        viewModelScope.launch(Dispatchers.IO) {
            val dateIso = _uiState.value.selectedDate.format(DateTimeUtils.ISO_DATE_FORMATTER)
            val tasks = taskDao.getByDate(dateIso)
            val memories = memoryDao.getByDate(dateIso)

            _uiState.update {
                it.copy(
                    tasksForSelectedDate = tasks,
                    memoriesForSelectedDate = memories
                )
            }
        }
    }

    private fun loadMonthIndicatorData() {
        viewModelScope.launch(Dispatchers.IO) {
            val yearMonthIso = _uiState.value.currentYearMonth.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"))
            val monthTasks = taskDao.getByMonth(yearMonthIso)
            val monthMemories = memoryDao.getByMonth(yearMonthIso)

            val taskDates = monthTasks.map { it.date }.toSet()
            val completedDates = monthTasks
                .groupBy { it.date }
                .filter { entry -> entry.value.all { it.isCompleted } }
                .keys

            val memoryDates = monthMemories.map { it.date }.toSet()

            _uiState.update {
                it.copy(
                    monthTaskDates = taskDates,
                    monthAllCompletedTaskDates = completedDates,
                    monthMemoryDates = memoryDates
                )
            }
        }
    }

    // Task CRUD
    fun saveTask(task: TaskItem) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.insert(task)
            if (task.hasAlarm && !task.isCompleted) {
                alarmScheduler.scheduleTaskAlarm(task)
            } else {
                alarmScheduler.cancelTaskAlarm(task.id)
            }
            loadData()
        }
    }

    fun toggleTaskComplete(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.toggleCompleted(taskId, isCompleted)
            if (isCompleted) {
                alarmScheduler.cancelTaskAlarm(taskId)
            } else {
                val task = taskDao.getById(taskId)
                if (task != null && task.hasAlarm) {
                    alarmScheduler.scheduleTaskAlarm(task)
                }
            }
            loadData()
        }
    }

    fun toggleSubtask(taskId: String, subtaskId: String, isDone: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val task = taskDao.getById(taskId) ?: return@launch
            val updatedSubtasks = task.subtasks.map {
                if (it.id == subtaskId) it.copy(isDone = isDone) else it
            }
            taskDao.update(task.copy(subtasks = updatedSubtasks))
            loadData()
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmScheduler.cancelTaskAlarm(taskId)
            taskDao.delete(taskId)
            loadData()
        }
    }

    // Memory CRUD
    fun saveMemory(memory: ForeverMemory) {
        viewModelScope.launch(Dispatchers.IO) {
            memoryDao.insert(memory)
            loadData()
        }
    }

    fun toggleMemoryPin(memoryId: String, isPinned: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            memoryDao.togglePinned(memoryId, isPinned)
            loadData()
        }
    }

    fun deleteMemory(memoryId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            memoryDao.delete(memoryId)
            loadData()
        }
    }

    // Search
    fun search(query: String) {
        val q = query.trim()
        _uiState.update { it.copy(searchQuery = q) }
        viewModelScope.launch(Dispatchers.IO) {
            if (q.isBlank()) {
                _uiState.update { it.copy(searchTasks = emptyList(), searchMemories = emptyList()) }
            } else {
                val t = taskDao.search(q)
                val m = memoryDao.search(q)
                _uiState.update { it.copy(searchTasks = t, searchMemories = m) }
            }
        }
    }

    // Settings
    fun setThemeMode(mode: ThemeMode) {
        prefs.setThemeMode(mode)
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun setAccentTheme(theme: AccentTheme) {
        prefs.setAccentTheme(theme)
        _uiState.update { it.copy(accentTheme = theme) }
    }

    fun set24Hour(value: Boolean) {
        prefs.set24Hour(value)
        _uiState.update { it.copy(is24Hour = value) }
    }

    fun setWeekStartsOnMonday(value: Boolean) {
        prefs.setWeekStartsOnMonday(value)
        _uiState.update { it.copy(weekStartsOnMonday = value) }
    }

    fun setSoundEnabled(value: Boolean) {
        prefs.setSoundEnabled(value)
        _uiState.update { it.copy(soundEnabled = value) }
    }

    fun setVibrationEnabled(value: Boolean) {
        prefs.setVibrationEnabled(value)
        _uiState.update { it.copy(vibrationEnabled = value) }
    }

    fun setDailyAlarmEnabled(value: Boolean) {
        prefs.setDailyAlarmEnabled(value)
        _uiState.update { it.copy(dailyAlarmEnabled = value) }
        if (value) {
            alarmScheduler.scheduleDailyRoutineAlarm(_uiState.value.dailyAlarmHour, _uiState.value.dailyAlarmMinute)
        } else {
            alarmScheduler.cancelDailyRoutineAlarm()
        }
    }

    fun setDailyAlarmTime(hour: Int, minute: Int) {
        prefs.setDailyAlarmTime(hour, minute)
        _uiState.update { it.copy(dailyAlarmHour = hour, dailyAlarmMinute = minute) }
        if (_uiState.value.dailyAlarmEnabled) {
            alarmScheduler.scheduleDailyRoutineAlarm(hour, minute)
        }
    }

    fun setUserName(name: String) {
        prefs.setUserName(name)
        _uiState.update { it.copy(userName = name) }
    }

    // Backup & Restore
    fun exportBackup(): String {
        return backupManager.exportToJson()
    }

    fun importBackup(jsonString: String, onResult: (ImportResult) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = backupManager.importFromJson(jsonString)
            if (result.success) {
                loadData()
            }
            onResult(result)
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
