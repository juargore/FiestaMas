package com.universal.fiestamas.presentation.navigation

import androidx.core.os.bundleOf
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.universal.fiestamas.domain.models.ListMediaItemService
import com.universal.fiestamas.domain.models.Role
import com.universal.fiestamas.domain.models.Screen
import com.universal.fiestamas.domain.models.ScreenInfo
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.domain.models.ServiceProviderData
import com.universal.fiestamas.domain.models.isShared
import com.universal.fiestamas.presentation.screens.camera.RecordingScreen
import com.universal.fiestamas.presentation.screens.home.services.ServicesCategoriesScreen
import com.universal.fiestamas.presentation.screens.home.services.types.ServicesTypesScreen
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.SubServicesScreen
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.add_edit_provider.AddEditProviderFirstStepScreen
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.add_edit_provider.AddEditProviderSecondStepScreen
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.ServicesScreen
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details.DetailsServiceScreen
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details.photos.PhotoViewerScreen
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details.photos.ServicePhotosScreen
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details.photos.VideoViewerScreen
import com.universal.fiestamas.presentation.utils.extensions.clearBackStack
import com.universal.fiestamas.presentation.utils.extensions.getArgument
import com.universal.fiestamas.presentation.utils.extensions.navigate
import com.universal.fiestamas.presentation.utils.extensions.navigateToSourceScreen
import com.universal.fiestamas.presentation.utils.extensions.popBackStackMultiple

