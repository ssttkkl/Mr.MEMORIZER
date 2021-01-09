package me.ssttkkl.mrmemorizer.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import me.ssttkkl.mrmemorizer.AppPreferences
import me.ssttkkl.mrmemorizer.R
import java.time.OffsetDateTime

class SetupAlarmService : IntentService("NotifyService") {

    override fun onHandleIntent(intent: Intent?) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pIntent = PendingIntent.getBroadcast(
            this@SetupAlarmService,
            0,
            Intent(this, AlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            OffsetDateTime.now().plusMinutes(1).toInstant().toEpochMilli(),
            AlarmManager.INTERVAL_DAY,
            pIntent
        )
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