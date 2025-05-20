package com.universal.fiestamas.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

val LighterBlue = Color(0xFFEAF7F9)
val LightBlue = Color(0xFFC3E5EC)
val LighterGray = Color(0xFFF0F3F4)
val LightGray = Color(0xFFF1F2F2)
val NormalGray = Color(0xFFB1B1B1)
val PinkFiestamas = Color(0xFFC8008E)
val LowPinkFiestaki = Color(0xFFEDE9F4)
val GreenFiestaki = Color(0xFFB3DBAC)
val PurpleFiestaki = Color(0xFF8852DB)
val OrangeFiestaki = Color(0xFFDC7633)

val StatusAll = Color(0xFF004FD5)
val StatusHired = Color(0xFF009E5D)
val StatusPending = Color(0xFFFFE800)
val StatusCanceled = Color(0xFFFF0000)

val GuestStatusPendingTextColor = Color(0xFF854D0E)
val GuestStatusPendingBackgroundColor = Color(0xFFFEF08A)
val GuestStatusAcceptedTextColor = Color(0xFF166534)
val GuestStatusAcceptedBackgroundColor = Color(0xFFBBF7D0)
val GuestStatusCanceledTextColor = Color(0xFF991B1B)
val GuestStatusCanceledBackgroundColor = Color(0xFFFECACA)
val GuestStatusSentTextColor = Color(0xFF1E40AF)
val GuestStatusSentBackgroundColor = Color(0xFFBFDBFE)
val GuestStatusDeclinedTextColor = Color(0xFF991B1B)
val GuestStatusDeclinedBackgroundColor = Color(0xFFFECACA)
val GuestStatusCheckedInTextColor = Color(0xFFFFFFFF)
val GuestStatusCheckedInBackgroundColor = PinkFiestamas

val GradientBackgroundFiestamas = Brush.verticalGradient(
    colors = listOf(LightBlue, LightGray),
    startY = 0f,
    endY = 700f
)