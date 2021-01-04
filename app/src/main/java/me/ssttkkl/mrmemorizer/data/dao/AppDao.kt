package me.ssttkkl.mrmemorizer.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import me.ssttkkl.mrmemorizer.data.entity.Category
import me.ssttkkl.mrmemorizer.data.entity.Note

@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertNote(vararg notes: Note)

    @Update
    fun updateNote(vararg notes: Note)

    @Query("DELETE FROM note WHERE note_id IN (:noteId)")
    fun deleteNote(vararg noteId: Long)

    @Query("SELECT * FROM note")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%'")
    fun getNotesWithKeyword(keyword: String): LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE note_id = :noteId")
    fun getNoteByIdSync(noteId: Long): Note?

    @Query("SELECT * FROM note WHERE note_id = :noteId")
    fun getNoteById(noteId: Long): LiveData<Note>

    @Query("SELECT * FROM note WHERE stage <= :maxStage AND next_notify_time <= :timestamp ORDER BY next_notify_time")
    fun getNotesReadyToReview(maxStage: Int, timestamp: Long): LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE stage <= :maxStage AND next_notify_time > :timestamp ORDER BY next_notify_time LIMIT 1")
    fun getNoteNextReview(maxStage: Int, timestamp: Long): LiveData<Note>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertCategory(vararg category: Category)

    @Update
    fun updateCategory(vararg category: Category)

    @Query("DELETE FROM category WHERE category_id IN (:categoryId)")
    fun deleteCategory(vararg categoryId: Long)

    @Transaction
    fun autoDeleteCategory(vararg categoryId: Long) {
        if (countNotesWithCategory(*categoryId) == 0L)
            deleteCategory(*categoryId)
    }

    @Query("SELECT * FROM category WHERE category_id = :categoryId")
    fun getCategoryById(categoryId: Long): LiveData<Category>

    @Query("SELECT * FROM category WHERE category_id = :categoryId")
    fun getCategoryByIdSync(categoryId: Long): Category?

    @Query("SELECT * FROM category WHERE name = :name LIMIT 1")
    fun getCategoryByNameSync(name: String): Category?

    @Query("SELECT * FROM category")
    fun getAllCategories(): LiveData<List<Category>>

    @Query("SELECT * FROM note WHERE category_id IN (:categoryId)")
    fun getNotesWithCategory(vararg categoryId: Long): LiveData<List<Note>>

    @Query("SELECT COUNT(category_id) FROM note WHERE category_id IN (:categoryId)")
    fun countNotesWithCategory(vararg categoryId: Long): Long
}