package me.ssttkkl.mrmemorizer.ui.notelist

import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.toLiveData
import me.ssttkkl.mrmemorizer.MyApp
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.data.entity.Category
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.data.entity.NoteType
import me.ssttkkl.mrmemorizer.ui.utils.LiveTicker
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent

class NoteListViewModel : ViewModel() {

    private val theAllCategory = Category(0, MyApp.context.getString(R.string.text_all_category))

    val allCategories = AppDatabase.getInstance().categoryDao.getAllCategories().map {
        listOf(theAllCategory) + it
    }

    val categoryFilter = MutableLiveData<Category>(theAllCategory)
    val searchQuery = MutableLiveData<String>("")

    private val switching = MediatorLiveData<Pair<String, Category>>().apply {
        addSource(searchQuery) { value = Pair(it, categoryFilter.value ?: theAllCategory) }
        addSource(categoryFilter) { value = Pair(searchQuery.value ?: "", it) }
    }

    val allTypeNotes: LiveData<PagedList<Note>> = switching.switchMap {
        AppDatabase.getInstance().noteDao
            .loadNotes(it.first, it.second.categoryId)
            .toLiveData(pageSize = 50)
    }
    val textNotes: LiveData<PagedList<Note>> = switching.switchMap {
        AppDatabase.getInstance().noteDao
            .loadNotes(NoteType.Text, it.first, it.second.categoryId)
            .toLiveData(pageSize = 50)
    }
    val mapNotes: LiveData<PagedList<Note>> = switching.switchMap {
        AppDatabase.getInstance().noteDao
            .loadNotes(NoteType.Map, it.first, it.second.categoryId)
            .toLiveData(pageSize = 50)
    }

    val tabName = listOf(
        MyApp.context.getString(R.string.text_all),
        MyApp.context.getString(R.string.text_text_note),
        MyApp.context.getString(R.string.text_mind_note)
    )
    val tabData = listOf(allTypeNotes, textNotes, mapNotes)

    private val ticker = LiveTicker(30 * 1000)
    fun getNextReviewTimeText(note: Note): LiveData<String> = ticker.map {
        val restSecond = note.nextNotifyTime.toEpochSecond() - it / 1000
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

    val showViewNoteViewEvent = SingleLiveEvent<Note>()

    fun onClickNote(note: Note) = showViewNoteViewEvent.call(note)
}