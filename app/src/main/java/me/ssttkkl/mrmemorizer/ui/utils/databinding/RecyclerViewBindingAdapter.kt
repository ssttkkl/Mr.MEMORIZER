package me.ssttkkl.mrmemorizer.ui.utils.databinding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

object RecyclerViewBindingAdapter {
    @BindingAdapter("list")
    @JvmStatic
    fun <T> setList(recView: RecyclerView, list: List<T>?) {
        (recView.adapter as? ListAdapter<T, *>)?.submitList(list)
    }
}