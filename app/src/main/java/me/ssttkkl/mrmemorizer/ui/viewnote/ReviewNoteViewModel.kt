package me.ssttkkl.mrmemorizer.ui.viewnote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent
import java.util.concurrent.atomic.AtomicBoolean

class ReviewNoteViewModel : ViewModel() {

    val noteId = MutableLiveData(0)

    val finishEvent = SingleLiveEvent<Unit>()

    private val initialized = AtomicBoolean(false)

    fun initialize(noteId: Int) {
        if (!initialized.getAndSet(true)) {
            this.noteId.value = noteId
        }
    }

    fun onClickDoReview(userGrade: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val origin = AppDatabase.getInstance().noteDao
                .getNoteByIdSync(noteId.value!!) ?: return@launch
            val note = origin.sm2(userGrade)
            AppDatabase.getInstance().noteDao.updateNoteSync(note)
        }
        finishEvent.call()
    }
}