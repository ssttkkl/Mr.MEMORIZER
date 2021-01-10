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

import com.google.gson.Gson;

import de.blox.graphview.GraphAdapter;
import de.blox.graphview.tree.BuchheimWalkerAlgorithm;
import de.blox.graphview.tree.BuchheimWalkerConfiguration;
import kotlin.Unit;
import me.ssttkkl.mrmemorizer.R;
import me.ssttkkl.mrmemorizer.data.entity.Tree;
import me.ssttkkl.mrmemorizer.databinding.FragmentViewMapNoteBinding;

public class ViewMapNoteFragment extends Fragment {

    private FragmentViewMapNoteBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        binding = FragmentViewMapNoteBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        ViewNoteViewModel viewModel = new ViewModelProvider(getActivity()).get(ViewNoteViewModel.class);
        viewModel.initialize(requireArguments().getInt("noteId"));
        binding.setViewModel(viewModel);

        initView();

        return binding.getRoot();
    }

    private void initView() {
        String s = binding.getViewModel().getNote().getValue().getContent();
        Gson gson = new Gson();
        Tree tree = gson.fromJson(s, Tree.class);
        GraphAdapter adapter = new MapGraphAdapter(tree.convertToGraph());
//        String test = "aaa\n\tbbb\n\tccc\n\tddd\n\t\teee";
//        GraphAdapter adapter = new MapGraphAdapter(new Tree(test).convertToGraph());
        binding.graph.setAdapter(adapter);

        final BuchheimWalkerConfiguration configuration = new BuchheimWalkerConfiguration.Builder()
                .setSiblingSeparation(100)
                .setLevelSeparation(300)
                .setSubtreeSeparation(300)
                .setOrientation(BuchheimWalkerConfiguration.ORIENTATION_TOP_BOTTOM)
                .build();
        binding.graph.setLayout(new BuchheimWalkerAlgorithm(configuration));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.toolbar);
        setHasOptionsMenu(true);

        binding.getViewModel().getShowEditNoteViewEvent().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                showEditNoteView();
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
            public void onChanged(Integer integer) {
                showEditNoteView();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_view_note, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_item_remove:
                binding.getViewModel().onClickRemove();
                break;
            case R.id.menu_item_edit:
                binding.getViewModel().onClickEdit();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showEditNoteView() {
        NavController navController = Navigation.findNavController(getView());
        Bundle bundle = new Bundle();
        bundle.putString("mode","edit");
        bundle.putInt("noteId",binding.getViewModel().getNoteId().getValue());
        navController.navigate(R.id.navigation_edit_note,bundle);
    }

    private void showDoReviewView(int noteId) {
        ReviewNoteDialogFragment.Companion.newInstance(noteId).show(getChildFragmentManager(), null);
    }

    private void finish() {
        Navigation.findNavController(getView()).navigateUp();
    }
}