package com.universal.fiestamas.presentation.screens.home.main.mifiesta

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.MyPartyEvent
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.ScreenInfo
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.auth.NetworkViewModel
import com.universal.fiestamas.presentation.screens.home.main.MainParentClass
import com.universal.fiestamas.presentation.theme.topRoundedCornerShape15
import com.universal.fiestamas.presentation.ui.RequireLoginScreenView
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.bottom_sheets.BottomSheetProviderManagement
import com.universal.fiestamas.presentation.ui.bottom_sheets.BottomSheetServiceStateOrderBy
import com.universal.fiestamas.presentation.ui.bottom_sheets.ManageItem
import com.universal.fiestamas.presentation.ui.calendar.BottomCalendar
import com.universal.fiestamas.presentation.ui.calendar.CircleEventPerDay
import com.universal.fiestamas.presentation.ui.calendar.models.CalendarDay
import com.universal.fiestamas.presentation.ui.dialogs.FavouritesDialog
import com.universal.fiestamas.presentation.ui.dialogs.FiestamasWifiFoundDialog
import com.universal.fiestamas.presentation.ui.dialogs.FiestamasWifiFoundDialogInfo
import com.universal.fiestamas.presentation.utils.extensions.isClient
import com.universal.fiestamas.presentation.utils.extensions.isProvider
import kotlinx.coroutines.launch

