package me.ssttkkl.mrmemorizer.ui.notelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.data.entity.Note

class NotePagerAdapter(
    val lifecycleOwner: LifecycleOwner,
    val viewModel: NoteListViewModel
) :
    RecyclerView.Adapter<NotePagerAdapter.ViewHolder>() {

    override fun getItemCount(): Int = viewModel.tabName.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.page_note_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel.tabData[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val recyclerView = view.findViewById<RecyclerView>(R.id.list).apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        private val adapter = NoteRecyclerViewAdapter(lifecycleOwner, viewModel).also {
            recyclerView.adapter = it
        }

        fun bind(data: LiveData<PagedList<Note>>) {
            adapter.data = data
        }
    }
}