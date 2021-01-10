package me.ssttkkl.mrmemorizer.ui.notelist

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.tabs.TabLayoutMediator
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.TOP_DEST
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.data.entity.NoteType
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
        setHasOptionsMenu(true)

        binding.toolbar.apply {
            (activity as? AppCompatActivity)?.setSupportActionBar(this)
            setupWithNavController(findNavController(), AppBarConfiguration(TOP_DEST))
        }

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

        binding.viewPager.adapter = NotePagerAdapter(viewLifecycleOwner, binding.viewModel!!)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = binding.viewModel?.tabName?.get(position)
        }.attach()

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_note_list, menu)

        (menu.findItem(R.id.app_bar_search).actionView as? SearchView)?.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String?): Boolean {
                    binding.viewModel?.searchQuery?.value = newText
                    return true
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }
            })
        }
    }

    private fun showViewNoteView(note: Note) {
        when(note.noteType){
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
}