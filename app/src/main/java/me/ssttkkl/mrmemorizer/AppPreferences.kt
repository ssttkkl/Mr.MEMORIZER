package me.ssttkkl.mrmemorizer

import androidx.preference.PreferenceManager

/*
第一个记忆周期是 5分钟
第二个记忆周期是30分钟
第三个记忆周期是12小时
这三个记忆周期属于短期记忆的范畴。
下面是几个比较重要的周期。
第四个记忆周期是 1天
第五个记忆周期是 2天
第六个记忆周期是 4天
第七个记忆周期是 7天
第八个记忆周期是15天
 */

object AppPreferences {

    val KEY_ADJUST_REVIEW_INTERVAL_MANUALLY = MyApp.context
        .getString(R.string.settings_key_edit_review_interval)
    val KEY_REVIEW_INTERVAL = MyApp.context
        .getString(R.string.settings_key_review_interval)

    private val pref = PreferenceManager.getDefaultSharedPreferences(MyApp.context)

    var reviewInterval: List<Int>
        get() = pref.getString(
            KEY_REVIEW_INTERVAL, null
        )
            ?.split('|')?.map { it.toInt() }
            ?: defaultReviewInterval // in second
        set(value) {
            pref.edit()
                .putString(
                    KEY_REVIEW_INTERVAL,
                    value.joinToString("|") { it.toString() }
                ).apply()
        }

    private val defaultReviewInterval: List<Int> = listOf(
        5 * 60, 30 * 60, 12 * 60 * 60, 24 * 60 * 60,
        2 * 24 * 60 * 60, 4 * 24 * 60 * 60, 7 * 24 * 60 * 60, 15 * 24 * 60 * 60
    )
}