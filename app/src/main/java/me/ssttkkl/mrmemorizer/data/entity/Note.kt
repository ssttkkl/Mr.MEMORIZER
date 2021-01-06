package me.ssttkkl.mrmemorizer.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import me.ssttkkl.mrmemorizer.AppPreferences
import java.time.OffsetDateTime

@Parcelize
@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "note_id") val noteId: Int = 0,
    @ColumnInfo(name = "note_type") val noteType: NoteType,
    val title: String,
    val content: String,
    @ColumnInfo(name = "category_id") val categoryId: Int,
    @ColumnInfo(name = "create_time") val createTime: OffsetDateTime,
    val stage: Int,
    @ColumnInfo(name = "next_notify_time") val nextNotifyTime: OffsetDateTime
) : Parcelable {
    companion object {
        fun newNote(
            noteType: NoteType,
            title: String,
            content: String,
            categoryId: Int
        ): Note {
            val now = OffsetDateTime.now()
            return Note(
                noteId = 0,
                noteType = noteType,
                title = title,
                content = content,
                categoryId = categoryId,
                createTime = now,
                stage = 0,
                nextNotifyTime = now.plusSeconds(AppPreferences.reviewInterval[0].toLong())
            )
        }
    }
}
