package com.universal.fiestamas.presentation.screens.home.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.Notification
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.theme.LowPinkFiestaki
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.theme.topRoundedCornerShape15
import com.universal.fiestamas.presentation.ui.RequireLoginScreenView
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.bottom_sheets.BottomSheetNotificationOrderBy
import com.universal.fiestamas.presentation.ui.cards.CardNotification
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.isProvider
import com.universal.fiestamas.presentation.utils.extensions.sidePadding

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationsScreen (
    vma: AuthViewModel = hiltViewModel(),
    vm: NotificationsViewModel = hiltViewModel(),
    myPartyServiceList: List<MyPartyService?>,
    onAuthProcessStarted: () -> Unit,
    onOpenChatConversation: (
        serviceEvent: MyPartyService?,
        isProvider: Boolean
    ) -> Unit,
    onBackClicked: () -> Unit
) {
    vma.checkIfUserIsSignedIn()

    val userDb by vma.firebaseUserDb.collectAsState()
    val activeUser by vma.firebaseUser.collectAsState()
    var isUserSignedIn by remember { mutableStateOf(false) }

    LaunchedEffect(activeUser) {
        isUserSignedIn = activeUser != null && activeUser?.email != null
    }

    GradientBackground(
        content = {
            if (!isUserSignedIn) {
                RequireLoginScreenView { onAuthProcessStarted() }
                return@GradientBackground
            }

            vm.getMessagesNotificationsByUserId(
                isProvider = userDb?.role.isProvider(),
                userId = activeUser?.uid.orEmpty(),
                myPartyServiceList = myPartyServiceList
            )

            val notificationList by vm.notificationServerList.collectAsState()
            val modalSheetState = rememberModalBottomSheetState(
                initialValue = ModalBottomSheetValue.Hidden,
                skipHalfExpanded = true
            )

            ModalBottomSheetLayout(
                sheetState = modalSheetState,
                sheetShape = topRoundedCornerShape15,
                sheetContent = {
                    BottomSheetNotificationOrderBy { /* not used so far */ }
                }
            ) {
                NotificationContentView(
                    notificationServerList = notificationList,
                    onOpenChatConversation = onOpenChatConversation
                )
            }
        },
        addBottomPadding = false,
        validateOfflineMode = true,
        onBackButtonClicked = { onBackClicked() }
    )
}

@Composable
fun NotificationContentView(
    vma: AuthViewModel = hiltViewModel(),
    notificationServerList: List<Notification>,
    onOpenChatConversation: (
        serviceEvent: MyPartyService?,
        isProvider: Boolean
    ) -> Unit
) {
    val userDb by vma.firebaseUserDb.collectAsState()

    Card(
        elevation = 10.dp,
        shape = allRoundedCornerShape12,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .sidePadding()
            .padding(top = 10.dp.autoSize(), bottom = 30.dp.autoSize())
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LowPinkFiestaki)
                    .padding(14.dp.autoSize())
            ) {
                TextMedium(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = stringResource(id = R.string.notification_title),
                    size = 25.sp.autoSize(),
                    fillMaxWidth = false
                )
            }
            if (notificationServerList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    TextMedium(
                        text = stringResource(R.string.notification_empty),
                        size = 16.sp.autoSize()
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp.autoSize()),
                    verticalArrangement = Arrangement.spacedBy(6.dp.autoSize()),
                    contentPadding = PaddingValues(horizontal = 3.dp.autoSize(), vertical = 8.dp.autoSize())
                ) {
                    items(notificationServerList) {
                        CardNotification(it) { notification ->
                            onOpenChatConversation(
                                notification.serviceEvent,
                                userDb?.role.isProvider()
                            )
                        }
                    }
                }
            }
        }
    }
}
