package com.universal.fiestamas.presentation.navigation

import androidx.core.os.bundleOf
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.favourites.FavouritesScreen
import com.universal.fiestamas.presentation.utils.BottomBarMenu
import com.universal.fiestamas.presentation.utils.extensions.navigate

fun NavGraphBuilder.favouritesNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.FAVOURITES,
        startDestination = FavouritesScreens.Favourites.route
    ) {
        composable(
            route = FavouritesScreens.Favourites.route
        ) {
            FavouritesScreen(
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class FavouritesScreens(val route: String) {
    object Favourites : FavouritesScreens(route = "FAVOURITES")
}
