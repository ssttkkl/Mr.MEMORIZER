package me.ssttkkl.mrmemorizer.ui.utils

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

object SpinnerBindingAdapter {
    @BindingAdapter("onItemSelected")
    @JvmStatic
    fun setOnSelectedItemChanged(spinner: Spinner, listener: InverseBindingListener) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                listener.onChange()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                listener.onChange()
            }
        }
    }

    @BindingAdapter("selectedPosition")
    @JvmStatic
    fun setSelectedPosition(spinner: Spinner, position: Int) = spinner.setSelection(position)

    @InverseBindingAdapter(attribute = "selectedPosition", event = "onItemSelected")
    @JvmStatic
    fun getSelectedPosition(spinner: Spinner): Int = spinner.selectedItemPosition
}