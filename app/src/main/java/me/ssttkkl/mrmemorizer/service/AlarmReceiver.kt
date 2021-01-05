package me.ssttkkl.mrmemorizer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.AppDatabase

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        GlobalScope.launch {
            try {
                val noteId = intent.getIntExtra(KEY_NOTE_ID, 0)
                if (noteId == 0)
                    error("illegal noteId: 0")

                val note = withContext(Dispatchers.IO) {
                    AppDatabase.getInstance().noteDao.getNoteByIdSync(noteId)
                } ?: error("no such note: $noteId")

                Log.d("NotifyService", "alarm for review note: $note")

                NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(context.getString(R.string.review_notification_title))
                    .setContentText(
                        context.getString(
                            R.string.review_notification_content,
                            note.title
                        )
                    )
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build().let {
                        NotificationManagerCompat.from(context)
                            .notify(noteId, it)
                    }
            } catch (exc: Exception) {
                exc.printStackTrace()
            } finally {
                SetupAlarmService.startSetupAlarm(context)
            }
        }
    }

    companion object {
        const val KEY_NOTE_ID = "me.ssttkkl.mrmemorizer.key-note-id"

        private const val NOTIFICATION_CHANNEL_ID = "review_notification"
    }
}