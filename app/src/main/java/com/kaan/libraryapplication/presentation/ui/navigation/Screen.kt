package com.kaan.libraryapplication.presentation.ui.navigation

sealed class Screen(val route: String) {
    // Auth
    data object Login : Screen("login")
    data object Register : Screen("register")

    // Main
    data object Home : Screen("home")
    data object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: String) = "book_detail/$bookId"
    }
    data object Search : Screen("search")
    data object Favorites : Screen("favorites")
    data object AdminHome : Screen("admin_home")
    data object Profile : Screen("profile")

    // Review
    data object AddReview : Screen("add_review/{bookId}") {
        fun createRoute(bookId: String) = "add_review/$bookId"
    }
}
