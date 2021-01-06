package me.ssttkkl.mrmemorizer.ui.settings.editinterval

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.databinding.FragmentEditIntervalBinding

class EditIntervalFragment : Fragment() {

    private lateinit var binding: FragmentEditIntervalBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditIntervalBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = ViewModelProvider(this)[EditIntervalViewModel::class.java].apply {
            finishEvent.observe(viewLifecycleOwner, Observer { finish() })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        binding.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter =
                ReviewIntervalListAdapter(requireContext(), viewLifecycleOwner, binding.viewModel!!)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_edit_interval, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_save -> binding.viewModel?.onClickSave()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun finish() {
        findNavController().navigateUp()
    }
}