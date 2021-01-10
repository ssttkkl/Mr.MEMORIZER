package me.ssttkkl.mrmemorizer.ui.viewnote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import me.ssttkkl.mrmemorizer.databinding.FragmentReviewNoteBinding

class ReviewNoteDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentReviewNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewNoteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = ViewModelProvider(this)[ReviewNoteViewModel::class.java].apply {
            finishEvent.observe(viewLifecycleOwner, Observer { dialog?.dismiss() })
            initialize(requireArguments().getInt(KEY_NOTE_ID))
        }
        return binding.root
    }

    companion object {
        private const val KEY_NOTE_ID = "note_id"

        @JvmStatic
        fun newInstance(noteId: Int) = ReviewNoteDialogFragment().apply {
            arguments = bundleOf(KEY_NOTE_ID to noteId)
        }
    }
}