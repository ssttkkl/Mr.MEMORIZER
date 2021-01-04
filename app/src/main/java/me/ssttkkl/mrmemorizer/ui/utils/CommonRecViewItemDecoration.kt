package me.ssttkkl.mrmemorizer.ui.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CommonRecViewItemDecoration(
    val header: Int,
    val bottom: Int,
    val spliter: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val pos = parent.getChildAdapterPosition(view)
        if (pos == 0)
            outRect.top = header
        if (pos == parent.adapter!!.itemCount - 1)
            outRect.bottom = bottom
        if (pos > 0)
            outRect.top = spliter
    }
}