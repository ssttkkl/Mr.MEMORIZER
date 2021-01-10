package me.ssttkkl.mrmemorizer.ui.viewnote;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.jetbrains.annotations.NotNull;

import de.blox.graphview.GraphAdapter;
import de.blox.graphview.tree.BuchheimWalkerAlgorithm;
import de.blox.graphview.tree.BuchheimWalkerConfiguration;
import kotlin.Unit;
import me.ssttkkl.mrmemorizer.R;
import me.ssttkkl.mrmemorizer.data.entity.Note;
import me.ssttkkl.mrmemorizer.data.entity.Tree;
import me.ssttkkl.mrmemorizer.databinding.FragmentViewMapNoteBinding;

public class ViewMapNoteFragment extends Fragment {
    private static final String TAG = "ViewMapNoteFragment";
    private FragmentViewMapNoteBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        binding = FragmentViewMapNoteBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        ViewNoteViewModel viewModel = new ViewModelProvider(this).get(ViewNoteViewModel.class);
        viewModel.initialize(requireArguments().getInt("noteId"));
        binding.setViewModel(viewModel);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        setHasOptionsMenu(true);

        binding.getViewModel().getNote().observe(getViewLifecycleOwner(), new Observer<Note>() {
            @Override
            public void onChanged(Note note) {
                showGraph();
            }
        });
        binding.getViewModel().getShowEditNoteViewEvent().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer noteId) {
                showEditNoteView(noteId);
            }
        });
        binding.getViewModel().getFinishEvent().observe(getViewLifecycleOwner(), new Observer<Unit>() {
            @Override
            public void onChanged(Unit unit) {
                finish();
            }
        });
        binding.getViewModel().getShowDoReviewViewEvent().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer noteId) {
                showDoReviewView(noteId);
            }
        });

        GraphAdapter adapter = new MapGraphAdapter();
        binding.graph.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_view_note, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        } else if (itemId == R.id.menu_item_remove) {
            binding.getViewModel().onClickRemove();
        } else if (itemId == R.id.menu_item_edit) {
            binding.getViewModel().onClickEdit();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showGraph() {
        Tree tree = binding.getViewModel().getNote().getValue().getTree().getValue();
        binding.graph.getAdapter().setGraph(tree.convertToGraph());

        final BuchheimWalkerConfiguration configuration = new BuchheimWalkerConfiguration.Builder()
                .setSiblingSeparation(100)
                .setLevelSeparation(300)
                .setSubtreeSeparation(300)
                .setOrientation(BuchheimWalkerConfiguration.ORIENTATION_LEFT_RIGHT)
                .build();
        binding.graph.setLayout(new BuchheimWalkerAlgorithm(configuration));
    }

    private void showEditNoteView(int noteId) {
        NavController navController = Navigation.findNavController(getView());
        Bundle bundle = new Bundle();
        bundle.putString("mode", "edit");
        bundle.putInt("noteId", noteId);
        navController.navigate(R.id.navigation_edit_note, bundle);
    }

    private void showDoReviewView(int noteId) {
        ReviewNoteDialogFragment.newInstance(noteId).show(getChildFragmentManager(), null);
    }

    private void finish() {
        Navigation.findNavController(getView()).navigateUp();
    }
}