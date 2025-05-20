package com.universal.fiestamas.presentation.ui.calendar.models

import java.io.Serializable
import java.time.LocalDate

data class CalendarDay(val date: LocalDate, val position: DayPosition) : Serializable
