package com.universal.fiestamas.presentation.ui.dialogs

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Guest
import com.universal.fiestamas.domain.models.Tag
import com.universal.fiestamas.domain.usecases.EventUseCase
import com.universal.fiestamas.domain.usecases.GuestUseCase
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.CardGuestStatus
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.CardTag
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.GuestStatus
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.InvitationsViewModel
import com.universal.fiestamas.presentation.theme.LightBlue
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.ui.CircleAvatarWithInitials
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.ImageLeftTextRightV2
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.FloatingContextMenu
import com.universal.fiestamas.presentation.utils.MenuData
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.getGuestStatus

@Composable
fun GuestDetailsDialog(
    isVisible: Boolean,
    guest: Guest?,
    context: Context,
    idClientEvent: String,
    allTags: List<Tag>,
    viewModel: InvitationsViewModel,
    onEdit: (Guest) -> Unit,
    onSendInvitation: (Guest) -> Unit,
    onCancelAccess: (Guest) -> Unit,
    onGiveAccess: (Guest) -> Unit,
    onDelete: (Guest) -> Unit,
    onDismiss: () -> Unit
) {
    if (guest == null) return

    if (isVisible) {
        var expanded by remember { mutableStateOf(false) }
        var showManageTagsDialog by remember { mutableStateOf(false) }
        val guestTags = remember { mutableStateListOf<Tag>() }

        LaunchedEffect(guest) {
            guest.tags?.let { tags ->
                guestTags.clear()
                guestTags.addAll(tags)
            }
        }

        val guestStatus = guest.status.getGuestStatus()
        val menuEdit = MenuData("Editar") { expanded = false; onEdit(guest); onDismiss() }
        val menuSendInvitation = MenuData("Enviar invitación") { expanded = false; onSendInvitation(guest); onDismiss() }
        val menuCancelAccess = MenuData("Cancelar acceso") { expanded = false; onCancelAccess(guest); onDismiss() }
        val menuGiveAccess = MenuData("Dar acceso") { expanded = false; onGiveAccess(guest); onDismiss() }
        val menuDelete = MenuData("Eliminar") { expanded = false; onDelete(guest); onDismiss() }

        val menuList: List<MenuData> = when (guestStatus) {
            GuestStatus.Pending -> listOf(menuEdit, menuSendInvitation, menuCancelAccess, menuDelete)
            GuestStatus.Canceled, GuestStatus.Declined -> listOf(menuEdit, menuGiveAccess)
            GuestStatus.Sent, GuestStatus.Accepted -> listOf(menuEdit, menuCancelAccess)
            GuestStatus.CheckedIn -> listOf()
            else -> listOf()
        }

        ManageGuestTagsDialog(
            isVisible = showManageTagsDialog,
            context = context,
            guest = guest,
            _guestTags = guest.tags.orEmpty(),
            _allTags = allTags,
            idClientEvent = idClientEvent,
            viewModel = viewModel,
            onDismiss = {
                guestTags.clear()
                guestTags.addAll(it)
                showManageTagsDialog = false
            }
        )

        BaseDialog(
            addCloseIcon = false,
            onDismiss = onDismiss,
            content = {
                Box(
                    modifier = Modifier
                        .height(65.dp)
                        .fillMaxWidth()
                ) {
                    FloatingContextMenu(
                        expanded = expanded,
                        menuData = menuList,
                        onDismissRequest = { expanded = it },
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircleAvatarWithInitials(
                            name = guest.name.orEmpty(),
                            circleSize = 60.dp,
                            textSize = 24.sp,
                            backgroundColor = LightBlue
                        )
                        HorizontalSpacer(width = 12.dp)
                        Column(
                            verticalArrangement = Arrangement.Center,
                        ) {
                            TextSemiBold(
                                text = guest.name.orEmpty(),
                                size = 18.sp,
                                fillMaxWidth = false
                            )
                            TextRegular(
                                text = guest.email.orEmpty(),
                                color = Color.Gray,
                                size = 14.sp,
                                fillMaxWidth = false
                            )
                        }
                    }
                    Image(
                        modifier = Modifier
                            .size(18.dp.autoSize())
                            .align(Alignment.TopEnd)
                            .clickable { expanded = true },
                        painter = painterResource(R.drawable.ic_three_dots),
                        contentDescription = null,
                    )
                }

                VerticalSpacer(height = 15.dp)

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    val vSpace = 7.dp
                    ItemDetailGuestInfo(
                        icon = if (guest.viewed?.viewed == true) R.drawable.ic_eye else R.drawable.ic_eye_slash,
                        iconDesc = "Visto",
                        value = {
                            val text = if (guest.viewed?.viewed == true) "      Visto ✔" else "      No Visto ✖"
                            TextMedium(
                                text = text,
                                size = 15.sp,
                                fillMaxWidth = false,
                                color = Color.Gray
                            )
                        }
                    )
                    VerticalSpacer(vSpace)
                    ItemDetailGuestInfo(
                        icon = R.drawable.ic_phone_calling,
                        iconDesc = "Teléfono",
                        value = {
                            TextMedium(
                                text = if (guest.phone.isNullOrEmpty()) "Sin Teléfono" else guest.phone.orEmpty(),
                                size = 15.sp,
                                fillMaxWidth = false,
                                color = Color.Gray
                            )
                        }
                    )
                    VerticalSpacer(vSpace)
                    ItemDetailGuestInfo(
                        icon = R.drawable.ic_chair_filled,
                        iconDesc = "No. mesa",
                        value = {
                            TextMedium(
                                text = if (guest.num_table == null || guest.num_table == 0) "Sin Asignar" else guest.num_table.toString(),
                                size = 15.sp,
                                color = Color.Gray,
                                fillMaxWidth = false
                            )
                        }
                    )
                    VerticalSpacer(vSpace)
                    ItemDetailGuestInfo(
                        icon = R.drawable.ic_tags_filled,
                        iconDesc = "Etiquetas",
                        value = {
                            if (guestTags.isNotEmpty()) {
                                guestTags.forEach {  tag ->
                                    CardTag(text = tag.name)
                                    VerticalSpacer(height = 4.dp)
                                }
                            } else {
                                TextMedium(
                                    text = "Sin Etiquetas",
                                    size = 15.sp,
                                    fillMaxWidth = false,
                                    color = Color.Gray
                                )
                            }
                            VerticalSpacer(height = 5.dp)
                            TextMedium(
                                modifier = Modifier.clickable { showManageTagsDialog = true },
                                text = "Administrar etiquetas...",
                                size = 14.sp,
                                fillMaxWidth = false,
                                color = Color.Blue
                            )
                            VerticalSpacer(height = 5.dp)
                        }
                    )
                    VerticalSpacer(vSpace)
                    ItemDetailGuestInfo(
                        icon = R.drawable.ic_user_filled,
                        iconDesc = "Estatus",
                        value = {
                            Row(modifier = Modifier
                                .width(140.dp)
                                .padding(start = 11.dp)) {
                                CardGuestStatus(guest.status)
                            }
                        }
                    )
                    VerticalSpacer(vSpace + 5.dp)
                }
            }
        )
    }
}

