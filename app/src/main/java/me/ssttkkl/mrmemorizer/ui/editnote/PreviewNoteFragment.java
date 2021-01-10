package me.ssttkkl.mrmemorizer.ui.editnote;

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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.jetbrains.annotations.NotNull;

import de.blox.graphview.Graph;
import de.blox.graphview.GraphAdapter;
import de.blox.graphview.GraphView;
import de.blox.graphview.tree.BuchheimWalkerAlgorithm;
import de.blox.graphview.tree.BuchheimWalkerConfiguration;
import kotlin.Unit;
import me.ssttkkl.mrmemorizer.R;
import me.ssttkkl.mrmemorizer.data.entity.Note;
import me.ssttkkl.mrmemorizer.data.entity.Tree;
import me.ssttkkl.mrmemorizer.databinding.FragmentViewMapNoteBinding;
import me.ssttkkl.mrmemorizer.ui.viewnote.MapGraphAdapter;
import me.ssttkkl.mrmemorizer.ui.viewnote.ReviewNoteDialogFragment;
import me.ssttkkl.mrmemorizer.ui.viewnote.ViewNoteViewModel;

public class PreviewNoteFragment extends Fragment {
    private static final String TAG = "ViewMapNoteFragment";
    private Toolbar toolbar;
    private GraphView graphView;
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview_map_note,null);
        toolbar = view.findViewById(R.id.toolbar);
        graphView = view.findViewById(R.id.graph);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        showGraph();
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        menu.clear();
//        inflater.inflate(R.menu.menu_view_note, menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    private void showGraph() {
        Tree tree = Tree.parseText(requireArguments().getString("noteContent"));
        GraphAdapter adapter = new MapGraphAdapter(tree.convertToGraph());
        graphView.setAdapter(adapter);
        final BuchheimWalkerConfiguration configuration = new BuchheimWalkerConfiguration.Builder()
                .setSiblingSeparation(100)
                .setLevelSeparation(300)
                .setSubtreeSeparation(300)
                .setOrientation(BuchheimWalkerConfiguration.ORIENTATION_LEFT_RIGHT)
                .build();
        graphView.setLayout(new BuchheimWalkerAlgorithm(configuration));
    }

    private void finish() {
        Navigation.findNavController(getView()).navigateUp();
    }

}
