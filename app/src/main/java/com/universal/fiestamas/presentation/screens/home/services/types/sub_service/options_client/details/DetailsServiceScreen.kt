package com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.common.ErrorResponse
import com.universal.fiestamas.domain.models.Event
import com.universal.fiestamas.domain.models.FirstQuestionsClientStored
import com.universal.fiestamas.domain.models.ListMediaItemService
import com.universal.fiestamas.domain.models.MediaItemService
import com.universal.fiestamas.domain.models.Role
import com.universal.fiestamas.domain.models.ScreenInfo
import com.universal.fiestamas.domain.models.isShared
import com.universal.fiestamas.presentation.screens.location.AddressAutoCompleteScreen
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details.DetailsServiceViewModel.LogServiceType
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.home.main.MainParentClass
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCorners
import com.universal.fiestamas.presentation.ui.CarouselPhotosAndVideos
import com.universal.fiestamas.presentation.ui.ExpandableText
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.RatingStar
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.CardServicesBackground
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.cards.CardServiceAttribute
import com.universal.fiestamas.presentation.ui.dialogs.ErrorDialog
import com.universal.fiestamas.presentation.ui.dialogs.FirstQuestionsDialog
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.ui.dialogs.SuccessDialogCustom
import com.universal.fiestamas.presentation.utils.Constants.ONE_SECOND
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.resetApplication
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.makePhoneCall
import com.universal.fiestamas.presentation.utils.showToast
import com.universal.fiestamas.presentation.utils.showToastOnUiThread
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DetailsServiceScreen(
    vm: DetailsServiceViewModel = hiltViewModel(),
    vma: AuthViewModel = hiltViewModel(),
    screenInfo: ScreenInfo,
    onNavigateToPhotosClicked: (ListMediaItemService) -> Unit,
    onAuthProcessStarted: () -> Unit,
    onBackClicked: () -> Unit
) {
    vma.checkIfUserIsSignedIn()
    vma.getFirebaseUserDb(MainParentClass.userId)

    screenInfo.service?.id?.let {
        vm.getServiceDetails(it)
    }

    val service by vm.service.collectAsState()
    val attributes by vm.attributes.collectAsState()
    val firebaseUserDb by vma.firebaseUserDb.collectAsState()
    val firebaseProviderDb by vma.firebaseProviderDb.collectAsState()
    val firebaseUser by vma.firebaseUser.collectAsState()

    val context = LocalContext.current
    var isUserSignedIn by remember { mutableStateOf(false) }
    var showProgressDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var validateErrorDialog by remember { mutableStateOf(true) }
    var serviceEventIdCreated  by remember { mutableStateOf("") }
    var showErrorToast: Pair<Boolean, String?> by remember { mutableStateOf(Pair(false, "")) }

    service?.id_provider?.let {
        vma.getFirebaseProviderDb(it)
    }

    LaunchedEffect(firebaseUser) {
        isUserSignedIn = firebaseUser != null && firebaseUser?.email != null
        screenInfo.service?.id?.let {
            vm.recordLogService(LogServiceType.VIEW, it)
        }
    }

    BackHandler {
        if (screenInfo.service.isShared()) {
            context.resetApplication()
        } else {
            onBackClicked()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { context.resetApplication() }

    if (showSuccessDialog) {
        val serviceTypeName = screenInfo.serviceCategory?.name.orEmpty()
        val serviceName = service?.name.orEmpty() // "La Fuente Rosa",
        val providerName = firebaseProviderDb?.name
        val providerLastName = firebaseProviderDb?.last_name
        val providerPhone = firebaseProviderDb?.phone_one.orEmpty()
        val serviceImage = service?.image ?: service?.images?.get(0).orEmpty()

        SuccessDialogCustom(
            serviceType = serviceTypeName,
            serviceName = serviceName,
            contactName = "$providerName $providerLastName",
            image = serviceImage,
            phone = providerPhone,
            email = stringResource(id = R.string.service_detail_message),
            whatsapp = stringResource(id = R.string.service_detail_whatsapp),
            onDismiss = { showSuccessDialog = false },
            onPhoneClicked = {
                showSuccessDialog = false
                makePhoneCall(context, firebaseProviderDb?.phone_one)
                context.resetApplication(ONE_SECOND)
            },
            onEmailClicked = {
                showSuccessDialog = false
                vm.saveServiceIdForNotification(serviceEventIdCreated)
                context.resetApplication()
            },
            onWhatsappClicked = {
                showSuccessDialog = false
                launcher.launch(vm.getIntentForWhatsApp(providerPhone))
            },
            onOkClicked = {
                showSuccessDialog = false
                context.resetApplication()
            }
        )
    }

    if (showErrorDialog && !showSuccessDialog) {
        ErrorDialog(
            showStatus = false,
            error = ErrorResponse(
                message = "Servicio previamente agregado\n\nEste servicio ya fue agregado anteriormente a tu evento.",
                status = 0
            ),
            onDismiss = {
                // in case user press 'Contact' again, avoid progress infinite
                vm.alreadyAddedServiceToExistingEvent = false
                showErrorDialog = false
            }
        )
    }

    if (showErrorToast.first) {
        showToast(context, showErrorToast.second ?: "Error al agregar servicio. Revise los logs.")
    }

    ProgressDialog(showProgressDialog)

    val mediaListForCarrousel = mutableListOf<MediaItemService>()
    (service?.images.orEmpty() + service?.videos.orEmpty()).also { combinedList ->
        for (item in combinedList) {
            if (item.contains(".mp4", true) || item.contains(".3gp", true)) {
                mediaListForCarrousel.add(MediaItemService(url = item, isVideo = true))
            } else {
                mediaListForCarrousel.add(MediaItemService(url = item, isVideo = false))
            }
        }
    }

    var showClientQuestionsDialog by remember { mutableStateOf(false) }
    var savedQuestionsForClient: FirstQuestionsClientStored? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.Expanded },
        skipHalfExpanded = true
    )

    BackHandler(modalSheetState.isVisible) {
        coroutineScope.launch { modalSheetState.hide() }
    }

    GradientBackground(
        content = {
            // load address data if exists from categories
            with (vm.getUserAddressIfExists()) {
                if (this != null) {
                    savedQuestionsForClient = FirstQuestionsClientStored(
                        event = null,
                        festejadosNames = "",
                        date = "",
                        time = "",
                        numberOfGuests = "",
                        city = this.city.orEmpty(),
                        location = this.location
                    )
                }
            }

            ModalBottomSheetLayout(
                sheetState = modalSheetState,
                sheetContent = {
                    AddressAutoCompleteScreen(searchForCities = true) { mAddress, _ ->
                        savedQuestionsForClient?.location = mAddress?.location
                        savedQuestionsForClient?.city = mAddress?.city.orEmpty()
                        coroutineScope.launch { modalSheetState.hide() }
                        showClientQuestionsDialog = true
                    }
                }
            ) {
                Scaffold {
                    if (showClientQuestionsDialog) {
                        FirstQuestionsDialog(
                            showEventsDropDown = screenInfo.service.isShared(),
                            serviceCategoryId = service?.id_service_category,
                            savedQuestions = savedQuestionsForClient,
                            onDismiss = {
                                savedQuestionsForClient = null
                                showClientQuestionsDialog = false
                            },
                            onAddressClicked = { questions ->
                                savedQuestionsForClient = questions
                                showClientQuestionsDialog = false
                                coroutineScope.launch { modalSheetState.show() }
                            },
                            onContinueClicked = { questions, event: Event? ->
                                showClientQuestionsDialog = false
                                showProgressDialog = true

                                val eventId = if (screenInfo.service.isShared()) event!!.id else screenInfo.event.id

                                vm.createEventByClient(
                                    clientId = firebaseUser!!.uid,
                                    eventId = eventId,
                                    questions = questions,
                                    onSuccess = {
                                        println("Success creating client event -> ID: ${it.data?.id}")

                                        // record log contact service on server
                                        vm.alreadyLoggedServiceContact = false
                                        vm.recordLogService(LogServiceType.CONTACT, screenInfo.service?.id)

                                        vm.addServiceToExistingEvent(
                                            eventId = it.data?.id.orEmpty(),
                                            serviceId = screenInfo.service?.id.orEmpty()
                                        ) { success, message, serviceEventId ->
                                            println("Response adding service to event -> ID: $serviceEventId")
                                            showProgressDialog = false
                                            if (success) {
                                                serviceEventIdCreated = serviceEventId
                                                showSuccessDialog = true
                                            } else {
                                                showErrorToast = Pair(true, message)
                                            }
                                        }
                                    },
                                    onFailure = {
                                        showProgressDialog = false
                                        showToastOnUiThread(context, it.message)
                                    }
                                )
                            }
                        )
                    }

                    Column(
                        modifier = Modifier
                            .padding(bottom = 15.dp, top = 10.dp)
                            .sidePadding()
                    ) {
                        CardServicesBackground(
                            isForDetailsService = true
                        ) {
                            LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
                                item { VerticalSpacer(10.dp) }
                                item {
                                    if (mediaListForCarrousel.isNotEmpty()) {
                                        CarouselPhotosAndVideos(mediaListForCarrousel) {
                                            onNavigateToPhotosClicked(ListMediaItemService(list = mediaListForCarrousel))
                                        }
                                    }
                                }
                                item { VerticalSpacer(5.dp) }
                                item {
                                    TextMedium(
                                        text = stringResource(id = R.string.service_verified_provider).toUpperCase(Locale.current),
                                        size = 12.sp.autoSize(),
                                        color = PinkFiestamas,
                                        align = TextAlign.Start
                                    )
                                }
                                item {
                                    VerticalSpacer(15.dp)
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                        items(attributes.orEmpty()) { attr ->
                                            CardServiceAttribute(
                                                icon = attr?.icon,
                                                name = attr?.name
                                            )
                                        }
                                    }
                                    VerticalSpacer(20.dp)
                                }
                                item {
                                    TextSemiBold(
                                        text = service?.name.orEmpty(),
                                        size = 22.sp.autoSize(),
                                        align = TextAlign.Start,
                                        includeFontPadding = false,
                                        horizontalSpace = (-1).sp,
                                    )
                                }
                                item {
                                    RatingStar(
                                        rating = service?.rating?.toFloat() ?: 0f,
                                        maxRating = 5,
                                        starSize = 16.dp.autoSize(),
                                        onRatingChanged = { }
                                    )
                                }
                                item { VerticalSpacer(10.dp) }
                                item {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            modifier = Modifier.height(15.dp.autoSize()),
                                            painter = painterResource(id = R.drawable.ic_location),
                                            contentDescription = null
                                        )
                                        HorizontalSpacer(width = 8.dp)
                                        TextSemiBold(
                                            text = service?.address.orEmpty(),
                                            size = 13.sp.autoSize(),
                                            color = PinkFiestamas,
                                            align = TextAlign.Start,
                                            includeFontPadding = false,
                                            modifier = Modifier.clickable {
                                                vm.openGoogleMaps(context, service?.lat, service?.lng)
                                            }
                                        )
                                    }
                                }
                                item { VerticalSpacer(20.dp) }
                                item { ExpandableText(service) } // Service description
                                item { VerticalSpacer(50.dp) }
                                item {
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        val buttonText = if (screenInfo.questionsProvider != null) {
                                            stringResource(id = R.string.service_add_service)
                                        } else {
                                            stringResource(id = R.string.service_contact)
                                        }.toUpperCase(Locale.current)

                                        ButtonPinkRoundedCorners(
                                            modifier = Modifier.align(Alignment.Center),
                                            isBigButton = true,
                                            text = buttonText
                                        ) {
                                            // record log click contact service on server
                                            vm.alreadyLoggedServiceClickContact = false
                                            vm.recordLogService(LogServiceType.CLICK_CONTACT, screenInfo.service?.id)

                                            if (!isUserSignedIn) {
                                                println("User is not logged -> redirect to Auth")
                                                onAuthProcessStarted()
                                                return@ButtonPinkRoundedCorners
                                            } else {
                                                if (screenInfo.role == Role.Unauthenticated ||
                                                    (screenInfo.role == Role.Client && screenInfo.clientEventId == null))
                                                {
                                                    // previously was not logged, but now it is client -> create event
                                                    // or is normal client that will create an event
                                                    screenInfo.role = Role.Client
                                                    showClientQuestionsDialog = true
                                                } else {
                                                    // user is normal client and comes from miFiesta
                                                    vm.serviceCanBeAddedToClientEvent(
                                                        clientEventId = screenInfo.clientEventId.orEmpty(),
                                                        serviceId = screenInfo.service?.id.orEmpty(),
                                                        onResult = { isPossibleToAddService ->
                                                            println("isPossibleToAddService: $isPossibleToAddService")
                                                            showProgressDialog = true
                                                            if (isPossibleToAddService) {
                                                                validateErrorDialog = false
                                                                vm.addServiceToExistingEvent(
                                                                    eventId = screenInfo.clientEventId.orEmpty(),
                                                                    serviceId = screenInfo.service?.id.orEmpty()
                                                                ) { success, message, serviceEventId ->
                                                                    showProgressDialog = false
                                                                    if (success) {
                                                                        if (screenInfo.questionsProvider != null) {
                                                                            // provider event created -> restart app
                                                                            context.resetApplication()
                                                                        } else {
                                                                            // client event created -> show success popup
                                                                            serviceEventIdCreated = serviceEventId
                                                                            showSuccessDialog = true
                                                                        }
                                                                    } else {
                                                                        showErrorToast = Pair(true, message)
                                                                    }
                                                                }
                                                            } else {
                                                                // service was already added to this event
                                                                showProgressDialog = false
                                                                if (validateErrorDialog) {
                                                                    showErrorDialog = true
                                                                }
                                                            }
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        isStatusBarForDetails = true,
        service = service,
        user = firebaseUserDb,
        rating = 0, // moved to body on screen
        titleScreen = service?.name.orEmpty(),
        addBottomPadding = false,
        onHeartButtonClicked = {
            if (!isUserSignedIn) {
                onAuthProcessStarted()
                return@GradientBackground
            }
            firebaseUser?.uid?.let { userId ->
                service?.id?.let { serviceId ->
                    showProgressDialog = true
                    vm.likeService(userId, serviceId) {
                        showProgressDialog = false
                        vm.alreadyLikedService = false
                    }
                }
            }
        },
        onBackButtonClicked = {
            if (screenInfo.service.isShared()) {
                context.resetApplication()
            } else {
                onBackClicked()
            }
        },
        showShareButton = !screenInfo.service.isShared(),
        onShareButtonClicked = {
            screenInfo.service?.id?.let { serviceId ->
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "https://fiestamas.com/service/$serviceId")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Compartir casa"))
            } ?: run {
                showToast(context, "El ID del Servicio está vacío")
            }
        }
    )
}
