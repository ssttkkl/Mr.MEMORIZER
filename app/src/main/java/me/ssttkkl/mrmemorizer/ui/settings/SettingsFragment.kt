package me.ssttkkl.mrmemorizer.ui.settings

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import me.ssttkkl.mrmemorizer.AppPreferences
import me.ssttkkl.mrmemorizer.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<Preference>(AppPreferences.KEY_DAILY_NOTICE_TIME)?.apply {
            val updateSummary = {
                val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                summary = AppPreferences.dailyNoticeTime.format(formatter)
            }

            updateSummary()

            setOnPreferenceClickListener {
                val oldTime = AppPreferences.dailyNoticeTime
                TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                    AppPreferences.dailyNoticeTime = LocalTime.of(hourOfDay, minute)
                    updateSummary()
                }, oldTime.hour, oldTime.minute, true).show()
                true
            }
        }
    }
}