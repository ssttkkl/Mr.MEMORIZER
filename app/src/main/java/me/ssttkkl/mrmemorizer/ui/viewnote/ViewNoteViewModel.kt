package me.ssttkkl.mrmemorizer.ui.viewnote

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ssttkkl.mrmemorizer.MyApp
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.res.ReviewStage
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent
import java.time.OffsetDateTime
import java.util.concurrent.atomic.AtomicBoolean

class ViewNoteViewModel : ViewModel() {

    var noteId: Long = 0
        private set
    lateinit var note: LiveData<Note>
        private set

    // private val dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

    val reviewProgress = lazy {
        Transformations.map(note) {
            MyApp.context.getString(
                R.string.text_review_progress,
                it.stage,
                ReviewStage.nextReviewDuration.size - 1
            )
        }
    }

    val nextReviewTime = lazy {
        Transformations.map(note) {
            val now = OffsetDateTime.now()
            if (now >= it.nextNotifyTime)
                MyApp.context.getString(R.string.text_next_review_time_out)
            else {
                val restSecond = it.nextNotifyTime.toEpochSecond() - now.toEpochSecond()
                if (restSecond / 60 in 0 until 60)
                    MyApp.context.getString(R.string.text_next_review_time_minute, restSecond / 60)
                else if (restSecond / 3600 in 0 until 24)
                    MyApp.context.getString(R.string.text_next_review_time_hour, restSecond / 3600)
                else
                    MyApp.context.getString(R.string.text_next_review_time_day, restSecond / 86400)
            }
        }
    }

    val showEditNoteViewEvent = SingleLiveEvent<Unit>()
    val finishEvent = SingleLiveEvent<Unit>()

    private val firstCreate = AtomicBoolean(true)

    fun start(noteId: Long) {
        if (firstCreate.getAndSet(false)) {
            this.noteId = noteId
            this.note = AppDatabase.getInstance().noteDao.getNoteByIdAsLiveData(noteId)
        }
    }

    fun onClickEdit() {
        showEditNoteViewEvent.call()
    }

    fun onClickRemove() {
        GlobalScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance().noteDao.deleteNote(noteId)
        }
        finishEvent.call()
    }
}