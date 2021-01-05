package me.ssttkkl.mrmemorizer.ui.edittextnote

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.databinding.FragmentEditTextNoteBinding

class EditTextNoteFragment : Fragment() {

    private lateinit var binding: FragmentEditTextNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditTextNoteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = ViewModelProvider(this)[EditTextNoteViewModel::class.java].apply {
            when (requireArguments().getString("mode")) {
                "new" -> initializeForNewNote()
                "edit" -> initializeForEditNote(requireArguments().getInt("noteId"))
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
            finishEvent.observe(viewLifecycleOwner, Observer { finish() })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_edit_note, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_item_save -> binding.viewModel?.onClickSave()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun finish() {
        findNavController().navigateUp()
    }
}