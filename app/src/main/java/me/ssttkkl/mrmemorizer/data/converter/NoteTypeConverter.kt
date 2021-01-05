package me.ssttkkl.mrmemorizer.data.converter

import androidx.room.TypeConverter
import me.ssttkkl.mrmemorizer.data.entity.NoteType

class NoteTypeConverter {
    @TypeConverter
    fun noteTypeToInt(noteType: NoteType): Int = noteType.ordinal

    @TypeConverter
    fun intToNoteType(ordinal: Int): NoteType = NoteType.values()[ordinal]
}