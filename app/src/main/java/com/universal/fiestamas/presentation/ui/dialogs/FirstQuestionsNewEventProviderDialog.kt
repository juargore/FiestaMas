package com.universal.fiestamas.presentation.ui.dialogs

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.FirstQuestionsProvider
import com.universal.fiestamas.domain.models.FirstQuestionsProviderStored
import com.universal.fiestamas.domain.models.Location
import com.universal.fiestamas.presentation.screens.location.AddressAutoCompleteViewModel
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCorners
import com.universal.fiestamas.presentation.ui.IconSimpleClose
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.RoundedPhoneEdittext
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.ValidationText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.cleanPhoneNumberNewFormat
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.extensions.stringToISO8601
import com.universal.fiestamas.presentation.utils.isValidEmail
import com.universal.fiestamas.presentation.utils.isValidPhoneNumberNewFormat
import com.universal.fiestamas.presentation.utils.showToast
import java.util.Calendar
import java.util.Locale

@Composable
fun FirstQuestionsNewEventProviderDialog(
    vma: AddressAutoCompleteViewModel = hiltViewModel(),
    isCancelable: Boolean = true,
    savedQuestions: FirstQuestionsProviderStored?,
    onAddressClicked: (FirstQuestionsProviderStored) -> Unit,
    onContinueClicked: (FirstQuestionsProvider) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var contactName by rememberSaveable { mutableStateOf(savedQuestions?.contactName.orEmpty()) }
    var phoneNumber by rememberSaveable { mutableStateOf(savedQuestions?.phone.orEmpty()) }
    var email by rememberSaveable { mutableStateOf(savedQuestions?.email.orEmpty()) }
    var date by rememberSaveable { mutableStateOf(savedQuestions?.date.orEmpty()) }
    var time by rememberSaveable { mutableStateOf(savedQuestions?.time.orEmpty()) }
    var city by rememberSaveable { mutableStateOf(savedQuestions?.city.orEmpty()) }
    val location: Location? by rememberSaveable { mutableStateOf(savedQuestions?.location) }
    var datePickerDialogState by remember { mutableStateOf<DatePickerDialogState?>(null) }

    /*
    var showValidationContactName by remember { mutableStateOf(contactName.isBlank()) }
    var showValidationTextPhone1 by remember { mutableStateOf(phoneNumber.isBlank()) }
    var showValidationTextPhone2 by remember { mutableStateOf(false) }
    var showValidationTextEmail1 by remember { mutableStateOf(email.isBlank()) }
    var showValidationTextEmail2 by remember { mutableStateOf(false) }
    var showValidationTextDate by remember { mutableStateOf(date.isBlank()) }
    var showValidationTextTime by remember { mutableStateOf(time.isBlank()) }
    var showValidationTextCity by remember { mutableStateOf(city.isBlank()) }
    */

    var showValidationContactName by remember { mutableStateOf(false) }
    var showValidationTextPhone1 by remember { mutableStateOf(false) }
    var showValidationTextPhone2 by remember { mutableStateOf(false) }
    var showValidationTextEmail1 by remember { mutableStateOf(false) }
    var showValidationTextEmail2 by remember { mutableStateOf(false) }
    var showValidationTextDate by remember { mutableStateOf(false) }
    var showValidationTextTime by remember { mutableStateOf(false) }
    var showValidationTextCity by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { } ,
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

                VerticalSpacer(height = 10.dp)

                RoundedEdittext(
                    placeholder = stringResource(R.string.new_event_contact_name),
                    value = contactName
                ) {
                    contactName = it
                    showValidationContactName = it.isBlank()
                }

                ValidationText(show = showValidationContactName, text = context.getString(R.string.gral_error_empty, "El nombre del contacto"))

                VerticalSpacer(8.dp)

                RoundedPhoneEdittext(
                    placeholder = stringResource(R.string.new_event_phone),
                    value = phoneNumber
                ) {
                    phoneNumber = it
                    showValidationTextPhone1 = it.isBlank()
                    showValidationTextPhone2 = it.isNotEmpty() && !isValidPhoneNumberNewFormat(it)
                }

                if (showValidationTextPhone1 || showValidationTextPhone2) {
                    VerticalSpacer(height = 2.dp)
                    TextRegular(
                        text = if (showValidationTextPhone1) {
                            context.getString(R.string.gral_error_empty, "El teléfono")
                        } else {
                            context.getString(R.string.gral_error_phone_formatted, "teléfono")
                        },
                        size = 13.sp,
                        color = Color.Red
                    )
                }

                VerticalSpacer(8.dp)

                RoundedEdittext(
                    placeholder = stringResource(R.string.new_event_email),
                    keyboardType = KeyboardType.Email,
                    value = email
                ) {
                    email = it
                    showValidationTextEmail1 = it.isBlank()
                    showValidationTextEmail2 = it.isNotEmpty() && !isValidEmail(it)
                }

                if (showValidationTextEmail1 || showValidationTextEmail2) {
                    VerticalSpacer(height = 2.dp)
                    TextRegular(
                        text = if (showValidationTextEmail1) {
                            context.getString(R.string.gral_error_empty, "El correo electrónico")
                        } else {
                            context.getString(R.string.login_email_invalid)
                        },
                        size = 13.sp,
                        color = Color.Red
                    )
                }

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

                Box {
                    RoundedEdittext(
                        modifier = Modifier.background(Color.White),
                        placeholder = stringResource(R.string.new_event_location),
                        isEnabled = false,
                        value = city,
                        onClicked = {
                            showValidationTextCity = false
                            city = ""
                            onAddressClicked(
                                FirstQuestionsProviderStored(
                                    contactName = contactName,
                                    phone = phoneNumber.cleanPhoneNumberNewFormat(),
                                    email = email,
                                    date = date,
                                    time = time,
                                    city = city,
                                    location = location
                                )
                            )
                        },
                        onValueChange = {
                            city = it
                            showValidationTextCity = it.isBlank()
                        }
                    )
                }

                ValidationText(show = showValidationTextCity, text = context.getString(R.string.gral_error_city))

                VerticalSpacer(30.dp)

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ButtonPinkRoundedCorners(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(id = R.string.new_event_next)
                    ) {
                        if (contactName.isBlank()) {
                            showToast(context, context.getString(R.string.gral_error_empty, "El nombre del contacto"))
                            return@ButtonPinkRoundedCorners
                        }
                        if (phoneNumber.isBlank()) {
                            showToast(context, context.getString(R.string.gral_error_empty, "El teléfono"))
                            return@ButtonPinkRoundedCorners
                        }
                        if (!isValidPhoneNumberNewFormat(phoneNumber)) {
                            showToast(context, context.getString(R.string.gral_error_phone_formatted, "teléfono"))
                            return@ButtonPinkRoundedCorners
                        }
                        if (email.isBlank()) {
                            showToast(context, context.getString(R.string.gral_error_empty, "El email"))
                            return@ButtonPinkRoundedCorners
                        }
                        if (!isValidEmail(email.trim())) {
                            showToast(context, context.getString(R.string.login_email_invalid))
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
                        if (city.isBlank()) {
                            showToast(context, context.getString(R.string.gral_error_city))
                            return@ButtonPinkRoundedCorners
                        }

                        val hour = time.substringBefore(":")
                        val minutes = time.substringAfter(":")
                        val hourFormatted = formatNumberToString(hour.toInt())
                        val minutesFormatted = formatNumberToString(minutes.toInt())

                        onContinueClicked(
                            FirstQuestionsProvider(
                                contactName = contactName,
                                phone = phoneNumber.cleanPhoneNumberNewFormat(),
                                email = email,
                                date = stringToISO8601("$date $hourFormatted:$minutesFormatted"),
                                location = location,
                                city = city
                            )
                        )
                    }
                }
                VerticalSpacer(20.dp)
            }
        }
        datePickerDialogState?.let { state ->
            DatePickerPopup(
                state = state
            )
        }
    }
}
