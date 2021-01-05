package me.ssttkkl.mrmemorizer.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.ssttkkl.mrmemorizer.MyApp
import me.ssttkkl.mrmemorizer.data.converter.NoteTypeConverter
import me.ssttkkl.mrmemorizer.data.converter.OffsetDateTimeConverter
import me.ssttkkl.mrmemorizer.data.dao.CategoryDao
import me.ssttkkl.mrmemorizer.data.dao.NoteDao
import me.ssttkkl.mrmemorizer.data.entity.Category
import me.ssttkkl.mrmemorizer.data.entity.Note

@TypeConverters(OffsetDateTimeConverter::class, NoteTypeConverter::class)
@Database(entities = [Note::class, Category::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract val noteDao: NoteDao
    abstract val categoryDao: CategoryDao

    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getInstance(): AppDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            MyApp.context,
                            AppDatabase::class.java, "app_db"
                        ).build()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}