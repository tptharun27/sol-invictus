package com.solinvictus.remember.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.solinvictus.remember.MainActivity
import com.solinvictus.remember.R
import com.solinvictus.remember.SolInvictusApp
import com.solinvictus.remember.data.db.DatabaseHelper
import com.solinvictus.remember.data.db.TaskDao
import com.solinvictus.remember.data.preferences.UserPreferences

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val taskId = intent.getStringExtra(AlarmScheduler.EXTRA_TASK_ID) ?: return
        val taskTitle = intent.getStringExtra(AlarmScheduler.EXTRA_TASK_TITLE) ?: "Task Reminder"
        val taskDesc = intent.getStringExtra(AlarmScheduler.EXTRA_TASK_DESC) ?: ""
        val isDaily = intent.getBooleanExtra(AlarmScheduler.EXTRA_IS_DAILY, false)
        val timeMillis = intent.getLongExtra(AlarmScheduler.EXTRA_TIME_MILLIS, 0L)
        val notificationId = taskId.hashCode()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val prefs = UserPreferences(context)

        when (action) {
            AlarmScheduler.ACTION_ALARM_TRIGGER -> {
                // 1. Play vibration if enabled
                if (prefs.isVibrationEnabled()) {
                    triggerVibration(context)
                }

                // 2. Build high priority notification with actions
                val openAppIntent = PendingIntent.getActivity(
                    context,
                    notificationId,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // Action: Complete Task
                val completeIntent = Intent(context, AlarmReceiver::class.java).apply {
                    this.action = AlarmScheduler.ACTION_COMPLETE_TASK
                    putExtra(AlarmScheduler.EXTRA_TASK_ID, taskId)
                }
                val completePendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId + 1,
                    completeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // Action: Snooze (10 mins)
                val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
                    this.action = AlarmScheduler.ACTION_SNOOZE_ALARM
                    putExtra(AlarmScheduler.EXTRA_TASK_ID, taskId)
                    putExtra(AlarmScheduler.EXTRA_TASK_TITLE, taskTitle)
                    putExtra(AlarmScheduler.EXTRA_TASK_DESC, taskDesc)
                    putExtra(AlarmScheduler.EXTRA_IS_DAILY, isDaily)
                }
                val snoozePendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId + 2,
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // Action: Dismiss
                val dismissIntent = Intent(context, AlarmReceiver::class.java).apply {
                    this.action = AlarmScheduler.ACTION_DISMISS_ALARM
                    putExtra(AlarmScheduler.EXTRA_TASK_ID, taskId)
                }
                val dismissPendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId + 3,
                    dismissIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val alarmSound = if (prefs.isSoundEnabled()) {
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                        ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                } else null

                val builder = NotificationCompat.Builder(context, SolInvictusApp.CHANNEL_ID_ALARM)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("⏰ $taskTitle")
                    .setContentText(if (taskDesc.isNotBlank()) taskDesc else "Time for your scheduled task!")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setContentIntent(openAppIntent)
                    .addAction(android.R.drawable.checkbox_on_background, "Mark Done", completePendingIntent)
                    .addAction(android.R.drawable.ic_lock_idle_alarm, "Snooze 10m", snoozePendingIntent)
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Dismiss", dismissPendingIntent)

                if (alarmSound != null) {
                    builder.setSound(alarmSound)
                }

                notificationManager.notify(notificationId, builder.build())

                // 3. If daily repeating, schedule the next occurrence for tomorrow!
                if (isDaily) {
                    if (taskId == AlarmScheduler.DAILY_ROUTINE_TASK_ID) {
                        val hour = prefs.getDailyAlarmHour()
                        val minute = prefs.getDailyAlarmMinute()
                        AlarmScheduler(context).scheduleDailyRoutineAlarm(hour, minute)
                    } else if (timeMillis > 0L) {
                        val db = DatabaseHelper.getInstance(context)
                        val task = TaskDao(db).getById(taskId)
                        if (task != null && task.hasAlarm && !task.isCompleted) {
                            AlarmScheduler(context).scheduleTaskAlarm(task)
                        }
                    }
                }
            }

            AlarmScheduler.ACTION_COMPLETE_TASK -> {
                notificationManager.cancel(notificationId)
                if (taskId != AlarmScheduler.DAILY_ROUTINE_TASK_ID) {
                    val db = DatabaseHelper.getInstance(context)
                    TaskDao(db).toggleCompleted(taskId, true)
                }
            }

            AlarmScheduler.ACTION_SNOOZE_ALARM -> {
                notificationManager.cancel(notificationId)
                // Schedule alarm 10 minutes into the future
                val snoozeMillis = System.currentTimeMillis() + 10 * 60 * 1000L
                val intentSnoozed = Intent(context, AlarmReceiver::class.java).apply {
                    this.action = AlarmScheduler.ACTION_ALARM_TRIGGER
                    putExtra(AlarmScheduler.EXTRA_TASK_ID, taskId)
                    putExtra(AlarmScheduler.EXTRA_TASK_TITLE, taskTitle)
                    putExtra(AlarmScheduler.EXTRA_TASK_DESC, taskDesc)
                    putExtra(AlarmScheduler.EXTRA_IS_DAILY, isDaily)
                    putExtra(AlarmScheduler.EXTRA_TIME_MILLIS, timeMillis)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId,
                    intentSnoozed,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
                alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, snoozeMillis, pendingIntent)
            }

            AlarmScheduler.ACTION_DISMISS_ALARM -> {
                notificationManager.cancel(notificationId)
            }
        }
    }

    private fun triggerVibration(context: Context) {
        val pattern = longArrayOf(0, 500, 200, 500, 200, 800)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, -1)
            }
        }
    }
}
