package me.ssttkkl.mrmemorizer.data.converter

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateConverter {
    @TypeConverter
    fun dateToEpochDay(date: LocalDate): Long = date.toEpochDay()

    @TypeConverter
    fun epochDayToDate(epochDay: Long): LocalDate {
        return LocalDate.ofEpochDay(epochDay)
    }
}