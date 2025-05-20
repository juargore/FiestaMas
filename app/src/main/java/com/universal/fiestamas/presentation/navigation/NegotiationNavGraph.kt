package com.universal.fiestamas.presentation.navigation

import androidx.core.os.bundleOf
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.InvitationsScreen
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.custom_invitation.CustomInvitationScreen
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.hostess.HostessScreen
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.tables.ManageTablesScreen
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.negotiation.ServiceNegotiationScreen
import com.universal.fiestamas.presentation.utils.extensions.getArgument
import com.universal.fiestamas.presentation.utils.extensions.navigate

fun NavGraphBuilder.negotiationNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.NEGOTIATION,
        startDestination = ServiceNegotiationScreens.ServiceNegotiation.route
    ) {
        composable(route = ServiceNegotiationScreens.ServiceNegotiation.route) { backStackEntry ->
            val myPartyService: MyPartyService? = backStackEntry.getArgument()
            val isProvider = backStackEntry.arguments?.getBoolean("isProvider") ?: false
            if (myPartyService != null) {
                ServiceNegotiationScreen(
                    myPartyService = myPartyService,
                    isProvider = isProvider,
                    onBackClicked = { navController.popBackStack() },
                    onNavigateNotificationsClicked = {
                        val bundle = bundleOf(
                            "openChat" to true,
                            "myPartyService" to myPartyService,
                            "isProvider" to isProvider
                        )
                        navController.navigate(Graph.NOTIFICATIONS, bundle)
                    },
                    onNavigateAdminInvitationsClicked = { providerId, idClientEvent ->
                        val bundle = bundleOf(
                            "providerId" to providerId,
                            "idClientEvent" to idClientEvent
                        )
                        navController.navigate(ServiceNegotiationScreens.Invitations.route, bundle)
                    },
                    reloadScreen = {
                        navController.popBackStack()
                        val bundle = bundleOf(
                            "myPartyService" to myPartyService,
                            "isProvider" to isProvider
                        )
                        navController.navigate(Graph.NEGOTIATION, bundle)
                    }
                )
            }
        }

        composable(route = ServiceNegotiationScreens.Invitations.route) { backStackEntry ->
            val providerId = backStackEntry.arguments?.getString("providerId").orEmpty()
            val idClientEvent = backStackEntry.arguments?.getString("idClientEvent").orEmpty()
            InvitationsScreen(
                idClientEvent = idClientEvent,
                onManageTables = {
                    val bundle = bundleOf("providerId" to providerId, "idClientEvent" to idClientEvent)
                    navController.navigate(ServiceNegotiationScreens.Tables.route, bundle)
                },
                onManageHostess = {
                    val bundle = bundleOf("providerId" to providerId, "idClientEvent" to idClientEvent)
                    navController.navigate(ServiceNegotiationScreens.Hostess.route, bundle)
                },
                onManageInvitation = {
                    val bundle = bundleOf("providerId" to providerId, "idClientEvent" to idClientEvent)
                    navController.navigate(ServiceNegotiationScreens.CustomInvitation.route, bundle)
                },
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable(route = ServiceNegotiationScreens.Tables.route) { backStackEntry ->
            val providerId = backStackEntry.arguments?.getString("providerId").orEmpty()
            val idClientEvent = backStackEntry.arguments?.getString("idClientEvent").orEmpty()
            ManageTablesScreen(
                idClientEvent = idClientEvent,
                onBackClicked = { navController.popBackStack() },
            )
        }

        composable(route = ServiceNegotiationScreens.Hostess.route) { backStackEntry ->
            val providerId = backStackEntry.arguments?.getString("providerId").orEmpty()
            val idClientEvent = backStackEntry.arguments?.getString("idClientEvent").orEmpty()
            HostessScreen(
                onBackClicked = { navController.popBackStack() },
            )
        }

        composable(route = ServiceNegotiationScreens.CustomInvitation.route) { backStackEntry ->
            val providerId = backStackEntry.arguments?.getString("providerId").orEmpty()
            val idClientEvent = backStackEntry.arguments?.getString("idClientEvent").orEmpty()
            CustomInvitationScreen(
                idClientEvent = idClientEvent,
                onBackClicked = { navController.popBackStack() },
            )
        }
    }
}

sealed class ServiceNegotiationScreens(val route: String) {
    object ServiceNegotiation : ServiceNegotiationScreens(route = "NEGOTIATION")
    object Invitations : ServiceNegotiationScreens(route = "INVITATION")
    object Tables : ServiceNegotiationScreens(route = "TABLES")
    object Hostess : ServiceNegotiationScreens(route = "HOSTESS")
    object CustomInvitation : ServiceNegotiationScreens(route = "CUSTOM_INVITATION")
}
