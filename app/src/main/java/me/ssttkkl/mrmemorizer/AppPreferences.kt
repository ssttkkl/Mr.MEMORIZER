package me.ssttkkl.mrmemorizer

import android.util.Log
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.ssttkkl.mrmemorizer.ui.utils.LiveToday
import java.time.LocalDate

object AppPreferences {

    const val NOTIFICATION_CHANNEL_ID = "review_notification"

    private val pref = PreferenceManager.getDefaultSharedPreferences(MyApp.context)

    private const val KEY_STATISTICS_UPDATED_DATE = "statistics_updated_date"
    private const val KEY_TODAY_REVIEW_TIMES = "today_review_times"
    private const val KEY_TOTAL_REVIEW_TIMES = "total_review_times"
    private const val KEY_TODAY_PUNCTUALLY_REVIEW_TIMES = "today_punctually_review_times"
    private const val KEY_TOTAL_PUNCTUALLY_REVIEW_TIMES = "total_punctually_review_times"

    private var statisticsUpdatedDate: LocalDate
        get() = LocalDate.ofEpochDay(pref.getLong(KEY_STATISTICS_UPDATED_DATE, 0))
        set(value) {
            pref.edit()
                .putLong(KEY_STATISTICS_UPDATED_DATE, value.toEpochDay())
                .apply()
        }

    init {
        GlobalScope.launch(Dispatchers.Main) {
            LiveToday.observeForever {
                if (!statisticsUpdatedDate.isEqual(it)) {
                    todayReviewTimes = 0
                    todayPunctuallyReviewTimes = 0
                }
            }
        }
    }

    // 今日复习次数
    var todayReviewTimes: Long
        get() = pref.getLong(KEY_TODAY_REVIEW_TIMES, 0)
        private set(value) {
            pref.edit()
                .putLong(KEY_TODAY_REVIEW_TIMES, value)
                .apply()
        }

    // 总共复习次数
    var totalReviewTimes: Long
        get() = pref.getLong(KEY_TOTAL_REVIEW_TIMES, 0)
        private set(value) {
            pref.edit()
                .putLong(KEY_TOTAL_REVIEW_TIMES, value)
                .apply()
        }

    // 今日准时复习次数
    var todayPunctuallyReviewTimes: Long
        get() = pref.getLong(KEY_TODAY_PUNCTUALLY_REVIEW_TIMES, 0)
        private set(value) {
            pref.edit()
                .putLong(KEY_TODAY_PUNCTUALLY_REVIEW_TIMES, value)
                .apply()
        }

    // 总共准时复习次数
    var totalPunctuallyReviewTimes: Long
        get() = pref.getLong(KEY_TOTAL_PUNCTUALLY_REVIEW_TIMES, 0)
        private set(value) {
            pref.edit()
                .putLong(KEY_TODAY_PUNCTUALLY_REVIEW_TIMES, value)
                .apply()
        }

    fun incReviewTime(punctual: Boolean) {
        todayReviewTimes++
        totalReviewTimes++
        if (punctual) {
            todayPunctuallyReviewTimes++
            totalPunctuallyReviewTimes++
        }

        Log.d("AppPreferences", "todayReviewTimes: $todayReviewTimes")
        Log.d("AppPreferences", "totalReviewTimes: $totalReviewTimes")
        Log.d("AppPreferences", "todayPunctuallyReviewTimes: $todayPunctuallyReviewTimes")
        Log.d("AppPreferences", "totalPunctuallyReviewTimes: $totalPunctuallyReviewTimes")
    }
}