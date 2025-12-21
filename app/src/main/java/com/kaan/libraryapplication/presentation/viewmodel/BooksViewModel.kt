package com.kaan.libraryapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.kaan.libraryapplication.data.local.db.entities.BookEntity
import com.kaan.libraryapplication.data.repository.BookRepository
import com.kaan.libraryapplication.di.AppContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BooksViewModel(private val bookRepository: BookRepository) : ViewModel() {

    // Expose flows directly from repository
    val allBooks: StateFlow<List<BookEntity>> = bookRepository.allBooks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        refreshBooks()
    }

    fun refreshBooks() {
        viewModelScope.launch {
            bookRepository.refreshBooks()
        }
    }

    fun addBook(title: String, author: String, isbn: String, description: String, category: String) {
        viewModelScope.launch {
            val book = BookEntity(
                bookId = java.util.UUID.randomUUID().toString(), // Temp ID, backend generates real one usually or overwrites
                title = title,
                author = author,
                isbn = isbn,
                description = description,
                category = category,
                publishedYear = 2024, // Default current year
                addedAt = System.currentTimeMillis()
            )
            bookRepository.addBook(book)
            refreshBooks()
        }
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            bookRepository.deleteBook(bookId)
            refreshBooks()
        }
    }



    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                BooksViewModel(AppContainer.bookRepository)
            }
        }
    }
}
