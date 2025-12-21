package com.kaan.libraryapplication.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User entity representing a user in the library system
 * Can be either STUDENT or ADMIN
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val username: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole,
    val fullName: String? = null,
    val profileImageUrl: String? = null,
    val createdAt: Long,
    val updatedAt: Long = createdAt
)

enum class UserRole {
    STUDENT,
    ADMIN
}
