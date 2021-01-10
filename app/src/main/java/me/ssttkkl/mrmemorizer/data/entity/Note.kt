package me.ssttkkl.mrmemorizer.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.Gson
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.OffsetDateTime
import kotlin.math.ceil
import kotlin.math.min

@Parcelize
@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "note_id") val noteId: Int = 0,
    @ColumnInfo(name = "note_type") val noteType: NoteType,
    val title: String,
    val content: String,
    @ColumnInfo(name = "category_id") val categoryId: Int,
    @ColumnInfo(name = "create_time") val createTime: OffsetDateTime,
    @ColumnInfo(name = "repetition_number") val repetitionNumber: Int = 0,
    @ColumnInfo(name = "easiness_factor") val easinessFactor: Double = 2.5,
    @ColumnInfo(name = "review_interval_days") val reviewIntervalDays: Long = 1,
    @ColumnInfo(name = "next_review_date") val nextReviewDate: LocalDate
) : Parcelable {

    @Ignore
    @IgnoredOnParcel
    val tree = lazy {
        if (noteType == NoteType.Map)
            Gson().fromJson(content, Tree::class.java)
        else
            throw error("noteType != NoteType.Map")
    }

    @Ignore
    @IgnoredOnParcel
    private val treeText = lazy {
        tree.value.toDisplayText()
    }

    val displayText
        get() = if (noteType == NoteType.Text)
            content
        else
            treeText.value

    // REF: https://en.wikipedia.org/wiki/SuperMemo#Description_of_SM-2_algorithm
    fun sm2(userGrade: Int): Note {
        if (userGrade !in 0..5)
            error("userGrade must be in 0..5")

        var newRepetitionNumber = 0
        var newReviewInterval = 1L
        var newEasinessFactor = easinessFactor

        if (userGrade >= 3) {
            newRepetitionNumber = repetitionNumber + 1
            newReviewInterval = when (repetitionNumber) {
                0 -> 1
                1 -> 6
                else -> ceil(reviewIntervalDays * easinessFactor).toLong()
            }
            newEasinessFactor = min(
                1.3,
                easinessFactor + (0.1 - (5 - userGrade) * (0.08 + (5 - userGrade) * 0.02))
            )
        }

        return this.copy(
            repetitionNumber = newRepetitionNumber,
            reviewIntervalDays = newReviewInterval,
            easinessFactor = newEasinessFactor,
            nextReviewDate = nextReviewDate.plusDays(newReviewInterval)
        )
    }

    companion object {
        fun newNote(
            noteType: NoteType,
            title: String,
            content: String,
            categoryId: Int
        ): Note {
            val now = OffsetDateTime.now()
            return Note(
                noteType = noteType,
                title = title,
                content = content,
                categoryId = categoryId,
                createTime = now,
                nextReviewDate = now.toLocalDate().plusDays(1)
            )
        }
    }
}
