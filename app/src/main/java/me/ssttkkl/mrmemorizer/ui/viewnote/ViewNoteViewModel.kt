package me.ssttkkl.mrmemorizer.ui.viewnote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ssttkkl.mrmemorizer.MyApp
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.res.ReviewStage
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent
import java.time.OffsetDateTime
import java.util.concurrent.atomic.AtomicBoolean

class ViewNoteViewModel : ViewModel() {

    val noteId = MutableLiveData<Long>()
    val note = Transformations.switchMap(noteId) {
        AppDatabase.getInstance().noteDao.getNoteById(it)
    }
    val reviewProgressText = Transformations.map(note) {
        MyApp.context.getString(
            R.string.text_review_progress,
            it.stage,
            ReviewStage.nextReviewDuration.size - 1
        )
    }
    val nextReviewTimeText = Transformations.map(note) {
        val restSecond = it.nextNotifyTime.toEpochSecond() - OffsetDateTime.now().toEpochSecond()
        when {
            restSecond < 0 -> MyApp.context.getString(R.string.text_next_review_time_out)
            restSecond / 60 in 0 until 60 -> MyApp.context.getString(
                R.string.text_next_review_time_minute,
                restSecond / 60
            )
            restSecond / 3600 in 0 until 24 -> MyApp.context.getString(
                R.string.text_next_review_time_hour,
                restSecond / 3600
            )
            else -> MyApp.context.getString(
                R.string.text_next_review_time_day,
                restSecond / 86400
            )
        }
    }

    val showEditNoteViewEvent = SingleLiveEvent<Unit>()
    val finishEvent = SingleLiveEvent<Unit>()

    private val firstCreate = AtomicBoolean(true)

    fun start(noteId: Long) {
        if (firstCreate.getAndSet(false)) {
            this.noteId.value = noteId
        }
    }

    fun onClickEdit() {
        showEditNoteViewEvent.call()
    }

    fun onClickRemove() {
        GlobalScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance().noteDao.deleteNote(noteId.value!!)
        }
        finishEvent.call()
    }
}