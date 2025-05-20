package com.universal.fiestamas.presentation.ui.dialogs

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Event
import com.universal.fiestamas.domain.models.FirstQuestionsClient
import com.universal.fiestamas.domain.models.FirstQuestionsClientStored
import com.universal.fiestamas.domain.models.Location
import com.universal.fiestamas.presentation.screens.location.AddressAutoCompleteViewModel
import com.universal.fiestamas.presentation.screens.home.services.ServicesCategoriesViewModel
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCorners
import com.universal.fiestamas.presentation.ui.IconSimpleClose
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.ValidationText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.ViewDropDownMenu
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.extensions.stringToISO8601
import com.universal.fiestamas.presentation.utils.showToast
import java.util.Calendar
import java.util.Locale

@Composable
fun FirstQuestionsDialog(
    vma: AddressAutoCompleteViewModel = hiltViewModel(),
    vms: ServicesCategoriesViewModel = hiltViewModel(),
    isCancelable: Boolean = true,
    showEventsDropDown: Boolean? = null,
    serviceCategoryId: String? = null,
    savedQuestions: FirstQuestionsClientStored?,
    onContinueClicked: (FirstQuestionsClient, Event?) -> Unit,
    onAddressClicked: (FirstQuestionsClientStored) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val events by vms.eventsByService.collectAsState()

    var festejadosNames by rememberSaveable { mutableStateOf(savedQuestions?.festejadosNames.orEmpty()) }
    var date by rememberSaveable { mutableStateOf(savedQuestions?.date.orEmpty()) }
    var time by rememberSaveable { mutableStateOf(savedQuestions?.time.orEmpty()) }
    var numberOfGuests by rememberSaveable { mutableStateOf(savedQuestions?.numberOfGuests.orEmpty()) }
    var city by rememberSaveable { mutableStateOf(savedQuestions?.city.orEmpty()) }
    val location: Location? by rememberSaveable { mutableStateOf(savedQuestions?.location) }
    var selectedEvent: Event? by rememberSaveable { mutableStateOf(savedQuestions?.event) }
    var datePickerDialogState by remember { mutableStateOf<DatePickerDialogState?>(null) }

    /*
    var showValidationFestejadosNames by remember { mutableStateOf(festejadosNames.isBlank()) }
    var showValidationTextDate by remember { mutableStateOf(date.isBlank()) }
    var showValidationTextTime by remember { mutableStateOf(time.isBlank()) }
    var showValidationNumberOfGuests by remember { mutableStateOf(numberOfGuests.isBlank()) }
    var showValidationTextCity by remember { mutableStateOf(city.isBlank()) }
    */

    var showValidationFestejadosNames by remember { mutableStateOf(false) }
    var showValidationTextDate by remember { mutableStateOf(false) }
    var showValidationTextTime by remember { mutableStateOf(false) }
    var showValidationNumberOfGuests by remember { mutableStateOf(false) }
    var showValidationTextCity by remember { mutableStateOf(false) }

    if (showEventsDropDown == true && serviceCategoryId != null) {
        vms.getEvents()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = isCancelable,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = allRoundedCornerShape10)
                    .padding(15.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sidePadding(5.dp)
                ) {
                    IconSimpleClose(modifier = Modifier.align(Alignment.CenterEnd)) {
                        vma.resetAddress()
                        onDismiss()
                    }
                }

                if (showEventsDropDown == true) {
                    events?.let { events ->
                        var initialValue = 0
                        val eventsStrings: MutableList<String> = events.map { it?.name!! }.toMutableList()

                        eventsStrings.add(0, "Seleccione una opción")

                        if (!savedQuestions?.event?.name.isNullOrEmpty()) {
                            val initialEvent = events.firstOrNull { event -> event?.name == savedQuestions?.event?.name }
                            initialEvent?.let { initialValue = events.indexOf(it) + 1 }
                        }

                        VerticalSpacer(height = 10.dp)
                        ViewDropDownMenu(
                            modifier = Modifier.fillMaxWidth(),
                            placeholder =  stringResource(R.string.service_event),
                            startedIndexSelected = initialValue,
                            options = eventsStrings,
                            onItemSelected = {
                                selectedEvent = events.firstOrNull { event -> event?.name == it }
                            }
                        )
                        VerticalSpacer(height = 2.dp)
                    }
                }

                VerticalSpacer(height = 10.dp)
                RoundedEdittext(
                    placeholder = stringResource(R.string.service_festejados_names),
                    value = festejadosNames
                ) {
                    festejadosNames = it
                    showValidationFestejadosNames = it.isBlank()
                }

                ValidationText(show = showValidationFestejadosNames, text = context.getString(R.string.gral_error_empty, "El nombre(s) del festejado(s)"))

                VerticalSpacer(8.dp)

                RoundedEdittext(
                    placeholder = stringResource(R.string.service_date),
                    isEnabled = false,
                    value = date,
                    onClicked = {
                        showValidationTextDate = false
                        datePickerDialogState = DatePickerDialogState(
                            onDismiss = { datePickerDialogState = null },
                            onSelectDate = { selectedDate ->
                                date = selectedDate
                                datePickerDialogState = null
                            }
                        )
                    },
                ) {
                    date = it
                    showValidationTextDate = it.isBlank()
                }

                ValidationText(show = showValidationTextDate, text = context.getString(R.string.gral_error_empty_female, "La fecha del evento"))

                VerticalSpacer(8.dp)

                RoundedEdittext(
                    placeholder = stringResource(R.string.service_time),
                    isEnabled = false,
                    value = time,
                    onClicked = {
                        showValidationTextTime = false
                        val mCalendar = Calendar.getInstance()
                        val mHour = mCalendar[Calendar.HOUR_OF_DAY]
                        val mMinute = mCalendar[Calendar.MINUTE]
                        val defaultHour = if (mHour < 12) mHour + 12 else mHour

                        TimePickerDialog(
                            context,
                            R.style.TimePickerTheme,
                            {_, hour : Int, minute: Int ->
                                time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                            }, defaultHour, mMinute, false
                        ).show()
                    },
                ) { }

                ValidationText(show = showValidationTextTime, text = context.getString(R.string.gral_error_empty_female, "La hora del evento"))

                VerticalSpacer(8.dp)

                RoundedEdittext(
                    placeholder = stringResource(R.string.service_number_of_guests),
                    keyboardType = KeyboardType.Number,
                    value = numberOfGuests
                ) {
                    numberOfGuests = it
                    showValidationNumberOfGuests = it.isBlank()
                }

                ValidationText(show = showValidationNumberOfGuests, text = context.getString(R.string.gral_error_empty_female, "La cantidad de invitados"))

                VerticalSpacer(8.dp)

                Box {
                    RoundedEdittext(
                        modifier = Modifier.background(Color.White),
                        placeholder = stringResource(R.string.service_city),
                        isEnabled = false,
                        value = city,
                        onClicked = {
                            showValidationTextCity = false
                            city = ""
                            onAddressClicked(
                                FirstQuestionsClientStored(
                                    event = selectedEvent,
                                    festejadosNames = festejadosNames,
                                    date = date,
                                    time = time,
                                    numberOfGuests = numberOfGuests,
                                    city = city,
                                    location = location
                                )
                            )
                        },
                        onValueChange = { city = it }
                    )
                }

                ValidationText(show = showValidationTextCity, text = context.getString(R.string.gral_error_city))

                VerticalSpacer(30.dp)

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ButtonPinkRoundedCorners(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(id = R.string.gral_continue)
                    ) {
                        if (festejadosNames.isBlank()) {
                            showToast(context, context.getString(R.string.gral_error_empty, "El nombre(s) del festejado(s)"))
                            return@ButtonPinkRoundedCorners
                        }
                        if (date.isBlank()) {
                            showToast(context, context.getString(R.string.gral_error_empty_female, "La fecha del evento"))
                            return@ButtonPinkRoundedCorners
                        }
                        if (time.isBlank()) {
                            showToast(context, context.getString(R.string.gral_error_empty_female, "La hora del evento"))
                            return@ButtonPinkRoundedCorners
                        }
                        if (numberOfGuests.isBlank()) {
                            showToast(context, context.getString(R.string.gral_error_empty_female, "La cantidad de invitados"))
                            return@ButtonPinkRoundedCorners
                        }
                        if (city.isBlank()) {
                            showToast(context, context.getString(R.string.gral_error_city))
                            return@ButtonPinkRoundedCorners
                        }
                        if (showEventsDropDown == true  && selectedEvent == null) {
                            showToast(context, "Seleccione un Evento de la lista")
                            return@ButtonPinkRoundedCorners
                        }

                        val hour = time.substringBefore(":")
                        val minutes = time.substringAfter(":")
                        val hourFormatted = formatNumberToString(hour.toInt())
                        val minutesFormatted = formatNumberToString(minutes.toInt())

                        val nEvent = if (showEventsDropDown == true && selectedEvent == null) {
                            events?.firstOrNull()
                        } else {
                            selectedEvent
                        }

                        onContinueClicked(
                            FirstQuestionsClient(
                                festejadosNames = festejadosNames,
                                date = stringToISO8601("$date $hourFormatted:$minutesFormatted"),
                                numberOfGuests = numberOfGuests,
                                city = city,
                                location = location,
                                event = nEvent
                            ),
                            if (showEventsDropDown == true) selectedEvent else null
                        )
                    }
                }
                VerticalSpacer(20.dp)
            }
        }
        datePickerDialogState?.let { state ->
            DatePickerPopup(state = state)
        }
    }
}
