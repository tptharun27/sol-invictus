package com.solinvictus.remember.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.solinvictus.remember.MainActivity
import com.solinvictus.remember.data.model.TaskItem
import com.solinvictus.remember.util.DateTimeUtils

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Schedules an alarm for a specific task.
     */
    fun scheduleTaskAlarm(task: TaskItem) {
        if (!task.hasAlarm || task.isCompleted) return

        val triggerAtMillis = if (task.isDailyAlarm) {
            // Extract hour and minute from timeMillis or task.time
            val cal = java.util.Calendar.getInstance().apply { timeInMillis = task.timeMillis }
            val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
            val minute = cal.get(java.util.Calendar.MINUTE)
            DateTimeUtils.calculateNextDailyAlarmMillis(hour, minute)
        } else {
            task.timeMillis
        }

        // Only schedule if in future or within 10 seconds
        if (triggerAtMillis < System.currentTimeMillis() - 10000L) {
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_TRIGGER
            putExtra(EXTRA_TASK_ID, task.id)
            putExtra(EXTRA_TASK_TITLE, task.title)
            putExtra(EXTRA_TASK_DESC, task.description)
            putExtra(EXTRA_IS_DAILY, task.isDailyAlarm)
            putExtra(EXTRA_TIME_MILLIS, task.timeMillis)
        }

        val requestCode = task.id.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // setAlarmClock gives highest priority and shows alarm icon in status bar
                val showIntent = PendingIntent.getActivity(
                    context,
                    requestCode,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, showIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
            Log.d("AlarmScheduler", "Alarm scheduled for task: ${task.title} at $triggerAtMillis")
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Exact alarm permission missing: ${e.message}")
        }
    }

    /**
     * Cancels an existing task alarm.
     */
    fun cancelTaskAlarm(taskId: String) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_TRIGGER
        }
        val requestCode = taskId.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d("AlarmScheduler", "Alarm cancelled for task: $taskId")
        }
    }

    /**
     * Schedules the master daily routine alarm.
     */
    fun scheduleDailyRoutineAlarm(hour: Int, minute: Int) {
        val triggerAtMillis = DateTimeUtils.calculateNextDailyAlarmMillis(hour, minute)
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_TRIGGER
            putExtra(EXTRA_TASK_ID, DAILY_ROUTINE_TASK_ID)
            putExtra(EXTRA_TASK_TITLE, "Daily Routine & Schedule Check")
            putExtra(EXTRA_TASK_DESC, "Review your tasks and milestones for today in Sol Invictus.")
            putExtra(EXTRA_IS_DAILY, true)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_ROUTINE_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val showIntent = PendingIntent.getActivity(
                    context,
                    DAILY_ROUTINE_REQUEST_CODE,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, showIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
            Log.d("AlarmScheduler", "Master daily alarm scheduled for $hour:$minute at $triggerAtMillis")
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Exact alarm permission missing: ${e.message}")
        }
    }

    /**
     * Cancels the master daily routine alarm.
     */
    fun cancelDailyRoutineAlarm() {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_TRIGGER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_ROUTINE_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    companion object {
        const val ACTION_ALARM_TRIGGER = "com.solinvictus.remember.ACTION_ALARM_TRIGGER"
        const val ACTION_COMPLETE_TASK = "com.solinvictus.remember.ACTION_COMPLETE_TASK"
        const val ACTION_SNOOZE_ALARM = "com.solinvictus.remember.ACTION_SNOOZE_ALARM"
        const val ACTION_DISMISS_ALARM = "com.solinvictus.remember.ACTION_DISMISS_ALARM"

        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_TASK_DESC = "extra_task_desc"
        const val EXTRA_IS_DAILY = "extra_is_daily"
        const val EXTRA_TIME_MILLIS = "extra_time_millis"

        const val DAILY_ROUTINE_TASK_ID = "sol_invictus_daily_routine"
        const val DAILY_ROUTINE_REQUEST_CODE = 999999
    }
}
