package com.kaan.libraryapplication.presentation.viewmodel

import com.kaan.libraryapplication.data.local.db.entities.BookEntity
import com.kaan.libraryapplication.data.local.db.entities.UserEntity
import com.kaan.libraryapplication.data.local.db.entities.UserRole
import com.kaan.libraryapplication.data.remote.api.ReviewResponse
import com.kaan.libraryapplication.data.repository.AuthRepository
import com.kaan.libraryapplication.data.repository.BookRepository
import com.kaan.libraryapplication.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val bookRepository: BookRepository = mockk(relaxed = true)
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private lateinit val viewModel: BookDetailViewModel

    @Test
    fun `init loads book successfully`() = runTest {
        val testBook = BookEntity(bookId = "1", title = "Book 1", author = "Author 1", isbn = "123", description = "Desc", category = "Cat", publishedYear = 2020, addedAt = 123L)
        coEvery { bookRepository.getBookById("1") } returns testBook
        every { authRepository.currentUserId } returns MutableStateFlow(null)

        viewModel = BookDetailViewModel(bookRepository, authRepository, "1")

        // Need to allow coroutines to finish
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is BookDetailUiState.Success)
        assertEquals(testBook, (state as BookDetailUiState.Success).book)
    }

    @Test
    fun `init loads book failure`() = runTest {
        coEvery { bookRepository.getBookById("1") } returns null
        every { authRepository.currentUserId } returns MutableStateFlow(null)

        viewModel = BookDetailViewModel(bookRepository, authRepository, "1")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is BookDetailUiState.Error)
        assertEquals("Book not found", (state as BookDetailUiState.Error).message)
    }

    @Test
    fun `init loads reviews`() = runTest {
        val mockReviews = listOf(ReviewResponse("r1", "1", "u1", "user", 5, "Great", 123L))
        coEvery { bookRepository.getReviews("1") } returns mockReviews
        every { authRepository.currentUserId } returns MutableStateFlow(null)

        viewModel = BookDetailViewModel(bookRepository, authRepository, "1")
        advanceUntilIdle()

        assertEquals(mockReviews, viewModel.reviews.value)
    }

    @Test
    fun `admin user role sets isAdmin to true`() = runTest {
        val adminUser = UserEntity("u1", "admin", "admin@lib.com", "hash", "Admin", UserRole.ADMIN)
        every { authRepository.currentUserId } returns MutableStateFlow("u1")
        coEvery { authRepository.getCurrentUser("u1") } returns adminUser

        viewModel = BookDetailViewModel(bookRepository, authRepository, "1")
        advanceUntilIdle()

        assertTrue(viewModel.isAdmin.value)
    }

    @Test
    fun `toggleFavorite toggles state and calls repository`() = runTest {
        every { authRepository.currentUserId } returns MutableStateFlow(null)
        viewModel = BookDetailViewModel(bookRepository, authRepository, "1")
        
        viewModel.toggleFavorite()
        advanceUntilIdle()

        assertTrue(viewModel.isFavorite.value)
        coVerify { bookRepository.toggleFavorite("1", true) }
    }

    @Test
    fun `addReview calls repository and reloads reviews`() = runTest {
        every { authRepository.currentUserId } returns MutableStateFlow(null)
        viewModel = BookDetailViewModel(bookRepository, authRepository, "1")
        
        viewModel.addReview(5, "Excellent")
        advanceUntilIdle()

        coVerify { 
            bookRepository.addReview("1", 5, "Excellent") 
            bookRepository.getReviews("1")
        }
    }

    @Test
    fun `deleteReview calls repository and reloads reviews`() = runTest {
        every { authRepository.currentUserId } returns MutableStateFlow(null)
        viewModel = BookDetailViewModel(bookRepository, authRepository, "1")
        
        viewModel.deleteReview("r1")
        advanceUntilIdle()

        coVerify { 
            bookRepository.deleteReview("r1")
            bookRepository.getReviews("1")
        }
    }
}
