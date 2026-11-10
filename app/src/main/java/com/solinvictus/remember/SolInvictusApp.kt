package com.solinvictus.remember

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build

class SolInvictusApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            // High Priority Alarm Channel
            val alarmChannel = NotificationChannel(
                CHANNEL_ID_ALARM,
                "Sol Invictus Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical alarms and task reminders"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 800)
                setSound(alarmSound, audioAttributes)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }

            // Normal Reminders Channel
            val reminderChannel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                "Memory & Event Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily summaries and anniversary milestones"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(alarmChannel)
            notificationManager.createNotificationChannel(reminderChannel)
        }
    }

    companion object {
        const val CHANNEL_ID_ALARM = "sol_invictus_alarm_channel"
        const val CHANNEL_ID_REMINDERS = "sol_invictus_reminders_channel"

        lateinit var instance: SolInvictusApp
            private set
    }
}
