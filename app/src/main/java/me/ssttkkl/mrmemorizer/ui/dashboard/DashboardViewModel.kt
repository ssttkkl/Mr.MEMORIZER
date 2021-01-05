package me.ssttkkl.mrmemorizer.ui.dashboard

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.toLiveData
import me.ssttkkl.mrmemorizer.MyApp
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.res.ReviewStage
import me.ssttkkl.mrmemorizer.ui.utils.LiveTicker
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent
import java.time.OffsetDateTime

class DashboardViewModel : ViewModel() {

    private val tick = LiveTicker(30 * 1000)

    val notesReadyToReview = Transformations.switchMap(tick) {
        AppDatabase.getInstance().noteDao.loadNotesReadyToReview(
            ReviewStage.nextReviewDuration.size - 1
        ).toLiveData(pageSize = 50)
    }
    val noteReadyToReviewCount = Transformations.map(notesReadyToReview) {
        MyApp.context.getString(R.string.text_item_count, it.size)
    }
    val hasNoteReadyToReview = Transformations.map(notesReadyToReview) {
        it.isNotEmpty()
    }
    val noteNextReview = Transformations.switchMap(tick) {
        AppDatabase.getInstance().noteDao
            .getNoteNextReview(ReviewStage.nextReviewDuration.size - 1)
    }
    val hasNoteNextReview = Transformations.map(noteNextReview) {
        it != null
    }
    val noteNextReviewTimeText = Transformations.map(noteNextReview) {
        if (it == null)
            null
        else {
            val restSecond =
                it.nextNotifyTime.toEpochSecond() - OffsetDateTime.now().toEpochSecond()
            when {
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
    }

    val showNewNoteViewEvent = SingleLiveEvent<Unit>()
    val showViewNoteViewEvent = SingleLiveEvent<Note>()

    fun onClickNewNote() = showNewNoteViewEvent.call()
    fun onClickNote(note: Note) = showViewNoteViewEvent.call(note)
}