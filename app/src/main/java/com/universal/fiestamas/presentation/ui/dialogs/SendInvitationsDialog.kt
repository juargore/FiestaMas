package com.universal.fiestamas.presentation.ui.dialogs

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.domain.models.Guest
import com.universal.fiestamas.domain.models.Tag
import com.universal.fiestamas.domain.usecases.EventUseCase
import com.universal.fiestamas.domain.usecases.GuestUseCase
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.CardGuestStatus
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.GuestStatus
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.InvitationsViewModel
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.getGuestStatus
import com.universal.fiestamas.presentation.utils.showToast

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SendInvitationsDialog(
    isVisible: Boolean,
    context: Context,
    allGuests: List<Guest>?,
    idClientEvent: String,
    viewModel: InvitationsViewModel,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        val pendingList = allGuests?.filter { it.status.getGuestStatus() == GuestStatus.Pending }
        //val pendingList = allGuests?.plus(allGuests)?.plus(allGuests)?.plus(allGuests)?.plus(allGuests)

        LaunchedEffect(pendingList) {
            if (pendingList != null) {
                viewModel.pendingGuestsSelected.addAll(pendingList)
            }
        }

        BaseDialog(
            title = "Enviar Invitaciones",
            addCloseIcon = true,
            onDismiss = onDismiss,
            content = {
                if (pendingList.isNullOrEmpty()) {
                    EmptyPendingList()
                    return@BaseDialog
                }

                DescriptionPendingList()

                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    FlowColumn {
                        pendingList.forEach { guest ->
                            ItemGuestSelectable(guest) { checked ->
                                if (checked) {
                                    viewModel.pendingGuestsSelected.add(guest)
                                } else {
                                    viewModel.pendingGuestsSelected.remove(guest)
                                }
                            }
                        }
                    }

                    VerticalSpacer(height = 12.dp)

                    Row(modifier = Modifier.padding(horizontal = 50.dp)) {
                        ButtonPinkRoundedCornersV2(
                            verticalPadding = 8.dp,
                            content = {
                                TextSemiBold(
                                    text = "Enviar",
                                    size = 18.sp,
                                    color = Color.White,
                                    shadowColor = Color.Gray
                                )
                            },
                            onClick = {
                                if (viewModel.pendingGuestsSelected.isEmpty()) {
                                    showToast(context, "Seleccione al menos a un invitado de la lista")
                                } else {
                                    val guestIds = viewModel.pendingGuestsSelected.map { it.id }
                                    viewModel.sendInvitationToManyGuests(idClientEvent, guestIds)
                                    showToast(context, "Enviando invitaciones.\nEspere un momento...", Toast.LENGTH_LONG)
                                    onDismiss()
                                }
                            }
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun EmptyPendingList() {
    TextRegular(
        text = "No hay invitados con estatus",
        color = Color.Gray
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(modifier = Modifier.width(85.dp)) {
            CardGuestStatus("PENDING")
        }
        TextRegular(
            text = " en la lista",
            color = Color.Gray,
            fillMaxWidth = false
        )
    }
}

@Composable
fun DescriptionPendingList() {
    TextRegular(
        text = "Quieres enviar invitaciones a estos ",
        color = Color.Gray
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextRegular(
            text = "usuarios con estatus ",
            color = Color.Gray,
            fillMaxWidth = false
        )
        Row(modifier = Modifier.width(85.dp)) {
            CardGuestStatus("PENDING")
        }
        TextRegular(
            text = " ?",
            color = Color.Gray,
            fillMaxWidth = false
        )
    }

    VerticalSpacer(height = 10.dp)
}

@Composable
fun ItemGuestSelectable(
    guest: Guest,
    onChecked: (Boolean) -> Unit
) {
    var checkedState by remember { mutableStateOf(true) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checkedState,
            onCheckedChange = {
                checkedState = it
                onChecked(it)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = PinkFiestamas,
                uncheckedColor = Color.Black,
                checkmarkColor = Color.White
            )
        )
        Column {
            TextMedium(
                text = guest.name.orEmpty(),
                fillMaxWidth = false,
                size = 15.sp,
                includeFontPadding = false
            )
            TextMedium(
                text = guest.email.orEmpty(),
                fillMaxWidth = false,
                size = 12.sp,
                color = Color.LightGray,
                includeFontPadding = false
            )
        }

    }
}

@Preview
@Composable
fun SendInvitationsDialogPreview() {
    SendInvitationsDialog(
        isVisible = true,
        context = LocalContext.current,
        allGuests = guests,
        idClientEvent = "",
        viewModel = InvitationsViewModel(
            GuestUseCase(IGuestRepositoryTest()),
            EventUseCase(IEventRepositoryTest())
        ),
        onDismiss = { }
    )
}

private val guests = listOf(
    Guest(
        id = "01",
        created_at = null,
        email = "test1@gmail.com",
        id_client_event = "",
        name = "Luis Pablo Rosales",
        num_table = 1,
        phone = "12345",
        status = "PENDING",
        tags = listOf(Tag("MyTag1"), Tag("MyTag2"), Tag("MyTag3"), Tag("MyTag4")),
        viewed = null
    ),
    Guest(
        id = "02",
        created_at = null,
        email = "test2@gmail.com",
        id_client_event = "",
        name = "Ana María Cuevas",
        num_table = 12,
        phone = "67890",
        status = "ACCEPTED",
        tags = listOf(),
        viewed = null
    ),
    Guest(
        id = "03",
        created_at = null,
        email = "test3@gmail.com",
        id_client_event = "",
        name = "Luisa Herández Díaz",
        num_table = 0,
        phone = "246801",
        status = "CHECKED_IN",
        tags = listOf(Tag("MyTagAA con nombre largo a ver que show aqui")),
        viewed = null
    ),
    Guest(
        id = "04",
        created_at = null,
        email = "test3@gmail.com",
        id_client_event = "",
        name = "José Rojas Luna",
        num_table = null,
        phone = "246801",
        status = "PENDING",
        tags = listOf(),
        viewed = null
    ),
    Guest(
        id = "04",
        created_at = null,
        email = "test4@gmail.com",
        id_client_event = "",
        name = "Erika Cisneros Salazar",
        num_table = null,
        phone = "246801",
        status = "SENT",
        tags = listOf(Tag("Tag test - 01")),
        viewed = null
    )
)