package com.solinvictus.remember.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.solinvictus.remember.data.db.DatabaseHelper
import com.solinvictus.remember.data.db.TaskDao
import com.solinvictus.remember.data.preferences.UserPreferences

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED || action == "android.intent.action.QUICKBOOT_POWERON") {
            Log.d("BootReceiver", "Device reboot detected. Rescheduling alarms...")
            val scheduler = AlarmScheduler(context)
            val prefs = UserPreferences(context)

            // 1. Reschedule master daily routine alarm if enabled
            if (prefs.isDailyAlarmEnabled()) {
                val hour = prefs.getDailyAlarmHour()
                val minute = prefs.getDailyAlarmMinute()
                scheduler.scheduleDailyRoutineAlarm(hour, minute)
            }

            // 2. Reschedule active task alarms
            try {
                val db = DatabaseHelper.getInstance(context)
                val activeTasks = TaskDao(db).getActiveAlarms()
                activeTasks.forEach { task ->
                    scheduler.scheduleTaskAlarm(task)
                }
                Log.d("BootReceiver", "Successfully rescheduled ${activeTasks.size} task alarms.")
            } catch (e: Exception) {
                Log.e("BootReceiver", "Failed to reschedule alarms on boot: ${e.message}")
            }
        }
    }
}
