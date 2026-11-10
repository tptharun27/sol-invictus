package com.solinvictus.remember.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solinvictus.remember.data.model.ForeverMemory
import com.solinvictus.remember.data.model.TaskItem
import com.solinvictus.remember.ui.components.CalendarView
import com.solinvictus.remember.ui.components.LiveHeader
import com.solinvictus.remember.ui.components.MemoryCard
import com.solinvictus.remember.ui.components.TaskCard
import com.solinvictus.remember.util.DateTimeUtils
import com.solinvictus.remember.viewmodel.MainUiState
import java.time.LocalDate

@Composable
fun CalendarScreen(
    state: MainUiState,
    onDateSelected: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onJumpToToday: () -> Unit,
    onToggleTaskComplete: (taskId: String, isCompleted: Boolean) -> Unit,
    onToggleSubtask: (taskId: String, subtaskId: String, isDone: Boolean) -> Unit,
    onEditTask: (TaskItem) -> Unit,
    onDeleteTask: (String) -> Unit,
    onToggleMemoryPin: (memoryId: String, isPinned: Boolean) -> Unit,
    onEditMemory: (ForeverMemory) -> Unit,
    onDeleteMemory: (String) -> Unit,
    onAddNewTask: () -> Unit,
    onAddNewMemory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedDateDisplay = remember(state.selectedDate) {
        DateTimeUtils.formatDisplayDate(state.selectedDate.format(DateTimeUtils.ISO_DATE_FORMATTER))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            LiveHeader(
                userName = state.userName,
                is24Hour = state.is24Hour,
                dailyAlarmActive = state.dailyAlarmEnabled,
                dailyAlarmTimeText = DateTimeUtils.formatTime(state.dailyAlarmHour, state.dailyAlarmMinute, state.is24Hour)
            )
        }

        item {
            CalendarView(
                currentYearMonth = state.currentYearMonth,
                selectedDate = state.selectedDate,
                taskDates = state.monthTaskDates,
                allCompletedTaskDates = state.monthAllCompletedTaskDates,
                memoryDates = state.monthMemoryDates,
                onDateSelected = onDateSelected,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onJumpToToday = onJumpToToday,
                weekStartsOnMonday = state.weekStartsOnMonday
            )
        }

        // Agenda Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DAY AGENDA",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = selectedDateDisplay,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onAddNewTask,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Task", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    }

                    Button(
                        onClick = onAddNewMemory,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Memory", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondary)
                    }
                }
            }
        }

        // Tasks for selected date
        if (state.tasksForSelectedDate.isNotEmpty()) {
            item {
                Text(
                    text = "Tasks (${state.tasksForSelectedDate.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(state.tasksForSelectedDate, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onToggleComplete = { onToggleTaskComplete(task.id, it) },
                    onEdit = { onEditTask(task) },
                    onDelete = { onDeleteTask(task.id) },
                    onSubtaskToggle = { subtaskId, isDone ->
                        onToggleSubtask(task.id, subtaskId, isDone)
                    }
                )
            }
        }

        // Memories for selected date
        if (state.memoriesForSelectedDate.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Forever Memories (${state.memoriesForSelectedDate.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(state.memoriesForSelectedDate, key = { it.id }) { memory ->
                MemoryCard(
                    memory = memory,
                    onTogglePin = { onToggleMemoryPin(memory.id, it) },
                    onEdit = { onEditMemory(memory) },
                    onDelete = { onDeleteMemory(memory.id) }
                )
            }
        }

        // Empty state when nothing on this date
        if (state.tasksForSelectedDate.isEmpty() && state.memoriesForSelectedDate.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No activities on this date",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap '+ Task' to schedule an alarm, or '+ Memory' to save a moment forever.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
