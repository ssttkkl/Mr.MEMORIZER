package me.ssttkkl.mrmemorizer.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.ssttkkl.mrmemorizer.data.entity.Note

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(vararg notes: Note)

    @Query("DELETE FROM note WHERE note_id = :noteId")
    fun deleteNote(noteId: Long)

    @Query("SELECT * FROM note")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE note_id = :noteId")
    fun getNoteById(noteId: Long): LiveData<Note>

    @Query("SELECT COUNT(note_id) FROM note WHERE next_notify_time <= :timestamp")
    fun countNoteNextNotifyTimeBefore(timestamp: Long): Long

    @Query("SELECT * FROM note WHERE next_notify_time <= :timestamp ORDER BY next_notify_time")
    fun getNoteNextNotifyTimeBefore(timestamp: Long): LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE next_notify_time > :timestamp ORDER BY next_notify_time LIMIT 1")
    fun getFirstNoteNextNotifyTimeAfter(timestamp: Long): LiveData<Note>
}