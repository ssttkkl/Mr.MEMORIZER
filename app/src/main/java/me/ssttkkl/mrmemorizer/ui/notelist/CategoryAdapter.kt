package me.ssttkkl.mrmemorizer.ui.notelist

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import me.ssttkkl.mrmemorizer.data.entity.Category

class CategoryAdapter(context: Context) : ArrayAdapter<Category>(
    context,
    android.R.layout.simple_spinner_dropdown_item,
    arrayListOf()
) {
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        return super.getView(position, convertView, parent).also {
            it.findViewById<TextView>(android.R.id.text1).text = getItem(position)?.name
        }
    }

    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        return super.getDropDownView(position, convertView, parent).also {
            it.findViewById<TextView>(android.R.id.text1).text = getItem(position)?.name
        }
    }
}