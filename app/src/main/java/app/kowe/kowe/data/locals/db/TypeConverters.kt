package app.kowe.kowe.data.locals.db

import android.arch.persistence.room.TypeConverter
import java.util.*

class TypeConverters {

    @TypeConverter
    fun toDate(value: Long) = Date(value)

    @TypeConverter
    fun fromDate(date: Date) = date.time

}