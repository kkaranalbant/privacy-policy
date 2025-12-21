package com.kaan.libraryapplication.data.remote.api

import com.kaan.libraryapplication.data.local.db.entities.UserEntity
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String, val user: UserEntity)
data class RegisterRequest(val username: String, val email: String, val password: String, val fullName: String?, val role: String)

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): LoginResponse
}
