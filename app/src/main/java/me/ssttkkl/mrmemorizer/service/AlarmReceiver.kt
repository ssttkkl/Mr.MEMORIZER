package me.ssttkkl.mrmemorizer.service

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ssttkkl.mrmemorizer.AppPreferences
import me.ssttkkl.mrmemorizer.MainActivity
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.AppDatabase
import java.time.LocalDate

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        GlobalScope.launch {
            try {
                val epochDay = LocalDate.now().toEpochDay()
                val count = AppDatabase.getInstance().noteDao
                    .countNotesNextReviewDateEqSync(epochDay)

                if (count > 0) {
                    val pIntent = PendingIntent.getActivity(
                        context,
                        0,
                        Intent(context, MainActivity::class.java),
                        PendingIntent.FLAG_ONE_SHOT
                    )
                    val noti =
                        NotificationCompat.Builder(context, AppPreferences.NOTIFICATION_CHANNEL_ID)
                            .setContentTitle(context.getString(R.string.review_notification_title))
                            .setContentText(
                                context.getString(
                                    R.string.review_notification_content,
                                    count
                                )
                            )
                            .setContentIntent(pIntent)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .build()
                    NotificationManagerCompat.from(context)
                        .notify(0, noti)
                }
            } catch (exc: Exception) {
                exc.printStackTrace()
            } finally {
                SetupAlarmService.startSetupAlarm(context)
            }
        }
    }
}