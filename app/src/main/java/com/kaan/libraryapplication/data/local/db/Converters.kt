package com.kaan.libraryapplication.data.local.db

import androidx.room.TypeConverter
import com.kaan.libraryapplication.data.local.db.entities.UserRole

/**
 * Room Type Converters for custom types
 */
class Converters {

    @TypeConverter
    fun fromUserRole(value: UserRole): String {
        return value.name
    }

    @TypeConverter
    fun toUserRole(value: String): UserRole {
        return UserRole.valueOf(value)
    }
}
