package com.universal.fiestamas.presentation.screens.home.main.mifiesta

import android.os.Handler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.presentation.screens.home.notifications.NotificationsViewModel
import com.universal.fiestamas.presentation.ui.ButtonOrderBy
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.calendar.CircleEventPerDay
import com.universal.fiestamas.presentation.ui.calendar.models.CalendarDay
import com.universal.fiestamas.presentation.ui.cards.CardServiceProvider
import com.universal.fiestamas.presentation.ui.cards.NewCardEventProvider
import com.universal.fiestamas.presentation.utils.Constants.DELAY_TO_REFRESH
import com.universal.fiestamas.presentation.utils.Constants.GRID_THREE_CELLS
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.extensions.toCircleServicePerDayList

@Suppress("UNUSED_PARAMETER", "DEPRECATION")
@Composable
fun MifiestaProviderContent(
    vmn: NotificationsViewModel = hiltViewModel(),
    vm: MainPartyViewModel,
    providerId: String,
    selectedDate: CalendarDay?,
    onNavigateHomeClicked: (
        hideServices: Boolean,
        hideEvents: Boolean
    ) -> Unit,
    onTotalNotificationsGet: (Int) -> Unit,
    onOrderByClicked: () -> Unit,
    onAdminClicked: () -> Unit,
    notifyCirclesList: (List<CircleEventPerDay>?) -> Unit,
    onNavigateServiceNegotiationClicked: (MyPartyService) -> Unit
) {
    vm.getMyPartyServicesByProvider(providerId)

    val services by vm.servicesListProvider.collectAsState()
    var titleProvider by remember { mutableStateOf("") }
    var titleService by remember { mutableStateOf("") }
    var isRefreshing by remember { mutableStateOf(false) }

    services?.let { vmn.getCountUnreadNotificationsByProviderId(providerId, it) }

    onTotalNotificationsGet(vmn.counterProviderUnreadNotifications.collectAsState().value)

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            isRefreshing = true
            vm.gotServicesByEventsByProvider = false
            vm.getMyPartyServicesByProvider(providerId)
            Handler().postDelayed({
                isRefreshing = false }, DELAY_TO_REFRESH)
        }
    ) {
        Column {
            TextSemiBold(
                modifier = Modifier.sidePadding(),
                text = titleService,
                align = TextAlign.Start,
                size = 19.sp.autoSize()
            )
            VerticalSpacer(height = 10.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .sidePadding()
            ) {
                ButtonOrderBy(
                    text = stringResource(id = R.string.service_management),
                    modifier = Modifier.align(Alignment.CenterStart)
                ) { onAdminClicked() }

                ButtonOrderBy(
                    text = stringResource(id = R.string.service_order_by),
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) { onOrderByClicked() }
            }

            VerticalSpacer(height = 10.dp)

            if (services.isNullOrEmpty()) {
                LazyVerticalGrid(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    contentPadding = PaddingValues(6.dp),
                    columns = GRID_THREE_CELLS
                ) {
                    items(listOf(MyPartyService())) {
                        NewCardEventProvider { onNavigateHomeClicked(true, false) }
                    }
                }
            } else {
                services?.let { servicesList ->
                    if (servicesList.isNotEmpty()) {
                        notifyCirclesList(servicesList.toCircleServicePerDayList())
                        servicesList.sortedBy { it?.name }.firstOrNull()?.let {
                            titleProvider = it.provider_contact_name
                            titleService = "${it.service_category_name} - ${it.name}"
                        }
                    }

                    // add empty item to show the new_party icon at start
                    val mList = servicesList.toMutableList()
                    mList.add(0, MyPartyService())

                    LazyVerticalGrid(
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                        contentPadding = PaddingValues(6.dp),
                        columns = GRID_THREE_CELLS
                    ) {
                        itemsIndexed(mList) { index, item ->
                            if (index == 0) {
                                NewCardEventProvider { onNavigateHomeClicked(true, false) }
                            } else {
                                CardServiceProvider(
                                    item = item,
                                    savedServiceIdNotification = vm.getServiceIdForNotification()
                                ) {
                                    onNavigateServiceNegotiationClicked(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
