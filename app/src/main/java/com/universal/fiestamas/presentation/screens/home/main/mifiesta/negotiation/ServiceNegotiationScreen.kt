package com.universal.fiestamas.presentation.screens.home.main.mifiesta.negotiation

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.common.BaseResult
import com.universal.fiestamas.domain.models.BidForQuote
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.request.ItemQuoteRequest
import com.universal.fiestamas.domain.models.request.QuoteV2
import com.universal.fiestamas.domain.models.request.RequestQuotation
import com.universal.fiestamas.domain.models.response.GetQuoteResponse
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.home.main.MainParentClass
import com.universal.fiestamas.presentation.theme.LightGray
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape8
import com.universal.fiestamas.presentation.ui.HorizontalLine
import com.universal.fiestamas.presentation.ui.MessagesFromNegotiationButton
import com.universal.fiestamas.presentation.ui.TabNewOrEditQuote
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.CardAuthBackground
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.cards.CardAcceptOrDeclineBidPink
import com.universal.fiestamas.presentation.ui.cards.CardGreenAccepted
import com.universal.fiestamas.presentation.ui.cards.CardRedDeclined
import com.universal.fiestamas.presentation.ui.cards.CardRightYellowBid
import com.universal.fiestamas.presentation.ui.cards.ProviderQuoteEmptyView
import com.universal.fiestamas.presentation.ui.cards.RequestQuoteEmptyView
import com.universal.fiestamas.presentation.ui.dialogs.NewExpressQuoteOrEditDialog
import com.universal.fiestamas.presentation.ui.dialogs.NewQuoteOrEditDialog
import com.universal.fiestamas.presentation.ui.dialogs.NoteBookDialog
import com.universal.fiestamas.presentation.ui.dialogs.NotesType
import com.universal.fiestamas.presentation.ui.dialogs.OptionsQuote
import com.universal.fiestamas.presentation.ui.dialogs.OptionsQuoteDialog
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.ui.dialogs.YesNoDialog
import com.universal.fiestamas.presentation.utils.Constants.ACCEPTED
import com.universal.fiestamas.presentation.utils.Constants.EXPRESS
import com.universal.fiestamas.presentation.utils.Constants.REJECTED
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.showToast
import com.universal.fiestamas.presentation.utils.showToastOnUiThread
import kotlinx.coroutines.launch

private var alreadyOpenedNotifications = false

