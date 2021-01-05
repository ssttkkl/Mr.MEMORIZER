package me.ssttkkl.mrmemorizer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SetupAlarmOnBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            SetupAlarmService.startSetupAlarm(context)
        }
    }
}