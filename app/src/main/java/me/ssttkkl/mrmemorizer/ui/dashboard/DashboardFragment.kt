package me.ssttkkl.mrmemorizer.ui.dashboard

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
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
        binding.toolbar.let {
            (activity as AppCompatActivity).setSupportActionBar(it)
            setHasOptionsMenu(true)
        }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> binding.viewModel?.onClickSettings()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
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
        when (note.noteType) {
            NoteType.Text -> findNavController().navigate(
                R.id.navigation_view_note,
                bundleOf("noteId" to note.noteId)
            )
            NoteType.Map -> findNavController().navigate(
                R.id.navigation_view_map_note,
                bundleOf("noteId" to note.noteId)
            )
        }
    }

    private fun showSettingsView() {
        findNavController().navigate(
            R.id.navigation_settings
        )
    }
}