package me.ssttkkl.mrmemorizer.ui.viewnote

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import dev.bandb.graphview.layouts.tree.BuchheimWalkerConfiguration
import dev.bandb.graphview.layouts.tree.BuchheimWalkerLayoutManager
import dev.bandb.graphview.layouts.tree.TreeEdgeDecoration
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.databinding.FragmentViewMapNoteBinding

class ViewMapNoteFragment : Fragment() {
    private lateinit var binding: FragmentViewMapNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
    ): View {
        binding = FragmentViewMapNoteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = ViewModelProvider(this).get(ViewNoteViewModel::class.java).apply {
            initialize(requireArguments().getInt("noteId"))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)

        binding.viewModel?.apply {
            showEditNoteViewEvent.observe(
                viewLifecycleOwner,
                Observer { noteId -> showEditNoteView(noteId) })
            finishEvent.observe(viewLifecycleOwner, Observer { finish() })
            showDoReviewViewEvent.observe(
                viewLifecycleOwner,
                Observer { noteId -> showDoReviewView(noteId) })
        }

        binding.graph.apply {
            val configuration = BuchheimWalkerConfiguration.Builder()
                .setSiblingSeparation(100)
                .setLevelSeparation(100)
                .setSubtreeSeparation(100)
                .setOrientation(BuchheimWalkerConfiguration.ORIENTATION_LEFT_RIGHT)
                .build()
            layoutManager = BuchheimWalkerLayoutManager(requireContext(), configuration)
            addItemDecoration(TreeEdgeDecoration())

            adapter = GraphAdapter(this@ViewMapNoteFragment).apply {
                data = binding.viewModel?.graph
            }
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

    private fun showEditNoteView(noteId: Int) {
        val navController = Navigation.findNavController(requireView())
        navController.navigate(
            R.id.navigation_edit_note,
            bundleOf("mode" to "edit", "noteId" to noteId)
        )
    }

    private fun showDoReviewView(noteId: Int) {
        ReviewNoteDialogFragment.newInstance(noteId).show(childFragmentManager, null)
    }

    private fun finish() {
        Navigation.findNavController(requireView()).navigateUp()
    }

    companion object {
        private const val TAG = "ViewMapNoteFragment"
    }
}