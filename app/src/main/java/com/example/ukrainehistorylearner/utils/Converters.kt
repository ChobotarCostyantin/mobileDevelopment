package com.example.ukrainehistorylearner.utils

import android.content.Context
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.ukrainehistorylearner.model.HistoricalPeriod

class Converters {
    @TypeConverter
    fun fromTagsList(tags: List<String>): String {
        return tags.joinToString(", ")
    }

    @TypeConverter
    fun toTagsList(data: String): List<String> {
        return if (data.isEmpty()) emptyList() else data.split(", ").map { it.trim() }
    }

    @TypeConverter
    fun fromPeriod(period: HistoricalPeriod): String {
        return period.name
    }

    @TypeConverter
    fun toPeriod(name: String): HistoricalPeriod {
        return HistoricalPeriod.valueOf(name)
    }
}