package com.example.fundo.data.room

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class DateTypeConverters {
    private val formatter: SimpleDateFormat
        get() = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())

    @TypeConverter
    fun toDateTime(value: String?): Date? =
        value?.let {
            try {
                formatter.parse(it)
            } catch (exception: Exception) {
                null
            }
        }

    @TypeConverter
    fun fromDateTime(date: Date?): String? =
        date?.let { formatter.format(date) }

    companion object {
        private const val DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }
}