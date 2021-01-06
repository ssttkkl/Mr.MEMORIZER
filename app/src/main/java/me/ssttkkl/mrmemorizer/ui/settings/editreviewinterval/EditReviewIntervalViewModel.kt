package me.ssttkkl.mrmemorizer.ui.settings.editreviewinterval

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.ssttkkl.mrmemorizer.AppPreferences

class EditReviewIntervalViewModel : ViewModel() {

    val reviewInterval = MutableLiveData<List<Int>>().apply {
        value = AppPreferences.reviewInterval
        observeForever { AppPreferences.reviewInterval = it }
    } // in seconds

    val selectedPosition = MutableLiveData(-1)
}