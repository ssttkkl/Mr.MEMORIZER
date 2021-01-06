package me.ssttkkl.mrmemorizer.ui.viewnote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ssttkkl.mrmemorizer.AppPreferences
import me.ssttkkl.mrmemorizer.MyApp
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.ui.utils.LiveTicker
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent
import me.ssttkkl.mrmemorizer.ui.utils.getNextReviewTimeText
import java.time.OffsetDateTime
import java.util.concurrent.atomic.AtomicBoolean

class ViewNoteViewModel : ViewModel() {

    val noteId = MutableLiveData<Int>()
    val note = Transformations.switchMap(noteId) {
        AppDatabase.getInstance().noteDao.getNoteById(it)
    }
    val category = Transformations.switchMap(note) {
        if (it.categoryId == 0)
            null
        else
            AppDatabase.getInstance().categoryDao.getCategoryById(it.categoryId)
    }
    val reviewProgressText = Transformations.map(note) {
        MyApp.context.getString(
            R.string.text_review_progress,
            it.stage,
            AppPreferences.reviewInterval.size
        )
    }

    val tick = LiveTicker(1000)
    val nextReviewTimeText = Transformations.switchMap(note) {
        it.getNextReviewTimeText(tick)
    }
    val doReviewButtonVisible = Transformations.switchMap(note) {
        Transformations.map(tick) { tick ->
            (it.stage != AppPreferences.reviewInterval.size) &&
                    (tick >= it.nextNotifyTime.toInstant().toEpochMilli())
        }
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
                AppDatabase.getInstance().noteDao.getNoteByIdSync(noteId.value!!) ?: return@launch
            val note = Note(
                noteType = originNote.noteType,
                noteId = originNote.noteId,
                title = originNote.title,
                content = originNote.content,
                categoryId = originNote.categoryId,
                createTime = originNote.createTime,
                stage = originNote.stage + 1,
                nextNotifyTime = if (originNote.stage + 1 < AppPreferences.reviewInterval.size)
                    OffsetDateTime.now()
                        .plusSeconds(AppPreferences.reviewInterval[originNote.stage + 1].toLong())
                else // 这个值已经没用了
                    OffsetDateTime.now()
            )
            AppDatabase.getInstance().noteDao.updateNoteSync(note)
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
                val note = db.noteDao.getNoteByIdSync(noteId.value!!) ?: return@withTransaction
                db.noteDao.deleteNoteSync(note.noteId)
                db.categoryDao.autoDeleteCategorySync(note.categoryId)
            }
        }
        finishEvent.call()
    }
}