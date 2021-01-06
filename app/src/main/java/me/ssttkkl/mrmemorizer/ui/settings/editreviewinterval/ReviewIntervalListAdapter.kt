package me.ssttkkl.mrmemorizer.ui.settings.editreviewinterval

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.ssttkkl.mrmemorizer.R

private val differ = object : DiffUtil.ItemCallback<Int>() {
    override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean = oldItem == newItem
    override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean = oldItem == newItem
}

class ReviewIntervalListAdapter(
    val context: Context,
    lifecycleOwner: LifecycleOwner,
    val viewModel: EditReviewIntervalViewModel
) : ListAdapter<Int, ReviewIntervalListAdapter.ViewHolder>(differ) {

    private var selectedPosition = -1
        set(value) {
            val old = field
            field = value
            if (old in 0 until itemCount)
                notifyItemChanged(old)
            if (value in 0 until itemCount)
                notifyItemChanged(value)
        }

    init {
        viewModel.reviewInterval.observe(lifecycleOwner) {
            submitList(it)
        }
        viewModel.selectedPosition.observe(lifecycleOwner) {
            selectedPosition = it
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(context)
            .inflate(android.R.layout.simple_selectable_list_item, parent, false)
    )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(android.R.id.text1)?.apply {
            val item = getItem(position)
            text = when {
                item in 0 until 60 ->
                    context.getString(R.string.text_next_review_time_value_at_second, item)
                item / 60 in 0 until 60 ->
                    context.getString(R.string.text_next_review_time_value_at_minute, item / 60)
                item / 3600 in 0 until 24 ->
                    context.getString(R.string.text_next_review_time_value_at_hour, item / 3600)
                else ->
                    context.getString(R.string.text_next_review_time_value_at_day, item / 86400)
            }
            isSelected = selectedPosition == position
            setOnClickListener { viewModel.selectedPosition.value = position }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}