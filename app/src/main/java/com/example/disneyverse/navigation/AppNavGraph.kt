package com.example.disneyverse.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.disneyverse.screens.FilmDetailScreen
import com.example.disneyverse.screens.HomeScreen
import com.example.disneyverse.screens.LoginScreen
import com.example.disneyverse.screens.ProfileScreen
import com.example.disneyverse.screens.RegisterScreen
import com.example.disneyverse.screens.UniverseFilmsScreen

@Composable
fun AppNavGraph(startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onGoRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("home") {
            HomeScreen(
                onUniverseClick = { universeId, universeName ->
                    navController.navigate("films/$universeId/$universeName")
                },
                onProfileClick = {
                    navController.navigate("profile")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "films/{universeId}/{universeName}",
            arguments = listOf(
                navArgument("universeId") { type = NavType.StringType },
                navArgument("universeName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            UniverseFilmsScreen(
                universeId = backStackEntry.arguments?.getString("universeId") ?: "",
                universeName = backStackEntry.arguments?.getString("universeName") ?: "",
                onBack = { navController.popBackStack() },
                onFilmClick = { filmId ->
                    navController.navigate("film/$filmId")
                }
            )
        }

        composable(
            route = "film/{filmId}",
            arguments = listOf(
                navArgument("filmId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            FilmDetailScreen(
                filmId = backStackEntry.arguments?.getString("filmId") ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable("profile") {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                },
                onFilmClick = { filmId ->
                    navController.navigate("film/$filmId")
                }
            )
        }
    }
}