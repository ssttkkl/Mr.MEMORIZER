package me.ssttkkl.mrmemorizer.ui.utils

import androidx.lifecycle.map
import me.ssttkkl.mrmemorizer.AppPreferences
import me.ssttkkl.mrmemorizer.MyApp
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.entity.Note

fun Note?.getNextReviewTimeText(tick: LiveTicker) = tick.map {
    when {
        this == null -> ""
        stage == AppPreferences.reviewInterval.size ->
            MyApp.context.getString(R.string.text_next_review_time_value_closed)
        else -> {
            val restSecond = this.nextNotifyTime.toEpochSecond() - it / 1000
            when {
                restSecond < 0 -> MyApp.context.getString(R.string.text_next_review_time_value_ready)
                restSecond in 0 until 60 -> MyApp.context.getString(
                    R.string.text_next_review_time_value_at_second,
                    restSecond.toDouble()
                )
                restSecond / 60 in 0 until 60 -> MyApp.context.getString(
                    R.string.text_next_review_time_value_at_minute,
                    restSecond / 60.0
                )
                restSecond / 3600 in 0 until 24 -> MyApp.context.getString(
                    R.string.text_next_review_time_value_at_hour,
                    restSecond / 3600.0
                )
                else -> MyApp.context.getString(
                    R.string.text_next_review_time_value_at_day,
                    restSecond / 86400.0
                )
            }
        }
    }
}