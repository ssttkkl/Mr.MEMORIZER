package me.ssttkkl.mrmemorizer.ui.settings.editinterval

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ssttkkl.mrmemorizer.AppPreferences
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent

class EditIntervalViewModel : ViewModel() {

    val reviewInterval = MutableLiveData<List<Pair<Int, Boolean>>>(
        AppPreferences.reviewInterval.map { it to false }
    )

    val finishEvent = SingleLiveEvent<Unit>()

    fun onClickAddItem() {
        reviewInterval.value = reviewInterval.value?.plus(0 to true)
    }

    fun onClickEditItem(position: Int) {
        reviewInterval.value = reviewInterval.value?.toMutableList()?.apply {
            this[position] = this[position].first to true
        }
    }

    fun onClickDoneItem(position: Int, text: String, unitOrdinal: Int) {
        val interval = text.toDoubleOrNull() ?: return
        val unit = if (unitOrdinal in TimeUnit.values().indices)
            TimeUnit.values()[unitOrdinal]
        else return

        reviewInterval.value = reviewInterval.value?.toMutableList()?.apply {
            this[position] = (interval * when (unit) {
                TimeUnit.Second -> 1
                TimeUnit.Minute -> 60
                TimeUnit.Hour -> 3600
                TimeUnit.Day -> 86400
            }).toInt() to false
            sortBy { it.first }
        }
    }

    fun onClickRemoveItem(position: Int) {
        reviewInterval.value = reviewInterval.value?.toMutableList()?.apply {
            removeAt(position)
        }
        maintainDatabaseWhenRemove()
    }

    fun onClickSave() {
        reviewInterval.value?.let { li ->
            AppPreferences.reviewInterval = li.map { it.first }
        }
        maintainDatabaseWhenRemove()
        finishEvent.call()
    }

    // 调整所有超过最大阶段的note至最大阶段
    private fun maintainDatabaseWhenRemove() = GlobalScope.launch(Dispatchers.IO) {
        AppDatabase.getInstance().withTransaction {
            val maxStage = reviewInterval.value!!.size
            val notes = AppDatabase.getInstance().noteDao
                .getNotesWhereStageGreaterThan(maxStage)
                .map { it.copy(stage = maxStage) }
                .toTypedArray()
            AppDatabase.getInstance().noteDao.updateNoteSync(*notes)
        }
    }
}