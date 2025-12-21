package com.kaan.libraryapplication.data.local.db.dao

import androidx.room.*
import com.kaan.libraryapplication.data.local.db.entities.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Update
    suspend fun updateBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("DELETE FROM books WHERE bookId = :bookId")
    suspend fun deleteBookById(bookId: String)

    @Query("SELECT * FROM books WHERE bookId = :bookId")
    suspend fun getBookById(bookId: String): BookEntity?

    @Query("SELECT * FROM books WHERE bookId = :bookId")
    fun getBookByIdFlow(bookId: String): Flow<BookEntity?>

    @Query("SELECT * FROM books WHERE isbn = :isbn LIMIT 1")
    suspend fun getBookByIsbn(isbn: String): BookEntity?

    @Query("SELECT * FROM books ORDER BY addedAt DESC")
    fun getAllBooksFlow(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books ORDER BY addedAt DESC")
    suspend fun getAllBooks(): List<BookEntity>

    @Query("SELECT * FROM books WHERE category = :category ORDER BY title ASC")
    fun getBooksByCategoryFlow(category: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE category = :category ORDER BY title ASC")
    suspend fun getBooksByCategory(category: String): List<BookEntity>

    @Query("SELECT * FROM books WHERE author = :author ORDER BY title ASC")
    fun getBooksByAuthorFlow(author: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE author = :author ORDER BY title ASC")
    suspend fun getBooksByAuthor(author: String): List<BookEntity>

    @Query("""
        SELECT * FROM books
        WHERE title LIKE '%' || :query || '%'
        OR author LIKE '%' || :query || '%'
        OR isbn LIKE '%' || :query || '%'
        ORDER BY title ASC
    """)
    fun searchBooksFlow(query: String): Flow<List<BookEntity>>

    @Query("""
        SELECT * FROM books
        WHERE title LIKE '%' || :query || '%'
        OR author LIKE '%' || :query || '%'
        OR isbn LIKE '%' || :query || '%'
        ORDER BY title ASC
    """)
    suspend fun searchBooks(query: String): List<BookEntity>

    @Query("SELECT * FROM books ORDER BY averageRating DESC LIMIT :limit")
    fun getTopRatedBooksFlow(limit: Int = 10): Flow<List<BookEntity>>

    @Query("SELECT * FROM books ORDER BY averageRating DESC LIMIT :limit")
    suspend fun getTopRatedBooks(limit: Int = 10): List<BookEntity>

    @Query("SELECT * FROM books ORDER BY addedAt DESC LIMIT :limit")
    fun getRecentBooksFlow(limit: Int = 10): Flow<List<BookEntity>>

    @Query("SELECT * FROM books ORDER BY addedAt DESC LIMIT :limit")
    suspend fun getRecentBooks(limit: Int = 10): List<BookEntity>

    @Query("SELECT * FROM books WHERE availableCopies > 0 ORDER BY title ASC")
    fun getAvailableBooksFlow(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE availableCopies > 0 ORDER BY title ASC")
    suspend fun getAvailableBooks(): List<BookEntity>

    @Query("SELECT DISTINCT category FROM books ORDER BY category ASC")
    fun getAllCategoriesFlow(): Flow<List<String>>

    @Query("SELECT DISTINCT category FROM books ORDER BY category ASC")
    suspend fun getAllCategories(): List<String>

    @Query("SELECT DISTINCT author FROM books ORDER BY author ASC")
    fun getAllAuthorsFlow(): Flow<List<String>>

    @Query("SELECT DISTINCT author FROM books ORDER BY author ASC")
    suspend fun getAllAuthors(): List<String>

    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBookCount(): Int

    @Query("SELECT COUNT(*) FROM books WHERE category = :category")
    suspend fun getBookCountByCategory(category: String): Int

    @Query("UPDATE books SET averageRating = :rating, totalReviews = :totalReviews WHERE bookId = :bookId")
    suspend fun updateBookRating(bookId: String, rating: Float, totalReviews: Int)

    @Query("UPDATE books SET availableCopies = :copies WHERE bookId = :bookId")
    suspend fun updateAvailableCopies(bookId: String, copies: Int)

    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()
}
