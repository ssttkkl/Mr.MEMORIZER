package me.ssttkkl.mrmemorizer.ui.viewnote

import androidx.lifecycle.*
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ssttkkl.mrmemorizer.MyApp
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent
import me.ssttkkl.mrmemorizer.ui.utils.getNextReviewTimeText
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicBoolean

class ViewNoteViewModel : ViewModel() {

    val noteId = MutableLiveData(0)
    val note = Transformations.switchMap(noteId) {
        AppDatabase.getInstance().noteDao.getNoteById(it)
    }.apply {
        observeForever {
            fabStage.value = // 当笔记待复习时为0，否则为-1
                if (it != null && !LocalDate.now().isBefore(it.nextReviewDate)) 0 else -1
        }
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

    val fabStage = MutableLiveData(-1) // -1：隐藏（笔记未到复习时间）；0: 点击展示笔记；1: 点击按钮完成复习
    val isFabVisible = fabStage.map { it != -1 }

    val noteContent = MediatorLiveData<String>().apply {
        fun updateValue() {
            value = if (fabStage.value == 0)
                MyApp.context.getString(R.string.text_note_hided_hint)
            else
                note.value?.content ?: ""
        }
        addSource(note) { updateValue() }
        addSource(fabStage) { updateValue() }
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

    fun onClickFab() {
        if (fabStage.value == 0)
            fabStage.value = 1
        else
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