package me.ssttkkl.mrmemorizer.ui.editnote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import dev.bandb.graphview.layouts.tree.BuchheimWalkerConfiguration
import dev.bandb.graphview.layouts.tree.BuchheimWalkerLayoutManager
import dev.bandb.graphview.layouts.tree.TreeEdgeDecoration
import me.ssttkkl.mrmemorizer.data.entity.Tree
import me.ssttkkl.mrmemorizer.data.entity.convertToGraph
import me.ssttkkl.mrmemorizer.databinding.FragmentPreviewMapNoteBinding
import me.ssttkkl.mrmemorizer.ui.viewnote.GraphAdapter

class PreviewNoteFragment : Fragment() {

    private lateinit var binding: FragmentPreviewMapNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
    ): View {
        binding = FragmentPreviewMapNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)

        binding.graph.apply {
            val configuration = BuchheimWalkerConfiguration.Builder()
                .setSiblingSeparation(100)
                .setLevelSeparation(100)
                .setSubtreeSeparation(100)
                .setOrientation(BuchheimWalkerConfiguration.ORIENTATION_LEFT_RIGHT)
                .build()
            layoutManager = BuchheimWalkerLayoutManager(requireContext(), configuration)
            addItemDecoration(TreeEdgeDecoration())

            adapter = GraphAdapter(this@PreviewNoteFragment).apply {
                val tree = Tree.parseText(requireArguments().getString("noteContent")!!)
                submitGraph(tree.convertToGraph())
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun finish() {
        Navigation.findNavController(requireView()).navigateUp()
    }

    companion object {
        private const val TAG = "ViewMapNoteFragment"
    }
}