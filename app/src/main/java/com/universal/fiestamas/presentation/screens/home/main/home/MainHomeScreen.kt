package com.universal.fiestamas.presentation.screens.home.main.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Event
import com.universal.fiestamas.domain.models.FirstQuestionsClientStored
import com.universal.fiestamas.domain.models.FirstQuestionsProviderStored
import com.universal.fiestamas.domain.models.Role
import com.universal.fiestamas.domain.models.Screen
import com.universal.fiestamas.domain.models.ScreenInfo
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.home.main.MainParentClass
import com.universal.fiestamas.presentation.screens.location.AddressAutoCompleteScreen
import com.universal.fiestamas.presentation.ui.IconArrowWhite
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.ViewFooterHome
import com.universal.fiestamas.presentation.ui.ViewHeaderHome
import com.universal.fiestamas.presentation.ui.cards.CardHomeEventType
import com.universal.fiestamas.presentation.ui.cards.CardHomeEventTypeCircled
import com.universal.fiestamas.presentation.ui.cards.CardHomeServiceCategory
import com.universal.fiestamas.presentation.ui.dialogs.FirstQuestionsNewEventProviderDialog
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.getRole
import com.universal.fiestamas.presentation.utils.showToastOnUiThread
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "FrequentlyChangedStateReadInComposition")
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
fun MainHomeScreen(
    vma: AuthViewModel = hiltViewModel(),
    vm: MainHomeViewModel = hiltViewModel(),
    hideServices: Boolean,
    hideEvents: Boolean,
    onNavigateServicesCategoriesClicked: (ScreenInfo) -> Unit,
    onNavigateServicesTypesClicked: (ScreenInfo) -> Unit,
    onNavigateProviderRegistrationClicked: () -> Unit,
    onNavigateMyParty: () -> Unit,
    onNavigateSearchClicked: () -> Unit,
) {
    vma.getFirebaseUserDb(MainParentClass.userId)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val firebaseUserDb by vma.firebaseUserDb.collectAsState()

    firebaseUserDb?.let { user ->
        val sharedServiceId = vm.getSharedServiceIdIfExists()
        if (sharedServiceId.isNotEmpty()) {
            if (user.role.getRole() == Role.Provider) {
                showToastOnUiThread(context, context.getString(R.string.main_home_create_account))
                vm.resetSharedServiceId()
                vm.getEventTypeList()
                vm.getServiceCategories()
            } else {
                if (user.role.getRole() != Role.Provider) {
                    val screenInfo = ScreenInfo(
                        role = user.role.getRole(),
                        startedScreen = Screen.Shared,
                        prevScreen = Screen.Shared,
                        event = Event(id = context.getString(R.string.gral_shared)),
                        questions = null,
                        questionsProvider = null,
                        serviceCategory = null,
                        clientEventId = null,
                        service = Service(sharedServiceId, context.getString(R.string.gral_shared))
                    )
                    onNavigateServicesCategoriesClicked(screenInfo)

                } else if (vm.shouldRedirectToMyParty() && user.role.getRole() != Role.Unauthenticated) {
                    onNavigateMyParty()
                } else {
                    vm.getEventTypeList()
                    vm.getServiceCategories()
                }
            }
        } else if (vm.shouldRedirectToMyParty() && user.role.getRole() != Role.Unauthenticated) {
            onNavigateMyParty()
        } else {
            vm.getEventTypeList()
            vm.getServiceCategories()
        }
    }

    var showProviderNewEventDialog by remember { mutableStateOf(false) }
    var isGettingAddressForClient by remember { mutableStateOf(false) }
    var selectedServiceCategory: ServiceCategory? by remember { mutableStateOf(null) }
    var selectedEvent: Event? by remember { mutableStateOf(null) }
    val eventTypesList by vm.eventList.collectAsState()
    val serviceCategoriesList by vm.serviceCategoryList.collectAsState()

    val savedQuestionsForClient: FirstQuestionsClientStored? by remember { mutableStateOf(null) }
    var savedQuestionsForProvider: FirstQuestionsProviderStored? by remember { mutableStateOf(null) }
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.Expanded },
        skipHalfExpanded = true
    )

    BackHandler(modalSheetState.isVisible) {
        coroutineScope.launch { modalSheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetContent = {
            AddressAutoCompleteScreen(searchForCities = true) { mAddress, _ ->
                if (isGettingAddressForClient) {
                    // is getting address for client
                    savedQuestionsForClient?.location = mAddress?.location
                    savedQuestionsForClient?.city = mAddress?.city.orEmpty()
                    coroutineScope.launch { modalSheetState.hide() }
                } else {
                    // is getting address for provider
                    savedQuestionsForProvider?.location = mAddress?.location
                    savedQuestionsForProvider?.city = mAddress?.city.orEmpty()
                    coroutineScope.launch { modalSheetState.hide() }
                    showProviderNewEventDialog = true
                }
            }
        }
    ) {
        Scaffold {
            if (showProviderNewEventDialog) {
                FirstQuestionsNewEventProviderDialog(
                    onDismiss = {
                        savedQuestionsForProvider = null
                        showProviderNewEventDialog = false
                        onNavigateMyParty()
                    },
                    savedQuestions = savedQuestionsForProvider,
                    onAddressClicked = { questions ->
                        isGettingAddressForClient = false
                        savedQuestionsForProvider = questions
                        showProviderNewEventDialog = false
                        coroutineScope.launch { modalSheetState.show() }
                    },
                    onContinueClicked = { questions ->
                        showProviderNewEventDialog = false
                        val screenInfo = ScreenInfo(
                            role = firebaseUserDb?.role.getRole(),
                            startedScreen = Screen.ServiceCategories,
                            prevScreen = Screen.Home,
                            event = selectedEvent!!,
                            questions = null,
                            questionsProvider = questions,
                            serviceCategory = null,
                            clientEventId = null
                        )
                        onNavigateServicesCategoriesClicked(screenInfo)
                    }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    ViewHeaderHome(
                        userDb = firebaseUserDb,
                        onClick = { onNavigateProviderRegistrationClicked() },
                        onSearch = { onNavigateSearchClicked() }
                    )
                }
                item {
                    val lazyListState = rememberLazyListState()
                    Box(
                        modifier = Modifier.padding(horizontal = 30.dp),
                    ) {
                        LazyRow(
                            state = lazyListState,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            items(eventTypesList.orEmpty()) { event ->
                                CardHomeEventTypeCircled(item = event) {
                                    if (firebaseUserDb?.role.getRole() != Role.Provider) {
                                        selectedServiceCategory = null
                                        selectedEvent = event
                                        val screenInfo = ScreenInfo(
                                            role = firebaseUserDb?.role.getRole(),
                                            startedScreen = Screen.ServiceCategories,
                                            prevScreen = Screen.Home,
                                            event = selectedEvent!!,
                                            questions = null,
                                            serviceCategory = null,
                                            clientEventId = null
                                        )
                                        onNavigateServicesCategoriesClicked(screenInfo)
                                    } else {
                                        selectedEvent = event
                                        showProviderNewEventDialog = true
                                    }
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                        ) {
                            Row(modifier = Modifier.align(Alignment.CenterStart)) {
                                if (lazyListState.firstVisibleItemScrollOffset > 0) {
                                    IconArrowWhite(resource = R.drawable.ic_arrow_prev)
                                }
                            }
                            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                                if (lazyListState.firstVisibleItemIndex == 0) {
                                    IconArrowWhite(resource = R.drawable.ic_arrow_next)
                                }
                            }
                        }
                    }
                }


                if (!hideEvents) {
                    item {
                        VerticalSpacer(height = 10.dp)
                        TextSemiBold(text = "Selecciona tu evento", size = 18.sp.autoSize())
                    }
                    item {
                        FlowRow {
                            eventTypesList?.forEachIndexed { i, item ->
                                CardHomeEventType(
                                    item = item,
                                    index = i,
                                    onItemClick = { event ->
                                        if (firebaseUserDb?.role.getRole() != Role.Provider) {
                                            selectedServiceCategory = null
                                            selectedEvent = event
                                            val screenInfo = ScreenInfo(
                                                role = firebaseUserDb?.role.getRole(),
                                                startedScreen = Screen.ServiceCategories,
                                                prevScreen = Screen.Home,
                                                event = selectedEvent!!,
                                                questions = null,
                                                serviceCategory = null,
                                                clientEventId = null
                                            )
                                            onNavigateServicesCategoriesClicked(screenInfo)
                                        } else {
                                            selectedEvent = event
                                            showProviderNewEventDialog = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                if (!hideServices) {
                    item {
                        VerticalSpacer(25.dp)
                        val text = if (firebaseUserDb?.role.getRole() == Role.Provider) {
                            stringResource(R.string.main_home_new_service)
                        } else ""
                        TextSemiBold(text = text, size = 18.sp.autoSize())
                    }
                    item {
                        if (firebaseUserDb?.role.getRole() == Role.Provider) {
                            FlowRow(
                                modifier = Modifier.padding(horizontal = 30.dp)
                            ) {
                                serviceCategoriesList?.forEachIndexed { i, item ->
                                    CardHomeServiceCategory(
                                        item = item,
                                        index = i,
                                        onItemClick = {
                                            val screenInfo = ScreenInfo(
                                                role = Role.Provider,
                                                startedScreen = Screen.ServiceTypes,
                                                prevScreen = Screen.Home,
                                                event = Event(id = context.getString(R.string.gral_phantom_event_id)),
                                                serviceCategory = it,
                                                questions = null,
                                                clientEventId = null
                                            )
                                            onNavigateServicesTypesClicked(screenInfo)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    item { VerticalSpacer(10.dp) }
                }
                item { ViewFooterHome() }
                item { VerticalSpacer(height = 40.dp) }
            }
        }
    }
}
