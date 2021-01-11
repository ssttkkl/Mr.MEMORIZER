package me.ssttkkl.mrmemorizer.ui.statistics


import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import me.ssttkkl.mrmemorizer.AppPreferences
import me.ssttkkl.mrmemorizer.data.AppDatabase


class StatisticsViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    var todayReviewTimes = AppPreferences.todayReviewTimes.toString()

    var totalReviewTimes = AppPreferences.totalReviewTimes.toString()

    var todayNoteCounts = AppDatabase.getInstance().noteDao.countNotesCreatedToday().map {
        it.toString()
    }

    var totalNoteCounts = AppDatabase.getInstance().noteDao.countNotes().map {
        it.toString()
    }

    var todayPunctuallyReviewTimes = AppPreferences.todayPunctuallyReviewTimes.toString()

    var totalPunctuallyReviewTimes = AppPreferences.totalPunctuallyReviewTimes.toString()

    var categoryCounts = AppDatabase.getInstance().categoryDao.countCategory().map {
        it.toString()
    }

}