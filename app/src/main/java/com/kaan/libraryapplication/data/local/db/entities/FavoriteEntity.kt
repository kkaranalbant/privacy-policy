package com.kaan.libraryapplication.data.local.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Favorite entity - Junction table for many-to-many relationship
 * between Users and Books
 *
 * Represents a user's favorite books
 */
@Entity(
    tableName = "favorites",
    primaryKeys = ["userId", "bookId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["bookId"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["bookId"])
    ]
)
data class FavoriteEntity(
    val userId: String,
    val bookId: String,
    val addedAt: Long
)
