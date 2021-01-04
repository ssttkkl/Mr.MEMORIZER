package me.ssttkkl.mrmemorizer.ui.editnote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent
import java.util.concurrent.atomic.AtomicBoolean

class EditNoteViewModel : ViewModel() {

    enum class Mode { New, Edit }

    val mode = MutableLiveData<Mode>()
    val originNote = MutableLiveData<Note>()
    val noteTitle = MutableLiveData<String>()
    val noteContent = MutableLiveData<String>()

    val finishEvent = SingleLiveEvent<Unit>()

    private var initialized = AtomicBoolean(false)

    fun initializeForNewNote() {
        if (!initialized.getAndSet(true)) {
            mode.value = Mode.New
            noteTitle.value = ""
            noteContent.value = ""
        }
    }

    fun initializeForEditNote(note: Note) {
        if (!initialized.getAndSet(true)) {
            mode.value = Mode.Edit
            originNote.value = note
            noteTitle.value = note.title
            noteContent.value = note.content
            // TODO:
        }
    }

    fun onClickSave() {
        when (mode.value!!) {
            Mode.New -> {
                GlobalScope.launch(Dispatchers.IO) {
                    val note = Note(
                        title = noteTitle.value ?: "",
                        content = noteContent.value ?: ""
                    )
                    AppDatabase.getInstance().noteDao.insertNote(note)
                }
            }
            Mode.Edit -> {
                val origin = originNote.value!!
                GlobalScope.launch(Dispatchers.IO) {
                    val note = Note(
                        noteId = origin.noteId,
                        title = noteTitle.value ?: "",
                        content = noteContent.value ?: "",
                        createTime = origin.createTime,
                        stage = origin.stage,
                        nextNotifyTime = origin.nextNotifyTime
                    )
                    AppDatabase.getInstance().noteDao.updateNote(note)
                }
            }
        }
        finishEvent.call()
    }
}