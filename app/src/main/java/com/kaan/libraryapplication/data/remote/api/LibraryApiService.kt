package com.kaan.libraryapplication.data.remote.api

import com.kaan.libraryapplication.data.local.db.entities.BookEntity
import com.kaan.libraryapplication.data.local.db.entities.UserEntity
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Body

interface LibraryApiService {
    @GET("books")
    suspend fun getBooks(): List<BookEntity>

    @GET("books/{id}")
    suspend fun getBookById(@Path("id") id: String): BookEntity

    @POST("books")
    suspend fun addBook(@Body book: BookEntity): BookEntity

    @PUT("books/{id}")
    suspend fun updateBook(@Path("id") id: String, @Body book: BookEntity): BookEntity

    @DELETE("books/{id}")
    suspend fun deleteBook(@Path("id") id: String)
    
    @GET("recommendations")
    suspend fun getRecommendations(): List<BookEntity>

    // Favorites
    @POST("favorites/{bookId}")
    suspend fun addToFavorites(@Path("bookId") bookId: String): retrofit2.Response<Unit>

    @DELETE("favorites/{bookId}")
    suspend fun removeFromFavorites(@Path("bookId") bookId: String): retrofit2.Response<Unit>

    @GET("favorites")
    suspend fun getFavorites(): List<String>

    // Reviews
    @POST("books/{bookId}/reviews")
    suspend fun addReview(@Path("bookId") bookId: String, @Body review: ReviewRequest): retrofit2.Response<Unit>

    @GET("books/{bookId}/reviews")
    suspend fun getReviews(@Path("bookId") bookId: String): List<ReviewResponse>

    @DELETE("reviews/{id}")
    suspend fun deleteReview(@Path("id") reviewId: String): retrofit2.Response<Unit>
}

data class ReviewRequest(val rating: Int, val comment: String)
data class ReviewResponse(
    val id: String,
    val userId: String,
    val userName: String,
    val bookId: String,
    val rating: Int,
    val comment: String,
    val timestamp: Long
)
