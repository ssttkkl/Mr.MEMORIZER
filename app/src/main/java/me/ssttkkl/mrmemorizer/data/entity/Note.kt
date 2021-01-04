package me.ssttkkl.mrmemorizer.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import me.ssttkkl.mrmemorizer.res.ReviewStage
import java.util.*

@Parcelize
@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "note_id")
    val noteId: Long,
    val title: String,
    val content: String,
    @ColumnInfo(name = "create_timestamp") val createTimestamp: Long,
    val stage: Int,
    @ColumnInfo(name = "next_notify_timestamp") val nextNotifyTimestamp: Long
) : Parcelable {
    constructor(title: String, content: String) : this(
        0,
        title,
        content,
        Calendar.getInstance().timeInMillis / 1000,
        0,
        Calendar.getInstance().timeInMillis / 1000 + ReviewStage.nextReviewDuration[0]
    )
}