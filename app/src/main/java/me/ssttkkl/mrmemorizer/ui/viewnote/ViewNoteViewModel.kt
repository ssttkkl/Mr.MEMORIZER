package me.ssttkkl.mrmemorizer.ui.viewnote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent
import me.ssttkkl.mrmemorizer.ui.utils.getNextReviewTimeText
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicBoolean

class ViewNoteViewModel : ViewModel() {

    val noteId = MutableLiveData(0)
    val note = Transformations.switchMap(noteId) {
        AppDatabase.getInstance().noteDao.getNoteById(it)
    }
    val category = Transformations.switchMap(note) {
        if (it.categoryId == 0)
            null
        else
            AppDatabase.getInstance().categoryDao.getCategoryById(it.categoryId)
    }

    val nextReviewTimeText = Transformations.switchMap(note) {
        it.getNextReviewTimeText()
    }

    val isDoReviewButtonVisible = Transformations.map(note) {
        it != null && !LocalDate.now().isBefore(it.nextReviewDate)
    }
    val isContentVisible = MutableLiveData<Boolean>(true)
    val isHideHintVisible = MutableLiveData<Boolean>(false)

    init {
        note.observeForever {
            if (it != null && !LocalDate.now().isBefore(it.nextReviewDate)) { // 当笔记待复习时
                isContentVisible.value = false
                isHideHintVisible.value = true
            }
        }
    }

    val showEditNoteViewEvent = SingleLiveEvent<Int>()
    val showDoReviewViewEvent = SingleLiveEvent<Int>()
    val finishEvent = SingleLiveEvent<Unit>()

    private val initialized = AtomicBoolean(false)

    fun initialize(noteId: Int) {
        if (!initialized.getAndSet(true)) {
            this.noteId.value = noteId
        }
    }

    fun onClickShow() {
        isContentVisible.value = true
        isHideHintVisible.value = false
    }

    fun onClickDoReview() {
        showDoReviewViewEvent.call(noteId.value)
    }

    fun onClickEdit() {
        showEditNoteViewEvent.call(noteId.value)
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