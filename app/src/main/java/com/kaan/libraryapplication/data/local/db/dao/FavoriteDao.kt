package com.kaan.libraryapplication.data.local.db.dao

import androidx.room.*
import com.kaan.libraryapplication.data.local.db.entities.BookEntity
import com.kaan.libraryapplication.data.local.db.entities.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND bookId = :bookId")
    suspend fun deleteFavorite(userId: String, bookId: String)

    @Query("DELETE FROM favorites WHERE userId = :userId")
    suspend fun deleteAllUserFavorites(userId: String)

    @Query("DELETE FROM favorites WHERE bookId = :bookId")
    suspend fun deleteAllBookFavorites(bookId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND bookId = :bookId)")
    suspend fun isFavorite(userId: String, bookId: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND bookId = :bookId)")
    fun isFavoriteFlow(userId: String, bookId: String): Flow<Boolean>

    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY addedAt DESC")
    fun getUserFavoritesFlow(userId: String): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY addedAt DESC")
    suspend fun getUserFavorites(userId: String): List<FavoriteEntity>

    @Query("""
        SELECT books.* FROM books
        INNER JOIN favorites ON books.bookId = favorites.bookId
        WHERE favorites.userId = :userId
        ORDER BY favorites.addedAt DESC
    """)
    fun getUserFavoriteBooksFlow(userId: String): Flow<List<BookEntity>>

    @Query("""
        SELECT books.* FROM books
        INNER JOIN favorites ON books.bookId = favorites.bookId
        WHERE favorites.userId = :userId
        ORDER BY favorites.addedAt DESC
    """)
    suspend fun getUserFavoriteBooks(userId: String): List<BookEntity>

    @Query("""
        SELECT books.* FROM books
        INNER JOIN favorites ON books.bookId = favorites.bookId
        WHERE favorites.userId = :userId AND books.category = :category
        ORDER BY favorites.addedAt DESC
    """)
    fun getUserFavoriteBooksByCategoryFlow(userId: String, category: String): Flow<List<BookEntity>>

    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId")
    suspend fun getUserFavoriteCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId")
    fun getUserFavoriteCountFlow(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM favorites WHERE bookId = :bookId")
    suspend fun getBookFavoriteCount(bookId: String): Int

    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()
}
