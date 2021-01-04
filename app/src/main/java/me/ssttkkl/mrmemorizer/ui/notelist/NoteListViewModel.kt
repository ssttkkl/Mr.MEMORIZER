package me.ssttkkl.mrmemorizer.ui.notelist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import me.ssttkkl.mrmemorizer.MyApp
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent
import java.time.OffsetDateTime

class NoteListViewModel : ViewModel() {

    val searchQuery = MutableLiveData<String>("")
    val notes = Transformations.switchMap(searchQuery) {
        if (it.isNullOrEmpty())
            AppDatabase.getInstance().dao.getAllNotes()
        else
            AppDatabase.getInstance().dao.getNotesWithKeyword(it)
    }

    fun getNextReviewTimeText(note: Note): String {
        val restSecond = note.nextNotifyTime.toEpochSecond() - OffsetDateTime.now().toEpochSecond()
        return when {
            restSecond < 0 -> MyApp.context.getString(R.string.text_next_review_time_value_ready)
            restSecond / 60 in 0 until 60 -> MyApp.context.getString(
                R.string.text_next_review_time_value_at_minute,
                restSecond / 60
            )
            restSecond / 3600 in 0 until 24 -> MyApp.context.getString(
                R.string.text_next_review_time_value_at_hour,
                restSecond / 3600
            )
            else -> MyApp.context.getString(
                R.string.text_next_review_time_value_at_day,
                restSecond / 86400
            )
        }
    }

    val showViewNoteViewEvent = SingleLiveEvent<Note>()

    fun onClickNote(note: Note) = showViewNoteViewEvent.call(note)
}