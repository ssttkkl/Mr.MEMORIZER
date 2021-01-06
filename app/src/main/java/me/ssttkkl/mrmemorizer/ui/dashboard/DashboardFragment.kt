package me.ssttkkl.mrmemorizer.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.data.entity.NoteType
import me.ssttkkl.mrmemorizer.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.listReadyToReview.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = NoteRecyclerViewAdapter(viewLifecycleOwner, binding.viewModel!!)
        }
        binding.viewModel?.apply {
            showNewNoteViewEvent.observe(viewLifecycleOwner, Observer { showNewNoteView(it) })
            showViewNoteViewEvent.observe(viewLifecycleOwner, Observer { showViewNoteView(it) })
            showSettingsViewEvent.observe(viewLifecycleOwner, Observer { showSettingsView() })
        }
    }

    private fun showNewNoteView(type: NoteType) {
        findNavController().navigate(
            R.id.navigation_edit_note,
            bundleOf(
                "mode" to "new",
                "noteType" to type
            )
        )
    }

    private fun showViewNoteView(note: Note) {
        findNavController().navigate(
            R.id.navigation_view_note,
            bundleOf("noteId" to note.noteId)
        )
    }

    private fun showSettingsView() {
        findNavController().navigate(
            R.id.action_navigation_dashboard_to_navigation_settings
        )
    }
}