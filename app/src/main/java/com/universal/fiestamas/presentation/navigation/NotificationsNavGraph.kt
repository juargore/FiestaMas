package com.universal.fiestamas.presentation.navigation

import androidx.core.os.bundleOf
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.presentation.screens.home.notifications.NotificationsScreen
import com.universal.fiestamas.presentation.screens.home.notifications.chat.ChatScreen
import com.universal.fiestamas.presentation.utils.extensions.clearBackStack
import com.universal.fiestamas.presentation.utils.extensions.getArgument
import com.universal.fiestamas.presentation.utils.extensions.navigate
import java.util.ArrayList

fun NavGraphBuilder.notificationsNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.NOTIFICATIONS,
        startDestination = NotificationsScreen.Notifications.route
    ) {
        composable(
            route = NotificationsScreen.Notifications.route
        ) { backStackEntry ->
            val openChat = backStackEntry.arguments?.getBoolean("openChat") ?: false

            if (openChat) {
                val myPartyService: MyPartyService? = backStackEntry.getArgument()
                val isProvider: Boolean = backStackEntry.arguments?.getBoolean("isProvider") ?: false
                val festejadosName = myPartyService?.event_data?.name
                val eventName = myPartyService?.event_data?.name_event_type

                ChatScreen(
                    myPartyService = myPartyService,
                    clientId = myPartyService?.id_client.orEmpty(),
                    providerId = myPartyService?.id_provider.orEmpty(),
                    serviceEventId = myPartyService?.id.orEmpty(),
                    serviceId = myPartyService?.id_service.orEmpty(),
                    isProvider = isProvider,
                    clientEventId = myPartyService?.id_client_event.orEmpty(),
                    eventName = "$eventName $festejadosName",
                    onBackClicked = { navController.popBackStack() },
                    onServiceNegotiationClicked = { navController.popBackStack() }
                )
            } else {
                val myPartyServiceList: ArrayList<MyPartyService?>? = backStackEntry.arguments?.getParcelableArrayList("myPartyServiceList")

                NotificationsScreen(
                    onBackClicked = { navController.popBackStack() },
                    myPartyServiceList = myPartyServiceList?.toList().orEmpty(),
                    onAuthProcessStarted = {
                        val bundle = bundleOf("showStartEmailScreen" to true)
                        navController.navigate(Graph.AUTHENTICATION, bundle)
                    },
                    onOpenChatConversation = { serviceEvent, isProvider ->
                        val bundle = bundleOf(
                            "myPartyService" to serviceEvent,
                            "isProvider" to isProvider
                        )
                        navController.navigate(NotificationsScreen.ChatMessages.route, bundle) {
                            popUpTo(NotificationsScreen.Notifications.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }

        composable(
            route = NotificationsScreen.ChatMessages.route
        ) { backStackEntry ->
            val myPartyService: MyPartyService? = backStackEntry.getArgument()
            val isProvider: Boolean = backStackEntry.arguments?.getBoolean("isProvider") ?: false
            val festejadosName = myPartyService?.event_data?.name
            val eventName = myPartyService?.event_data?.name_event_type

            ChatScreen(
                myPartyService = myPartyService,
                clientId = myPartyService?.id_client.orEmpty(),
                providerId = myPartyService?.id_provider.orEmpty(),
                serviceEventId = myPartyService?.id.orEmpty(),
                serviceId = myPartyService?.id_service.orEmpty(),
                isProvider = isProvider,
                clientEventId = myPartyService?.id_client_event.orEmpty(),
                eventName = "$eventName $festejadosName",
                onBackClicked = { navController.popBackStack() },
                onServiceNegotiationClicked = { partyService ->
                    navController.popBackStack()
                    navController.popBackStack()
                    val bundle = bundleOf(
                        "myPartyService" to partyService,
                        "isProvider" to isProvider
                    )
                    navController.navigate(Graph.NEGOTIATION, bundle)
                }
            )
        }
    }
}

sealed class NotificationsScreen(val route: String) {
    object Notifications : NotificationsScreen(route = "NOTIFICATIONS")
    object ChatMessages : NotificationsScreen(route = "CHAT_MESSAGES")
}
