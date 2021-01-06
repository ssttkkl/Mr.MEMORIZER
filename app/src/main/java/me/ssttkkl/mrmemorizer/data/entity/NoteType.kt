package me.ssttkkl.mrmemorizer.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class NoteType : Parcelable {
    Text, Map
}