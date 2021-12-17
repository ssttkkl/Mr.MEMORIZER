package me.ssttkkl.mrmemorizer.ui.viewnote

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import dev.bandb.graphview.AbstractGraphAdapter
import dev.bandb.graphview.graph.Graph
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.ui.viewnote.GraphAdapter.ViewHolder

class GraphAdapter(val lifecycleOwner: LifecycleOwner) : AbstractGraphAdapter<ViewHolder>() {

    private val observer = Observer<Graph?> {
        submitGraph(it)
        notifyDataSetChanged()
    }

    var data: LiveData<Graph?>? = null
        set(value) {
            field?.removeObserver(observer)
            field = value
            field?.observe(lifecycleOwner, observer)
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = getNodeData(position).toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_graph_node, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text)
    }
}