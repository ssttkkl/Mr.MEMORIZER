package me.ssttkkl.mrmemorizer.ui.editnote

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.databinding.FragmentEditNoteBinding

class EditNoteFragment : Fragment() {

    companion object {
        const val KEY_MODE = "mode"
        const val MODE_NEW = "new"
        const val MODE_EDIT = "edit"
        const val KEY_NOTE_TYPE = "noteType"
        const val KEY_NOTE_ID = "noteId"
    }

    private lateinit var binding: FragmentEditNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditNoteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = ViewModelProvider(this)[EditNoteViewModel::class.java].apply {
            when (requireArguments().getString(KEY_MODE)) {
                MODE_NEW -> initializeForNewNote(requireArguments().getParcelable(KEY_NOTE_TYPE)!!)
                MODE_EDIT -> initializeForEditNote(requireArguments().getInt(KEY_NOTE_ID))
            }
        }
        return binding.root
    }

    private val categoryAdapter = lazy {
        ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.let {
            (activity as AppCompatActivity).setSupportActionBar(it)
            setHasOptionsMenu(true)
        }
        binding.editTextCategory.setAdapter(categoryAdapter.value)

        binding.viewModel?.apply {
            allCategories.observe(viewLifecycleOwner, Observer {
                categoryAdapter.value.apply {
                    clear()
                    addAll(it.map { it.name })
                    notifyDataSetChanged()
                }
            })
            showPreviewViewEvent.observe(viewLifecycleOwner, Observer { showPreviewView() })
            finishEvent.observe(viewLifecycleOwner, Observer { finish() })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_edit_note, menu)

        binding.viewModel?.previewVisible?.observe(viewLifecycleOwner, Observer {
            menu.findItem(R.id.menu_item_preview).isVisible = it
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_item_save -> binding.viewModel?.onClickSave()
            R.id.menu_item_preview -> binding.viewModel?.onClickPreview()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun showPreviewView() {

    }

    private fun finish() {
        findNavController().navigateUp()
    }
}