package com.kaan.libraryapplication.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kaan.libraryapplication.data.local.db.dao.UserDao
import com.kaan.libraryapplication.data.local.db.entities.UserEntity
import com.kaan.libraryapplication.data.remote.api.AuthApiService
import com.kaan.libraryapplication.data.remote.api.LoginRequest
import com.kaan.libraryapplication.data.remote.api.RegisterRequest
import com.kaan.libraryapplication.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepository(
    private val userDao: UserDao,
    private val apiService: AuthApiService,
    private val dataStore: DataStore<Preferences>
) {
    private val TOKEN_KEY = stringPreferencesKey(Constants.KEY_AUTH_TOKEN)
    private val USER_ID_KEY = stringPreferencesKey(Constants.KEY_USER_ID)

    val authToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }
    
    val currentUserId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    suspend fun login(email: String, passwordHash: String): Result<UserEntity> {
        return try {
            val response = apiService.login(LoginRequest(email, passwordHash))
            // Save token
            dataStore.edit { prefs ->
                prefs[TOKEN_KEY] = response.token
                prefs[USER_ID_KEY] = response.user.userId
            }
            // Save user to local DB
            userDao.insertUser(response.user)
            Result.success(response.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(username: String, email: String, password: String, fullName: String): Result<UserEntity> {
        return try {
            val response = apiService.register(
                RegisterRequest(username, email, password, fullName, "STUDENT")
            )
            // Save token
             dataStore.edit { prefs ->
                prefs[TOKEN_KEY] = response.token
                prefs[USER_ID_KEY] = response.user.userId
            }
            userDao.insertUser(response.user)
            Result.success(response.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(USER_ID_KEY)
        }
        userDao.deleteAllUsers() // Optional: Clear local user cache
    }
    
    suspend fun getCurrentUser(userId: String): UserEntity? {
        return userDao.getUserById(userId)
    }
}
