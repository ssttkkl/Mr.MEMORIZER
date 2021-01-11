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
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent
import me.ssttkkl.mrmemorizer.ui.utils.getNextReviewTimeText

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

    fun getNoteNextReviewTimeText(note: Note?) = note.getNextReviewTimeText()

    val showNewNoteViewEvent = SingleLiveEvent<NoteType>()
    val showViewNoteViewEvent = SingleLiveEvent<Note>()

    fun onClickNote(note: Note) = showViewNoteViewEvent.call(note)
    fun onClickNewTextNote() = showNewNoteViewEvent.call(NoteType.Text)
    fun onClickNewMapNote() = showNewNoteViewEvent.call(NoteType.Map)
}