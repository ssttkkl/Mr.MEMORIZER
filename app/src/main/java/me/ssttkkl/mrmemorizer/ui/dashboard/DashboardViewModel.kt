package me.ssttkkl.mrmemorizer.ui.dashboard

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.toLiveData
import kotlinx.coroutines.*
import me.ssttkkl.mrmemorizer.MyApp
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.data.entity.NoteType
import me.ssttkkl.mrmemorizer.res.ReviewStage
import me.ssttkkl.mrmemorizer.ui.utils.LiveTicker
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent
import me.ssttkkl.mrmemorizer.ui.utils.getNextReviewTimeText
import java.time.Instant

class DashboardViewModel : ViewModel() {

    private val tick = LiveTicker(1000)

    // 通过addSource监听数据库更新，一旦更新马上重新构造worker
    // 因此该LiveData会在数据库更新时，以及有note达到复习时间时更新
    val noteNextReview = object : MediatorLiveData<Note>() {
        var worker: Job? = null

        fun newWorker() = GlobalScope.launch {
            while (true) {
                val next = withContext(Dispatchers.IO) {
                    AppDatabase.getInstance().noteDao.getNoteNextReviewSync(ReviewStage.nextReviewDuration.size - 1)
                }.also { postValue(it) } ?: break
                delay(next.nextNotifyTime.toInstant().toEpochMilli() - Instant.now().toEpochMilli())
            }
        }

        init {
            addSource(AppDatabase.getInstance().noteDao.getNoteNextReview(ReviewStage.nextReviewDuration.size)) {
                worker?.cancel()
                worker = newWorker()
            }
        }

        override fun onActive() {
            super.onActive()
            if (worker?.isActive != true)
                worker = newWorker()
        }

        override fun onInactive() {
            super.onInactive()
            worker?.cancel()
        }
    }

    val hasNoteNextReview = Transformations.map(noteNextReview) {
        it != null
    }

    fun getNoteNextReviewTimeText(note: Note?) = note.getNextReviewTimeText(tick)

    val notesReadyToReview = Transformations.switchMap(noteNextReview) {
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

    val showNewNoteViewEvent = SingleLiveEvent<NoteType>()
    val showViewNoteViewEvent = SingleLiveEvent<Note>()

    fun onClickNewTextNote() = showNewNoteViewEvent.call(NoteType.Text)
    fun onClickNewMapNote() = showNewNoteViewEvent.call(NoteType.Map)
    fun onClickNote(note: Note) = showViewNoteViewEvent.call(note)
}