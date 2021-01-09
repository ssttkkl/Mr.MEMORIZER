package me.ssttkkl.mrmemorizer.ui.utils

import androidx.lifecycle.map
import me.ssttkkl.mrmemorizer.MyApp
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.entity.Note

fun Note?.getNextReviewTimeText() = LiveToday.map {
    if (this == null)
        ""
    else {
        val restDay = nextReviewDate.toEpochDay() - it.toEpochDay()
        if (restDay <= 0)
            MyApp.context.getString(R.string.text_next_review_time_value_ready)
        else
            MyApp.context.getString(
                R.string.text_next_review_time_value_at_day,
                restDay
            )
    }
}