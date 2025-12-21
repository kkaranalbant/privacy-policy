package com.kaan.libraryapplication.data.local.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Review entity representing a user's review of a book
 * Has foreign keys to both User and Book
 * Rating is 1-5 stars
 */
@Entity(
    tableName = "reviews",
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
        Index(value = ["bookId"]),
        Index(value = ["userId", "bookId"])
    ]
)
data class ReviewEntity(
    @PrimaryKey
    val reviewId: String,
    val userId: String,
    val bookId: String,
    val rating: Int, // 1-5
    val comment: String,
    val createdAt: Long,
    val updatedAt: Long = createdAt
)
