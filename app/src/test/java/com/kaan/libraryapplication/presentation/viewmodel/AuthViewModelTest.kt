package com.kaan.libraryapplication.presentation.viewmodel

import com.kaan.libraryapplication.data.local.db.entities.UserEntity
import com.kaan.libraryapplication.data.repository.AuthRepository
import com.kaan.libraryapplication.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository: AuthRepository = mockk()
    private lateinit val viewModel: AuthViewModel

    @Test
    fun `login success updates uiState to Success`() = runTest {
        // Arrange
        val testUser = UserEntity(
            userId = "123",
            username = "testuser",
            email = "test@library.com",
            passwordHash = "hash",
            fullName = "Test User",
            role = "STUDENT"
        )
        coEvery { authRepository.login("test@library.com", "password") } returns Result.success(testUser)
        viewModel = AuthViewModel(authRepository)

        // Act
        viewModel.login("test@library.com", "password")

        // Assert
        val currentState = viewModel.uiState.value
        assertTrue(currentState is AuthUiState.Success)
        assertEquals(testUser, (currentState as AuthUiState.Success).user)
    }

    @Test
    fun `login failure updates uiState to Error`() = runTest {
        // Arrange
        val errorMessage = "Invalid credentials"
        coEvery { authRepository.login("wrong@library.com", "wrongpass") } returns Result.failure(Exception(errorMessage))
        viewModel = AuthViewModel(authRepository)

        // Act
        viewModel.login("wrong@library.com", "wrongpass")

        // Assert
        val currentState = viewModel.uiState.value
        assertTrue(currentState is AuthUiState.Error)
        assertEquals(errorMessage, (currentState as AuthUiState.Error).message)
    }

    @Test
    fun `logout updates uiState to Idle`() = runTest {
        // Arrange
        coEvery { authRepository.logout() } returns Unit
        viewModel = AuthViewModel(authRepository)
        // Manually setting to a different state before testing logout
        // Since we cannot set state directly, we just check if it goes to Idle after logout

        // Act
        viewModel.logout()

        // Assert
        val currentState = viewModel.uiState.value
        assertEquals(AuthUiState.Idle, currentState)
    }
}
