package com.kaan.libraryapplication.presentation.viewmodel

import com.kaan.libraryapplication.data.local.db.entities.BookEntity
import com.kaan.libraryapplication.data.repository.BookRepository
import com.kaan.libraryapplication.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BooksViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val bookRepository: BookRepository = mockk(relaxed = true)
    private lateinit val viewModel: BooksViewModel

    @Test
    fun `init fetches allBooks and calls refreshBooks`() = runTest {
        val mockBooks = listOf(
            BookEntity(bookId = "1", title = "Book 1", author = "Author 1", isbn = "123", description = "Desc", category = "Cat", publishedYear = 2020, addedAt = 123L)
        )
        val booksFlow = MutableStateFlow(mockBooks)
        every { bookRepository.allBooks } returns booksFlow

        viewModel = BooksViewModel(bookRepository)

        // allBooks flow is populated
        assertEquals(mockBooks, viewModel.allBooks.value)

        // refreshBooks is called in init
        coVerify(exactly = 1) { bookRepository.refreshBooks() }
    }

    @Test
    fun `addBook calls repository and refresh`() = runTest {
        every { bookRepository.allBooks } returns MutableStateFlow(emptyList())
        viewModel = BooksViewModel(bookRepository)

        viewModel.addBook("Title", "Author", "ISBN", "Desc", "Cat")

        coVerify { 
            bookRepository.addBook(any()) 
            bookRepository.refreshBooks()
        }
    }

    @Test
    fun `deleteBook calls repository and refresh`() = runTest {
        every { bookRepository.allBooks } returns MutableStateFlow(emptyList())
        viewModel = BooksViewModel(bookRepository)

        viewModel.deleteBook("1")

        coVerify { 
            bookRepository.deleteBook("1") 
            bookRepository.refreshBooks()
        }
    }
}
