package com.universal.fiestamas.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.universal.fiestamas.presentation.screens.home.main.home.MainHomeScreen
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.MainPartyScreen
import com.universal.fiestamas.presentation.screens.home.main.profile.MainProfileScreen
import com.universal.fiestamas.presentation.utils.BottomBarMenu
import com.universal.fiestamas.presentation.utils.extensions.navigate

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBarMenu.Home.route
    ) {

        composable(route = BottomBarMenu.Home.route) { backStackEntry ->
            val hideServices: Boolean = backStackEntry.arguments?.getBoolean("hideServices") ?: false
            val hideEvents: Boolean = backStackEntry.arguments?.getBoolean("hideEvents") ?: false

            MainHomeScreen(
                hideEvents = hideEvents,
                hideServices = hideServices,
                onNavigateServicesCategoriesClicked = { screenInfo -> // events cards
                    val bundle = bundleOf("screenInfo" to screenInfo)
                    navController.navigate(Graph.SERVICES_SELECTION, bundle)
                },
                onNavigateServicesTypesClicked = { screenInfo -> // services cards
                    val bundle = bundleOf("screenInfo" to screenInfo)
                    navController.navigate(Graph.SERVICES_SELECTION, bundle)
                },
                /*onNavigateAuthClicked = {
                    navController.navigate(BottomBarMenu.Profile.route)
                },*/
                onNavigateMyParty = {
                    navController.navigate(BottomBarMenu.Party.route)
                },
                /*onRedirectToHome = {
                    navController.navigate(BottomBarMenu.Home.route)
                },*/
                onNavigateSearchClicked = {
                    navController.navigate(Graph.SEARCH)
                },
                onNavigateProviderRegistrationClicked = {
                    val bundle = bundleOf("showStartEmailScreen" to true)
                    navController.navigate(Graph.AUTHENTICATION, bundle)
                }
            )
        }

        composable(route = BottomBarMenu.Party.route) {
            MainPartyScreen(
                onNavigateHomeClicked = { hideServices: Boolean, hideEvents: Boolean ->
                    val bundle = bundleOf(
                        "hideServices" to hideServices,
                        "hideEvents" to hideEvents,
                    )
                    navController.navigate(BottomBarMenu.Home.route, bundle)
                },
                onNavigateNotificationsClicked = { myPartyServiceList ->
                    val bundle = bundleOf("myPartyServiceList" to myPartyServiceList)
                    navController.navigate(Graph.NOTIFICATIONS, bundle)
                },
                onNavigateAuthClicked = {
                    navController.navigate(BottomBarMenu.Profile.route)
                },
                onNavigateServicesCategoriesClicked = { screenInfo -> // horizontal cards
                    val bundle = bundleOf("screenInfo" to screenInfo)
                    navController.navigate(Graph.SERVICES_SELECTION, bundle)
                },
                onNavigateServiceNegotiationClicked = { myPartyService, isProvider -> // vertical or grid cards
                    val bundle = bundleOf(
                        "myPartyService" to myPartyService,
                        "isProvider" to isProvider
                    )
                    navController.navigate(Graph.NEGOTIATION, bundle)
                },
                onNavigateFavouritesClicked = {
                    navController.navigate(Graph.FAVOURITES)
                },
                onNavigateAdminPromosClicked = { providerId ->
                    val bundle = bundleOf("providerId" to providerId)
                    navController.navigate(Graph.PROMOS, bundle)
                },
                onNavigateAdminServicesClicked = { providerId ->
                    val bundle = bundleOf("providerId" to providerId)
                    navController.navigate(Graph.ADMIN_SERVICES, bundle)
                },
                onNavigateSearchClicked = {
                    navController.navigate(Graph.SEARCH)
                },
                onRedirectToHome = {
                    navController.navigate(BottomBarMenu.Home.route)
                }
            )
        }

        composable(route = BottomBarMenu.Profile.route) {
            MainProfileScreen(
                onAuthProcessStarted = { isNewAccountFromGmail, googleUserData, exists, email, account, refresh ->
                    val bundle = bundleOf(
                        "isNewAccountFromGmail" to isNewAccountFromGmail,
                        "googleUserData" to googleUserData,
                        "exists" to exists,
                        "email" to email,
                        "mustRefreshApp" to true,
                        "showStartEmailScreen" to false,
                        "account" to account,
                        "refreshAppIfAccountIsCreated" to refresh
                    )
                    navController.navigate(Graph.AUTHENTICATION, bundle)
                },
                onNavigateUpdatePasswordClicked = { email, userId ->
                    val bundle = bundleOf(
                        "email" to email,
                        "userId" to userId
                    )
                    navController.navigate(Graph.PROFILE_SUB_SCREENS, bundle)
                },
                onRedirectToHome = {
                    navController.navigate(BottomBarMenu.Home.route)
                }
            )
        }

        servicesSelectionNavGraph(navController = navController)
        notificationsNavGraph(navController = navController)
        negotiationNavGraph(navController = navController)
        authNavGraph(navController = navController)
        profileSubScreensNavGraph(navController = navController)
        favouritesNavGraph(navController = navController)
        promosNavGraph(navController = navController)
        adminServicesNavGraph(navController = navController)
        searchNavGraph(navController = navController)
    }
}
