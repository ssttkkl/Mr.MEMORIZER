package me.ssttkkl.mrmemorizer.ui.notelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.databinding.FragmentNoteListBinding

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

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.list.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = NoteRecyclerViewAdapter(viewLifecycleOwner, binding.viewModel!!)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                binding.viewModel?.searchQuery?.value = newText
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
        })

        binding.spinnerCategory.apply {
            adapter = CategoryAdapter(requireContext()).also { categoryAdapter = it }
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    binding.viewModel?.categoryFilter?.value = categoryAdapter.getItem(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    setSelection(0)
                }
            }
        }

        binding.viewModel?.apply {
            allCategories.observe(viewLifecycleOwner, Observer {
                categoryAdapter.apply {
                    clear()
                    addAll(it)
                    notifyDataSetChanged()
                }
            })
            showViewNoteViewEvent.observe(viewLifecycleOwner, Observer {
                showViewNoteView(it)
            })
        }
    }

    private fun showViewNoteView(note: Note) {
        findNavController().navigate(
            R.id.navigation_view_note,
            bundleOf("noteId" to note.noteId)
        )
    }
}