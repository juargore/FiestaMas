package com.universal.fiestamas.presentation.ui.dialogs

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.utils.extensions.splitStringDate
import java.util.Calendar

@Composable
fun DatePickerPopup(
    state: DatePickerDialogState,
    isEndDate: Boolean = false,
    startDate: String? = null
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var selectedDateText by remember { mutableStateOf("") }

    var year: Int = calendar[Calendar.YEAR]
    var month = calendar[Calendar.MONTH]
    var dayOfMonth = calendar[Calendar.DAY_OF_MONTH]

    if (isEndDate && !startDate.isNullOrEmpty()) {
        splitStringDate(startDate)?.let {
            val (nYear, nMonth, nDayOfMonth) = it
            year = nYear
            month = nMonth
            dayOfMonth = nDayOfMonth
        }
    }

    val datePicker = DatePickerDialog(
        ContextThemeWrapper(context, R.style.CustomDatePickerDialog),
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            val monthStr = formatNumberToString(selectedMonth + 1)
            val dayStr = formatNumberToString(selectedDayOfMonth)

            selectedDateText = "$selectedYear-$monthStr-$dayStr"
            state.onSelectDate(selectedDateText)
        }, year, month, dayOfMonth
    )
    datePicker.datePicker.minDate = calendar.timeInMillis

    if (isEndDate && !startDate.isNullOrEmpty()) {
        splitStringDate(startDate)?.let {
            val (nYear, nMonth, nDayOfMonth) = it
            val minCalendar = Calendar.getInstance().apply {
                set(nYear, nMonth, nDayOfMonth)
            }
            datePicker.datePicker.minDate = minCalendar.timeInMillis
        }
    }

    datePicker.setOnDismissListener { state.onDismiss() }
    datePicker.show()
}

fun formatNumberToString(number: Int): String {
    return when (number) {
        in 0..9 -> "0$number"
        in 10..99 -> number.toString()
        else -> throw IllegalArgumentException("Invalid number. Only numbers from 0 to 99 are supported.")
    }
}

data class DatePickerDialogState(
    val onDismiss: () -> Unit,
    val onSelectDate: (real: String) -> Unit
)