@Suppress("UNUSED_PARAMETER")
@Composable
fun ServiceNegotiationScreen(
    vmt: ServiceNegotiationViewModel = hiltViewModel(),
    vma: AuthViewModel = hiltViewModel(),
    myPartyService: MyPartyService,
    isProvider: Boolean,
    onBackClicked: () -> Unit,
    onNavigateNotificationsClicked: () -> Unit,
    onNavigateAdminInvitationsClicked: (
        userId: String,
        idClientEvent: String
    ) -> Unit,
    reloadScreen: () -> Unit
) {

    // first, the serviceEvent was created  on Db
    // then, User clicked in "chat" in popup details screen
    // now, open the chat screen of this serviceEvent
    if (vmt.getServiceIdForNotification().isNotEmpty() && !alreadyOpenedNotifications) {
        alreadyOpenedNotifications = true
        onNavigateNotificationsClicked()
    }

    vmt.getUnreadCounterChatMessagesByServiceEvent(
        serviceEventId = myPartyService.id,
        senderId = if (!isProvider) myPartyService.id_provider else myPartyService.id_client
    )
    vma.getFirebaseUserDb(MainParentClass.userId)
    vmt.getMyPartyService(myPartyService.id)
    vmt.getQuote(myPartyService.id)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val cQuote by vmt.currentQuote.collectAsState()
    val cPartyService by vmt.myPartyService.collectAsState()
    val userDb by vma.firebaseUserDb.collectAsState()
    val counterUnreadMessages by vmt.unreadMessagesCount.collectAsState()

    var showNewOrEditExpressQuoteDialog by remember { mutableStateOf(false) }
    var showNewOrEditQuoteDialog by remember { mutableStateOf(false) }
    var showOptionsQuoteDialog by remember { mutableStateOf(false) }
    var showYesNoDialogToCancelStatus by remember { mutableStateOf(false) }
    var showYesNoDialogToEditQuote by remember { mutableStateOf(false) }
    var showProgressDialog by remember { mutableStateOf(false) }
    var isFinishingProcessToDecline by remember { mutableStateOf(false) }
    var showNoteBookDialog by remember { mutableStateOf(false) }
    var savedNotes by remember { mutableStateOf("") }
    var idClientEvent by remember { mutableStateOf("") }
    var alreadyExistsQuote by remember { mutableStateOf(false) }
    var alreadyAcceptedQuote by remember { mutableStateOf(false) }
    var isUsingExpressQuote by remember { mutableStateOf(false) }
    var serviceEventStatus by remember { mutableStateOf("") }
    var noteType: NotesType by remember { mutableStateOf(NotesType.IMPORTANT) }

    LaunchedEffect(cQuote, cPartyService) {
        alreadyExistsQuote = cQuote != null
        serviceEventStatus = cPartyService?.status.orEmpty()
        alreadyAcceptedQuote = cQuote?.allow_edit == false
        isUsingExpressQuote = cQuote?.type == EXPRESS
        idClientEvent = cPartyService?.id_client_event.orEmpty()
    }

    GradientBackground(
        content = {
            ProgressDialog(showProgressDialog)

            NewQuoteOrEditDialog(
                isVisible = showNewOrEditQuoteDialog,
                isEditingData = alreadyExistsQuote,
                quote = cQuote,
                editableList = cQuote?.elements?.map { it.toQuoteProductsInformation() },
                onDismiss = { showNewOrEditQuoteDialog = false },
                onSendNewQuoteClicked = { items, providerNotes, _ ->
                    showNewOrEditQuoteDialog = false
                    showProgressDialog = true
                    vmt.createNewQuoteV2(
                        quote = QuoteV2(
                            type = "CLASSIC",
                            title = "Te han mandado una nueva cotización!",
                            notes = providerNotes,
                            id_service_event = myPartyService.id,
                            elements = items.map { it.toItemQuoteRequest() }
                        ),
                        onFinished = { message ->
                            showProgressDialog = false
                            message?.let { showToastOnUiThread(context, message) }
                        }
                    )
                },
                onEditQuoteClicked = { nItems, _, providerNotes, _, _, _ ->
                    showNewOrEditQuoteDialog = false
                    showProgressDialog = true
                    vmt.editQuoteV2(
                        quoteId = cQuote?.id.orEmpty(),
                        quote = QuoteV2(
                            type = "CLASSIC",
                            notes = providerNotes,
                            id_service_event = myPartyService.id,
                            elements = nItems.map { it.toItemQuoteRequest() }
                        ),
                        onFinished = { message ->
                            showProgressDialog = false
                            message?.let { showToastOnUiThread(context, message) }
                            Handler(Looper.getMainLooper()).post { reloadScreen() }
                        }
                    )
                }
            )

            NewExpressQuoteOrEditDialog(
                isVisible = showNewOrEditExpressQuoteDialog,
                isEditingData = alreadyExistsQuote,
                originalTotal = "",
                originalNotes = cQuote?.notes.orEmpty(),
                onDismiss = { showNewOrEditExpressQuoteDialog = false },
                onSendNewQuoteClicked = { providerNotes, total ->
                    showNewOrEditExpressQuoteDialog = false
                    showProgressDialog = true
                    vmt.createNewQuoteV2(
                        quote = QuoteV2(
                            type = "EXPRESS",
                            notes = providerNotes,
                            id_service_event = myPartyService.id,
                            title = "Te han mandado una nueva cotización!",
                            elements = listOf(
                                ItemQuoteRequest(
                                    qty = 1,
                                    description = myPartyService.description,
                                    price = total,
                                    subTotal = total
                                )
                            )
                        ),
                        onFinished = { message ->
                            showProgressDialog = false
                            message?.let { showToastOnUiThread(context, message) }
                        }
                    )
                },
                onEditQuoteClicked = { providerNotes, total ->
                    showNewOrEditExpressQuoteDialog = false
                    showProgressDialog = true
                    vmt.editQuoteV2(
                        quoteId = cQuote?.id.orEmpty(),
                        quote = QuoteV2(
                            type = "EXPRESS",
                            notes = providerNotes,
                            id_service_event = myPartyService.id,
                            elements = listOf(
                                ItemQuoteRequest(
                                    qty = 1,
                                    description = myPartyService.description,
                                    price = total,
                                    subTotal = total
                                )
                            )
                        ),
                        onFinished = { message ->
                            showProgressDialog = false
                            message?.let { showToastOnUiThread(context, message) }
                            Handler(Looper.getMainLooper()).post { reloadScreen() }
                        }
                    )
                }
            )

            OptionsQuoteDialog(
                isVisible = showOptionsQuoteDialog,
                onDismiss = { showOptionsQuoteDialog = false },
                onActionSelected = { option ->
                    when(option) {
                        OptionsQuote.Hired, OptionsQuote.Pending -> {
                            showProgressDialog = true
                            updateStatus(context, vmt, myPartyService.id, option) { message ->
                                showOptionsQuoteDialog = false
                                showToastOnUiThread(context, message)
                                showProgressDialog = false
                            }
                        }
                        OptionsQuote.Cancel -> {
                            showOptionsQuoteDialog = false
                            showYesNoDialogToCancelStatus = true
                        }
                    }
                }
            )

            YesNoDialog(
                isVisible = showYesNoDialogToCancelStatus,
                icon = R.drawable.ic_question_circled,
                message = stringResource(R.string.mifiesta_confirm_cancel_event),
                onDismiss = { showYesNoDialogToCancelStatus = false },
                onOk = {
                    showYesNoDialogToCancelStatus = false
                    showProgressDialog = true
                    updateStatus(context, vmt, myPartyService.id, OptionsQuote.Cancel) { message ->
                        showToastOnUiThread(context, message)
                        showProgressDialog = false
                    }
                }
            )

            YesNoDialog(
                isVisible = showYesNoDialogToEditQuote,
                icon = R.drawable.ic_alert_circled,
                title = stringResource(R.string.mifiesta_status_requires_approval),
                message =
                stringResource(R.string.mifiesta_confirm_edit_quote),
                onDismiss = { showYesNoDialogToEditQuote = false },
                onOk = {
                    isFinishingProcessToDecline = true
                    showYesNoDialogToEditQuote = false
                    cQuote?.let {
                        showProgressDialog = true
                        val request = RequestQuotation(
                            title = "${myPartyService.event_data?.name_event_type} ${myPartyService.event_data?.name}",
                            content = "Se requiere editar cotización",
                            id_client_event = myPartyService.id_client_event,
                            id_sender = myPartyService.id_provider,
                            name_sender = myPartyService.provider_contact_name,
                            id_receiver = myPartyService.id_client,
                            name_receiver = myPartyService.client_contact_name,

                            id_service_event = myPartyService.id,
                            id_service = myPartyService.id_service.orEmpty(),
                            type = "APPROVAL"
                        )
                        vmt.requestEditQuoteFromProviderToClient(request) {
                            vmt.alreadyRequestedEditQuoteFromProviderToClient = false
                            showToastOnUiThread(context, it)
                            showProgressDialog = false
                        }
                    }
                }
            )

            NoteBookDialog(
                isVisible = showNoteBookDialog,
                type = noteType,
                savedNotes = savedNotes,
                onDismiss = { showNoteBookDialog = false },
                onSaveClicked = { newNotes ->
                    showNoteBookDialog = false
                    cQuote?.id?.let { quoteId ->
                        showProgressDialog = true

                        var notesProvider: String? = null
                        var notesClient: String? = null
                        var notesImportant: String? = null

                        when (noteType) {
                            NotesType.PERSONAL_CLIENT -> notesClient = newNotes
                            NotesType.PERSONAL_PROVIDER -> notesProvider = newNotes
                            NotesType.IMPORTANT -> notesImportant = newNotes
                        }

                        vmt.addNotesToQuote(
                            quoteId = quoteId,
                            personalNotesProvider = notesProvider,
                            personalNotesClient = notesClient,
                            importantNotes = notesImportant,
                            onFinished = { msg ->
                                showProgressDialog = false
                                showToastOnUiThread(context, msg)
                            }
                        )
                    }
                }
            )

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column {
                    VerticalSpacer(height = 3.dp)
                    TopEventInformation(myPartyService)

                    CardAuthBackground(
                        centerContent = false,
                        addScroll = false,
                        bottomPadding = 10.dp.autoSize()
                    ) {
                        TopContactInformation(
                            clientId = if (isProvider) {
                                myPartyService.event_data?.id_client
                            } else {
                                myPartyService.id_provider
                            }
                        )

                        if (!isProvider) {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)) {
                                HorizontalLine(color = Color.LightGray)
                            }
                            TopServiceInformation(myPartyService)
                        }

                        VerticalSpacer(height = 10.dp)

                        Column(modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = LightGray,
                                shape = allRoundedCornerShape8
                            )) {

                            if (isProvider) {
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)) {
                                    // left gray button to create or edit quotation
                                    Box(
                                        modifier = Modifier.weight(0.49f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        TabNewOrEditQuote(
                                            text = if (alreadyExistsQuote) stringResource(R.string.mifiesta_edit) else stringResource(R.string.mifiesta_quote),
                                            icon = if (alreadyExistsQuote) R.drawable.ic_edit_fiestamas else R.drawable.ic_add_fiestamas,
                                            onClick = {
                                                if (alreadyExistsQuote) {
                                                    if (cQuote?.allow_edit == true) {
                                                        if (isUsingExpressQuote) {
                                                            showNewOrEditExpressQuoteDialog = true
                                                        } else {
                                                            showNewOrEditQuoteDialog = true
                                                        }
                                                    } else {
                                                        showYesNoDialogToEditQuote = true
                                                    }
                                                } else {
                                                    if (isUsingExpressQuote) {
                                                        showNewOrEditExpressQuoteDialog = true
                                                    } else {
                                                        showNewOrEditQuoteDialog = true
                                                    }
                                                }
                                            }
                                        )
                                    }
                                    // right pink button to create express quotation
                                    if (!alreadyExistsQuote) {
                                        Box(modifier = Modifier.weight(0.02f))
                                        Box(
                                            modifier = Modifier.weight(0.49f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(modifier = Modifier
                                                .background(
                                                    color = PinkFiestamas,
                                                    shape = allRoundedCornerShape12
                                                )
                                                .fillMaxWidth()
                                                .clickable {
                                                    showNewOrEditExpressQuoteDialog = true
                                                }
                                            ) {
                                                TextRegular(
                                                    modifier = Modifier.padding(vertical = 8.dp.autoSize()),
                                                    text = stringResource(id = R.string.mifiesta_quote_express),
                                                    color = Color.White,
                                                    size = 12.sp.autoSize()
                                                )
                                            }
                                        }
                                    } else {
                                        Box(modifier = Modifier.weight(0.02f))
                                        Box(
                                            modifier = Modifier
                                                .weight(0.49f)
                                                .height(32.dp.autoSize()),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            MessagesFromNegotiationButton(
                                                counterUnreadMessages = counterUnreadMessages,
                                                onNavigateNotificationsClicked = {
                                                    onNavigateNotificationsClicked()
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            if (isProvider) {
                                cQuote?.let { quoteResponse ->
                                    ScreenTabForBid(
                                        vms = vmt,
                                        isProvider = true,
                                        quote = quoteResponse,
                                        userOrProviderId = myPartyService.id_provider,
                                        status = serviceEventStatus,
                                        serviceEventId = myPartyService.id,
                                        pushTitle = "${myPartyService.event_data?.name_event_type} ${myPartyService.event_data?.name}",
                                        onOptionsClicked = { showOptionsQuoteDialog = true },
                                        showProgress = { showProgressDialog = it },
                                        onNoteBookClicked = {
                                            savedNotes = it
                                            showNoteBookDialog = true
                                            noteType = NotesType.PERSONAL_PROVIDER
                                        },
                                        onNotesQuoteClicked = {
                                            savedNotes = it
                                            showNoteBookDialog = true
                                            noteType = NotesType.IMPORTANT
                                        },
                                        reloadScreen = reloadScreen
                                    )
                                } ?: run {
                                    ProviderQuoteEmptyView(
                                        status = serviceEventStatus,
                                        onStatusClicked = { showOptionsQuoteDialog = true }
                                    )
                                }
                            } else {
                                if (cQuote == null) {
                                    RequestQuoteEmptyView(
                                        status = serviceEventStatus,
                                        onStatusClicked = { showOptionsQuoteDialog = true },
                                        onRequestClicked = {
                                            coroutineScope.launch {
                                                showProgressDialog = true
                                                val request = RequestQuotation(
                                                    title = "${myPartyService.event_data?.name_event_type} ${myPartyService.event_data?.name}",
                                                    content = "El usuario ${userDb?.name} requiere cotización",
                                                    id_client_event = myPartyService.id_client_event,
                                                    id_sender = myPartyService.id_client,
                                                    name_sender = myPartyService.client_contact_name,
                                                    id_receiver = myPartyService.id_provider,
                                                    name_receiver = myPartyService.provider_contact_name,
                                                    id_service_event = myPartyService.id,
                                                    id_service = myPartyService.id_service.orEmpty()
                                                )
                                                vmt.requestQuoteFromClientToProvider(request) {
                                                    vmt.alreadyRequestedQuoteFromClientToProvider = false
                                                    showProgressDialog = false
                                                    showToastOnUiThread(context, it)
                                                }
                                            }
                                        }
                                    )
                                } else {
                                    ScreenTabForBid(
                                        vms = vmt,
                                        isProvider = false,
                                        quote = cQuote!!,
                                        userOrProviderId = myPartyService.event_data?.id_client.orEmpty(),
                                        status = serviceEventStatus,
                                        serviceEventId = myPartyService.id,
                                        pushTitle = "${myPartyService.event_data?.name_event_type} ${myPartyService.event_data?.name}",
                                        onOptionsClicked = { showOptionsQuoteDialog = true },
                                        showProgress = { showProgressDialog = it },
                                        onNoteBookClicked = {
                                            savedNotes = it
                                            showNoteBookDialog = true
                                            noteType = NotesType.PERSONAL_CLIENT
                                        },
                                        onNotesQuoteClicked = {
                                            savedNotes = it
                                            showNoteBookDialog = true
                                            noteType = NotesType.IMPORTANT
                                        },
                                        reloadScreen = reloadScreen
                                    )
                                }
                            }
                        }
                    }
                }

                // todo: uncomment to show fab to redirect Invitations
                /*
                FloatingButtonV2(
                    icon = R.drawable.ic_id,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(22.dp)
                ) {
                    onNavigateAdminInvitationsClicked(
                        userDb?.id.orEmpty(), // userId
                        idClientEvent // idClientEvent
                    )
                }
                */
            }
        },
        addBottomPadding = false,
        endButton = R.drawable.ic_envelope_black,
        validateOfflineMode = true,
        notificationsCounter = counterUnreadMessages.toString(),
        onBackButtonClicked = { onBackClicked() },
        onEndButtonClicked = { onNavigateNotificationsClicked() }
    )
}

@Suppress("UNUSED_PARAMETER")
@Composable
fun ScreenTabForBid(
    vms: ServiceNegotiationViewModel,
    quote: GetQuoteResponse,
    isProvider: Boolean,
    userOrProviderId: String,
    status: String,
    serviceEventId: String,
    pushTitle: String,
    onOptionsClicked: () -> Unit,
    showProgress: (Boolean) -> Unit,
    onNoteBookClicked: (String) -> Unit,
    onNotesQuoteClicked: (String) -> Unit,
    reloadScreen: () -> Unit
) {
    val context = LocalContext.current
    val bids = remember { mutableStateListOf<BidForQuote>() }
    var showYesNoDialogToDeclineOffer by remember { mutableStateOf(false) }
    val alreadyAccepted by remember { mutableStateOf(quote.bids.lastOrNull()?.status == ACCEPTED) }

    LaunchedEffect(quote.bids) {
        bids.clear()
        bids.addAll(quote.bids)
    }

    YesNoDialog(
        isVisible = showYesNoDialogToDeclineOffer,
        icon = R.drawable.ic_question_circled,
        title = stringResource(R.string.mifiesta_confirm_decline_quote_title),
        message = stringResource(R.string.mifiesta_confirm_decline_quote_body),
        onDismiss = { showYesNoDialogToDeclineOffer = false },
        onOk = {
            showYesNoDialogToDeclineOffer = false
            vms.acceptOrDeclineOfferV2(
                serviceEventId = serviceEventId,
                quoteId = quote.id,
                userId = userOrProviderId,
                total = bids.last().bid,
                accepted = false,
                title = pushTitle,
                content = "Se rechazó la edición de la cotización"
            )
            showToast(context, "Enviando respuesta...")
        }
    )

    Column(modifier = Modifier
        .fillMaxWidth()
        .background(color = LightGray)
        .padding(6.dp.autoSize())
    ) {

        Column(modifier = Modifier.weight(0.7f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.White,
                        shape = allRoundedCornerShape8
                    ),
                verticalArrangement = Arrangement.Bottom
            ) {

                Column(
                    modifier = Modifier
                        .weight(1f)  // Ocupa el espacio disponible
                        .verticalScroll(rememberScrollState())
                ) {
                    ProductsInformationDetails(quote.elements)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp.autoSize())
                    ) {
                        HorizontalLine(color = Color.LightGray)
                    }
                }

                Column(
                    modifier = Modifier.padding(vertical = 10.dp.autoSize())
                ) {
                    bids.lastOrNull()?.let { bid ->
                        if (isProvider) {
                            CardRightYellowBid(
                                item = bid,
                                isButtonEnabled = false,
                                showCostOnEdittext = true,
                                text = stringResource(R.string.mifiesta_my_offer),
                                onButtonClicked = { }
                            )
                        } else {
                            val buttonEnabled = bid.status != ACCEPTED && bid.status != REJECTED

                            CardAcceptOrDeclineBidPink(
                                item = bid,
                                isButtonEnabled = buttonEnabled,
                                onAccept = {
                                    vms.acceptOrDeclineOfferV2(
                                        serviceEventId = serviceEventId,
                                        quoteId = quote.id,
                                        userId = userOrProviderId,
                                        total = bids.last().bid,
                                        accepted = true,
                                        title = pushTitle,
                                        content = "Se aceptó la edición de la cotización"
                                    )
                                    showToast(context, "Enviando respuesta...")
                                },
                                onDecline = {
                                    showYesNoDialogToDeclineOffer = true
                                }
                            )
                        }

                        if (bid.status == ACCEPTED) {
                            CardGreenAccepted(bid)
                        } else if (bid.status == REJECTED) {
                            CardRedDeclined()
                        }
                    }
                }

                /*
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 12.dp)
                        ) {
                            if (isProvider) {
                                bids.forEachIndexed { i, bid ->
                                    if (bid.status == NEGOTIATING) {
                                        if (bid.user_role == PROVIDER) {
                                            item {
                                                var buttonEnabled = false
                                                if (i == bids.size - 1 && bids.size > 1) buttonEnabled = true

                                                var showCost = false
                                                if (i <= bids.size - 1) showCost = true

                                                CardRightYellowBid(
                                                    item = bid,
                                                    isButtonEnabled = buttonEnabled,
                                                    showCostOnEdittext = showCost,
                                                    text = stringResource(R.string.mifiesta_my_offer),
                                                    onButtonClicked = { }
                                                )
                                            }
                                        } else {
                                            item {
                                                val nextItemStatus =if (i+1 < bids.size) {
                                                    bids[i+1].status
                                                } else NEGOTIATING

                                                if (nextItemStatus != ACCEPTED) {
                                                    CardLeftGreenBid(
                                                        item = bid,
                                                        showPinkButton = false,
                                                        isButtonEnabled = i == bids.size - 1, // only if is last item is enabled,
                                                        isEdittextEnabled = false,
                                                        showCostOnEdittext = true,
                                                        onPinkButtonClicked = { /* no applicable */ },
                                                        onButtonClicked = {
                                                            // provider accepted the bid
                                                            showProgress(true)
                                                            vms.acceptOffer(serviceEventId, quote.id, userOrProviderId) { response ->
                                                                showProgress(false)
                                                                when (response) {
                                                                    is BaseResult.Error -> showToastOnUiThread(context, response.rawResponse.message)
                                                                    is BaseResult.Success -> showToastOnUiThread(context, context.getString(R.string.mifiesta_quote_accepted))
                                                                }
                                                            }
                                                        }
                                                    )
                                                }
                                            }

                                            // add yellow button under the accepted one
                                            if (i == bids.size - 1) {
                                                item {
                                                    CardRightYellowBid(
                                                        item = bid,
                                                        isButtonEnabled = true,
                                                        showCostOnEdittext = false,
                                                        text = stringResource(R.string.mifiesta_my_offer),
                                                        onButtonClicked = {
                                                            if (it.bid == 0) {
                                                                showToastOnUiThread(context, context.getString(R.string.mifiesta_quote_add_quantity_to_offer))
                                                            } else {
                                                                // provider made a new offer
                                                                showProgress(true)
                                                                vms.createNewOffer(
                                                                    serviceEventId = serviceEventId,
                                                                    quoteId = quote.id,
                                                                    userId = userOrProviderId,
                                                                    bid = it.bid,
                                                                    onFinished = { showProgress(false) }
                                                                )
                                                            }
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (bid.status == ACCEPTED) {
                                        item {
                                            // remove CardBothAcceptedBid as requested by PM
                                            //CardBothAcceptedBid(bid)
                                            CardLeftGreenBid(
                                                item = bid,
                                                showPinkButton = false,
                                                isButtonEnabled = false,
                                                isEdittextEnabled = false,
                                                showCostOnEdittext = true,
                                                isAcceptedItem = true,
                                                onButtonClicked = { },
                                                onPinkButtonClicked = { }
                                            )
                                        }
                                        alreadyAccepted = true
                                        finalCost = bid.bid
                                        updateStatus(
                                            context = context,
                                            vmt = vms,
                                            serviceEventId = serviceEventId,
                                            status = OptionsQuote.Hired,
                                            onFinished = { /* nothing since status is updated realtime on db */ }
                                        )
                                    }
                                }
                            }

                            if (!isProvider) {
                                bids.forEachIndexed { i, bid ->
                                    if (bid.status == NEGOTIATING) {
                                        if (bid.user_role == CLIENT) {
                                            item {
                                                CardRightYellowBid(
                                                    item = bid,
                                                    isButtonEnabled = bid.isTemp,
                                                    showCostOnEdittext = !bid.isTemp,
                                                    text = if (bid.isTemp) stringResource(R.string.mifiesta_i_offer) else stringResource(R.string.mifiesta_my_offer),
                                                    onButtonClicked = {
                                                        if (it.bid == 0) {
                                                            showToastOnUiThread(context, context.getString(R.string.mifiesta_quote_add_quantity_to_offer))
                                                        } else {
                                                            // client made a new offer
                                                            showProgress(true)
                                                            vms.createNewOffer(
                                                                serviceEventId = serviceEventId,
                                                                quoteId = quote.id,
                                                                userId = userOrProviderId,
                                                                bid = it.bid,
                                                                onFinished = {
                                                                    showProgress(false)
                                                                    //Handler(Looper.getMainLooper()).post { reloadScreen() }
                                                                }
                                                            )
                                                        }
                                                    }
                                                )
                                            }
                                        } else {
                                            item {
                                                val nextItemStatus = if (i+1 < bids.size) {
                                                    bids[i+1].status
                                                } else NEGOTIATING

                                                if (nextItemStatus != ACCEPTED) {
                                                    CardLeftGreenBid(
                                                        item = bid,
                                                        showPinkButton =  i == 0 && bids.size == 1,
                                                        isButtonEnabled = i == bids.size - 1, // only if is last item is enabled
                                                        isEdittextEnabled = false,
                                                        showCostOnEdittext = true,
                                                        onPinkButtonClicked = {
                                                            // create local bid to show on yellow block
                                                            bids.add(
                                                                BidForQuote(
                                                                    bid = 0,
                                                                    id_user = "",
                                                                    status = NEGOTIATING,
                                                                    user_role = CLIENT,
                                                                    isTemp = true
                                                                )
                                                            )
                                                        },
                                                        onButtonClicked = {
                                                            // client accepted offer
                                                            showProgress(true)
                                                            vms.acceptOffer(serviceEventId, quote.id, userOrProviderId) { response ->
                                                                showProgress(false)
                                                                when (response) {
                                                                    is BaseResult.Error -> showToastOnUiThread(context, response.rawResponse.message)
                                                                    is BaseResult.Success -> showToastOnUiThread(context, context.getString(R.string.mifiesta_quote_accepted))
                                                                }
                                                            }
                                                        }
                                                    )
                                                }
                                            }

                                            // add yellow button under the accepted one
                                            if (i == bids.size - 1 && i != 0) {
                                                item {
                                                    CardRightYellowBid(
                                                        item = bid,
                                                        isButtonEnabled = true,
                                                        showCostOnEdittext = false,
                                                        text = stringResource(R.string.mifiesta_my_offer),
                                                        onButtonClicked = {
                                                            if (it.bid == 0) {
                                                                showToastOnUiThread(context, context.getString(R.string.mifiesta_quote_add_quantity_to_offer))
                                                            } else {
                                                                // provider made a new offer
                                                                showProgress(true)
                                                                vms.createNewOffer(
                                                                    serviceEventId = serviceEventId,
                                                                    quoteId = quote.id,
                                                                    userId = userOrProviderId,
                                                                    bid = it.bid,
                                                                    onFinished = {
                                                                        showProgress(false)
                                                                        //Handler(Looper.getMainLooper()).post { reloadScreen() }
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (bid.status == ACCEPTED) {
                                        item {
                                            // remove CardBothAcceptedBid as requested by PM
                                            //CardBothAcceptedBid(bid)
                                            CardLeftGreenBid(
                                                item = bid,
                                                showPinkButton = false,
                                                isButtonEnabled = false,
                                                isEdittextEnabled = false,
                                                showCostOnEdittext = true,
                                                isAcceptedItem = true,
                                                onButtonClicked = { },
                                                onPinkButtonClicked = { }
                                            )
                                        }
                                        alreadyAccepted = true
                                        finalCost = bid.bid
                                        updateStatus(
                                            context = context,
                                            vmt = vms,
                                            serviceEventId = serviceEventId,
                                            status = OptionsQuote.Hired,
                                            onFinished = { /* nothing since status is updated realtime on db */ }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }*/
            }
        }

        Column(modifier = Modifier.weight(0.3f)) {
            BottomData(
                status = status,
                providerNotes = quote.notes,
                noteBook = if (isProvider) quote.noteBook_provider.orEmpty() else quote.noteBook_client.orEmpty(),
                alreadyAccepted = alreadyAccepted,
                finalEventCost = bids.lastOrNull()?.bid ?: 0,
                onNoteBookClicked = onNoteBookClicked,
                onNotesQuoteClicked = onNotesQuoteClicked,
                onOptionsClicked = { onOptionsClicked() }
            )
        }
    }
}

fun updateStatus(
    context: Context,
    vmt: ServiceNegotiationViewModel,
    serviceEventId: String,
    status: OptionsQuote,
    onFinished: (String) -> Unit
) {
    vmt.updateServiceStatus(
        serviceEventId = serviceEventId,
        status = status,
        onFinished = { response ->
            when (response) {
                is BaseResult.Error -> onFinished(response.rawResponse.message)
                is BaseResult.Success -> onFinished(context.getString(R.string.mifiesta_status_updated))
            }
        }
    )
}