fun NavGraphBuilder.servicesSelectionNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.SERVICES_SELECTION,
        startDestination = ServicesScreens.ServicesSelection.route
    ) {
        composable(
            route = ServicesScreens.ServicesSelection.route
        ) { backStackEntry ->
            val screenInfoArg: ScreenInfo? = backStackEntry.getArgument()
            val serviceId: String? = backStackEntry.arguments?.getString("serviceId")

            if (screenInfoArg?.service.isShared()) {
                // is a shared link opened by app -> redirect to services details
                val bundle = bundleOf("screenInfo" to screenInfoArg)
                navController.navigate(ServicesScreens.ServiceDetails.route, bundle)
                return@composable
            }

            if (screenInfoArg?.questionsProvider != null) {
                // is a provider that wants to add a self event -> redirect to services options
                val bundle = bundleOf("screenInfo" to screenInfoArg)
                navController.navigate(ServicesScreens.ServicesOptions.route, bundle)
                return@composable
            }

            // comes from popup to edit service for provider -> open screen to edit
            if (serviceId != null) {
                AddEditProviderFirstStepScreen(
                    serviceId = serviceId,
                    screenInfo = null,
                    onBackClicked = { navController.popBackStack() },
                    onNavigateToAddOrEditServiceProviderSecondScreenClicked =
                    { screenInfo, serviceProviderData, images, videos, isEditing ->
                        val bundle = bundleOf(
                            "screenInfo" to screenInfo,
                            "serviceProviderData" to serviceProviderData,
                            "isEditing" to isEditing,
                            "images" to images,
                            "videos" to videos
                        )
                        navController.navigate(ServicesScreens.AddOrEditServiceProvider2.route, bundle)
                    }
                )
                return@composable
            }

            // event_type was clicked -> jump to first screen
            if (screenInfoArg?.serviceCategory == null && screenInfoArg != null) {
                ServicesCategoriesScreen(
                    screenInfo = screenInfoArg,
                    onNavigateServicesTypesClicked = { screenInfo ->
                        val bundle = bundleOf("screenInfo" to screenInfo)
                        navController.navigate(ServicesScreens.ServicesTypes.route, bundle)
                    },
                    onAuthProcessStarted = {
                        val bundle = bundleOf("showStartEmailScreen" to true)
                        navController.navigate(Graph.AUTHENTICATION, bundle)
                    },
                    onBackClicked = { navController.popBackStack() }
                )
                return@composable
            }

            // service_category was clicked -> jump to second screen
            if (screenInfoArg != null) {
                ServicesTypesScreen(
                    screenInfo = screenInfoArg,
                    onNavigateSubServicesClicked = { screenInfo ->
                        if (screenInfo.serviceType == null) {
                            // there is no services types (no sub-services either)
                            // if client -> go to services options screen
                            // if provider -> go to add service screen 1
                            if (screenInfo.startedScreen == Screen.ServiceTypes) {
                                // user clicked in service type card on home screen
                                // service category screen was skipped
                                screenInfo.prevScreen = Screen.None
                            } else {
                                navController.popBackStack()
                                screenInfo.prevScreen = Screen.ServiceCategories
                            }
                            val bundle = bundleOf("screenInfo" to screenInfo)
                            if (screenInfo.role == Role.Provider) {
                                navController.navigate(ServicesScreens.AddOrEditServiceProvider1.route, bundle)
                            } else {
                                navController.navigate(ServicesScreens.ServicesOptions.route, bundle)
                            }
                        } else {
                            // there is at least one service type -> go to sub-services screen
                            val bundle = bundleOf("screenInfo" to screenInfo)
                            navController.navigate(ServicesScreens.SubServices.route, bundle)
                        }
                    },
                    onNavigateServicesOptionsClicked = { screenInfo ->
                        val bundle = bundleOf("screenInfo" to screenInfo)
                        navController.navigate(ServicesScreens.ServicesOptions.route, bundle)
                    },
                    onAuthProcessStarted = {
                        val bundle = bundleOf("showStartEmailScreen" to true)
                        navController.navigate(Graph.AUTHENTICATION, bundle)
                    },
                    onBackClicked = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = ServicesScreens.ServicesTypes.route
        ) { backStackEntry ->
            val screenInfoArg: ScreenInfo = backStackEntry.getArgument()!!

            ServicesTypesScreen(
                screenInfo = screenInfoArg,
                onNavigateSubServicesClicked = { screenInfo ->
                    if (screenInfo.serviceType == null) {
                        navController.popBackStack()
                        screenInfo.prevScreen = Screen.ServiceCategories
                        val bundle = bundleOf("screenInfo" to screenInfo)
                        navController.navigate(ServicesScreens.ServicesOptions.route, bundle)
                    } else {
                        val bundle = bundleOf("screenInfo" to screenInfo)
                        navController.navigate(ServicesScreens.SubServices.route, bundle)
                    }
                },
                onNavigateServicesOptionsClicked = { screenInfo ->
                    val bundle = bundleOf("screenInfo" to screenInfo)
                    navController.navigate(ServicesScreens.ServicesOptions.route, bundle)
                },
                onAuthProcessStarted = {
                    val bundle = bundleOf("showStartEmailScreen" to true)
                    navController.navigate(Graph.AUTHENTICATION, bundle)
                },
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable(
            route = ServicesScreens.SubServices.route
        ) {backStackEntry ->
            val screenInfoArg: ScreenInfo = backStackEntry.getArgument()!!

            SubServicesScreen(
                screenInfo = screenInfoArg,
                onNavigateServicesOptionsClicked = { screenInfo ->
                    if (screenInfo.subService == null) {
                        screenInfo.prevScreen = Screen.ServiceTypes
                        navController.popBackStack()
                    }
                    val bundle = bundleOf("screenInfo" to screenInfo)
                    if (screenInfo.role == Role.Provider) {
                        navController.navigate(ServicesScreens.AddOrEditServiceProvider1.route, bundle)
                    } else {
                        navController.navigate(ServicesScreens.ServicesOptions.route, bundle)
                    }
                },
                onTitleScreenClicked = {
                    if (screenInfoArg.prevScreen == Screen.ServiceTypes &&
                        screenInfoArg.startedScreen == Screen.ServiceCategories) {
                        navController.popBackStackMultiple(2)
                    }
                    if (screenInfoArg.prevScreen == Screen.ServiceTypes &&
                        screenInfoArg.startedScreen == Screen.ServiceTypes) {
                        navController.popBackStackMultiple(1)
                    }
                },
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable(
            route = ServicesScreens.ServicesOptions.route
        ) {backStackEntry ->
            val screenInfoArg: ScreenInfo = backStackEntry.getArgument()!!

            ServicesScreen(
                screenInfo = screenInfoArg,
                onNavigateToDetailsServiceClicked = { screenInfo ->
                    try {
                        val bundle = bundleOf("screenInfo" to screenInfo)
                        navController.navigate(ServicesScreens.ServiceDetails.route, bundle)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                onAuthProcessStarted = {
                    val bundle = bundleOf("showStartEmailScreen" to true)
                    navController.navigate(Graph.AUTHENTICATION, bundle)
                },
                onBackClicked = {
                    if (screenInfoArg.questionsProvider != null) {
                        navController.clearBackStack()
                    } else {
                        if (screenInfoArg.startedScreen == Screen.ServiceTypes &&
                            screenInfoArg.prevScreen == Screen.None) {
                            navController.clearBackStack()
                        } else {
                            navController.popBackStack()
                        }
                    }
                },
                onTitleScreenClicked = {
                    navigateToSourceScreen(
                        screenInfoArg =  screenInfoArg,
                        navController = navController
                    )
                }
            )
        }

        composable(
            route = ServicesScreens.AddOrEditServiceProvider1.route
        ) { backStackEntry ->
            val screenInfoArg: ScreenInfo = backStackEntry.getArgument()!!

            AddEditProviderFirstStepScreen(
                serviceId = null,
                screenInfo = screenInfoArg,
                onBackClicked = {
                    if (screenInfoArg.prevScreen == Screen.None) {
                        navController.clearBackStack()
                    } else {
                        navController.popBackStack()
                    }
                },
                onNavigateToAddOrEditServiceProviderSecondScreenClicked =
                { screenInfo, serviceProviderData, images, videos, isEditing ->
                    val bundle = bundleOf(
                        "screenInfo" to screenInfo,
                        "serviceProviderData" to serviceProviderData,
                        "isEditing" to isEditing,
                        "images" to images,
                        "videos" to videos
                    )
                    navController.navigate(ServicesScreens.AddOrEditServiceProvider2.route, bundle)
                }
            )
        }

        composable(
            route = ServicesScreens.AddOrEditServiceProvider2.route
        ) { backStackEntry ->
            val screenInfoArg: ScreenInfo = backStackEntry.getArgument()!!
            val serviceProviderData: ServiceProviderData = backStackEntry.getArgument()!!
            val isEditing = backStackEntry.arguments?.getBoolean("isEditing") ?: false
            val images = backStackEntry.arguments?.getStringArrayList("images") ?: emptyList<String>()
            val videos = backStackEntry.arguments?.getStringArrayList("videos") ?: emptyList<String>()

            AddEditProviderSecondStepScreen(
                isEditing = isEditing,
                screenInfo = screenInfoArg,
                images = images,
                videos = videos,
                serviceProviderData = serviceProviderData,
                onTitleClickedOrServiceAdded = {
                    navigateToSourceScreen(
                        screenInfoArg =  screenInfoArg,
                        navController = navController,
                        extraScreen = 1,
                    )
                },
                onOpenRecordingScreen = {
                    navController.navigate(ServicesScreens.RecordingScreen.route, bundleOf())
                },
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable(
            route = ServicesScreens.ServiceDetails.route
        ) { backStackEntry ->
            val screenInfoArg: ScreenInfo = backStackEntry.getArgument()!!

            DetailsServiceScreen(
                screenInfo = screenInfoArg,
                onNavigateToPhotosClicked = { photosList ->
                    val bundle = bundleOf(
                        "listMediaItemService" to photosList,
                        "service" to screenInfoArg.service
                    )
                    navController.navigate(ServicesScreens.ServicePhotos.route, bundle)
                },
                onAuthProcessStarted = {
                    val bundle = bundleOf("showStartEmailScreen" to true)
                    navController.navigate(Graph.AUTHENTICATION, bundle)
                },
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable(
            route = ServicesScreens.ServicePhotos.route
        ) { backStackEntry ->
            val mediaList: ListMediaItemService? = backStackEntry.getArgument()
            val service: Service? = backStackEntry.getArgument()

            if (mediaList != null && service != null) {
                ServicePhotosScreen(
                    mediaList = mediaList.list,
                    service = service,
                    onNavigatePhotoViewerClicked = { list, selectedPhoto ->
                        val bundle = bundleOf(
                            "photosList" to list,
                            "selectedPhoto" to selectedPhoto
                        )
                        navController.navigate(ServicesScreens.ServicePhotoViewer.route, bundle)
                    },
                    onNavigateVideoViewerClicked = { videoUrl ->
                        val bundle = bundleOf("videoUrl" to videoUrl)
                        navController.navigate(ServicesScreens.ServiceVideoViewer.route, bundle)
                    },
                    onAuthProcessStarted = {
                        val bundle = bundleOf("showStartEmailScreen" to true)
                        navController.navigate(Graph.AUTHENTICATION, bundle)
                    },
                    onBackClicked = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = ServicesScreens.ServicePhotoViewer.route
        ) { backStackEntry ->
            val photosList = backStackEntry.arguments?.getStringArrayList("photosList")
            val selectedPhoto = backStackEntry.arguments?.getString("selectedPhoto")

            if (photosList != null && selectedPhoto != null) {
                PhotoViewerScreen(
                    photosList = photosList,
                    selectedPhoto = selectedPhoto,
                    onBackClicked = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = ServicesScreens.ServiceVideoViewer.route
        ) { backStackEntry ->
            val videoUrl = backStackEntry.arguments?.getString("videoUrl")
            if (videoUrl != null) {
                VideoViewerScreen(
                    url = videoUrl,
                    onBackClicked = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = ServicesScreens.RecordingScreen.route
        ) {
            RecordingScreen(
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class ServicesScreens(val route: String) {
    object ServicesSelection : ServicesScreens(route = "SERVICES_SELECTION")
    object ServicesTypes: ServicesScreens(route = "SERVICES_TYPES")
    object SubServices: ServicesScreens(route = "SUB_SERVICES")
    object ServicesOptions: ServicesScreens(route = "SERVICES_OPTIONS")
    object ServiceDetails: ServicesScreens(route = "SERVICE_DETAILS")
    object AddOrEditServiceProvider1: ServicesScreens(route = "ADD_EDIT_SERVICE_PROVIDER1")
    object AddOrEditServiceProvider2: ServicesScreens(route = "ADD_EDIT_SERVICE_PROVIDER2")
    object ServicePhotos: ServicesScreens(route = "SERVICE_PHOTOS")
    object ServicePhotoViewer: ServicesScreens(route = "SERVICE_PHOTO_VIEWER")
    object ServiceVideoViewer: ServicesScreens(route = "SERVICE_VIDEO_VIEWER")
    object RecordingScreen: ServicesScreens(route = "RECORDING_SCREEN")
}
