package com.kaan.libraryapplication.data.local.db.dao

import androidx.room.*
import com.kaan.libraryapplication.data.local.db.entities.RatingDistribution
import com.kaan.libraryapplication.data.local.db.entities.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    @Update
    suspend fun updateReview(review: ReviewEntity)

    @Delete
    suspend fun deleteReview(review: ReviewEntity)

    @Query("DELETE FROM reviews WHERE reviewId = :reviewId")
    suspend fun deleteReviewById(reviewId: String)

    @Query("DELETE FROM reviews WHERE userId = :userId")
    suspend fun deleteAllUserReviews(userId: String)

    @Query("DELETE FROM reviews WHERE bookId = :bookId")
    suspend fun deleteAllBookReviews(bookId: String)

    @Query("SELECT * FROM reviews WHERE reviewId = :reviewId")
    suspend fun getReviewById(reviewId: String): ReviewEntity?

    @Query("SELECT * FROM reviews WHERE reviewId = :reviewId")
    fun getReviewByIdFlow(reviewId: String): Flow<ReviewEntity?>

    @Query("SELECT * FROM reviews WHERE userId = :userId AND bookId = :bookId LIMIT 1")
    suspend fun getUserBookReview(userId: String, bookId: String): ReviewEntity?

    @Query("SELECT * FROM reviews WHERE userId = :userId AND bookId = :bookId LIMIT 1")
    fun getUserBookReviewFlow(userId: String, bookId: String): Flow<ReviewEntity?>

    @Query("SELECT * FROM reviews WHERE bookId = :bookId ORDER BY createdAt DESC")
    fun getBookReviewsFlow(bookId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE bookId = :bookId ORDER BY createdAt DESC")
    suspend fun getBookReviews(bookId: String): List<ReviewEntity>

    @Query("SELECT * FROM reviews WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserReviewsFlow(userId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getUserReviews(userId: String): List<ReviewEntity>

    @Query("SELECT * FROM reviews WHERE rating >= :minRating ORDER BY createdAt DESC")
    fun getReviewsByRatingFlow(minRating: Int): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentReviewsFlow(limit: Int = 10): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecentReviews(limit: Int = 10): List<ReviewEntity>

    @Query("SELECT COUNT(*) FROM reviews WHERE bookId = :bookId")
    suspend fun getBookReviewCount(bookId: String): Int

    @Query("SELECT COUNT(*) FROM reviews WHERE bookId = :bookId")
    fun getBookReviewCountFlow(bookId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM reviews WHERE userId = :userId")
    suspend fun getUserReviewCount(userId: String): Int

    @Query("SELECT AVG(rating) FROM reviews WHERE bookId = :bookId")
    suspend fun getBookAverageRating(bookId: String): Float?

    @Query("SELECT AVG(rating) FROM reviews WHERE bookId = :bookId")
    fun getBookAverageRatingFlow(bookId: String): Flow<Float?>

    @Query("""
        SELECT rating, COUNT(*) as count
        FROM reviews
        WHERE bookId = :bookId
        GROUP BY rating
        ORDER BY rating DESC
    """)
    suspend fun getBookRatingDistribution(bookId: String): List<RatingDistribution>

    @Query("SELECT COUNT(*) FROM reviews")
    suspend fun getTotalReviewCount(): Int

    @Query("DELETE FROM reviews")
    suspend fun deleteAllReviews()
}
