package me.ssttkkl.mrmemorizer.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    val categoryId: Int,
    val name: String
) : Parcelable {
    constructor(name: String) : this(0, name)
}