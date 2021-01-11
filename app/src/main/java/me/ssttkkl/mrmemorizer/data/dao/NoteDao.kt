package me.ssttkkl.mrmemorizer.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import me.ssttkkl.mrmemorizer.data.entity.Note
import me.ssttkkl.mrmemorizer.data.entity.NoteType
import java.time.LocalDate
import java.time.ZoneId

@Dao
interface NoteDao {

    // 增删改

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertNoteSync(vararg notes: Note)

    @Update
    fun updateNoteSync(vararg notes: Note)

    @Query("DELETE FROM note WHERE note_id IN (:noteId)")
    fun deleteNoteSync(vararg noteId: Int)

    // 查询Note

    @Query("SELECT * FROM note WHERE note_id = :noteId")
    fun getNoteByIdSync(noteId: Int): Note?

    @Query("SELECT * FROM note WHERE note_id = :noteId")
    fun getNoteById(noteId: Int): LiveData<Note>

    @Query("SELECT * FROM note")
    fun getNotes(): DataSource.Factory<Int, Note>

    @Query("SELECT * FROM note WHERE note_type = :noteType")
    fun getNotesWithType(noteType: NoteType): DataSource.Factory<Int, Note>

    @Query("SELECT * FROM note WHERE title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%'")
    fun getNotesWithKeyword(keyword: String): DataSource.Factory<Int, Note>

    @Query("SELECT * FROM note WHERE category_id = :categoryId")
    fun getNotesWithCategory(categoryId: Int): DataSource.Factory<Int, Note>

    @Query("SELECT * FROM note WHERE note_type = :noteType AND title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%'")
    fun getNotesWithTypeAndKeyword(
        noteType: NoteType,
        keyword: String
    ): DataSource.Factory<Int, Note>

    @Query("SELECT * FROM note WHERE note_type = :noteType AND category_id = :categoryId")
    fun getNotesWithTypeAndCategory(
        noteType: NoteType,
        categoryId: Int
    ): DataSource.Factory<Int, Note>

    @Query("SELECT * FROM note WHERE category_id = :categoryId AND title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%'")
    fun getNotesWithKeywordAndCategory(
        keyword: String,
        categoryId: Int
    ): DataSource.Factory<Int, Note>

    @Query("SELECT * FROM note WHERE note_type = :noteType AND title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%' AND category_id = :categoryId")
    fun getNotesWithTypeAndKeywordAndCategory(
        noteType: NoteType,
        keyword: String,
        categoryId: Int
    ): DataSource.Factory<Int, Note>

    fun loadNotes(keyword: String, categoryId: Int) = when {
        keyword.isNotEmpty() && categoryId != 0 ->
            getNotesWithKeywordAndCategory(keyword, categoryId)
        keyword.isNotEmpty() ->
            getNotesWithKeyword(keyword)
        categoryId != 0 ->
            getNotesWithCategory(categoryId)
        else ->
            getNotes()
    }

    fun loadNotes(noteType: NoteType, keyword: String, categoryId: Int) = when {
        keyword.isNotEmpty() && categoryId != 0 ->
            getNotesWithTypeAndKeywordAndCategory(noteType, keyword, categoryId)
        keyword.isNotEmpty() ->
            getNotesWithTypeAndKeyword(noteType, keyword)
        categoryId != 0 ->
            getNotesWithTypeAndCategory(noteType, categoryId)
        else ->
            getNotesWithType(noteType)
    }

    @Query("SELECT * FROM note WHERE next_review_date = :epochDay")
    fun loadNotesNextReviewDateEq(epochDay: Long): DataSource.Factory<Int, Note>

    @Query("SELECT COUNT(note_id) FROM note WHERE next_review_date = :epochDay")
    fun countNotesNextReviewDateEqSync(epochDay: Long): Long

    @Query("SELECT COUNT(note_id) FROM note")
    fun countNotes(): LiveData<Long>

    @Query("SELECT COUNT(note_id) FROM note WHERE create_time BETWEEN :startTime AND :endTime")
    fun countNotesCreatedDuring(startTime: Long, endTime: Long): LiveData<Long>

    fun countNotesCreatedToday(): LiveData<Long> {
        val today = LocalDate.now()
        return countNotesCreatedDuring(
            today.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(),
            today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
        )
    }
}