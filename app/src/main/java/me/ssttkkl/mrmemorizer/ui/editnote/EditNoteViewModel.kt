package me.ssttkkl.mrmemorizer.ui.editnote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ssttkkl.mrmemorizer.MyApp
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.data.entity.Category
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.data.entity.NoteType
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent
import java.util.concurrent.atomic.AtomicBoolean

class EditNoteViewModel : ViewModel() {

    enum class Mode { New, Edit }

    private var mode = Mode.New
    private var originNote: Note? = null

    val noteType = MutableLiveData<NoteType>(NoteType.Text)
    val noteTitle = MutableLiveData<String>("")
    val noteCategory = MutableLiveData<String>("")
    val noteContent = MutableLiveData<String>("")

    val toolbarTitle = noteType.map {
        when {
            mode == Mode.New && it == NoteType.Text ->
                MyApp.context.getString(R.string.title_new_text_note)
            mode == Mode.Edit && it == NoteType.Text ->
                MyApp.context.getString(R.string.title_edit_text_note)
            mode == Mode.New && it == NoteType.Map ->
                MyApp.context.getString(R.string.title_new_map_note)
            mode == Mode.Edit && it == NoteType.Map ->
                MyApp.context.getString(R.string.title_edit_map_note)
            else -> ""
        }
    }
    val previewVisible = noteType.map { it == NoteType.Map }
    val allCategories = AppDatabase.getInstance().categoryDao.getAllCategories()

    val showPreviewViewEvent = SingleLiveEvent<Unit>()
    val finishEvent = SingleLiveEvent<Unit>()

    private var initialized = AtomicBoolean(false)

    fun initializeForNewNote(noteType: NoteType) {
        if (!initialized.getAndSet(true)) {
            this.mode = Mode.New
            this.noteType.value = noteType
        }
    }

    fun initializeForEditNote(noteId: Int) {
        if (!initialized.getAndSet(true)) {
            mode = Mode.Edit

            GlobalScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getInstance()
                val origin = db.noteDao.getNoteByIdSync(noteId)?.also {
                    originNote = it
                } ?: error("note $noteId not found")

                noteType.postValue(origin.noteType)
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
                // 获得category的id（如果不存在就插入数据库中）
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
                            noteType = noteType.value!!,
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
                                noteType = noteType.value!!,
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

    fun onClickPreview() = showPreviewViewEvent.call()
}