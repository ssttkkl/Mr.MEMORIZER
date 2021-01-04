package me.ssttkkl.mrmemorizer.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import me.ssttkkl.mrmemorizer.MyApp
import me.ssttkkl.mrmemorizer.data.dao.NoteDao
import me.ssttkkl.mrmemorizer.data.entity.Note

@Database(entities = [Note::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract val noteDao: NoteDao

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