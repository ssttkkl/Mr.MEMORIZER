package me.ssttkkl.mrmemorizer.data

import androidx.room.TypeConverter
import java.time.Instant
import java.time.OffsetDateTime

class OffsetDateTimeConverter {
    @TypeConverter
    fun dateTimeToTimestamp(dateTime: OffsetDateTime): Long = dateTime.toInstant().toEpochMilli()

    @TypeConverter
    fun timestampToDatetime(timestamp: Long): OffsetDateTime {
        val offset = OffsetDateTime.now().offset
        return Instant.ofEpochMilli(timestamp).atOffset(offset)
    }
}