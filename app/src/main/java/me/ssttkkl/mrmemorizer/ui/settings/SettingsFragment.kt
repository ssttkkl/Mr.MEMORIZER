package me.ssttkkl.mrmemorizer.ui.settings

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import me.ssttkkl.mrmemorizer.AppPreferences
import me.ssttkkl.mrmemorizer.R

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var viewModel: SettingsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java].apply {
            showEditReviewIntervalEvent.observe(
                viewLifecycleOwner,
                Observer { showEditReviewInterval() })
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<Preference>(AppPreferences.KEY_ADJUST_REVIEW_INTERVAL_MANUALLY)?.let {
            it.setOnPreferenceClickListener { viewModel.onClickEditReviewInterval();true }
        }
    }

    private fun showEditReviewInterval() {
        findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToEditReviewIntervalFragment())
    }
}