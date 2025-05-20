package com.universal.fiestamas.presentation.utils.extensions

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.core.net.toUri
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigator
import androidx.navigation.navOptions
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.AddressData
import com.universal.fiestamas.domain.models.Event
import com.universal.fiestamas.domain.models.FirstQuestionsClient
import com.universal.fiestamas.domain.models.GoogleUserData
import com.universal.fiestamas.domain.models.ListMediaItemService
import com.universal.fiestamas.domain.models.LoginAccount
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.Screen
import com.universal.fiestamas.domain.models.ScreenInfo
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.domain.models.ServiceProviderData
import com.universal.fiestamas.domain.models.ServiceType
import com.universal.fiestamas.domain.models.SubService
import com.universal.fiestamas.presentation.navigation.Graph

/**
* Extension function that allows pass data as bundle object
* easily and keeps the structure of original navigate
* */
@SuppressLint("RestrictedApi")
fun NavController.navigate(
    route: String,
    args: Bundle,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    val routeLink = NavDeepLinkRequest
        .Builder
        .fromUri(NavDestination.createRoute(route).toUri())
        .build()

    val deepLinkMatch = graph.matchDeepLink(routeLink)
    if (deepLinkMatch != null) {
        val destination = deepLinkMatch.destination
        val id = destination.id
        navigate(id, args, navOptions, navigatorExtras)
    } else {
        navigate(route, navOptions, navigatorExtras)
    }
}

@SuppressLint("RestrictedApi")
fun NavController.navigate(
    route: String,
    args: Bundle? = null,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    val navOptions = navOptions(builder)
    val routeLink = NavDeepLinkRequest
        .Builder
        .fromUri(NavDestination.createRoute(route).toUri())
        .build()

    val deepLinkMatch = graph.matchDeepLink(routeLink)
    if (deepLinkMatch != null) {
        val destination = deepLinkMatch.destination
        val id = destination.id
        navigate(id, args, navOptions)
    } else {
        navigate(route, navOptions)
    }
}

fun NavController.popBackStackMultiple(times: Int) {
    repeat(times) {
        popBackStack()
    }
}

fun NavController.clearBackStack() {
    navigate(
        route = Graph.HOME,
        navOptions = NavOptions.Builder()
            .setPopUpTo(Graph.SERVICES_SELECTION, true)
            .build()
    )
}

inline fun <reified T : Parcelable> NavBackStackEntry.getArgument(): T? {
    return when(T::class.java) {
        Event::class.java -> arguments?.parcelable("event")
        ServiceCategory::class.java -> arguments?.parcelable("serviceCategory")
        ServiceType::class.java -> arguments?.parcelable("serviceType")
        SubService::class.java -> arguments?.parcelable("subServiceType")
        Service::class.java -> arguments?.parcelable("service")
        FirstQuestionsClient::class.java -> arguments?.parcelable("questions")
        MyPartyService::class.java -> arguments?.parcelable("myPartyService")
        ScreenInfo::class.java -> arguments?.parcelable("screenInfo")
        ServiceProviderData::class.java -> arguments?.parcelable("serviceProviderData")
        LoginAccount::class.java -> arguments?.parcelable("account")
        ListMediaItemService::class.java -> arguments?.parcelable("listMediaItemService")
        GoogleUserData::class.java -> arguments?.parcelable("googleUserData")
        Uri::class.java ->  arguments?.parcelable("videoUri")
        Address::class.java ->  arguments?.parcelable("businessAddress")
        AddressData::class.java ->  arguments?.parcelable("address")
        else -> null
    }
}

fun navigateToSourceScreen(
    screenInfoArg: ScreenInfo,
    navController: NavHostController,
    extraScreen: Int = 0, // 1 or 0
) {
    if (screenInfoArg.startedScreen == Screen.ServiceCategories) {
        if (screenInfoArg.prevScreen == Screen.ServiceCategories) {
            navController.popBackStackMultiple(1 + extraScreen)
        }
        if (screenInfoArg.prevScreen == Screen.ServiceTypes) {
            navController.popBackStackMultiple(2 + extraScreen)
        }
        if (screenInfoArg.prevScreen == Screen.SubServices) {
            navController.popBackStackMultiple(3 + extraScreen)
        }
    }
    if (screenInfoArg.startedScreen == Screen.ServiceTypes) {
        if (screenInfoArg.prevScreen == Screen.ServiceTypes) {
            navController.popBackStackMultiple(1 + extraScreen)
        }
        if (screenInfoArg.prevScreen == Screen.SubServices) {
            navController.popBackStackMultiple(2 + extraScreen)
        }
        if (screenInfoArg.prevScreen == Screen.None) {
            navController.clearBackStack()
        }
    }
}
