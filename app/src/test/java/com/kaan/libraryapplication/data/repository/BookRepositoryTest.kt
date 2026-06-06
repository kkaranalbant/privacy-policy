package com.kaan.libraryapplication.data.repository

import com.kaan.libraryapplication.data.local.db.dao.BookDao
import com.kaan.libraryapplication.data.local.db.entities.BookEntity
import com.kaan.libraryapplication.data.remote.api.LibraryApiService
import com.kaan.libraryapplication.data.remote.api.ReviewRequest
import com.kaan.libraryapplication.data.remote.api.ReviewResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookRepositoryTest {

    private lateinit var bookRepository: BookRepository
    private val bookDao: BookDao = mockk(relaxed = true)
    private val apiService: LibraryApiService = mockk(relaxed = true)

    @Before
    fun setup() {
        bookRepository = BookRepository(bookDao, apiService)
    }

    @Test
    fun `refreshBooks fetches from API and inserts into DAO`() = runTest {
        val remoteBooks = listOf(
            BookEntity(bookId = "1", title = "Book 1", author = "Author 1", isbn = "123", description = "Desc", category = "Cat", publishedYear = 2020, addedAt = 123L)
        )
        coEvery { apiService.getBooks() } returns remoteBooks

        bookRepository.refreshBooks()

        coVerify { 
            apiService.getBooks()
            bookDao.insertBooks(remoteBooks)
        }
    }

    @Test
    fun `getBookById returns local if exists`() = runTest {
        val localBook = BookEntity(bookId = "1", title = "Book 1", author = "Author 1", isbn = "123", description = "Desc", category = "Cat", publishedYear = 2020, addedAt = 123L)
        coEvery { bookDao.getBookById("1") } returns localBook

        val result = bookRepository.getBookById("1")

        assertEquals(localBook, result)
        coVerify(exactly = 0) { apiService.getBookById(any()) }
    }

    @Test
    fun `getBookById returns remote if local null`() = runTest {
        val remoteBook = BookEntity(bookId = "1", title = "Book 1", author = "Author 1", isbn = "123", description = "Desc", category = "Cat", publishedYear = 2020, addedAt = 123L)
        coEvery { bookDao.getBookById("1") } returns null
        coEvery { apiService.getBookById("1") } returns remoteBook

        val result = bookRepository.getBookById("1")

        assertEquals(remoteBook, result)
        coVerify { 
            apiService.getBookById("1")
            bookDao.insertBook(remoteBook)
        }
    }

    @Test
    fun `addBook calls API and inserts to DAO`() = runTest {
        val book = BookEntity(bookId = "1", title = "Book 1", author = "Author 1", isbn = "123", description = "Desc", category = "Cat", publishedYear = 2020, addedAt = 123L)
        coEvery { apiService.addBook(book) } returns book

        bookRepository.addBook(book)

        coVerify { 
            apiService.addBook(book)
            bookDao.insertBook(book)
        }
    }

    @Test
    fun `deleteBook calls API and DAO`() = runTest {
        bookRepository.deleteBook("1")

        coVerify { 
            apiService.deleteBook("1")
            bookDao.deleteBookById("1")
        }
    }

    @Test
    fun `searchBooks returns from DAO`() = runTest {
        val books = listOf(BookEntity(bookId = "1", title = "Book 1", author = "Author 1", isbn = "123", description = "Desc", category = "Cat", publishedYear = 2020, addedAt = 123L))
        coEvery { bookDao.searchBooks("query") } returns books

        val result = bookRepository.searchBooks("query")

        assertEquals(books, result)
        coVerify { bookDao.searchBooks("query") }
    }

    @Test
    fun `toggleFavorite calls API`() = runTest {
        bookRepository.toggleFavorite("1", true)
        coVerify { apiService.addToFavorites("1") }

        bookRepository.toggleFavorite("1", false)
        coVerify { apiService.removeFromFavorites("1") }
    }

    @Test
    fun `addReview calls API`() = runTest {
        bookRepository.addReview("1", 5, "Great")
        coVerify { apiService.addReview("1", ReviewRequest(5, "Great")) }
    }

    @Test
    fun `getReviews returns from API`() = runTest {
        val reviews = listOf(ReviewResponse("r1", "1", "u1", "user", 5, "Great", 123L))
        coEvery { apiService.getReviews("1") } returns reviews

        val result = bookRepository.getReviews("1")

        assertEquals(reviews, result)
        coVerify { apiService.getReviews("1") }
    }

    @Test
    fun `deleteReview calls API`() = runTest {
        bookRepository.deleteReview("r1")
        coVerify { apiService.deleteReview("r1") }
    }
}
