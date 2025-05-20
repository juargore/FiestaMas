package com.universal.fiestamas.presentation.ui.bottom_sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.universal.fiestamas.domain.models.BottomNotificationStatus
import com.universal.fiestamas.domain.models.NotificationStatus
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.TopDecorationBottomSheet
import com.universal.fiestamas.presentation.ui.VerticalSpacer

@Composable
fun BottomSheetNotificationOrderBy(onItemSelected: (BottomNotificationStatus) -> Unit) {
    val mList = listOf(
        BottomNotificationStatus(NotificationStatus.All,  "Todos"),
        BottomNotificationStatus(NotificationStatus.Read, "Leídos"),
        BottomNotificationStatus(NotificationStatus.Unread, "No leídos")
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalSpacer(height = 4.dp)
        TopDecorationBottomSheet()
        VerticalSpacer(height = 15.dp)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mList) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemSelected(it) }
                ) {
                    TextSemiBold(
                        modifier = Modifier.padding(horizontal = 15.dp),
                        text = it.name,
                        fillMaxWidth = false
                    )
                }
            }
        }
        VerticalSpacer(height = 24.dp)
    }
}


@Preview
@Composable
fun BottomSheetNotificationOrderByPreview() {
    BottomSheetNotificationOrderBy(
        onItemSelected = {

        }
    )
}
