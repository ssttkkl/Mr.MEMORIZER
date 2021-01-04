package me.ssttkkl.mrmemorizer.ui.notelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.databinding.FragmentNoteListBinding
import me.ssttkkl.mrmemorizer.ui.utils.CommonRecViewItemDecoration

class NoteListFragment : Fragment() {

    private lateinit var binding: FragmentNoteListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteListBinding.inflate(inflater, container, false)
        binding.viewModel = ViewModelProvider(this)[NoteListViewModel::class.java]
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        // setHasOptionsMenu(true)

        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(CommonRecViewItemDecoration(8, 8, 8))
            adapter = NoteRecyclerViewAdapter(this@NoteListFragment, binding.viewModel!!)
        }

        binding.viewModel?.apply {
            showViewNoteViewEvent.observe(this@NoteListFragment, Observer { showViewNoteView(it) })
        }
    }

    private fun showViewNoteView(note: Note) {
        findNavController().navigate(
            R.id.navigation_view_note,
            bundleOf("noteId" to note.noteId)
        )
    }
}