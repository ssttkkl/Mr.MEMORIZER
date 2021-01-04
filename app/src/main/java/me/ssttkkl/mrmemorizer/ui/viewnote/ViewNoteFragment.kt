package me.ssttkkl.mrmemorizer.ui.viewnote

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.databinding.FragmentViewNoteBinding

class ViewNoteFragment : Fragment() {

    private lateinit var binding: FragmentViewNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewNoteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = ViewModelProvider(this)[ViewNoteViewModel::class.java].apply {
            initialize(arguments?.getLong("noteId")!!)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)

        binding.viewModel?.apply {
            showEditNoteViewEvent.observe(this@ViewNoteFragment, Observer { showEditNoteView() })
            finishEvent.observe(this@ViewNoteFragment, Observer { finish() })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_view_note, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_item_remove -> binding.viewModel?.onClickRemove()
            R.id.menu_item_edit -> binding.viewModel?.onClickEdit()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun showEditNoteView() {
        findNavController().navigate(
            R.id.navigation_edit_note,
            bundleOf("note" to binding.viewModel!!.note.value)
        )
    }

    private fun finish() {
        findNavController().navigateUp()
    }
}