package com.universal.fiestamas.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.core.os.bundleOf
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.promos.PromosScreen
import com.universal.fiestamas.presentation.utils.extensions.navigate

fun NavGraphBuilder.promosNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.PROMOS,
        startDestination = PromosScreens.Promos.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(
            route = PromosScreens.Promos.route
        ) { backStackEntry ->
            val providerId = backStackEntry.arguments?.getString("providerId").orEmpty()
            PromosScreen(
                providerId = providerId,
                onBackClicked = { navController.popBackStack() },
                reloadScreen = {
                    navController.popBackStack()
                    val bundle = bundleOf("providerId" to providerId)
                    navController.navigate(Graph.PROMOS, bundle)
                }
            )
        }
    }
}

sealed class PromosScreens(val route: String) {
    object Promos : PromosScreens(route = "PROMOS")
}
