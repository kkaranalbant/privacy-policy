package com.kaan.libraryapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.kaan.libraryapplication.data.local.db.entities.BookEntity
import com.kaan.libraryapplication.data.local.db.entities.UserRole
import com.kaan.libraryapplication.data.repository.BookRepository
import com.kaan.libraryapplication.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class BookDetailUiState {
    data object Loading : BookDetailUiState()
    data class Success(val book: BookEntity) : BookDetailUiState()
    data class Error(val message: String) : BookDetailUiState()
}

class BookDetailViewModel(
    private val bookRepository: BookRepository,
    private val authRepository: com.kaan.libraryapplication.data.repository.AuthRepository,
    private val bookId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Loading)
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    private val _reviews = MutableStateFlow<List<com.kaan.libraryapplication.data.remote.api.ReviewResponse>>(emptyList())
    val reviews: StateFlow<List<com.kaan.libraryapplication.data.remote.api.ReviewResponse>> = _reviews.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    init {
        loadBook()
        loadReviews()
        checkIfFavorite()
        checkUserRole()
    }

    private fun checkUserRole() {
        viewModelScope.launch {
            authRepository.currentUserId.collect { userId ->
                if (userId != null) {
                    val user = authRepository.getCurrentUser(userId)
                    _isAdmin.value = user?.role == UserRole.ADMIN
                }
            }
        }
    }

    private fun loadBook() {
        viewModelScope.launch {
            _uiState.value = BookDetailUiState.Loading
            val book = bookRepository.getBookById(bookId)
            if (book != null) {
                _uiState.value = BookDetailUiState.Success(book)
            } else {
                _uiState.value = BookDetailUiState.Error("Book not found")
            }
        }
    }

    private fun loadReviews() {
        viewModelScope.launch {
            _reviews.value = bookRepository.getReviews(bookId)
        }
    }

    private fun checkIfFavorite() {
        viewModelScope.launch {
             // Placeholder
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val current = _isFavorite.value
            bookRepository.toggleFavorite(bookId, !current)
            _isFavorite.value = !current
        }
    }

    fun addReview(rating: Int, comment: String) {
        viewModelScope.launch {
            bookRepository.addReview(bookId, rating, comment)
            loadReviews() // Refresh list
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            bookRepository.deleteReview(reviewId)
            loadReviews()
        }
    }

    companion object {
        fun provideFactory(bookId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                BookDetailViewModel(AppContainer.bookRepository, AppContainer.authRepository, bookId)
            }
        }
    }
}
