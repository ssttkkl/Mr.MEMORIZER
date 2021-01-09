package me.ssttkkl.mrmemorizer.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.databinding.ItemDashboardNoteReadyToReviewBinding

private val noteItemCallback = object : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean =
        oldItem.noteId == newItem.noteId

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem == newItem
}

class NoteRecyclerViewAdapter(
    val lifecycleOwner: LifecycleOwner,
    private val viewModel: DashboardViewModel
) : PagedListAdapter<Note, NoteRecyclerViewAdapter.ViewHolder>(noteItemCallback) {

    init {
        viewModel.notesTodayReview.observe(lifecycleOwner, Observer {
            submitList(it)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemDashboardNoteReadyToReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemDashboardNoteReadyToReviewBinding) :
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