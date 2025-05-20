package com.universal.fiestamas.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.universal.fiestamas.presentation.screens.home.main.profile.UpdatePasswordScreen

fun NavGraphBuilder.profileSubScreensNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.PROFILE_SUB_SCREENS,
        startDestination = ProfileSubScreens.UpdatePassword.route
    ) {
        composable(
            route = ProfileSubScreens.UpdatePassword.route
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email").orEmpty()
            val userId = backStackEntry.arguments?.getString("userId").orEmpty()
            UpdatePasswordScreen(
                userEmail = email,
                userId = userId,
                onBackClicked = { navController.popBackStack() }
            )
        }
    }
}

sealed class ProfileSubScreens(val route: String) {
    object UpdatePassword : ProfileSubScreens(route = "UPDATE_PASSWORD")
}
