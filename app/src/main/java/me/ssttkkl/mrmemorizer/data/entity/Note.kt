package me.ssttkkl.mrmemorizer.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import me.ssttkkl.mrmemorizer.res.ReviewStage
import java.time.OffsetDateTime

@Parcelize
@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "note_id")
    val noteId: Int,
    val title: String,
    val content: String,
    @ColumnInfo(name = "category_id") val categoryId: Int,
    @ColumnInfo(name = "create_time") val createTime: OffsetDateTime,
    val stage: Int,
    @ColumnInfo(name = "next_notify_time") val nextNotifyTime: OffsetDateTime
) : Parcelable {
    constructor(title: String, content: String, categoryId: Int) : this(
        0,
        title,
        content,
        categoryId,
        OffsetDateTime.now(),
        0,
        OffsetDateTime.now().plusSeconds(ReviewStage.nextReviewDuration[0])
    )
}