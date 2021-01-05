package me.ssttkkl.mrmemorizer.data.converter

import androidx.room.TypeConverter
import java.time.Instant
import java.time.OffsetDateTime

class OffsetDateTimeConverter {
    @TypeConverter
    fun dateTimeToTimestamp(dateTime: OffsetDateTime): Long = dateTime.toEpochSecond()

    @TypeConverter
    fun timestampToDatetime(timestamp: Long): OffsetDateTime {
        val offset = OffsetDateTime.now().offset
        return Instant.ofEpochSecond(timestamp).atOffset(offset)
    }
}