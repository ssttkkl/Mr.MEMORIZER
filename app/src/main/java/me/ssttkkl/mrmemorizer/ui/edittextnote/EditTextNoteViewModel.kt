package me.ssttkkl.mrmemorizer.ui.edittextnote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.data.entity.Category
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.data.entity.NoteType
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent
import java.util.concurrent.atomic.AtomicBoolean

class EditTextNoteViewModel : ViewModel() {

    enum class Mode { New, Edit }

    private lateinit var mode: Mode
    private var originNote: Note? = null

    val noteTitle = MutableLiveData<String>("")
    val noteCategory = MutableLiveData<String>("")
    val noteContent = MutableLiveData<String>("")

    val allCategories = AppDatabase.getInstance().categoryDao.getAllCategories()

    val finishEvent = SingleLiveEvent<Unit>()

    private var initialized = AtomicBoolean(false)

    fun initializeForNewNote() {
        if (!initialized.getAndSet(true)) {
            mode = Mode.New
        }
    }

    fun initializeForEditNote(noteId: Int) {
        if (!initialized.getAndSet(true)) {
            mode = Mode.Edit

            GlobalScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getInstance()
                val origin = db.noteDao.getNoteByIdSync(noteId)
                if (origin?.noteType != NoteType.Text) {
                    error("${origin.toString()} is not a text note.")
                }

                originNote = origin
                noteTitle.postValue(origin.title)
                noteContent.postValue(origin.content)
                if (origin.categoryId != 0) {
                    val category = db.categoryDao.getCategoryByIdSync(origin.categoryId)
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
                    var category = db.categoryDao.getCategoryByNameSync(categoryName)
                    if (category == null) {
                        db.categoryDao.insertCategorySync(Category(categoryName))
                        category = db.categoryDao.getCategoryByNameSync(categoryName)
                    }
                    category!!.categoryId
                }

                when (mode) {
                    Mode.New -> {
                        val note = Note(
                            noteType = NoteType.Text,
                            title = noteTitle.value ?: "",
                            content = noteContent.value ?: "",
                            categoryId = categoryId
                        )
                        db.noteDao.insertNoteSync(note)
                    }
                    Mode.Edit -> {
                        val origin = originNote!!
                        GlobalScope.launch(Dispatchers.IO) {
                            val note = Note(
                                noteType = NoteType.Text,
                                noteId = origin.noteId,
                                title = noteTitle.value ?: "",
                                content = noteContent.value ?: "",
                                categoryId = categoryId,
                                createTime = origin.createTime,
                                stage = origin.stage,
                                nextNotifyTime = origin.nextNotifyTime
                            )
                            db.noteDao.updateNoteSync(note)
                            db.categoryDao.autoDeleteCategorySync(origin.categoryId)
                        }
                    }
                }
            }
        }
        finishEvent.call()
    }
}