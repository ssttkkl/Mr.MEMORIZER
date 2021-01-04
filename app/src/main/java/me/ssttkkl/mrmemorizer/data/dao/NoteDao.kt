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
    fun getAllNotes(): List<Note>

    @Query("SELECT * FROM note")
    fun getAllNotesAsLiveData(): LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE note_id = :noteId")
    fun getNoteById(noteId: Long): Note?

    @Query("SELECT * FROM note WHERE note_id = :noteId")
    fun getNoteByIdAsLiveData(noteId: Long): LiveData<Note>
}