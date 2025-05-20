package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.MyPartyEvent
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.calendar.clickable
import com.universal.fiestamas.presentation.ui.cards.CardMyPartyHorizontal
import com.universal.fiestamas.presentation.utils.extensions.autoSize

@Composable
fun ConfirmationServiceToEventDialog(
    event: MyPartyEvent?,
    isVisible: Boolean,
    isCancelable: Boolean = true,
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible && event != null) {
        BaseDialog(
            addCloseIcon = false,
            sidePadding = 4.dp.autoSize(),
            isCancelable = isCancelable,
            onDismiss = onDismiss,
            content = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp.autoSize())
                ) {
                    CardMyPartyHorizontal(event) { }
                    HorizontalSpacer(width = 10.dp)
                    Column {
                        TextSemiBold(
                            text = "¿Deseas agregar el Servicio a este EVENTO?",
                            verticalSpace = 18.sp.autoSize(),
                            color = PinkFiestamas,
                            size = 16.sp.autoSize()
                        )
                        VerticalSpacer(height = 10.dp)
                        Row (
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(PinkFiestamas, shape = allRoundedCornerShape12)
                                    .clip(allRoundedCornerShape12)
                                    .clickable { onDismiss() }
                            ) {
                                TextMedium(
                                    modifier = Modifier.padding(
                                        vertical = 6.dp.autoSize(),
                                        horizontal = 15.dp.autoSize()
                                    ),
                                    text = stringResource(id = R.string.gral_cancel),
                                    color = Color.White,
                                    fillMaxWidth = false,
                                    size = 14.sp.autoSize()
                                )
                            }
                            HorizontalSpacer(width = 5.dp)
                            Box(
                                modifier = Modifier
                                    .background(PinkFiestamas, shape = allRoundedCornerShape12)
                                    .clip(allRoundedCornerShape12)
                                    .clickable { onAccept() }
                            ) {
                                TextMedium(
                                    modifier = Modifier.padding(
                                        vertical = 6.dp.autoSize(),
                                        horizontal = 15.dp.autoSize()
                                    ),
                                    text = stringResource(id = R.string.gral_accept),
                                    color = Color.White,
                                    fillMaxWidth = false,
                                    size = 14.sp.autoSize()
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun ConfirmationServiceToEventDialogPreview(){
    ConfirmationServiceToEventDialog(
        event = null,
        isVisible = true,
        onAccept = {},
        onDismiss = {}
    )
}
