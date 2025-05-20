package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.MyPartyEvent
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.cards.CardMyPartyHorizontal
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.showToast

@Composable
fun ClientEventsDialog(
    horizontalList: List<MyPartyEvent?>?,
    isVisible: Boolean,
    isCancelable: Boolean = true,
    onEventSelected: (MyPartyEvent) -> Unit,
    onNewPartyClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible && horizontalList != null) {
        val context = LocalContext.current

        BaseDialog(
            addCloseIcon = false,
            isCancelable = isCancelable,
            onDismiss = onDismiss,
            sidePadding = 2.dp.autoSize(),
            content = {
                TextSemiBold(
                    text = "Selecciona un Evento",
                    size = 18.sp.autoSize()
                )
                VerticalSpacer(height = 10.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp.autoSize())
                        .sidePadding(8.dp.autoSize())
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp.autoSize()),
                        contentPadding = PaddingValues(horizontal = 6.dp.autoSize(), vertical = 6.dp.autoSize()),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(0.7f)
                    ) {
                        items(horizontalList) { event ->
                            CardMyPartyHorizontal(
                                item = event,
                                showPrice = false
                            ) { item ->
                                val pendingDays = item.pendingDays ?: 0
                                val isValidEvent = pendingDays > -1
                                if (isValidEvent) {
                                    onEventSelected(item)
                                } else {
                                    showToast(context, "Este evento ya ocurrió. No se pueden agregar más servicios.")
                                }
                            }
                        }
                    }
                    Image(
                        painter = painterResource(id = R.drawable.img_new_party),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(100.dp.autoSize())
                            .padding(vertical = 0.dp, horizontal = 4.dp.autoSize())
                            .clickable { onNewPartyClicked() }
                    )
                }
            }
        )
    }
}


@Preview
@Composable
fun ClientEventsDialogPreview(){
    ClientEventsDialog(
        horizontalList = null,
        isVisible = true,
        onEventSelected = {},
        onNewPartyClicked = {},
        onDismiss = {}
    )
}
