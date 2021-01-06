package me.ssttkkl.mrmemorizer.ui.settings.editinterval

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.ssttkkl.mrmemorizer.R
import me.ssttkkl.mrmemorizer.databinding.ItemIntervalBinding
import me.ssttkkl.mrmemorizer.databinding.ItemIntervalEditBinding

private val differ = object : DiffUtil.ItemCallback<Pair<Int, Boolean>>() {
    override fun areContentsTheSame(oldItem: Pair<Int, Boolean>, newItem: Pair<Int, Boolean>) =
        oldItem == newItem

    override fun areItemsTheSame(oldItem: Pair<Int, Boolean>, newItem: Pair<Int, Boolean>) =
        oldItem == newItem
}

private const val VIEW_TYPE_NORMAL = 0
private const val VIEW_TYPE_EDIT = 1

class ReviewIntervalListAdapter(
    val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    val viewModel: EditIntervalViewModel
) : ListAdapter<Pair<Int, Boolean>, ReviewIntervalListAdapter.MyViewHolder>(differ) {

    init {
        viewModel.reviewInterval.observe(lifecycleOwner) {
            submitList(it)
        }
    }

    override fun getItemViewType(position: Int) =
        if (getItem(position).second)
            VIEW_TYPE_EDIT
        else
            VIEW_TYPE_NORMAL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        when (viewType) {
            VIEW_TYPE_NORMAL -> NormalViewHolder(
                ItemIntervalBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_EDIT -> EditViewHolder(
                ItemIntervalEditBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )
            else -> error("illegal viewType: $viewType")
        }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = holder.bind(position)

    abstract inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(position: Int)
    }

    inner class NormalViewHolder(val binding: ItemIntervalBinding) :
        MyViewHolder(binding.root) {
        override fun bind(position: Int) {
            binding.lifecycleOwner = lifecycleOwner
            binding.viewModel = viewModel
            binding.position = position

            val interval = getItem(position).first
            binding.text = when {
                interval in 0 until 60 -> context.getString(
                    R.string.text_next_review_time_value_at_second,
                    interval.toDouble()
                )
                interval / 60 in 0 until 60 -> context.getString(
                    R.string.text_next_review_time_value_at_minute,
                    interval / 60.0
                )
                interval / 3600 in 0 until 24 -> context.getString(
                    R.string.text_next_review_time_value_at_hour,
                    interval / 3600.0
                )
                else -> context.getString(
                    R.string.text_next_review_time_value_at_day,
                    interval / 86400.0
                )
            }
        }
    }

    inner class EditViewHolder(val binding: ItemIntervalEditBinding) :
        MyViewHolder(binding.root) {
        override fun bind(position: Int) {
            binding.lifecycleOwner = lifecycleOwner
            binding.viewModel = viewModel
            binding.position = position

            val interval = getItem(position).first
            when {
                interval in 0 until 60 -> {
                    binding.text = interval.toString()
                    binding.unitOrdinal = TimeUnit.Second.ordinal
                }
                interval / 60 in 0 until 60 -> {
                    binding.text = (interval / 60.0).toString()
                    binding.unitOrdinal = TimeUnit.Minute.ordinal
                }
                interval / 3600 in 0 until 24 -> {
                    binding.text = (interval / 3600.0).toString()
                    binding.unitOrdinal = TimeUnit.Hour.ordinal
                }
                else -> {
                    binding.text = (interval / 86400.0).toString()
                    binding.unitOrdinal = TimeUnit.Day.ordinal
                }
            }
        }
    }
}