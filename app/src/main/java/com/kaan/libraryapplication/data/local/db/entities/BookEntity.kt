package com.kaan.libraryapplication.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Book entity representing a book in the library
 * Contains book details, rating, and review count
 */
@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val bookId: String,
    val title: String,
    val author: String,
    val isbn: String,
    val category: String,
    val description: String,
    val publisher: String? = null,
    val publishedYear: Int,
    val pageCount: Int? = null,
    val language: String = "en",
    val coverImageUrl: String? = null,
    val averageRating: Float = 0f,
    val totalReviews: Int = 0,
    val availableCopies: Int = 1,
    val totalCopies: Int = 1,
    val addedAt: Long,
    val updatedAt: Long = addedAt
)
