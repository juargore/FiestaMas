package com.universal.fiestamas.presentation.ui.calendar.heatmapcalendar

import com.universal.fiestamas.presentation.ui.calendar.models.CalendarDay
import java.io.Serializable

data class HeatMapWeek(val days: List<CalendarDay>) : Serializable
