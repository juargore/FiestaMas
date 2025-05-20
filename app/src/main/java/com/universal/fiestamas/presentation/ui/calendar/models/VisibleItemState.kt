package com.universal.fiestamas.presentation.ui.calendar.models

import java.io.Serializable

internal class VisibleItemState(
    val firstVisibleItemIndex: Int = 0,
    val firstVisibleItemScrollOffset: Int = 0,
) : Serializable
