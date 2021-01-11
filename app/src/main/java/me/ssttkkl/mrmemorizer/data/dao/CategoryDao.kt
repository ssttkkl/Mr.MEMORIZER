package me.ssttkkl.mrmemorizer.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import me.ssttkkl.mrmemorizer.data.entity.Category

@Dao
interface CategoryDao {

    // 查询Category

    @Query("SELECT * FROM category WHERE category_id = :categoryId")
    fun getCategoryById(categoryId: Int): LiveData<Category>

    @Query("SELECT * FROM category WHERE category_id = :categoryId")
    fun getCategoryByIdSync(categoryId: Int): Category?

    @Query("SELECT * FROM category WHERE name = :name LIMIT 1")
    fun getCategoryByNameSync(name: String): Category?

    @Query("SELECT * FROM category")
    fun getAllCategories(): LiveData<List<Category>>

    @Query("SELECT COUNT(category_id) FROM category")
    fun countCategory(): LiveData<Long>

    @Query("SELECT COUNT(category_id) FROM note WHERE category_id IN (:categoryId)")
    fun countNotesWithCategorySync(vararg categoryId: Int): Int

    // 增删改

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertCategorySync(vararg category: Category)

    @Query("DELETE FROM category WHERE category_id IN (:categoryId)")
    fun deleteCategorySync(vararg categoryId: Int)

    /**
     * 自动回收Category
     * 当没有任何Note属于该Category时进行回收
     */
    @Transaction
    fun autoDeleteCategorySync(vararg categoryId: Int) {
        if (countNotesWithCategorySync(*categoryId) == 0)
            deleteCategorySync(*categoryId)
    }
}