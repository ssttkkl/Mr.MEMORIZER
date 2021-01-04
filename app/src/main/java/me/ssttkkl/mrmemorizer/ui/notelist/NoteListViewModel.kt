package me.ssttkkl.mrmemorizer.ui.notelist

import androidx.lifecycle.ViewModel
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent

class NoteListViewModel : ViewModel() {
    val notes = AppDatabase.getInstance().noteDao.getAllNotesAsLiveData()

    val showNewNoteViewEvent = SingleLiveEvent<Unit>()
    val showViewNoteViewEvent = SingleLiveEvent<Note>()

    fun onClickAddButton() = showNewNoteViewEvent.call()
    fun onClickNote(note: Note) = showViewNoteViewEvent.call(note)
}