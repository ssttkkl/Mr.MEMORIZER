package me.ssttkkl.mrmemorizer

import androidx.preference.PreferenceManager

object AppPreferences {

    const val NOTIFICATION_CHANNEL_ID = "review_notification"

    private val pref = PreferenceManager.getDefaultSharedPreferences(MyApp.context)
}