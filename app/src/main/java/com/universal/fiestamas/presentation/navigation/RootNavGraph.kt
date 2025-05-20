package com.universal.fiestamas.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.universal.fiestamas.presentation.screens.home.main.MainParentScreen

@Composable
fun RootNavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.HOME
    ) {
        composable(route = Graph.HOME) {
            MainParentScreen()
        }
        authNavGraph(navController = navController)
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val SERVICES_SELECTION = "services_selection_graph"
    const val SERVICES_SELECTION_V2 = "services_selection_v2_graph"
    const val NOTIFICATIONS = "notifications_graph"
    const val NEGOTIATION = "negotiation"
    const val PROFILE_SUB_SCREENS = "profile_sub_screens_graph"
    const val FAVOURITES = "favourites"
    const val PROMOS = "promos"
    const val INVITATIONS = "invitations"
    const val ADMIN_SERVICES = "admin_services"
    const val SEARCH = "search"
}
