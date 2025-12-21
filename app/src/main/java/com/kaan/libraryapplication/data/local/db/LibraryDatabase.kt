package com.kaan.libraryapplication.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kaan.libraryapplication.data.local.db.dao.BookDao
import com.kaan.libraryapplication.data.local.db.dao.FavoriteDao
import com.kaan.libraryapplication.data.local.db.dao.ReviewDao
import com.kaan.libraryapplication.data.local.db.dao.UserDao
import com.kaan.libraryapplication.data.local.db.entities.BookEntity
import com.kaan.libraryapplication.data.local.db.entities.FavoriteEntity
import com.kaan.libraryapplication.data.local.db.entities.ReviewEntity
import com.kaan.libraryapplication.data.local.db.entities.UserEntity

/**
 * Main database for the Library Application
 * Contains 4 entities: User, Book, Favorite, Review
 */
@Database(
    entities = [
        UserEntity::class,
        BookEntity::class,
        FavoriteEntity::class,
        ReviewEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LibraryDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun reviewDao(): ReviewDao
}
