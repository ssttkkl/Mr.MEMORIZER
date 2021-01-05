package me.ssttkkl.mrmemorizer.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.res.ReviewStage

class SetupAlarmService : IntentService("NotifyService") {

    override fun onHandleIntent(intent: Intent?) {
        GlobalScope.launch {
            try {
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val nextReviewNote = withContext(Dispatchers.IO) {
                    AppDatabase.getInstance().noteDao.getNoteNextReviewSync(
                        ReviewStage.nextReviewDuration.size
                    )
                }

                if (nextReviewNote == null) {
                    // cancel the existing alarm for there is no more note to review
                    val pIntent = PendingIntent.getBroadcast(
                        this@SetupAlarmService,
                        0,
                        Intent(this@SetupAlarmService, AlarmReceiver::class.java),
                        PendingIntent.FLAG_NO_CREATE
                    )
                    if (pIntent != null) {
                        alarmManager.cancel(pIntent)
                        Log.d("NotifyService", "the existing alarm has been cancelled")
                    }
                } else {
                    // setup a new alarm, the existing one will automatically be replaced
                    Log.d("NotifyService", "next alarm: $nextReviewNote")

                    val nextReviewTimestamp = nextReviewNote.nextNotifyTime.toEpochSecond()
                    val pIntent = PendingIntent.getBroadcast(
                        this@SetupAlarmService,
                        0,
                        Intent(this@SetupAlarmService, AlarmReceiver::class.java).apply {
                            putExtra(AlarmReceiver.KEY_NOTE_ID, nextReviewNote.noteId)
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        nextReviewTimestamp * 1000,
                        pIntent
                    )
                }
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
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

        private const val NOTIFICATION_CHANNEL_ID = "review_notification"

        fun startSetupAlarm(context: Context) {
            val intent = Intent(context, SetupAlarmService::class.java)
            context.startService(intent)
        }
    }
}