package me.ssttkkl.mrmemorizer.ui.viewnote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.room.withTransaction
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

    val noteId = MutableLiveData<Int>()
    val note = Transformations.switchMap(noteId) {
        AppDatabase.getInstance().dao.getNoteById(it)
    }
    val category = Transformations.switchMap(note) {
        if (it.categoryId == 0)
            null
        else
            AppDatabase.getInstance().dao.getCategoryById(it.categoryId)
    }
    val reviewProgressText = Transformations.map(note) {
        MyApp.context.getString(
            R.string.text_review_progress,
            it.stage,
            ReviewStage.nextReviewDuration.size - 1
        )
    }
    val nextReviewTimeText = Transformations.map(note) {
        if (it.stage == ReviewStage.nextReviewDuration.size)
            MyApp.context.getString(R.string.text_next_review_time_value_closed)
        else {
            val restSecond =
                it.nextNotifyTime.toEpochSecond() - OffsetDateTime.now().toEpochSecond()
            when {
                restSecond < 0 -> MyApp.context.getString(R.string.text_next_review_time_value_ready)
                restSecond / 60 in 0 until 60 -> MyApp.context.getString(
                    R.string.text_next_review_time_value_at_minute,
                    restSecond / 60
                )
                restSecond / 3600 in 0 until 24 -> MyApp.context.getString(
                    R.string.text_next_review_time_value_at_hour,
                    restSecond / 3600
                )
                else -> MyApp.context.getString(
                    R.string.text_next_review_time_value_at_day,
                    restSecond / 86400
                )
            }
        }
    }
    val doReviewButtonVisible = Transformations.map(note) {
        if (it.stage == ReviewStage.nextReviewDuration.size)
            false
        else
            !it.nextNotifyTime.isAfter(OffsetDateTime.now())
    }

    val showEditNoteViewEvent = SingleLiveEvent<Unit>()
    val finishEvent = SingleLiveEvent<Unit>()

    private val initialized = AtomicBoolean(false)

    fun initialize(noteId: Int) {
        if (!initialized.getAndSet(true)) {
            this.noteId.value = noteId
        }
    }

    fun onClickDoReview() {
        GlobalScope.launch(Dispatchers.IO) {
            val originNote =
                AppDatabase.getInstance().dao.getNoteByIdSync(noteId.value!!) ?: return@launch
            val note = Note(
                noteId = originNote.noteId,
                title = originNote.title,
                content = originNote.content,
                categoryId = originNote.categoryId,
                createTime = originNote.createTime,
                stage = originNote.stage + 1,
                nextNotifyTime = if (originNote.stage + 1 < ReviewStage.nextReviewDuration.size)
                    OffsetDateTime.now()
                        .plusSeconds(ReviewStage.nextReviewDuration[originNote.stage + 1])
                else // 这个值已经没用了
                    OffsetDateTime.now()
            )
            AppDatabase.getInstance().dao.updateNoteSync(note)
        }
        finishEvent.call()
    }

    fun onClickEdit() {
        showEditNoteViewEvent.call()
    }

    fun onClickRemove() {
        GlobalScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getInstance()
            db.withTransaction {
                val note = db.dao.getNoteByIdSync(noteId.value!!) ?: return@withTransaction
                db.dao.deleteNoteSync(note.noteId)
                db.dao.autoDeleteCategory(note.categoryId)
            }
        }
        finishEvent.call()
    }
}