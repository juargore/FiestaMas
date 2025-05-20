package com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.custom_invitation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.InvitationsViewModel
import com.universal.fiestamas.presentation.theme.GuestStatusSentBackgroundColor
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape6
import com.universal.fiestamas.presentation.theme.topRoundedCornerShape15
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.bottom_sheets.BottomSheetConfigureInvitation
import com.universal.fiestamas.presentation.ui.bottom_sheets.BottomSheetCustomizeInvitation
import com.universal.fiestamas.presentation.ui.bottom_sheets.InvitationDateFormat
import com.universal.fiestamas.presentation.ui.calendar.clickable
import com.universal.fiestamas.presentation.utils.extensions.convertTimestampToDateAndHour
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomInvitationScreen(
    vm: InvitationsViewModel = hiltViewModel(),
    idClientEvent: String,
    onBackClicked: () -> Unit
) {
    vm.getClientEventById(idClientEvent)

    val coroutineScope = rememberCoroutineScope()
    val clientEvent by vm.clientEvent.collectAsState()

    var invitationImage by remember { mutableIntStateOf(R.drawable.invitation_test0) }
    var eventName by remember { mutableStateOf("") }
    var eventNameSize by remember { mutableStateOf(12.sp) }
    var eventNameColor by remember { mutableStateOf(Color.Black) }
    var eventDateText by remember { mutableStateOf("") }
    var eventDateFormat by remember { mutableStateOf(InvitationDateFormat.DDmmYYYY) }
    var eventDateSize by remember { mutableStateOf(12.sp) }
    var eventDateColor by remember { mutableStateOf(Color.Black) }
    var eventLocation by remember { mutableStateOf("") }
    var eventLocationSize by remember { mutableStateOf(12.sp) }
    var eventLocationColor by remember { mutableStateOf(Color.Black) }

    var bottomSheetContent by remember {
        mutableStateOf(BottomSheetContent.Customize)
    }
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    LaunchedEffect(clientEvent) {
        clientEvent?.let { cEvent ->
            eventName = "${cEvent.name_event_type} ${cEvent.name}"
            eventLocation = cEvent.location
            eventDateText = when (eventDateFormat) {
                InvitationDateFormat.DDmmYYYY -> cEvent.date?.toDate().toString()
                InvitationDateFormat.LongFormat -> {
                    val date = convertTimestampToDateAndHour(cEvent.date)
                    "${date.first} ${date.second}"
                }
            }
        }
    }

    ///*
    GradientBackground(
        content = {
    //*/
            ModalBottomSheetLayout(
                sheetState = modalSheetState,
                sheetShape = topRoundedCornerShape15,
                sheetContent = {
                    when (bottomSheetContent) {
                        BottomSheetContent.Customize -> {
                            BottomSheetCustomizeInvitation { image ->
                                coroutineScope.launch { modalSheetState.hide() }
                                invitationImage = image
                            }
                        }
                        BottomSheetContent.Configure ->
                            clientEvent?.let {
                                BottomSheetConfigureInvitation(
                                    clientEvent = it,
                                    onSave = { eName, eColor, eSize, dFormat, dColor, dSize, lName, lColor, lSize ->
                                        eventName = eName;          eventDateFormat = dFormat;  eventLocation = lName
                                        eventNameColor = eColor;    eventDateColor = dColor;    eventLocationColor = lColor
                                        eventNameSize = eSize;      eventDateSize = dSize;      eventLocationSize = lSize
                                        coroutineScope.launch {
                                            modalSheetState.hide()
                                        }
                                    }
                                )
                            }
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    InvitationButtons(
                        onCustomize = {
                            coroutineScope.launch {
                                bottomSheetContent = BottomSheetContent.Customize
                                modalSheetState.show()
                            }
                        },
                        onConfigure = {
                            coroutineScope.launch {
                                bottomSheetContent = BottomSheetContent.Configure
                                modalSheetState.show()
                            }
                        }
                    )

                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(invitationImage),
                            contentDescription = null
                        )
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = eventName,
                                fontSize = eventNameSize,
                                color = eventNameColor
                            )
                            VerticalSpacer(height = 10.dp)
                            Text(
                                text = getDateAsText(eventDateFormat),
                                fontSize = eventDateSize,
                                color = eventDateColor
                            )
                            VerticalSpacer(height = 10.dp)
                            Text(
                                text = eventLocation,
                                fontSize = eventLocationSize,
                                color = eventLocationColor
                            )
                        }
                    }
                }
            }
    ///*
    },
    addBottomPadding = false,
    showLogoFiestamas = false,
    titleScreen = "Mi Invitación",
        onBackButtonClicked = { onBackClicked() }
    )
    //*/
}

private fun getDateAsText(format: InvitationDateFormat): String {
    return when (format) {
        InvitationDateFormat.DDmmYYYY -> "20/09/24"
        InvitationDateFormat.LongFormat -> "20 de septiembre de 2024, 12:06"
    }
}

@Composable
fun InvitationButtons(
    onCustomize: () -> Unit,
    onConfigure: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .weight(0.5f)
                .padding(end = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            OptionButton(
                text = "Personalizar",
                onClick = onCustomize
            )
        }
        Box(
            modifier = Modifier
                .weight(0.5f)
                .padding(start = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            OptionButton(
                text = "Configurar",
                onClick = onConfigure
            )
        }
    }
}

@Composable
fun OptionButton(
    text: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(allRoundedCornerShape6)
            .background(GuestStatusSentBackgroundColor.copy(alpha = 0.8f))
            .border(1.dp, GuestStatusSentBackgroundColor, allRoundedCornerShape6)
            .clickable { onClick() }
    ) {
        TextSemiBold(
            modifier = Modifier.padding(vertical = 8.dp),
            text = text,
            size = 14.sp,
            maxLines = 1,
            color = Color.Black,
        )
    }
}

enum class BottomSheetContent {
    Customize,
    Configure
}


@Preview
@Composable
fun CustomInvitationScreenPreview() {
    CustomInvitationScreen(
        idClientEvent = "",
        onBackClicked = { }
    )
}
