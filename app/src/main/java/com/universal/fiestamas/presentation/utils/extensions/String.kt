package com.universal.fiestamas.presentation.utils.extensions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.domain.models.ServiceStatus
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.GuestStatus
import com.universal.fiestamas.presentation.theme.NormalGray
import com.universal.fiestamas.presentation.theme.StatusAll
import com.universal.fiestamas.presentation.theme.StatusCanceled
import com.universal.fiestamas.presentation.theme.StatusHired
import com.universal.fiestamas.presentation.theme.StatusPending
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.toColor(): Color {
    return Color(android.graphics.Color.parseColor(this))
}

fun String.toLocalDateTime(): LocalDateTime {
    val localDate = LocalDate.parse(this, DateTimeFormatter.ISO_DATE)
    return localDate.atStartOfDay()
}

fun String.toLocalDate(): LocalDate {
    return LocalDate.parse(this, DateTimeFormatter.ISO_DATE)
}

fun String?.isProvider(): Boolean {
    return this == "provider"
}

fun String?.isClient(): Boolean {
    return this == "client"
}

fun String.toUnit(): String {
    return when (this) {
        "Persona" -> "person"
        "Pieza" -> "pz"
        "Kg" -> "kg"
        "Evento" -> "event"
        else -> ""
    }
}

fun String.toUnitReadable(): String {
    return when (this) {
        "person" -> "persona"
        "pz" -> "pieza"
        "kg" -> "kg"
        "event" -> "Evento"
        else -> ""
    }
}

fun String.getStatus(): ServiceStatus {
    return when (this) {
        "CONTACTED" -> ServiceStatus.Hired
        "PENDING" -> ServiceStatus.Pending
        else -> ServiceStatus.Canceled
    }
}

fun String.getStatusName(): String {
    return when (this) {
        "CONTACTED" -> "Contratado"
        "PENDING" -> "Pendiente"
        else -> "Cancelado"
    }
}

fun String?.hasBeenAlreadySentToServer(): Boolean {
    return this.orEmpty().contains("fiestaki-") ||
            this.orEmpty().contains("fiestamas-")
}

fun ServiceStatus.getStatusColor() : Color {
    return when(this) {
        ServiceStatus.All -> StatusAll
        ServiceStatus.Hired -> StatusHired
        ServiceStatus.Pending -> StatusPending
        ServiceStatus.Canceled -> StatusCanceled
        ServiceStatus.DateAsc -> NormalGray
        ServiceStatus.DateDsc -> Color.Gray
    }
}

fun String?.getGuestStatus(): GuestStatus =
    when(this?.toUpperCase(Locale.current)) {
        "PENDING" -> GuestStatus.Pending
        "ACCEPTED" -> GuestStatus.Accepted
        "CANCELED" -> GuestStatus.Canceled
        "SENT" -> GuestStatus.Sent
        "DECLINED" -> GuestStatus.Declined
        "CHECKED_IN" -> GuestStatus.CheckedIn
        else -> GuestStatus.Unknown
    }

fun Int.toTextUnit(): TextUnit {
    return this.sp
}