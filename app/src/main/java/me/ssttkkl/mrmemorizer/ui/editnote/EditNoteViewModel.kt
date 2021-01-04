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

    var noteBeforeEdit: Note? = null
        private set

    val noteTitle = MutableLiveData<String>()
    val noteContent = MutableLiveData<String>()

    val finishEvent = SingleLiveEvent<Unit>()

    private var firstCreate = AtomicBoolean(true)

    fun startAsNewNote() {
        if (firstCreate.getAndSet(false)) {
            mode.value = Mode.New
            noteTitle.value = ""
            noteContent.value = ""
        }
    }

    fun startAsEditNote(note: Note) {
        if (firstCreate.getAndSet(false)) {
            mode.value = Mode.Edit
            noteBeforeEdit = note
            noteTitle.value = note.title
            noteContent.value = note.content
            // TODO:
        }
    }

    fun onClickSave() {
        val note = when (mode.value!!) {
            Mode.New -> Note(
                title = noteTitle.value ?: "",
                content = noteContent.value ?: ""
            )
            Mode.Edit -> Note(
                noteId = noteBeforeEdit!!.noteId,
                title = noteTitle.value ?: "",
                content = noteContent.value ?: "",
                createTimestamp = noteBeforeEdit!!.createTimestamp,
                stage = noteBeforeEdit!!.stage,
                nextNotifyTimestamp = noteBeforeEdit!!.nextNotifyTimestamp
            )
        }
        GlobalScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance().noteDao.insertNote(note)
        }
        finishEvent.call()
    }
}