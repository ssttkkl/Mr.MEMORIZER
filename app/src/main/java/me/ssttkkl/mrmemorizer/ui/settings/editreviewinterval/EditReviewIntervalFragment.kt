package me.ssttkkl.mrmemorizer.ui.settings.editreviewinterval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.ssttkkl.mrmemorizer.R

class EditReviewIntervalFragment : Fragment() {

    private lateinit var viewModel: EditReviewIntervalViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_review_interval, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[EditReviewIntervalViewModel::class.java]

        view.findViewById<RecyclerView>(R.id.list)?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ReviewIntervalListAdapter(requireContext(), viewLifecycleOwner, viewModel)
        }
    }
}