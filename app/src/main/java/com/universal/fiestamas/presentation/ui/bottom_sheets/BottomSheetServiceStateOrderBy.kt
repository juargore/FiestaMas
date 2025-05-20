package com.universal.fiestamas.presentation.ui.bottom_sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.domain.models.ServiceStatus
import com.universal.fiestamas.presentation.ui.CircleStatus
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.TopDecorationBottomSheet
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.autoSize

@Composable
fun BottomSheetServiceStateOrderBy(onItemSelected: (BottomServiceStatus) -> Unit) {
    val mList = listOf(
        BottomServiceStatus(ServiceStatus.All,  "Todos"),
        BottomServiceStatus(ServiceStatus.Hired, "Contratados"),
        BottomServiceStatus(ServiceStatus.Pending, "Pendientes"),
        BottomServiceStatus(ServiceStatus.Canceled, "Cancelados"),
        BottomServiceStatus(ServiceStatus.DateAsc, "Fecha Ascendente"),
        BottomServiceStatus(ServiceStatus.DateDsc, "Fecha Descendente")
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalSpacer(height = 4.dp.autoSize())
        TopDecorationBottomSheet()
        VerticalSpacer(height = 20.dp.autoSize())

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp.autoSize())
        ) {
            items(mList) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemSelected(it) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.width(70.dp.autoSize())) {
                        CircleStatus(
                            modifier = Modifier.align(Alignment.Center),
                            status = it.status
                        )
                    }
                    TextSemiBold(
                        text = it.name,
                        size = 16.sp.autoSize(),
                        fillMaxWidth = false
                    )
                }
                VerticalSpacer(height = 10.dp.autoSize())
            }
        }
        VerticalSpacer(height = 20.dp.autoSize())
    }
}

data class BottomServiceStatus(
    val status: ServiceStatus,
    val name: String
)