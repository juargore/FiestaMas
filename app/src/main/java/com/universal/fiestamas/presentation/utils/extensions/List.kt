package com.universal.fiestamas.presentation.utils.extensions

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.graphics.Color
import com.universal.fiestamas.domain.models.MyPartyEvent
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.ServiceStatus
import com.universal.fiestamas.presentation.ui.calendar.CircleEventPerDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun List<MyPartyEvent?>.toCircleEventPerDayList(): List<CircleEventPerDay> {
    return map { myPartyEvent ->
        CircleEventPerDay(
            time = timestampToLocalDateTime(myPartyEvent!!.date),
            color = myPartyEvent.color_hex.toColor()
        )
    }
}

fun List<MyPartyService?>.toCircleServicePerDayList(): List<CircleEventPerDay> {
    return map { myPartyService ->
        CircleEventPerDay(
            time = timestampToLocalDateTime(myPartyService!!.date),
            color = myPartyService.event_data?.color_hex?.toColor() ?: Color.White
        )
    }
}

fun LazyListState.scrollToDate(context: CoroutineContext, horizontalList: List<MyPartyEvent?>, targetDate: String) {
    val targetLocalDate = targetDate.toLocalDate()
    val index = horizontalList.indexOfFirst { timestampToLocalDate(it?.date) == targetLocalDate }

    if (index != -1) {
        CoroutineScope(context).launch {
            animateScrollToItem(index)
        }
    } else {
        // If the date is not found in the list, you can handle it here (e.g., show a toast, do nothing, etc.)
    }
}

fun List<MyPartyService?>?.sortByServiceStatus(status: ServiceStatus): List<MyPartyService?>? {
    if (this == null) return null
    val sortedList = when (status) {
        ServiceStatus.Hired -> this.sortedByDescending { it?.serviceStatus == ServiceStatus.Pending }
            .sortedByDescending { it?.serviceStatus == ServiceStatus.Canceled }
        ServiceStatus.Pending -> this.sortedByDescending { it?.serviceStatus == ServiceStatus.Canceled }
            .sortedByDescending { it?.serviceStatus == ServiceStatus.Hired }
        ServiceStatus.Canceled -> this.sortedByDescending { it?.serviceStatus == ServiceStatus.Hired }
            .sortedByDescending { it?.serviceStatus == ServiceStatus.Pending }
        ServiceStatus.DateAsc -> this.sortedByDescending { it?.date }
        ServiceStatus.DateDsc -> this.sortedBy { it?.date }
        else -> this
    }
    return sortedList.reversed()
}
