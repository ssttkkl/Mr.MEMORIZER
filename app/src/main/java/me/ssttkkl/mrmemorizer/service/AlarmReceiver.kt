package me.ssttkkl.mrmemorizer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ssttkkl.mrmemorizer.AppPreferences
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

                NotificationCompat.Builder(context, AppPreferences.NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(context.getString(R.string.review_notification_title))
                    .setContentText(context.getString(R.string.review_notification_content, count))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()
                    .let { NotificationManagerCompat.from(context).notify(0, it) }
            } catch (exc: Exception) {
                exc.printStackTrace()
            } finally {
                SetupAlarmService.startSetupAlarm(context)
            }
        }
    }
}