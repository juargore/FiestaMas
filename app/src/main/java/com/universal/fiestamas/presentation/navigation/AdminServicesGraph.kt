package com.universal.fiestamas.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.core.os.bundleOf
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.admin_services.AdminServicesScreen
import com.universal.fiestamas.presentation.utils.BottomBarMenu
import com.universal.fiestamas.presentation.utils.extensions.navigate

fun NavGraphBuilder.adminServicesNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.ADMIN_SERVICES,
        startDestination = AdminServicesScreens.AdminServices.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(
            route = AdminServicesScreens.AdminServices.route
        ) { backStackEntry ->
            val providerId = backStackEntry.arguments?.getString("providerId").orEmpty()
            AdminServicesScreen(
                providerId = providerId,
                onNewServiceClicked = {
                    navController.popBackStack()
                    val bundle = bundleOf(
                        "hideServices" to false,
                        "hideEvents" to true,
                    )
                    navController.navigate(BottomBarMenu.Home.route, bundle)
                },
                onEditServiceClicked = { service ->
                    navController.popBackStack()
                    val bundle = bundleOf("serviceId" to service.id)
                    navController.navigate(Graph.SERVICES_SELECTION, bundle)
                },
                onBackClicked = { navController.popBackStack() }
            )
        }
    }
}

sealed class AdminServicesScreens(val route: String) {
    object AdminServices : AdminServicesScreens(route = "ADMIN_SERVICES")
}
