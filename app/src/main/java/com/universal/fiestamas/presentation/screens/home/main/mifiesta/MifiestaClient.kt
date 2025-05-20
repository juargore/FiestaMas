package com.universal.fiestamas.presentation.screens.home.main.mifiesta

import android.os.Handler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Event
import com.universal.fiestamas.domain.models.MyPartyEvent
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.Role
import com.universal.fiestamas.domain.models.Screen
import com.universal.fiestamas.domain.models.ScreenInfo
import com.universal.fiestamas.presentation.screens.home.notifications.NotificationsViewModel
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.ViewAddOrderMyParty
import com.universal.fiestamas.presentation.ui.ViewHorizontalMyParty
import com.universal.fiestamas.presentation.ui.ViewVerticalMyParty
import com.universal.fiestamas.presentation.ui.calendar.CircleEventPerDay
import com.universal.fiestamas.presentation.ui.calendar.models.CalendarDay
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.ui.dialogs.YesNoDialogV2
import com.universal.fiestamas.presentation.utils.Constants.DELAY_TO_REFRESH
import com.universal.fiestamas.presentation.utils.extensions.toCircleEventPerDayList
import com.universal.fiestamas.presentation.utils.extensions.toColor
import com.universal.fiestamas.presentation.utils.showToast
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext

@Suppress("DEPRECATION")
@Composable
fun MifiestaClientContent(
    vm: MainPartyViewModel = hiltViewModel(),
    vmn: NotificationsViewModel = hiltViewModel(),
    clientId: String,
    coroutineContext: CoroutineContext,
    selectedDate: CalendarDay?,
    onOrderByClicked: () -> Unit,
    onNavigateHomeClicked: () -> Unit,
    onShowBottomCalendar: () -> Unit,
    notifyTotalNotifications: (String) -> Unit,
    notifyEventList: (List<CircleEventPerDay>?) -> Unit,
    notifyHorizontalList: (List<MyPartyEvent?>?) -> Unit,
    onNavigateServicesCategoriesClicked: (ScreenInfo) -> Unit,
    onNavigateServiceNegotiationClicked: (MyPartyService) -> Unit
) {

    val context = LocalContext.current
    val horizontalList by vm.horizontalListClient.collectAsState()
    val verticalList by vm.verticalListClient.collectAsState() // MyPartyService

    var selectedEvent: Event? by remember { mutableStateOf(null) }
    var backgroundColor: Color by remember { mutableStateOf(Color.Gray) }
    var showProgressDialog by remember { mutableStateOf(false) }
    var showMiddleItem by remember { mutableStateOf(false) }
    var showOldEventDialog by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    notifyTotalNotifications(vmn.counterClientUnreadNotifications.collectAsState().value)
    notifyHorizontalList(horizontalList)

    verticalList?.let { serviceEventsList ->
        vmn.getCountUnreadNotificationsByClientId(clientId, serviceEventsList)
    }

    ProgressDialog(showProgressDialog)

    YesNoDialogV2(
        isVisible = showOldEventDialog,
        addAcceptButton = false,
        title = "Evento pasado",
        message = "Este evento ya ocurrió. No se pueden agregar más servicios.",
        onDismiss = { showOldEventDialog = false },
        onPrimaryButtonClicked = { }
    )

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            isRefreshing = true
            vm.mustRefreshListClient = true
            vm.getEventsWithServices(clientId)
            Handler().postDelayed({
                isRefreshing = false }, DELAY_TO_REFRESH)
        }
    ) {
        Column {
            VerticalSpacer(5.dp)
            ViewHorizontalMyParty(
                context = coroutineContext,
                selectedDate = selectedDate,
                shouldShowArrows = horizontalList.orEmpty().size > 2,
                horizontalList = horizontalList.orEmpty(),
                onNewPartyClicked = { onNavigateHomeClicked() },
                onItemClicked = {
                    selectedEvent = it.toEvent()
                    backgroundColor = it.color_hex.toColor()
                    showMiddleItem = true
                    vm.filterServiceListByEvent(it.id)
                }
            )
            ViewAddOrderMyParty(
                showMiddleItem = showMiddleItem,
                onAddServicesClicked = {
                    if (selectedEvent == null) {
                        if ((horizontalList?.size ?: 0) == 1) {
                            selectedEvent = horizontalList?.first()?.toEvent()
                        } else {
                            showToast(context, context.getString(R.string.mifiesta_add_event_to_continue))
                            return@ViewAddOrderMyParty
                        }
                    }
                    val pendingDays = selectedEvent?.pendingDays ?: 0
                    if (pendingDays > -1) {
                        vm.mustRefreshListClient = true
                        val screenInfo = ScreenInfo(
                            role = Role.Client,
                            startedScreen = Screen.ServiceCategories,
                            prevScreen = Screen.Mifiesta,
                            event = selectedEvent!!,
                            questions = null,
                            serviceCategory = null,
                            clientEventId = selectedEvent?.clientEventId
                        )
                        onNavigateServicesCategoriesClicked(screenInfo)
                        return@ViewAddOrderMyParty
                    }
                    showOldEventDialog = true
                },
                onSeeAllEventsClicked = {
                    selectedEvent = null
                    showMiddleItem = false
                    showProgressDialog = true
                    vm.mustRefreshListClient = true
                    backgroundColor = Color.Gray
                    vm.getEventsWithServices(clientId) {
                        showProgressDialog = false
                    }
                },
                onOrderByClicked = onOrderByClicked,
                onShowBottomCalendar = onShowBottomCalendar
            )

            verticalList?.let { verticalItem ->
                notifyEventList(horizontalList?.toCircleEventPerDayList())

                // first, the serviceEvent was created  on Db
                // then, User clicked in "chat" in popup details screen
                // now, open the chat screen of this serviceEvent
                verticalItem.forEach { service ->
                    if (service?.id == vm.getServiceIdForNotification() && !alreadyClicked) {
                        alreadyClicked = true
                        vm.mustRefreshListClient = true
                        onNavigateServiceNegotiationClicked(service)
                        return@forEach
                    }
                }

                ViewVerticalMyParty(
                    horizontalList = horizontalList,
                    verticalList = verticalItem,
                    backgroundColor = backgroundColor,
                    onItemClicked = { myPartyService ->
                        vm.mustRefreshListClient = true
                        val event = horizontalList?.firstOrNull {
                            it?.toEvent()?.clientEventId == myPartyService.id_client_event
                        }
                        myPartyService.date = event?.date
                        onNavigateServiceNegotiationClicked(myPartyService)
                    }
                )
            }
        }
    }
}

private var alreadyClicked = false
