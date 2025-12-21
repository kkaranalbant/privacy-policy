package com.kaan.libraryapplication.presentation.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Home Screen
        composable(route = Screen.Home.route) {
            com.kaan.libraryapplication.presentation.ui.screens.HomeScreen(
                onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetail.createRoute(bookId))
                }
            )
        }

        // Admin Home Screen
        composable(route = Screen.AdminHome.route) {
            com.kaan.libraryapplication.presentation.ui.screens.AdminHomeScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                },
                onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetail.createRoute(bookId))
                }
            )
        }

        // Login Screen
        composable(route = Screen.Login.route) {
            com.kaan.libraryapplication.presentation.ui.screens.LoginScreen(
                onLoginSuccess = { role ->
                    val destination = if (role == "ADMIN") Screen.AdminHome.route else Screen.Home.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // Register Screen
        composable(route = Screen.Register.route) {
            com.kaan.libraryapplication.presentation.ui.screens.RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Book Detail Screen
        composable(
            route = Screen.BookDetail.route,
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: return@composable
            com.kaan.libraryapplication.presentation.ui.screens.BookDetailScreen(
                bookId = bookId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Search Screen
        composable(route = Screen.Search.route) {
            PlaceholderScreen("Search Screen")
        }

        // Favorites Screen
        composable(route = Screen.Favorites.route) {
            com.kaan.libraryapplication.presentation.ui.screens.FavoritesScreen()
        }

        // Profile Screen
        composable(route = Screen.Profile.route) {
            com.kaan.libraryapplication.presentation.ui.screens.ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        // Add Review Screen
        composable(
            route = Screen.AddReview.route,
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
        ) {
            PlaceholderScreen("Add Review Screen")
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
