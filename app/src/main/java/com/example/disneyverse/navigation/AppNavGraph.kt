package com.example.disneyverse.navigation

import android.net.Uri
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

object AppRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val FILMS = "films"
    const val FILM = "film"
    const val PARAM_UNIVERSE_ID = "universeId"
    const val PARAM_UNIVERSE_NAME = "universeName"
    const val PARAM_FILM_ID = "filmId"

    fun filmsRoute(universeId: String, universeName: String): String {
        return "$FILMS/$universeId/${Uri.encode(universeName)}"
    }

    fun filmRoute(filmId: String): String {
        return "$FILM/$filmId"
    }
}

@Composable
fun AppNavGraph(startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onGoRegister = {
                    navController.navigate(AppRoutes.REGISTER) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoutes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.REGISTER) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoutes.HOME) {
            HomeScreen(
                onUniverseClick = { universeId, universeName ->
                    navController.navigate(AppRoutes.filmsRoute(universeId, universeName)) {
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    navController.navigate(AppRoutes.PROFILE) {
                        launchSingleTop = true
                    }
                },
                onLogout = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "${AppRoutes.FILMS}/{${AppRoutes.PARAM_UNIVERSE_ID}}/{${AppRoutes.PARAM_UNIVERSE_NAME}}",
            arguments = listOf(
                navArgument(AppRoutes.PARAM_UNIVERSE_ID) { type = NavType.StringType },
                navArgument(AppRoutes.PARAM_UNIVERSE_NAME) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val rawUniverseName = backStackEntry.arguments?.getString(AppRoutes.PARAM_UNIVERSE_NAME) ?: ""
            UniverseFilmsScreen(
                universeId = backStackEntry.arguments?.getString(AppRoutes.PARAM_UNIVERSE_ID) ?: "",
                universeName = Uri.decode(rawUniverseName),
                onBack = { navController.popBackStack() },
                onFilmClick = { filmId ->
                    navController.navigate(AppRoutes.filmRoute(filmId)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "${AppRoutes.FILM}/{${AppRoutes.PARAM_FILM_ID}}",
            arguments = listOf(
                navArgument(AppRoutes.PARAM_FILM_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            FilmDetailScreen(
                filmId = backStackEntry.arguments?.getString(AppRoutes.PARAM_FILM_ID) ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.PROFILE) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onFilmClick = { filmId ->
                    navController.navigate(AppRoutes.filmRoute(filmId)) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}