object Testing {
    var clientId: String? = null
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainPartyScreen(
    vma: AuthViewModel = hiltViewModel(),
    vm: MainPartyViewModel = hiltViewModel(),
    networkViewModel: NetworkViewModel = hiltViewModel(),
    onNavigateHomeClicked: (
        hideServices: Boolean,
        hideEvents: Boolean
    ) -> Unit,
    onNavigateAuthClicked: () -> Unit,
    onNavigateServicesCategoriesClicked: (ScreenInfo) -> Unit,
    onNavigateNotificationsClicked: (List<MyPartyService?>) -> Unit,
    onNavigateServiceNegotiationClicked: (MyPartyService, Boolean) -> Unit,
    onNavigateFavouritesClicked: () -> Unit,
    onNavigateSearchClicked: () -> Unit,
    onNavigateAdminPromosClicked: (String) -> Unit,
    onNavigateAdminServicesClicked: (String) -> Unit,
    onRedirectToHome: () -> Unit
) {
    vm.resetUserAddressOnShPrefs()
    vm.resetRedirectionToMyPartyFromHome()
    vma.checkIfUserIsSignedIn()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userDb by vma.firebaseUserDb.collectAsState()
    val activeUser by vma.firebaseUser.collectAsState()

    var userId by remember { mutableStateOf("") }
    var isUserSignedIn by remember { mutableStateOf(false) }
    var showToolbarSearchIcon by remember { mutableStateOf(false) }
    var showFavouritesDialog by remember { mutableStateOf(false) }
    var totalToolbarNotifications: String? by remember { mutableStateOf(null) }
    var horizontalList: List<MyPartyEvent?> by remember { mutableStateOf(emptyList()) }
    var bottomSheetContent by remember { mutableStateOf(BottomSheetContent.Calendar) }
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    var showFiestamasWifiDialog by remember { mutableStateOf(false) }
    var showFiestamasWifiDialogInfo by remember { mutableStateOf(false) }
    var networkState: NetworkViewModel.FiestamasConnectionState by remember { mutableStateOf(
        NetworkViewModel.FiestamasConnectionState.UNDETECTED
    ) }

    LaunchedEffect(activeUser) {
        isUserSignedIn = activeUser != null && activeUser?.email != null
        userId = activeUser?.uid.orEmpty()
        Testing.clientId = userId

        if (vma.getProviderShouldBeRedirectedToServices()) {
            onNavigateAdminServicesClicked(userId)
        }
    }

    BackHandler(modalSheetState.isVisible) {
        coroutineScope.launch { modalSheetState.hide() }
    }

    FiestamasWifiFoundDialog(
        isVisible = showFiestamasWifiDialog,
        networkState = networkState,
        wasWifiDialogAlreadyShownToUser = networkViewModel.wasWifiDialogAlreadyShownToUser(),
        onDismiss = { showFiestamasWifiDialog = false },
        onConnectToNetwork = {
            showFiestamasWifiDialog = false
            networkViewModel.connectToWifi(context)
        },
        onConnectionRejected = {
            showFiestamasWifiDialog = false
            showFiestamasWifiDialogInfo = true
            networkViewModel.informThatWifiDialogWasAlreadyShownToUser()
        },
        onRedirectToHome = {
            showFiestamasWifiDialog = false
            onRedirectToHome()
        }
    )

    FiestamasWifiFoundDialogInfo(
        isVisible = showFiestamasWifiDialogInfo,
        onDismiss = { showFiestamasWifiDialogInfo = false }
    )

    GradientBackground(
        content = {
            if (!isUserSignedIn) {
                showToolbarSearchIcon = false
                totalToolbarNotifications = null
                RequireLoginScreenView { onNavigateAuthClicked() }
                return@GradientBackground
            }

            FavouritesDialog(
                isVisible = showFavouritesDialog,
                firebaseUserDb = userDb,
                horizontalList = horizontalList,
                onSeeAllClicked = {
                    showFavouritesDialog = false
                    onNavigateFavouritesClicked()
                },
                onDismiss = {
                    showFavouritesDialog = false
                    vm.mustRefreshListClient = true
                }
            )

            showToolbarSearchIcon = true
            vma.getFirebaseUserDb(MainParentClass.userId)

            var circlesPerDay: List<CircleEventPerDay>? by remember { mutableStateOf(null) }
            var selectedDate: CalendarDay? by remember { mutableStateOf(null) }

            ModalBottomSheetLayout(
                sheetState = modalSheetState,
                sheetShape = topRoundedCornerShape15,
                sheetContent = {
                    when (bottomSheetContent) {
                        BottomSheetContent.Calendar -> BottomCalendar(
                            circleEventList = circlesPerDay ?: emptyList(),
                            onDateSelected = { calendarDay ->
                                coroutineScope.launch { modalSheetState.hide() }
                                selectedDate = calendarDay
                            }
                        )
                        BottomSheetContent.OrderBy -> BottomSheetServiceStateOrderBy(
                            onItemSelected = { bottomServiceStatus ->
                                coroutineScope.launch { modalSheetState.hide() }
                                if (userDb?.role.isProvider()) {
                                    vm.sortProviderServiceListByStatus(bottomServiceStatus.status)
                                } else {
                                    vm.sortClientServiceListByStatus(bottomServiceStatus.status)
                                }
                            }
                        )
                        BottomSheetContent.Admin -> BottomSheetProviderManagement(
                            onItemSelected = { bottomItem ->
                                coroutineScope.launch { modalSheetState.hide() }
                                when (bottomItem.item) {
                                    ManageItem.Services -> onNavigateAdminServicesClicked(userId)
                                    ManageItem.Promotions -> onNavigateAdminPromosClicked(userId)
                                }
                            }
                        )
                    }
                }
            ) {
                if (userDb?.role.isProvider()) {
                    MifiestaProviderContent(
                        vm = vm,
                        providerId = userId,
                        selectedDate = selectedDate,
                        onTotalNotificationsGet = { totalToolbarNotifications = it.toString() },
                        onNavigateHomeClicked = { hideServices, hideEvents ->
                            onNavigateHomeClicked(hideServices, hideEvents)
                        },
                        onNavigateServiceNegotiationClicked = {
                            onNavigateServiceNegotiationClicked(it, userDb?.role.isProvider())
                        },
                        notifyCirclesList = { circlesPerDay = it },
                        onOrderByClicked = {
                            coroutineScope.launch {
                                bottomSheetContent = BottomSheetContent.OrderBy
                                modalSheetState.show()
                            }
                        },
                        onAdminClicked = {
                            coroutineScope.launch {
                                bottomSheetContent = BottomSheetContent.Admin
                                modalSheetState.show()
                            }
                        }
                    )
                } else {
                    MifiestaClientContent(
                        clientId = userId,
                        selectedDate = selectedDate,
                        coroutineContext = coroutineScope.coroutineContext,
                        onNavigateHomeClicked = { onNavigateHomeClicked(false, false) },
                        onNavigateServicesCategoriesClicked = { onNavigateServicesCategoriesClicked(it) },
                        onNavigateServiceNegotiationClicked = {
                            onNavigateServiceNegotiationClicked(it, userDb?.role.isProvider())
                        },
                        notifyTotalNotifications = { totalToolbarNotifications = it },
                        notifyHorizontalList = { it?.let { horizontalList = it } },
                        notifyEventList = { circlesPerDay = it },
                        onOrderByClicked = {
                            coroutineScope.launch {
                                bottomSheetContent = BottomSheetContent.OrderBy
                                modalSheetState.show()
                            }
                        },
                        onShowBottomCalendar = {
                            coroutineScope.launch {
                                bottomSheetContent = BottomSheetContent.Calendar
                                modalSheetState.show()
                            }
                        }
                    )
                }
            }
        },
        showWifiIcon = true,
        showBackButton = false,
        showHeartIcon = userDb?.role.isClient(),
        endButton = if (isUserSignedIn) R.drawable.ic_bell else null,
        showSearchButton = false, //showToolbarSearchIcon,
        notificationsCounter = totalToolbarNotifications,
        onEndButtonClicked = {
            var myPartyServiceList = vm.verticalListClient.value ?: emptyList()
            if (userDb?.role.isProvider()) {
                myPartyServiceList = vm.servicesListProvider.value ?: emptyList()
            }
            onNavigateNotificationsClicked(myPartyServiceList)
        },
        onSearchButtonClicked = { onNavigateSearchClicked() },
        onHeartButtonClicked = { showFavouritesDialog = true },
        onWiFiIconClicked = {
            networkState = it
            showFiestamasWifiDialog = true
        }
    )
}

enum class BottomSheetContent {
    Calendar,
    OrderBy,
    Admin
}
