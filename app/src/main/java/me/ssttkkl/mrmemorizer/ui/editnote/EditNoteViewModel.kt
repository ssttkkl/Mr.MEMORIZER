package me.ssttkkl.mrmemorizer.ui.editnote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.data.entity.Category
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent
import java.util.concurrent.atomic.AtomicBoolean

class EditNoteViewModel : ViewModel() {

    enum class Mode { New, Edit }

    private lateinit var mode: Mode
    private var originNote: Note? = null

    val noteTitle = MutableLiveData<String>("")
    val noteCategory = MutableLiveData<String>("")
    val noteContent = MutableLiveData<String>("")

    val categories = AppDatabase.getInstance().dao.getAllCategories()

    val finishEvent = SingleLiveEvent<Unit>()

    private var initialized = AtomicBoolean(false)

    fun initializeForNewNote() {
        if (!initialized.getAndSet(true)) {
            mode = Mode.New
        }
    }

    fun initializeForEditNote(noteId: Long) {
        if (!initialized.getAndSet(true)) {
            mode = Mode.Edit
            GlobalScope.launch(Dispatchers.IO) {
                val origin = AppDatabase.getInstance().dao.getNoteByIdSync(noteId)
                originNote = origin

                noteTitle.postValue(origin?.title)
                noteContent.postValue(origin?.content)
                if (origin?.categoryId != null && origin.categoryId != 0L) {
                    val category =
                        AppDatabase.getInstance().dao.getCategoryByIdSync(origin.categoryId)
                    noteCategory.postValue(category?.name)
                }
            }
        }
    }

    fun onClickSave() {
        GlobalScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getInstance()
            db.withTransaction {
                val categoryName = noteCategory.value ?: ""
                val categoryId = if (categoryName.isEmpty()) 0 else {
                    var category = db.dao.getCategoryByNameSync(categoryName)
                    if (category == null) {
                        db.dao.insertCategory(Category(categoryName))
                        category = db.dao.getCategoryByNameSync(categoryName)
                    }
                    category!!.categoryId
                }

                when (mode) {
                    Mode.New -> {
                        val note = Note(
                            title = noteTitle.value ?: "",
                            content = noteContent.value ?: "",
                            categoryId = categoryId
                        )
                        db.dao.insertNote(note)
                    }
                    Mode.Edit -> {
                        val origin = originNote!!
                        GlobalScope.launch(Dispatchers.IO) {
                            val note = Note(
                                noteId = origin.noteId,
                                title = noteTitle.value ?: "",
                                content = noteContent.value ?: "",
                                categoryId = categoryId,
                                createTime = origin.createTime,
                                stage = origin.stage,
                                nextNotifyTime = origin.nextNotifyTime
                            )
                            db.dao.updateNote(note)
                            db.dao.autoDeleteCategory(origin.categoryId)
                        }
                    }
                }
            }
        }
        finishEvent.call()
    }
}