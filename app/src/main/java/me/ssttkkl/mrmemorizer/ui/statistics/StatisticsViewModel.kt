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

    var todayPunctuallyReviewRate = if (AppPreferences.todayReviewTimes == 0L)
        "-"
    else
        "%.2f%%".format(100.0 * AppPreferences.todayPunctuallyReviewTimes / AppPreferences.todayReviewTimes)

    var totalPunctuallyReviewRate = if (AppPreferences.totalReviewTimes == 0L)
        "-"
    else
        "%.2f%%".format(100.0 * AppPreferences.totalPunctuallyReviewTimes / AppPreferences.totalReviewTimes)

    var categoryCounts = AppDatabase.getInstance().categoryDao.countCategory().map {
        it.toString()
    }

}