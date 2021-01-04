package me.ssttkkl.mrmemorizer.ui.viewnote

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent
import java.util.concurrent.atomic.AtomicBoolean

class ViewNoteViewModel : ViewModel() {

    var noteId: Long = 0
        private set
    lateinit var note: LiveData<Note>
        private set

    val showEditNoteViewEvent = SingleLiveEvent<Unit>()
    val finishEvent = SingleLiveEvent<Unit>()

    private val firstCreate = AtomicBoolean(true)

    fun start(noteId: Long) {
        if (firstCreate.getAndSet(false)) {
            this.noteId = noteId
            this.note = AppDatabase.getInstance().noteDao.getNoteByIdAsLiveData(noteId)
        }
    }

    fun onClickEdit() {
        showEditNoteViewEvent.call()
    }

    fun onClickRemove() {
        GlobalScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance().noteDao.deleteNote(noteId)
        }
        finishEvent.call()
    }
}