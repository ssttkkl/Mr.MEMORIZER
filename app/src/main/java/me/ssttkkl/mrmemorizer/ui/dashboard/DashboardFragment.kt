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
            adapter = NoteRecyclerViewAdapter(this@DashboardFragment, binding.viewModel!!)
        }
        binding.viewModel?.apply {
            showNewNoteViewEvent.observe(this@DashboardFragment, Observer { showNewNoteView(it) })
            showViewNoteViewEvent.observe(this@DashboardFragment, Observer { showViewNoteView(it) })
        }
    }

    private fun showNewNoteView(type: NoteType) {
        findNavController().navigate(
            when (type) {
                NoteType.Text -> R.id.navigation_edit_text_note
                NoteType.Map -> throw UnsupportedOperationException() // TODO:
            }, bundleOf("mode" to "new")
        )
    }

    private fun showViewNoteView(note: Note) {
        findNavController().navigate(
            R.id.navigation_view_note,
            bundleOf("noteId" to note.noteId)
        )
    }
}