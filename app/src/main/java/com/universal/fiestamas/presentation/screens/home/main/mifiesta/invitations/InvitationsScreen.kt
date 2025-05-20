package com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Guest
import com.universal.fiestamas.presentation.theme.GuestStatusAcceptedBackgroundColor
import com.universal.fiestamas.presentation.theme.GuestStatusAcceptedTextColor
import com.universal.fiestamas.presentation.theme.GuestStatusCanceledBackgroundColor
import com.universal.fiestamas.presentation.theme.GuestStatusCanceledTextColor
import com.universal.fiestamas.presentation.theme.GuestStatusCheckedInBackgroundColor
import com.universal.fiestamas.presentation.theme.GuestStatusCheckedInTextColor
import com.universal.fiestamas.presentation.theme.GuestStatusDeclinedBackgroundColor
import com.universal.fiestamas.presentation.theme.GuestStatusDeclinedTextColor
import com.universal.fiestamas.presentation.theme.GuestStatusPendingBackgroundColor
import com.universal.fiestamas.presentation.theme.GuestStatusPendingTextColor
import com.universal.fiestamas.presentation.theme.GuestStatusSentBackgroundColor
import com.universal.fiestamas.presentation.theme.GuestStatusSentTextColor
import com.universal.fiestamas.presentation.theme.LighterGray
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape14
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape6
import com.universal.fiestamas.presentation.theme.topRoundedCornerShape15
import com.universal.fiestamas.presentation.ui.BottomShadow
import com.universal.fiestamas.presentation.ui.ButtonOrderBy
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.IconArrow
import com.universal.fiestamas.presentation.ui.ImageLeftTextRightV2
import com.universal.fiestamas.presentation.ui.RegularEditText
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.bottom_sheets.BottomSheetInvitationsFilterBy
import com.universal.fiestamas.presentation.ui.dialogs.AddGuestDialog
import com.universal.fiestamas.presentation.ui.dialogs.EditGuestDialog
import com.universal.fiestamas.presentation.ui.dialogs.GuestDetailsDialog
import com.universal.fiestamas.presentation.ui.dialogs.ManageTagsDialog
import com.universal.fiestamas.presentation.ui.dialogs.RVSPTrackingDialog
import com.universal.fiestamas.presentation.ui.dialogs.SendInvitationsDialog
import com.universal.fiestamas.presentation.ui.dialogs.YesNoDialogV2
import com.universal.fiestamas.presentation.ui.empty_screen.BaseEmptyScreen
import com.universal.fiestamas.presentation.utils.FloatingContextMenu
import com.universal.fiestamas.presentation.utils.MenuData
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.getGuestStatus
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.showToast
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalLayoutApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun InvitationsScreen(
    vm: InvitationsViewModel = hiltViewModel(),
    idClientEvent: String,
    onManageTables: () -> Unit,
    onManageHostess: () -> Unit,
    onManageInvitation: () -> Unit,
    onBackClicked: () -> Unit
) {

    vm.getGuestsList(idClientEvent)
    vm.getAllTagsByEvent(idClientEvent)

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }

    val coroutineScope = rememberCoroutineScope()
    val guestsList by vm.guestsList.collectAsState()
    val resetSearchTerm by vm.resetSearch.collectAsState()
    val emptyResults by vm.emptyResults.collectAsState()
    val allTags by vm.tagsList.collectAsState()

    var bottomSheetContent by remember { mutableStateOf(BottomSheetContent.FilterBy) }
    var showAddGuestDialog by remember { mutableStateOf(false) }
    var showRSVPTrackingDialog by remember { mutableStateOf(false) }
    var showManageTagsDialog by remember { mutableStateOf(false) }
    var showSendInvitationsDialog by remember { mutableStateOf(false) }
    var guestToEdit: Guest? by remember { mutableStateOf(null) }
    var guestToShow: Guest? by remember { mutableStateOf(null) }
    var dialogState by remember { mutableStateOf<ConfirmationDialogState>(ConfirmationDialogState.None) }
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    ConfirmationDialogs(
        dialogState = dialogState,
        onDismiss = { dialogState = ConfirmationDialogState.None },
        onPrimaryAction = {
            when (val state = dialogState) {
                is ConfirmationDialogState.DeleteGuest -> vm.deleteGuest(state.guest.id)
                is ConfirmationDialogState.SendInvitation -> vm.sendInvitationToOneGuest(idClientEvent, state.guest.id)
                is ConfirmationDialogState.CancelAccess -> vm.updateGuestStatus(GuestStatus.Canceled, state.guest.id)
                is ConfirmationDialogState.GiveAccess -> vm.updateGuestStatus(GuestStatus.Pending, state.guest.id)
                is ConfirmationDialogState.None -> Unit
            }
            dialogState = ConfirmationDialogState.None
        }
    )

    AddGuestDialog(
        viewModel = vm,
        context = context,
        idClientEvent = idClientEvent,
        isVisible = showAddGuestDialog,
        onDismiss = { showAddGuestDialog = false }
    )

    EditGuestDialog(
        viewModel = vm,
        context = context,
        guestToEdit = guestToEdit,
        isVisible = guestToEdit != null,
        onDismiss = { guestToEdit = null },
    )

    RVSPTrackingDialog(
        isVisible = showRSVPTrackingDialog,
        guestList = guestsList,
        onDismiss = { showRSVPTrackingDialog = false }
    )

    ManageTagsDialog(
        viewModel = vm,
        context = context,
        tagsList = allTags,
        idClientEvent = idClientEvent,
        isVisible = showManageTagsDialog,
        onDismiss = { showManageTagsDialog = false }
    )

    GuestDetailsDialog(
        isVisible = guestToShow != null,
        guest = guestToShow,
        viewModel = vm,
        context = context,
        allTags = allTags,
        idClientEvent = idClientEvent,
        onEdit = { guestToEdit = it },
        onDelete = { dialogState = ConfirmationDialogState.DeleteGuest(it)},
        onSendInvitation = { dialogState = ConfirmationDialogState.SendInvitation(it) },
        onCancelAccess = { dialogState = ConfirmationDialogState.CancelAccess(it) },
        onGiveAccess = { dialogState = ConfirmationDialogState.GiveAccess(it) },
        onDismiss = { guestToShow = null }
    )
    
    SendInvitationsDialog(
        isVisible = showSendInvitationsDialog,
        context = context,
        allGuests = guestsList,
        idClientEvent = idClientEvent,
        viewModel = vm,
        onDismiss = { showSendInvitationsDialog = false }
    )

    GradientBackground(
        content = {
            ModalBottomSheetLayout(
                sheetState = modalSheetState,
                sheetShape = topRoundedCornerShape15,
                sheetContent = {
                    when (bottomSheetContent) {
                        BottomSheetContent.FilterBy -> BottomSheetInvitationsFilterBy {
                            coroutineScope.launch { modalSheetState.hide() }
                            vm.filterGuestListByStatus(it)
                        }
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {
                                keyboardController?.hide()
                                focusManager.clearFocus(true)
                            }
                        )
                ) {
                    Column {
                        HeaderWithButtons(
                            onRSVPTracking = { showRSVPTrackingDialog = true },
                            onNewGuest = { showAddGuestDialog = true },
                            onManageTags = { showManageTagsDialog = true },
                            onSendInvitations = { showSendInvitationsDialog = true },
                            onMyInvitation = { onManageInvitation() },
                            onHostess = { onManageHostess() },
                            onManageTables = { onManageTables() }
                        )
                        SearchAndFilterSection(
                            resetValue = resetSearchTerm,
                            onValueChanged = { vm.onSearchTerm(it) },
                            onFilterBy = {
                                coroutineScope.launch {
                                    bottomSheetContent = BottomSheetContent.FilterBy
                                    modalSheetState.show()
                                }
                            }
                        )
                        if (guestsList == null) {
                            NoDataFromServerScreen {
                                showAddGuestDialog = true
                            }
                        } else {
                            if (guestsList!!.isEmpty()) {
                                EmptyScreenResults(emptyResults)
                            } else {
                                Column(
                                    modifier = Modifier
                                        .verticalScroll(rememberScrollState())
                                        .sidePadding()
                                ) {
                                    FlowRow(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        guestsList?.forEach { guest ->
                                            CardGuest(
                                                guest = guest,
                                                onClick = { guestToShow = guest },
                                                onEdit = { guestToEdit = guest },
                                                onSendInvitation = { dialogState = ConfirmationDialogState.SendInvitation(guest) },
                                                onCancelAccess = { dialogState = ConfirmationDialogState.CancelAccess(guest) },
                                                onGiveAccess = { dialogState = ConfirmationDialogState.GiveAccess(guest) },
                                                onDelete = { dialogState = ConfirmationDialogState.DeleteGuest(guest) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        addBottomPadding = false,
        showLogoFiestamas = false,
        titleScreen = "Invitaciones",
        onBackButtonClicked = { onBackClicked() }
    )
}


sealed class ConfirmationDialogState {
    object None : ConfirmationDialogState()
    data class DeleteGuest(val guest: Guest) : ConfirmationDialogState()
    data class SendInvitation(val guest: Guest) : ConfirmationDialogState()
    data class CancelAccess(val guest: Guest) : ConfirmationDialogState()
    data class GiveAccess(val guest: Guest) : ConfirmationDialogState()
}

@Composable
fun ConfirmationDialogs(
    dialogState: ConfirmationDialogState,
    onDismiss: () -> Unit,
    onPrimaryAction: () -> Unit
) {
    val context = LocalContext.current
    val (title, message, toastText) = when (dialogState) {
        is ConfirmationDialogState.DeleteGuest -> {
            Triple(
                first = "Eliminar Invitado",
                second = "¿Confirma que desea eliminar el invitado seleccionado?",
                third = "Eliminando invitado..."
            )
        }
        is ConfirmationDialogState.SendInvitation -> {
            Triple(
                first = "Enviar Invitación",
                second = "¿Quieres enviar una invitación a ${dialogState.guest.name}?",
                third = "Enviando invitación..."
            )
        }
        is ConfirmationDialogState.CancelAccess -> {
            Triple(
                first = "Cancelar acceso al invitado",
                second = "¿Quieres cancelar el acceso a ${dialogState.guest.name}?",
                third = "Cancelando acceso..."
            )
        }
        is ConfirmationDialogState.GiveAccess -> {
            Triple(
                first = "Dar acceso al invitado",
                second = "¿Quieres dar acceso a ${dialogState.guest.name}?",
                third = "Dando acceso..."
            )
        }
        ConfirmationDialogState.None -> Triple(null, null, null)
    }
    if (title != null && message != null) {
        YesNoDialogV2(
            isVisible = true,
            title = title,
            message = message,
            onDismiss = onDismiss,
            onPrimaryButtonClicked = {
                onPrimaryAction()
                showToast(context, toastText)
            }
        )
    }
}

@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
fun HeaderWithButtons(
    onRSVPTracking: () -> Unit,
    onNewGuest: () -> Unit,
    onManageTags: () -> Unit,
    onMyInvitation: () -> Unit,
    onSendInvitations: () -> Unit,
    onHostess: () -> Unit,
    onManageTables: () -> Unit,
) {
    val headerItemsList = listOf(
        Triple(R.drawable.ic_list_filled, "RSVP\nTracking", HeaderOptions.RSVP),
        Triple(R.drawable.ic_users_filled, "Nuevo\nInvitado", HeaderOptions.NewGuest),
        Triple(R.drawable.ic_tags_filled, "Administrar\nEtiquetas", HeaderOptions.ManageTags),
        Triple(R.drawable.ic_send_envelope, "Enviar\nInvitaciones", HeaderOptions.SendInvitations),
        Triple(R.drawable.ic_id, "Mi\nInvitación", HeaderOptions.MyInvitation),
        Triple(R.drawable.ic_chair_filled, "Administrar\nmesas", HeaderOptions.ManageTables),
        Triple(R.drawable.ic_user_filled, "Servicio\nHostess", HeaderOptions.Hostess)
    )
    val lazyListState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(LighterGray)
            .height(80.dp)
            .padding(10.dp)
    ) {
        LazyRow(
            state = lazyListState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(headerItemsList) { item ->
                HeaderButton(
                    image = item.first,
                    text = item.second
                ) {
                    when (item.third) {
                        HeaderOptions.RSVP -> onRSVPTracking()
                        HeaderOptions.NewGuest -> onNewGuest()
                        HeaderOptions.ManageTags -> onManageTags()
                        HeaderOptions.MyInvitation -> onMyInvitation()
                        HeaderOptions.SendInvitations -> onSendInvitations()
                        HeaderOptions.ManageTables -> onManageTables()
                        HeaderOptions.Hostess -> onHostess()
                    }
                }
            }
        }
        if (lazyListState.canScrollBackward) {
            Box(modifier = Modifier.align(Alignment.CenterStart)) {
                IconArrow(R.drawable.ic_arrow_prev)
            }
        }
        if (lazyListState.canScrollForward) {
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                IconArrow(R.drawable.ic_arrow_next)
            }
        }
    }
    BottomShadow()
}

@Composable
fun HeaderButton(
    image: Int,
    text: String,
    onItemClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .clickable { onItemClicked() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val colorFilter =
            if (image == R.drawable.ic_list_filled) null else ColorFilter.tint(color = PinkFiestamas)

        Image(
            modifier = Modifier.size(30.dp),
            painter = painterResource(image),
            colorFilter = colorFilter,
            contentDescription = null
        )
        VerticalSpacer(height = 5.dp)
        TextRegular(
            text = text,
            size = 11.sp,
            color = Color.DarkGray,
            verticalSpace = 12.sp,
            fillMaxWidth = false
        )
    }
}

@Composable
fun NoDataFromServerScreen(
    onAddClicked: () -> Unit
) {
    BaseEmptyScreen(
        grayText = "Sin Invitados",
        buttonText = "Agregar Invitado",
        imageTop = {
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(R.drawable.ic_users_filled),
                colorFilter = ColorFilter.tint(color = PinkFiestamas),
                contentDescription = null
            )
        },
        onAddClicked = onAddClicked
    )
}

@Composable
fun EmptyScreenResults(from: EmptyResults) {
    val textToDisplay = if (from == EmptyResults.FilterBy) {
        "Sin resultados que coincidan con el filtro seleccionado."
    } else {
        "Sin resultados que coincidan con tu búsqueda."
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextRegular(
            text = textToDisplay,
            color = Color.DarkGray,
            size = 12.sp
        )
    }
}

@Composable
fun SearchAndFilterSection(
    onValueChanged: (String) -> Unit,
    resetValue: Boolean,
    onFilterBy: () -> Unit
) {
    var searchTerm by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(resetValue) {
        searchTerm = ""
    }

    Row(
        modifier = Modifier.padding(12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RegularEditText(
            modifier = Modifier
                .height(40.dp)
                .sidePadding(5.dp)
                .weight(.7f),
            value = searchTerm,
            placeholder = "  Buscar invitado",
            onValueChange = {
                searchTerm = it
                onValueChanged(it)
            }
        )
        ButtonOrderBy(
            modifier = Modifier.weight(.3f),
            text = "Filtrar por",
            onItemClick = { onFilterBy() }
        )
    }
}

@Composable
fun CardGuest(
    guest: Guest,
    onEdit: () -> Unit,
    onClick: () -> Unit,
    onSendInvitation: () -> Unit,
    onCancelAccess: () -> Unit,
    onGiveAccess: () -> Unit,
    onDelete: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(allRoundedCornerShape14)
                .background(Color.White)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(0.8f)
                        .clickable { onClick() },
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextMedium(
                        text = guest.name.orEmpty(),
                        size = 13.sp.autoSize(),
                        align = TextAlign.Start,
                        fillMaxWidth = false,
                        addThreeDots = true,
                        maxLines = 1
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(0.2f)
                        .padding(horizontal = 6.dp)
                        .clickable { expanded = true },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    Image(
                        modifier = Modifier.size(18.dp.autoSize()),
                        painter = painterResource(R.drawable.ic_three_dots),
                        contentDescription = null,
                    )
                }
            }

            VerticalSpacer(4.dp)

            Row(
                modifier = Modifier.clickable { onClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(0.75f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImageLeftTextRightV2(
                        contentText = {
                            if (guest.num_table == null || guest.num_table == 0) {
                                TextMedium(
                                    text = "Sin Asignar",
                                    size = 12.sp,
                                    fillMaxWidth = false,
                                    color = Color.Gray
                                )
                            } else {
                                TextMedium(
                                    text = guest.num_table.toString(),
                                    size = 15.sp,
                                    fillMaxWidth = false
                                )
                            }
                        },
                        imageLeft = {
                            Image(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(R.drawable.ic_chair_filled),
                                colorFilter = ColorFilter.tint(color = PinkFiestamas),
                                contentDescription = null
                            )
                        }
                    )
                    HorizontalSpacer(width = 14.dp)
                    ImageLeftTextRightV2(
                        imageLeft = {
                            Image(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(R.drawable.ic_tags_filled),
                                colorFilter = ColorFilter.tint(color = PinkFiestamas),
                                contentDescription = null
                            )
                        },
                        contentText = {
                            if (guest.tags?.isNotEmpty() == true) {
                                guest.tags.forEach {  tag ->
                                    CardTag(text = tag.name)
                                    HorizontalSpacer(3.dp)
                                }
                            } else {
                                TextMedium(
                                    text = "Sin Etiquetas",
                                    size = 12.sp,
                                    fillMaxWidth = false,
                                    color = Color.Gray
                                )
                            }
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(0.25f)
                        .padding(start = 5.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    CardGuestStatus(guest.status)
                }
            }
        }

        val guestStatus = guest.status.getGuestStatus()
        val menuEdit = MenuData("Editar") { expanded = false; onEdit() }
        val menuSendInvitation = MenuData("Enviar invitación") { expanded = false; onSendInvitation() }
        val menuCancelAccess = MenuData("Cancelar acceso") { expanded = false; onCancelAccess() }
        val menuGiveAccess = MenuData("Dar acceso") { expanded = false; onGiveAccess() }
        val menuDelete = MenuData("Eliminar") { expanded = false; onDelete() }

        val menuList: List<MenuData> = when (guestStatus) {
            GuestStatus.Pending -> listOf(
                menuEdit,
                menuSendInvitation,
                menuCancelAccess,
                menuDelete
            )
            GuestStatus.Canceled,
            GuestStatus.Declined -> listOf(
                menuEdit,
                menuGiveAccess
            )
            GuestStatus.Sent,
            GuestStatus.Accepted -> listOf(
                menuEdit,
                menuCancelAccess
            )
            GuestStatus.CheckedIn -> listOf()
            else -> listOf()
        }
        FloatingContextMenu(
            expanded = expanded,
            menuData = menuList,
            onDismissRequest = { expanded = it },
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
fun CardTag(
    modifier: Modifier = Modifier,
    text: String,
    textSize: TextUnit = 11.sp,
    onDeleteTag: (() -> Unit)? = null,
    onAssignTag: (() -> Unit)? = null
) {
    var showSpinner by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(allRoundedCornerShape6)
            .border(1.dp, PinkFiestamas, allRoundedCornerShape6)
            .background(PinkFiestamas.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clickable { onAssignTag?.invoke() }
    ) {
        TextSemiBold(
            text = text,
            size = textSize,
            maxLines = 1,
            fillMaxWidth = false,
            addThreeDots = true
        )
        onDeleteTag?.let {
            HorizontalSpacer(width = 10.dp)
            if (showSpinner) {
                CircularProgressIndicator(modifier = Modifier.size(14.dp))
            } else {
                TextSemiBold(
                    text = "x ",
                    size = textSize,
                    fillMaxWidth = false,
                    modifier = Modifier
                        .clickable {
                            showSpinner = true
                            onDeleteTag()
                            Handler(Looper.getMainLooper()).postDelayed({
                                showSpinner = false
                            }, 2000L)
                        }
                )
            }
        }
    }
}

@Composable
fun CardGuestStatus(
    statusString: String?
) {
    val guestStatus = statusString.getGuestStatus()
    val textToDisplay = when (guestStatus) {
        GuestStatus.Pending -> "PENDIENTE"
        GuestStatus.Accepted -> "ACEPTADO"
        GuestStatus.Canceled -> "CANCELADO"
        GuestStatus.Sent -> "ENVIADO"
        GuestStatus.Declined -> "DECLINADO"
        GuestStatus.CheckedIn -> "ADMITIDO"
        else -> "N/A"
    }
    val backgroundColor = when (guestStatus) {
        GuestStatus.Pending -> GuestStatusPendingBackgroundColor
        GuestStatus.Accepted -> GuestStatusAcceptedBackgroundColor
        GuestStatus.Canceled -> GuestStatusCanceledBackgroundColor
        GuestStatus.Sent -> GuestStatusSentBackgroundColor
        GuestStatus.Declined -> GuestStatusDeclinedBackgroundColor
        GuestStatus.CheckedIn -> GuestStatusCheckedInBackgroundColor
        else -> Color.LightGray
    }
    val textColor = when (guestStatus) {
        GuestStatus.Pending -> GuestStatusPendingTextColor
        GuestStatus.Accepted -> GuestStatusAcceptedTextColor
        GuestStatus.Canceled -> GuestStatusCanceledTextColor
        GuestStatus.Sent -> GuestStatusSentTextColor
        GuestStatus.Declined -> GuestStatusDeclinedTextColor
        GuestStatus.CheckedIn -> GuestStatusCheckedInTextColor
        else -> Color.Gray
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(allRoundedCornerShape6)
            .background(backgroundColor.copy(alpha = 0.8f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        TextSemiBold(
            text = textToDisplay,
            size = 11.sp,
            maxLines = 1,
            color = textColor
        )
    }
}

enum class GuestStatus {
    All,
    Pending,
    Accepted,
    Canceled,
    Sent,
    Declined,
    CheckedIn,
    Unknown
}

enum class HeaderOptions {
    RSVP,
    NewGuest,
    ManageTags,
    MyInvitation,
    SendInvitations,
    Hostess,
    ManageTables
}

enum class BottomSheetContent {
    FilterBy
}

enum class EmptyResults {
    Search,
    FilterBy
}
