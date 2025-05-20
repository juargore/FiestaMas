package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.domain.models.Guest
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.GuestStatus
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
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape14
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.getGuestStatus

@Composable
fun RVSPTrackingDialog(
    isVisible: Boolean,
    guestList: List<Guest>?,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        BaseDialog(
            title = "Resumen de tus Invitados",
            addCloseIcon = false,
            onDismiss = onDismiss,
            content = {
                val statusCounts = mapOf(
                    GuestStatus.Pending to Pair(GuestStatusPendingBackgroundColor, GuestStatusPendingTextColor),
                    GuestStatus.Accepted to Pair(GuestStatusAcceptedBackgroundColor, GuestStatusAcceptedTextColor),
                    GuestStatus.Canceled to Pair(GuestStatusCanceledBackgroundColor, GuestStatusCanceledTextColor),
                    GuestStatus.Sent to Pair(GuestStatusSentBackgroundColor, GuestStatusSentTextColor),
                    GuestStatus.Declined to Pair(GuestStatusDeclinedBackgroundColor, GuestStatusDeclinedTextColor),
                    GuestStatus.CheckedIn to Pair(GuestStatusCheckedInBackgroundColor, GuestStatusCheckedInTextColor)
                )

                VerticalSpacer(height = 12.dp)
                ItemStatistics(
                    backgroundColor = Color.White,
                    textColor = Color.Black,
                    text = "Total de Invitados: ${guestList?.size ?: 0}"
                )
                VerticalSpacer(height = 10.dp)

                statusCounts.forEach { (status, colors) ->
                    val count = guestList?.count { it.status.getGuestStatus() == status } ?: 0
                    if (count > 0) {
                        ItemStatistics(
                            backgroundColor = colors.first,
                            textColor = colors.second,
                            text = when (status) {
                                GuestStatus.Pending -> "Invitaciones pendientes: $count"
                                GuestStatus.Accepted -> "Invitados confirmados: $count"
                                GuestStatus.Canceled -> "Invitaciones canceladas: $count"
                                GuestStatus.Sent -> "Invitaciones enviadas: $count"
                                GuestStatus.Declined -> "Invitaciones rechazadas: $count"
                                GuestStatus.CheckedIn -> "Invitados que asistieron: $count"
                                else -> ""
                            }
                        )
                        VerticalSpacer(height = 10.dp)
                    }
                }

            }
        )
    }
}

@Composable
fun ItemStatistics(
    backgroundColor: Color,
    textColor: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .shadow(elevation = 4.dp, shape = allRoundedCornerShape14)
            .background(backgroundColor, shape = allRoundedCornerShape14)
            .height(60.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)

    ) {
        TextSemiBold(
            text = text,
            size = 18.sp,
            maxLines = 1,
            color = textColor
        )
    }
}

@Preview
@Composable
fun RVSPTrackingDialogPreview() {
    RVSPTrackingDialog(
        isVisible = true,
        guestList = listOf(
            Guest(
                id = "",
                created_at = null,
                email = "",
                id_client_event = null,
                name = "Test1",
                num_table = 1,
                phone = "",
                status = "ACCEPTED",
                tags = null,
                viewed = null
            )
        ),
        onDismiss = {}
    )
}
