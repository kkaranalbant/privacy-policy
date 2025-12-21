package com.kaan.libraryapplication.util

object Constants {
    // Network
    const val BASE_URL = "http://10.0.2.2:3000/"
    const val TIMEOUT_SECONDS = 30L

    // Database
    const val DATABASE_NAME = "library_database"
    const val DATABASE_VERSION = 1

    // DataStore
    const val PREFERENCES_NAME = "library_preferences"

    // Keys
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_ROLE = "user_role"
    const val KEY_THEME_MODE = "theme_mode"
    const val KEY_LAST_SYNC = "last_sync"

    // Categories
    val BOOK_CATEGORIES = listOf(
        "Fiction",
        "Non-Fiction",
        "Science",
        "Technology",
        "History",
        "Biography",
        "Children",
        "Education"
    )

    // Work Manager
    const val SYNC_WORK_NAME = "sync_books_work"
    const val SYNC_INTERVAL_HOURS = 6L
}
