package me.ssttkkl.mrmemorizer.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.paging.toLiveData
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.data.entity.NoteType
import me.ssttkkl.mrmemorizer.ui.utils.LiveToday
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent

class DashboardViewModel : ViewModel() {

    val notesTodayReview = LiveToday.switchMap {
        AppDatabase.getInstance().noteDao.loadNotesNextReviewDateEq(it.toEpochDay())
            .toLiveData(pageSize = 50)
    }

    val emptyHintVisible = notesTodayReview.map { it.isEmpty() }

    val showNewNoteViewEvent = SingleLiveEvent<NoteType>()
    val showViewNoteViewEvent = SingleLiveEvent<Note>()
    val showSettingsViewEvent = SingleLiveEvent<Unit>()

    fun onClickNewTextNote() = showNewNoteViewEvent.call(NoteType.Text)
    fun onClickNewMapNote() = showNewNoteViewEvent.call(NoteType.Map)
    fun onClickNote(note: Note) = showViewNoteViewEvent.call(note)
    fun onClickSettings() = showSettingsViewEvent.call()
}