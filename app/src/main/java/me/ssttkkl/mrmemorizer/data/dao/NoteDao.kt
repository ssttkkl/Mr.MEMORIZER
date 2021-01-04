package me.ssttkkl.mrmemorizer.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import me.ssttkkl.mrmemorizer.data.entity.Note

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertNote(vararg notes: Note)

    @Update
    fun updateNote(vararg notes: Note)

    @Query("DELETE FROM note WHERE note_id IN (:noteId)")
    fun deleteNote(vararg noteId: Long)

    @Query("SELECT * FROM note")
    fun loadAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%'")
    fun searchNotes(keyword: String): LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE note_id = :noteId")
    fun getNoteByIdRaw(noteId: Long): Note?

    @Query("SELECT * FROM note WHERE note_id = :noteId")
    fun getNoteById(noteId: Long): LiveData<Note>

    @Query("SELECT * FROM note WHERE stage <= :maxStage AND next_notify_time <= :timestamp ORDER BY next_notify_time")
    fun getNoteReadyToReview(maxStage: Int, timestamp: Long): LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE stage <= :maxStage AND next_notify_time > :timestamp ORDER BY next_notify_time LIMIT 1")
    fun getNoteNextReview(maxStage: Int, timestamp: Long): LiveData<Note>
}