package me.ssttkkl.mrmemorizer.ui.notelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.databinding.ItemNoteListBinding

private val noteItemCallback = object : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean =
        oldItem.noteId == newItem.noteId

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem == newItem
}

class NoteRecyclerViewAdapter(
    val lifecycleOwner: LifecycleOwner,
    private val viewModel: NoteListViewModel
) : PagedListAdapter<Note, NoteRecyclerViewAdapter.ViewHolder>(noteItemCallback) {

    private val observer = Observer<PagedList<Note>> { submitList(it) }

    var data: LiveData<PagedList<Note>>? = null
        set(value) {
            field?.removeObserver(observer)
            value?.observe(lifecycleOwner, observer)
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemNoteListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemNoteListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.lifecycleOwner = lifecycleOwner
            binding.viewModel = viewModel
        }

        fun bind(note: Note?) {
            binding.note = note
            binding.executePendingBindings()
        }
    }
}