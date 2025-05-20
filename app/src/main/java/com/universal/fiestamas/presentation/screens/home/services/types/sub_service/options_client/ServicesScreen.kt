package com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.FirstQuestionsProvider
import com.universal.fiestamas.domain.models.Location
import com.universal.fiestamas.domain.models.ScreenInfo
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.domain.models.response.FirebaseUserDb
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.home.main.MainParentClass
import com.universal.fiestamas.presentation.ui.CardServiceOption
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.LinkedStrings
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.ViewDropDownMenu
import com.universal.fiestamas.presentation.ui.backgrounds.CardServicesBackground
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.utils.Constants
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.showToastOnUiThread
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ServicesScreen (
    vm: ServicesViewModel = hiltViewModel(),
    vma: AuthViewModel = hiltViewModel(),
    screenInfo: ScreenInfo,
    onAuthProcessStarted: () -> Unit,
    onNavigateToDetailsServiceClicked: (ScreenInfo) -> Unit,
    onTitleScreenClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current
    val user by vma.firebaseUserDb.collectAsState()
    val firebaseUser by vma.firebaseUser.collectAsState()
    val providerEventCreated by vm.eventCreated.collectAsState()
    var isUserSignedIn by remember { mutableStateOf(false) }
    var showProgressDialog by remember { mutableStateOf(false) }

    vma.checkIfUserIsSignedIn()

    if (screenInfo.questionsProvider != null) {
        // is provider and wants to create self event
        ProgressDialog(
            isVisible = vm.showProgressDialogForProvider.collectAsState().value,
            message = stringResource(R.string.progress_creating_event)
        )
        vm.createEventByProvider(
            clientId = MainParentClass.userId.orEmpty(),
            eventId = screenInfo.event.id,
            questions = screenInfo.questionsProvider
        )
        vm.getAllServicesByProvider(MainParentClass.userId.orEmpty())
    } else {
        // is client and is adding services to his event
        vm.getServicesAccordingData(context, screenInfo)
    }

    ProgressDialog(showProgressDialog)

    LaunchedEffect(firebaseUser) {
        isUserSignedIn = firebaseUser != null && firebaseUser?.email != null
    }

    GradientBackground(
        content = {
            Column(
                modifier = Modifier
                    .padding(bottom = 15.dp)
                    .sidePadding()
            ) {
                LinkedStrings(
                    strings = if (screenInfo.questionsProvider != null) {
                        listOf(stringResource(R.string.service_select_service))
                    } else {
                        vm.getLinkedStrings(screenInfo)
                    },
                    modifier = Modifier.padding(start = 10.dp),
                    small = true,
                    separator = "<"
                )
                VerticalSpacer(10.dp)
                CardServicesBackground {
                    ClientView(
                        questionsEventProvider = screenInfo.questionsProvider,
                        user = user,
                        screenInfo = screenInfo,
                        servicesByType = if (screenInfo.questionsProvider != null) {
                            vm.allServicesProvider.collectAsState().value
                        } else {
                            vm.servicesByType.collectAsState().value
                        },
                        onHeartClicked = { service ->
                            if (!isUserSignedIn) {
                                onAuthProcessStarted()
                                return@ClientView
                            }
                            firebaseUser?.uid?.let { userId ->
                                showProgressDialog = true
                                vm.likeService(userId, service.id) {
                                    showProgressDialog = false
                                    vm.alreadyLikedService = false
                                }
                            }
                        },
                        onNavigateToDetailsServiceClicked = { screenInfo ->
                            if (screenInfo.questionsProvider != null) {
                                // provider
                                if (providerEventCreated?.data?.id == null) {
                                    showToastOnUiThread(context, "Error: Provider event is null")
                                } else {
                                    println("Provider event ID: ${providerEventCreated?.data?.id}")
                                    screenInfo.clientEventId = providerEventCreated?.data?.id
                                    onNavigateToDetailsServiceClicked(screenInfo)
                                }
                            } else {
                                // client
                                onNavigateToDetailsServiceClicked(screenInfo)
                            }
                        }
                    )
                }
            }
        },
        titleScreen = screenInfo.event.name,
        showLogoFiestamas = false,
        addBottomPadding = false,
        onBackButtonClicked = { onBackClicked() },
        onTitleScreenClicked = {
            if (screenInfo.event.name.isNotEmpty()) {
                onTitleScreenClicked()
            }
        }
    )
}

