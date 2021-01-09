package me.ssttkkl.mrmemorizer.data.converter

import androidx.room.TypeConverter
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

class OffsetDateTimeConverter {
    @TypeConverter
    fun dateTimeToTimestamp(dateTime: OffsetDateTime): Long = dateTime.toEpochSecond()

    @TypeConverter
    fun timestampToDatetime(timestamp: Long): OffsetDateTime {
        val instant = Instant.ofEpochSecond(timestamp)
        val offset = ZoneId.systemDefault().rules.getOffset(instant)
        return instant.atOffset(offset)
    }
}