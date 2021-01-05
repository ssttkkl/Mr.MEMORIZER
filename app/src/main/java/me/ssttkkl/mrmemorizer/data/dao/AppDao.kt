package me.ssttkkl.mrmemorizer.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import me.ssttkkl.mrmemorizer.data.entity.Category
import me.ssttkkl.mrmemorizer.data.entity.Note

@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertNoteSync(vararg notes: Note)

    @Update
    fun updateNoteSync(vararg notes: Note)

    @Query("DELETE FROM note WHERE note_id IN (:noteId)")
    fun deleteNoteSync(vararg noteId: Int)

    @Query("SELECT * FROM note")
    fun getAllNotes(): DataSource.Factory<Int, Note>

    @Query("SELECT * FROM note WHERE title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%'")
    fun getNotesWithKeyword(keyword: String): DataSource.Factory<Int, Note>

    @Query("SELECT * FROM note WHERE category_id = :categoryId")
    fun getNotesWithCategory(categoryId: Int): DataSource.Factory<Int, Note>

    @Query("SELECT * FROM note WHERE category_id = :categoryId AND title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%'")
    fun getNotesWithKeywordAndCategory(
        keyword: String,
        categoryId: Int
    ): DataSource.Factory<Int, Note>

    fun loadNotes(keyword: String, categoryId: Int) = when {
        keyword.isNotEmpty() && categoryId != 0 -> getNotesWithKeywordAndCategory(
            keyword,
            categoryId
        )
        keyword.isNotEmpty() -> getNotesWithKeyword(keyword)
        categoryId != 0 -> getNotesWithCategory(categoryId)
        else -> getAllNotes()
    }

    @Query("SELECT * FROM note WHERE note_id = :noteId")
    fun getNoteByIdSync(noteId: Int): Note?

    @Query("SELECT * FROM note WHERE note_id = :noteId")
    fun getNoteById(noteId: Int): LiveData<Note>

    @Query("SELECT * FROM note WHERE stage <= :maxStage AND next_notify_time <= :timestamp ORDER BY next_notify_time")
    fun getNotesReadyToReview(maxStage: Int, timestamp: Long): DataSource.Factory<Int, Note>

    @Query("SELECT * FROM note WHERE stage <= :maxStage AND next_notify_time > :timestamp ORDER BY next_notify_time LIMIT 1")
    fun getNoteNextReview(maxStage: Int, timestamp: Long): LiveData<Note>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertCategorySync(vararg category: Category)

    @Query("DELETE FROM category WHERE category_id IN (:categoryId)")
    fun deleteCategorySync(vararg categoryId: Int)

    @Transaction
    fun autoDeleteCategory(vararg categoryId: Int) {
        if (countNotesWithCategorySync(*categoryId) == 0)
            deleteCategorySync(*categoryId)
    }

    @Query("SELECT * FROM category WHERE category_id = :categoryId")
    fun getCategoryById(categoryId: Int): LiveData<Category>

    @Query("SELECT * FROM category WHERE category_id = :categoryId")
    fun getCategoryByIdSync(categoryId: Int): Category?

    @Query("SELECT * FROM category WHERE name = :name LIMIT 1")
    fun getCategoryByNameSync(name: String): Category?

    @Query("SELECT * FROM category")
    fun getAllCategories(): LiveData<List<Category>>

    @Query("SELECT * FROM note WHERE category_id IN (:categoryId)")
    fun getNotesWithCategory(vararg categoryId: Int): LiveData<List<Note>>

    @Query("SELECT COUNT(category_id) FROM note WHERE category_id IN (:categoryId)")
    fun countNotesWithCategorySync(vararg categoryId: Int): Int
}