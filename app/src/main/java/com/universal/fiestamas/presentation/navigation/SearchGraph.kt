package com.universal.fiestamas.presentation.navigation

import androidx.core.os.bundleOf
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.universal.fiestamas.presentation.screens.home.main.search.SearchScreen
import com.universal.fiestamas.presentation.utils.extensions.navigate

fun NavGraphBuilder.searchNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.SEARCH,
        startDestination = SearchScreens.Search.route
    ) {
        composable(route = SearchScreens.Search.route) {
            SearchScreen(
                onNavigateServiceSearched = { screenInfo ->
                    navController.popBackStack()
                    val bundle = bundleOf("screenInfo" to screenInfo)
                    navController.navigate(Graph.SERVICES_SELECTION, bundle)
                },
                onBackClicked = { navController.popBackStack() },
                reloadScreen = {
                    navController.popBackStack()
                    navController.navigate(Graph.SEARCH)
                }
            )
        }
    }
}

sealed class SearchScreens(val route: String) {
    object Search : SearchScreens(route = "SEARCH")
}
