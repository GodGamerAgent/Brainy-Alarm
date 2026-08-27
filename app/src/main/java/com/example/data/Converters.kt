package com.example.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromIntList(list: List<Int>?): String {
        return list?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toIntList(data: String?): List<Int> {
        if (data.isNullOrEmpty()) return emptyList()
        return data.split(",").mapNotNull { it.trim().toIntOrNull() }
    }

    @TypeConverter
    fun fromChallengeList(list: List<ChallengeType>?): String {
        return list?.joinToString(",") { it.name } ?: ""
    }

    @TypeConverter
    fun toChallengeList(data: String?): List<ChallengeType> {
        if (data.isNullOrEmpty()) return emptyList()
        return data.split(",").mapNotNull { name ->
            try {
                ChallengeType.valueOf(name.trim())
            } catch (e: Exception) {
                null
            }
        }
    }
}
