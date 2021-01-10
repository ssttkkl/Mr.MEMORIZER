package me.ssttkkl.mrmemorizer.ui.viewnote;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import de.blox.graphview.Graph;
import de.blox.graphview.GraphAdapter;
import de.blox.graphview.GraphView;
import de.blox.graphview.Node;
import me.ssttkkl.mrmemorizer.R;

public class MapGraphAdapter extends GraphAdapter<MapGraphAdapter.SimpleViewHolder> {

    public MapGraphAdapter() {
        super(new Graph());
    }

    public MapGraphAdapter(Graph graph) {
        super(graph);
    }

    @Override
    public void onBindViewHolder(@NotNull SimpleViewHolder simpleViewHolder, @NotNull Object data, int i) {
        simpleViewHolder.textView.setText((String) ((Node) data).getData());
    }

    @NonNull
    @NotNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int i) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_graph_node, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public int getCount() {
        return getGraph().getNodeCount();
    }

    @Override
    public Object getItem(int i) {
        return getGraph().getNodeAtPosition(i);
    }

    @Override
    public boolean isEmpty() {
        return !getGraph().hasNodes();
    }

    static class SimpleViewHolder extends GraphView.ViewHolder {
        TextView textView;

        SimpleViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
        }
    }
}
