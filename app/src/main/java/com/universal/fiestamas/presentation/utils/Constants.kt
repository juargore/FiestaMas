package com.universal.fiestamas.presentation.utils

import androidx.compose.foundation.lazy.grid.GridCells

object Constants {

    const val TWO_COLUMNS = 2
    const val THREE_COLUMNS = 3

    const val CLIENT = "client"
    const val PROVIDER = "provider"

    const val THREE_SECONDS = 3000L
    const val TWO_SECONDS = 2000L
    const val ONE_SECOND = 1000L
    const val HALF_SECOND = 500L
    const val BUTTON_ANIMATION_DURATION = 250L

    val GRID_THREE_CELLS = GridCells.Fixed(THREE_COLUMNS)
    val GRID_TWO_CELLS = GridCells.Fixed(TWO_COLUMNS)

    const val ACCEPTED = "ACCEPTED"
    const val REJECTED = "REJECTED"
    const val EXPRESS = "EXPRESS"
    const val NEGOTIATING = "NEGOTIATING"

    const val NOTIFICATION_SCREEN = "screen"
    const val NOTIFICATION_SERVICE_EVENT_ID = "serviceEventId"
    const val NOTIFICATION_CHAT = "chat"

    const val DELAY_TO_REFRESH = 1500L

    const val BY_PERSON = "Por Persona"
    const val BY_PIECE = "Por Pieza"
    const val BY_KG = "Por Kilogramo"
    const val BY_EVENT = "Por Evento"
}
