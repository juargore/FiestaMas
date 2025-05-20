package com.universal.fiestamas.presentation.navigation

import android.annotation.SuppressLint
import androidx.core.os.bundleOf
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.universal.fiestamas.domain.models.AddressData
import com.universal.fiestamas.domain.models.Attribute
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.domain.models.ServiceType
import com.universal.fiestamas.domain.models.SubService
import com.universal.fiestamas.presentation.screens.home.services.ServicesCategoriesScreenV2
import com.universal.fiestamas.presentation.screens.home.services.types.ServicesTypesScreenV2
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.SubServicesScreenV2
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.add_edit_provider.AddServiceProviderScreen1V2
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.add_edit_provider.AddServiceProviderScreen2V2
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.add_edit_provider.AddServiceProviderScreen3V2
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.add_edit_provider.AddServiceProviderScreen4V2
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.add_edit_provider.SuccessServiceCreatedScreenV2
import com.universal.fiestamas.presentation.utils.extensions.clearBackStack
import com.universal.fiestamas.presentation.utils.extensions.getArgument
import com.universal.fiestamas.presentation.utils.extensions.navigate

@SuppressLint("NewApi")
fun NavGraphBuilder.servicesSelectionNavGraphV2(navController: NavHostController) {

    navigation(
        route = Graph.SERVICES_SELECTION_V2,
        startDestination = ServicesScreensV2.ServicesCategoriesV2.route
    ) {
        composable(
            route = ServicesScreensV2.ServicesCategoriesV2.route
        ) {
            ServicesCategoriesScreenV2(
                onNavigateServicesTypesV2 = { serviceCategory ->
                    val bundle = bundleOf("serviceCategory" to serviceCategory)
                    navController.navigate(ServicesScreensV2.ServicesTypesV2.route, bundle)
                },
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = ServicesScreensV2.ServicesTypesV2.route
        ) { backStackEntry ->
            val serviceCategory: ServiceCategory = backStackEntry.getArgument()!!

            ServicesTypesScreenV2(
                serviceCategory = serviceCategory,
                onNavigateAddServiceProvider1V2 = { serviceType: ServiceType? ->
                    val bundle = bundleOf(
                        "serviceCategory" to serviceCategory,
                        "serviceType" to serviceType,
                    )
                    navController.navigate(ServicesScreensV2.AddServiceProvider1V2.route, bundle)
                },
                onNavigateSubServicesV2 = { serviceType: ServiceType ->
                    val bundle = bundleOf(
                        "serviceCategory" to serviceCategory,
                        "serviceType" to serviceType,
                    )
                    navController.navigate(ServicesScreensV2.SubServicesV2.route, bundle)
                },
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = ServicesScreensV2.SubServicesV2.route
        ) { backStackEntry ->
            val serviceCategory: ServiceCategory = backStackEntry.getArgument()!!
            val serviceType: ServiceType = backStackEntry.getArgument()!!

            SubServicesScreenV2(
                serviceType = serviceType,
                onNavigateAddServiceProvider1V2 = { subService ->
                    val bundle = bundleOf(
                        "serviceCategory" to serviceCategory,
                        "serviceType" to serviceType,
                        "subService" to subService
                    )
                    navController.navigate(ServicesScreensV2.AddServiceProvider1V2.route, bundle)
                },
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }
    }

    composable(
        route = ServicesScreensV2.AddServiceProvider1V2.route
    ) { backStackEntry ->
        val serviceCategory: ServiceCategory = backStackEntry.getArgument()!!
        val serviceType: ServiceType? = backStackEntry.getArgument()
        val subService: SubService? = backStackEntry.getArgument()

        AddServiceProviderScreen1V2(
            serviceCategory = serviceCategory,
            serviceType = serviceType,
            subService = subService,
            onContinue = { selectedAttributes, suggestedAttributes ->
                val bundle = bundleOf(
                    "serviceCategory" to serviceCategory,
                    "serviceType" to serviceType,
                    "subService" to subService
                ).also {
                    it.putParcelableArrayList("selectedAttributes", ArrayList(selectedAttributes))
                    it.putStringArrayList("suggestedAttributes", ArrayList(suggestedAttributes))
                }
                navController.navigate(ServicesScreensV2.AddServiceProvider2V2.route, bundle)
            },
            onBackClicked = {
                navController.popBackStack()
            }
        )
    }

    composable(
        route = ServicesScreensV2.AddServiceProvider2V2.route
    ) { backStackEntry ->
        val serviceCategory: ServiceCategory = backStackEntry.getArgument()!!
        val serviceType: ServiceType? = backStackEntry.getArgument()

        AddServiceProviderScreen2V2(
            serviceCategory = serviceCategory,
            serviceType = serviceType,
            onContinue = { serviceName, serviceDescription, serviceMin, serviceMax, servicePrice, serviceUnity ->
                val existingBundle = backStackEntry.arguments
                val newBundle = bundleOf(
                    "serviceName" to serviceName,
                    "serviceDescription" to serviceDescription,
                    "serviceMin" to serviceMin,
                    "serviceMax" to serviceMax,
                    "servicePrice" to servicePrice,
                    "serviceUnity" to serviceUnity
                )
                val finalBundle = existingBundle.also { it?.putAll(newBundle) }
                navController.navigate(ServicesScreensV2.AddServiceProvider3V2.route, finalBundle)
            },
            onBackClicked = {
                navController.popBackStack()
            }
        )
    }

    composable(
        route = ServicesScreensV2.AddServiceProvider3V2.route
    ) { backStackEntry ->
        AddServiceProviderScreen3V2(
            onContinue = { address: AddressData, distance: Int ->
                val existingBundle = backStackEntry.arguments
                val newBundle = bundleOf(
                    "address" to address,
                    "distance" to distance
                )
                val finalBundle = existingBundle.also { it?.putAll(newBundle) }
                navController.navigate(ServicesScreensV2.AddServiceProvider4V2.route, finalBundle)
            },
            onBackClicked = {
                navController.popBackStack()
            }
        )
    }

    composable(
        route = ServicesScreensV2.AddServiceProvider4V2.route
    ) { backStackEntry ->
        val serviceCategory: ServiceCategory = backStackEntry.getArgument()!!
        val serviceType: ServiceType? = backStackEntry.getArgument()
        val subService: SubService? = backStackEntry.getArgument()
        val selectedAttributes = backStackEntry.arguments?.getParcelableArrayList("selectedAttributes", Attribute::class.java).orEmpty()
        val suggestedAttributes = backStackEntry.arguments?.getStringArrayList("suggestedAttributes").orEmpty()
        val serviceName = backStackEntry.arguments?.getString("serviceName").orEmpty()
        val serviceDesc = backStackEntry.arguments?.getString("serviceDescription").orEmpty()
        val serviceMin = backStackEntry.arguments?.getString("serviceMin").orEmpty()
        val serviceMax = backStackEntry.arguments?.getString("serviceMax").orEmpty()
        val servicePrice = backStackEntry.arguments?.getString("servicePrice").orEmpty()
        val serviceUnity = backStackEntry.arguments?.getString("serviceUnity").orEmpty()
        val address: AddressData = backStackEntry.getArgument()!!
        val distance = backStackEntry.arguments?.getInt("distance")!!

        AddServiceProviderScreen4V2(
            serviceCategory = serviceCategory,
            serviceType = serviceType,
            subService = subService,
            selectedAttributes = selectedAttributes,
            suggestedAttributes = suggestedAttributes,
            serviceName = serviceName,
            serviceDesc = serviceDesc,
            serviceMin = serviceMin,
            serviceMax = serviceMax,
            servicePrice = servicePrice,
            serviceUnity = serviceUnity,
            address = address,
            distance = distance,
            onSuccessServiceCreated = {
                navController.clearBackStack()
                navController.navigate(ServicesScreensV2.SuccessServiceCreatedV2.route)
            },
            onBackClicked = {
                navController.popBackStack()
            }
        )
    }

    composable(
        route = ServicesScreensV2.SuccessServiceCreatedV2.route
    ) {
        SuccessServiceCreatedScreenV2()
    }
}

sealed class ServicesScreensV2(val route: String) {
    object ServicesCategoriesV2 : ServicesScreensV2(route = "SERVICES_CATEGORIES_V2")
    object ServicesTypesV2 : ServicesScreensV2(route = "SERVICES_TYPES_V2")
    object SubServicesV2 : ServicesScreensV2(route = "SUB_SERVICES_V2")
    object AddServiceProvider1V2: ServicesScreens(route = "ADD_SERVICE_PROVIDER1_V2")
    object AddServiceProvider2V2: ServicesScreens(route = "ADD_SERVICE_PROVIDER2_V2")
    object AddServiceProvider3V2: ServicesScreens(route = "ADD_SERVICE_PROVIDER3_V2")
    object AddServiceProvider4V2: ServicesScreens(route = "ADD_SERVICE_PROVIDER4_V2")
    object SuccessServiceCreatedV2 : AuthScreen(route = "SUCCESS_SERVICE_CREATED_V2")
}
