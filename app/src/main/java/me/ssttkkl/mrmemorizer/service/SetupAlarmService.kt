package me.ssttkkl.mrmemorizer.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import me.ssttkkl.mrmemorizer.AppPreferences
import me.ssttkkl.mrmemorizer.R
import java.time.LocalDate
import java.time.ZoneId

class SetupAlarmService : IntentService("NotifyService") {

    override fun onHandleIntent(intent: Intent?) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (AppPreferences.dailyNoticeEnabled) {
            val pIntent = PendingIntent.getBroadcast(
                this@SetupAlarmService,
                0,
                Intent(this, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val millis = AppPreferences.dailyNoticeTime
                .atDate(LocalDate.now())
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                millis,
                AlarmManager.INTERVAL_DAY,
                pIntent
            )
            Log.d(
                "SetupAlarmService",
                "alarm has been set up at everyday ${AppPreferences.dailyNoticeTime}"
            )
        } else {
            PendingIntent.getBroadcast(
                this@SetupAlarmService,
                0,
                Intent(this, AlarmReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE
            )?.let { alarmManager.cancel(it) }
            Log.d("SetupAlarmService", "alarm has been cancelled")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AppPreferences.NOTIFICATION_CHANNEL_ID,
                getString(R.string.review_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val mgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mgr.createNotificationChannel(channel)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    companion object {

        fun startSetupAlarm(context: Context) {
            val intent = Intent(context, SetupAlarmService::class.java)
            context.startService(intent)
        }
    }
}