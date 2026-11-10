package com.solinvictus.remember

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.solinvictus.remember.data.model.ForeverMemory
import com.solinvictus.remember.data.model.TaskItem
import com.solinvictus.remember.ui.dialogs.MemoryEditDialog
import com.solinvictus.remember.ui.dialogs.TaskEditDialog
import com.solinvictus.remember.ui.screens.CalendarScreen
import com.solinvictus.remember.ui.screens.MemoriesScreen
import com.solinvictus.remember.ui.screens.SettingsScreen
import com.solinvictus.remember.ui.screens.StatsScreen
import com.solinvictus.remember.ui.screens.TasksScreen
import com.solinvictus.remember.ui.theme.SolInvictusTheme
import com.solinvictus.remember.util.DateTimeUtils
import com.solinvictus.remember.viewmodel.MainViewModel
import com.solinvictus.remember.viewmodel.NavTab

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContent {
            val state by viewModel.uiState.collectAsState()

            // Request Notification permission for Android 13+
            val context = LocalContext.current
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                // Handled
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            SolInvictusTheme(
                themeMode = state.themeMode,
                accentTheme = state.accentTheme
            ) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsState()

    var showTaskDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskItem?>(null) }

    var showMemoryDialog by remember { mutableStateOf(false) }
    var editingMemory by remember { mutableStateOf<ForeverMemory?>(null) }

    var isFabExpanded by remember { mutableStateOf(false) }

    val selectedDateIso = remember(state.selectedDate) {
        state.selectedDate.format(DateTimeUtils.ISO_DATE_FORMATTER)
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavBarItem(
                    label = "Calendar",
                    icon = Icons.Default.CalendarMonth,
                    isSelected = state.currentTab == NavTab.CALENDAR,
                    onClick = { viewModel.selectTab(NavTab.CALENDAR) }
                )
                NavBarItem(
                    label = "Tasks",
                    icon = Icons.Default.CheckCircle,
                    isSelected = state.currentTab == NavTab.TASKS,
                    onClick = { viewModel.selectTab(NavTab.TASKS) }
                )
                NavBarItem(
                    label = "Memories",
                    icon = Icons.Default.AutoAwesome,
                    isSelected = state.currentTab == NavTab.MEMORIES,
                    onClick = { viewModel.selectTab(NavTab.MEMORIES) }
                )
                NavBarItem(
                    label = "Stats",
                    icon = Icons.Default.Insights,
                    isSelected = state.currentTab == NavTab.STATS,
                    onClick = { viewModel.selectTab(NavTab.STATS) }
                )
                NavBarItem(
                    label = "Settings",
                    icon = Icons.Default.Settings,
                    isSelected = state.currentTab == NavTab.SETTINGS,
                    onClick = { viewModel.selectTab(NavTab.SETTINGS) }
                )
            }
        },
        floatingActionButton = {
            if (state.currentTab != NavTab.SETTINGS && state.currentTab != NavTab.STATS) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AnimatedVisibility(
                        visible = isFabExpanded,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Memory Option
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable {
                                        isFabExpanded = false
                                        editingMemory = null
                                        showMemoryDialog = true
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Remember Forever",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "New Memory",
                                        tint = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Task Option
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable {
                                        isFabExpanded = false
                                        editingTask = null
                                        showTaskDialog = true
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Task & Alarm",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Alarm,
                                        contentDescription = "New Task",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    FloatingActionButton(
                        onClick = { isFabExpanded = !isFabExpanded },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = if (isFabExpanded) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = "Quick Add"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (state.currentTab) {
                NavTab.CALENDAR -> {
                    CalendarScreen(
                        state = state,
                        onDateSelected = { viewModel.selectDate(it) },
                        onPreviousMonth = { viewModel.previousMonth() },
                        onNextMonth = { viewModel.nextMonth() },
                        onJumpToToday = { viewModel.jumpToToday() },
                        onToggleTaskComplete = { id, comp -> viewModel.toggleTaskComplete(id, comp) },
                        onToggleSubtask = { tid, stid, done -> viewModel.toggleSubtask(tid, stid, done) },
                        onEditTask = {
                            editingTask = it
                            showTaskDialog = true
                        },
                        onDeleteTask = { viewModel.deleteTask(it) },
                        onToggleMemoryPin = { id, pinned -> viewModel.toggleMemoryPin(id, pinned) },
                        onEditMemory = {
                            editingMemory = it
                            showMemoryDialog = true
                        },
                        onDeleteMemory = { viewModel.deleteMemory(it) },
                        onAddNewTask = {
                            editingTask = null
                            showTaskDialog = true
                        },
                        onAddNewMemory = {
                            editingMemory = null
                            showMemoryDialog = true
                        }
                    )
                }

                NavTab.TASKS -> {
                    TasksScreen(
                        state = state,
                        onFilterChange = { viewModel.setTaskFilter(it) },
                        onToggleTaskComplete = { id, comp -> viewModel.toggleTaskComplete(id, comp) },
                        onToggleSubtask = { tid, stid, done -> viewModel.toggleSubtask(tid, stid, done) },
                        onEditTask = {
                            editingTask = it
                            showTaskDialog = true
                        },
                        onDeleteTask = { viewModel.deleteTask(it) },
                        onAddNewTask = {
                            editingTask = null
                            showTaskDialog = true
                        }
                    )
                }

                NavTab.MEMORIES -> {
                    MemoriesScreen(
                        state = state,
                        onToggleMemoryPin = { id, pinned -> viewModel.toggleMemoryPin(id, pinned) },
                        onEditMemory = {
                            editingMemory = it
                            showMemoryDialog = true
                        },
                        onDeleteMemory = { viewModel.deleteMemory(it) },
                        onAddNewMemory = {
                            editingMemory = null
                            showMemoryDialog = true
                        }
                    )
                }

                NavTab.STATS -> {
                    StatsScreen(state = state)
                }

                NavTab.SETTINGS -> {
                    SettingsScreen(
                        state = state,
                        onSetThemeMode = { viewModel.setThemeMode(it) },
                        onSetAccentTheme = { viewModel.setAccentTheme(it) },
                        onSet24Hour = { viewModel.set24Hour(it) },
                        onSetWeekStartsOnMonday = { viewModel.setWeekStartsOnMonday(it) },
                        onSetSoundEnabled = { viewModel.setSoundEnabled(it) },
                        onSetVibrationEnabled = { viewModel.setVibrationEnabled(it) },
                        onSetDailyAlarmEnabled = { viewModel.setDailyAlarmEnabled(it) },
                        onSetDailyAlarmTime = { h, m -> viewModel.setDailyAlarmTime(h, m) },
                        onSetUserName = { viewModel.setUserName(it) },
                        onExportBackup = { viewModel.exportBackup() },
                        onImportBackup = { json, cb -> viewModel.importBackup(json, cb) }
                    )
                }
            }
        }
    }

    // Task Dialog
    if (showTaskDialog) {
        TaskEditDialog(
            initialDate = selectedDateIso,
            initialTask = editingTask,
            is24Hour = state.is24Hour,
            onDismiss = { showTaskDialog = false },
            onSave = {
                viewModel.saveTask(it)
                showTaskDialog = false
            }
        )
    }

    // Memory Dialog
    if (showMemoryDialog) {
        MemoryEditDialog(
            initialDate = selectedDateIso,
            initialMemory = editingMemory,
            onDismiss = { showMemoryDialog = false },
            onSave = {
                viewModel.saveMemory(it)
                showMemoryDialog = false
            }
        )
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.NavBarItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp)
            )
        },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