@Composable
fun ClientView(
    vm: ServicesViewModel = hiltViewModel(),
    questionsEventProvider: FirstQuestionsProvider?,
    servicesByType: List<Service?>?,
    user: FirebaseUserDb?,
    screenInfo: ScreenInfo,
    onNavigateToDetailsServiceClicked: (ScreenInfo) -> Unit,
    onHeartClicked: (Service) -> Unit
) {

    val listOfServices = remember { mutableStateListOf<Service>() }

    DisposableEffect(servicesByType) {
        val address = vm.getUserAddressIfExists()
        if (address == null) {
            listOfServices.clear()
            servicesByType?.filterNotNull()?.let { services ->
                listOfServices.addAll(services)
            }
        } else {
            val tempItemList = mutableListOf<Service>()
            val userLatitude = address.location?.lat?.toDouble() ?: 0.0
            val userLongitude = address.location?.lng?.toDouble() ?: 0.0

            servicesByType?.filterNotNull()?.forEach { service ->
                val serviceLatitude = service.lat?.toDouble() ?: 0.0
                val serviceLongitude = service.lng?.toDouble() ?: 0.0
                val distanceBetweenUserAndService = calculateDistanceBetweenPoints(
                    lat1 = userLatitude,
                    lng1 = userLongitude,
                    lat2 = serviceLatitude,
                    lng2 = serviceLongitude
                )
                if (distanceBetweenUserAndService <= 40) {
                    tempItemList.add(service)
                }
            }
            listOfServices.clear()
            listOfServices.addAll(tempItemList)
        }
        onDispose {  }
    }

    val optionsDistanceMax = listOf(
        Pair("40km", 40),
        Pair("80km", 80),
        Pair("120km", 120),
        Pair("160km", 160),
        Pair("200km", 200),
        Pair("240km", 240),
        Pair("280km", 280),
        Pair("320km", 320),
        Pair("360km", 360),
        Pair("400km", 400)
    )

    with (vm.getUserAddressIfExists()) {
        val address = this
        val userLocation: Location?

        if (address != null) {
            userLocation = address.location
            Row(
                modifier = Modifier.padding(10.dp.autoSize()),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp.autoSize())
                )
                HorizontalSpacer(width = 6.dp)
                TextMedium(
                    text = if (address.city.isNullOrEmpty()) "" else address.city ?: "",
                    fillMaxWidth = false,
                    size = 15.sp.autoSize()
                )

                HorizontalSpacer(width = 15.dp)

                ViewDropDownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    addWhiteBackground = false,
                    placeholder = stringResource(id = R.string.service_distance_max_one),
                    options = optionsDistanceMax.map { it.first },
                    onItemSelected = { optionSelected ->
                        val tempItemList = mutableListOf<Service>()
                        val distanceSelected = optionsDistanceMax.find { it.first == optionSelected }?.second!!
                        val userLatitude = userLocation?.lat?.toDouble() ?: 0.0
                        val userLongitude = userLocation?.lng?.toDouble() ?: 0.0

                        servicesByType?.filterNotNull()?.forEach { service ->
                            val serviceLatitude = service.lat?.toDouble() ?: 0.0
                            val serviceLongitude = service.lng?.toDouble() ?: 0.0
                            val distanceBetweenUserAndService = calculateDistanceBetweenPoints(
                                lat1 = userLatitude,
                                lng1 = userLongitude,
                                lat2 = serviceLatitude,
                                lng2 = serviceLongitude
                            )
                            if (distanceBetweenUserAndService <= distanceSelected) {
                                tempItemList.add(service)
                            }
                        }
                        listOfServices.clear()
                        listOfServices.addAll(tempItemList)
                    }
                )
            }
        }
    }

    listOfServices.let { options ->
        if (options.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(Constants.TWO_COLUMNS),
                contentPadding = PaddingValues(vertical = 6.dp, horizontal = 2.dp),
                modifier = Modifier
            ) {
                itemsIndexed(options) { i, item ->
                    CardServiceOption(
                        service = item,
                        user = user,
                        index = i,
                        onItemClick = { service ->
                            val nService = Service(service.id, service.name)
                            val mScreenInfo = ScreenInfo(
                                role = screenInfo.role,
                                startedScreen = screenInfo.startedScreen,
                                prevScreen = screenInfo.prevScreen,
                                event = screenInfo.event,
                                questions = screenInfo.questions,
                                questionsProvider = questionsEventProvider,
                                serviceCategory = screenInfo.serviceCategory,
                                clientEventId = screenInfo.clientEventId,
                                serviceType = screenInfo.serviceType,
                                subService = screenInfo.subService,
                                service = nService
                            )
                            onNavigateToDetailsServiceClicked(mScreenInfo)
                        },
                        onHeartClick = { onHeartClicked(it) }
                    )
                }
            }   
        } else {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                TextRegular(
                    modifier = Modifier.align(Alignment.Center),
                    text = "No hay servicios disponibles por el momento.\nIntente otra categoría o vuelva más tarde.",
                    size = 16.sp.autoSize(),
                    verticalSpace = 17.sp.autoSize()
                )
            }
        }
    }
}

fun calculateDistanceBetweenPoints(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val earthRadius = 6371 // earth radius in km
    val lat1InRadians = lat1.toRadians()
    val lat2InRadians = lat2.toRadians()
    val deltaLatInRadians = (lat2 - lat1).toRadians()
    val deltaLngInRadians = (lng2 - lng1).toRadians()

    val a = sin(deltaLatInRadians / 2.0) * sin(deltaLatInRadians / 2) +
            cos(lat1InRadians) * cos(lat2InRadians) * sin(deltaLngInRadians / 2) * sin(deltaLngInRadians / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadius * c // distance in km
}

fun Double.toRadians(): Double {
    return (this * (Math.PI / 180))
}