@Composable
fun ItemDetailGuestInfo(
    icon: Int,
    iconDesc: String,
    value: @Composable () -> Unit
) {
    Row {
        ImageLeftTextRightV2(
            imageLeft = {
                Image(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(icon),
                    colorFilter = ColorFilter.tint(color = PinkFiestamas),
                    contentDescription = null
                )
            },
            contentText = {
                TextMedium(
                    text = " $iconDesc:  ",
                    size = 15.sp,
                    fillMaxWidth = false
                )
            }
        )
        Column { value() }
    }
}


@Preview
@Composable
fun GuestDetailsDialogPreview() {
    GuestDetailsDialog(
        isVisible = true,
        context = LocalContext.current,
        idClientEvent = "",
        allTags = listOf(),
        guest = Guest(
            id = "01",
            created_at = Timestamp.now(),
            email = "test1@gmail.com",
            id_client_event = "123",
            name = "Juan Pablo Suárez López",
            num_table = null,
            phone = "3312345432",
            status = "PENDING",
            tags = listOf(
                Tag("MyTag1"),
                Tag("MyTag2ButLongTextHere"),
                Tag("My Tag 3"),
                Tag("Random text.. -1>"),
                Tag("Another tag"),
                Tag("MyTag123")
            ),
            viewed = null
            /*viewed = Viewed(
                date = Timestamp.now(),
                viewed = false
            )*/
        ),
        viewModel = InvitationsViewModel(
            GuestUseCase(IGuestRepositoryTest()),
            EventUseCase(IEventRepositoryTest())
        ),
        onDismiss = { },
        onEdit = { },
        onCancelAccess = { },
        onDelete = { },
        onGiveAccess = { },
        onSendInvitation = { }
    )
}
