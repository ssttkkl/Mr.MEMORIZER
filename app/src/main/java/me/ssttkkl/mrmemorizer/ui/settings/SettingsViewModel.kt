package me.ssttkkl.mrmemorizer.ui.settings

import androidx.lifecycle.ViewModel
import me.ssttkkl.mrmemorizer.ui.utils.SingleLiveEvent

class SettingsViewModel : ViewModel() {

    val showEditReviewIntervalEvent = SingleLiveEvent<Unit>()

    fun onClickEditReviewInterval() = showEditReviewIntervalEvent.call()
}