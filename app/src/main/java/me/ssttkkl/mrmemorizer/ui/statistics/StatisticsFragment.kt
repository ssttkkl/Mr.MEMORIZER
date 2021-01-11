package me.ssttkkl.mrmemorizer.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.TOP_DEST
import me.ssttkkl.mrmemorizer.databinding.FragmentStatisticsBinding
import me.ssttkkl.mrmemorizer.databinding.FragmentViewNoteBinding
import me.ssttkkl.mrmemorizer.ui.viewnote.ViewNoteViewModel

class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = ViewModelProvider(this)[StatisticsViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.apply {
            (activity as? AppCompatActivity)?.setSupportActionBar(this)
            setupWithNavController(findNavController(), AppBarConfiguration(TOP_DEST))
        }
        setHasOptionsMenu(true)

    }
}