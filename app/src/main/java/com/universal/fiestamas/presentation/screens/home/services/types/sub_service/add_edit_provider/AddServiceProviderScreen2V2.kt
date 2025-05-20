package com.universal.fiestamas.presentation.screens.home.services.types.sub_service.add_edit_provider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.domain.models.ServiceType
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape24
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.HorizontalProgressView
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.UnityViewV2
import com.universal.fiestamas.presentation.ui.ValidationText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.utils.Constants.BY_EVENT
import com.universal.fiestamas.presentation.utils.Constants.BY_KG
import com.universal.fiestamas.presentation.utils.Constants.BY_PERSON
import com.universal.fiestamas.presentation.utils.Constants.BY_PIECE
import com.universal.fiestamas.presentation.utils.extensions.sidePadding

@Composable
fun AddServiceProviderScreen2V2(
    serviceCategory: ServiceCategory,
    serviceType: ServiceType?,
    onContinue: (
        serviceName: String,
        serviceDescription: String,
        serviceMin: String,
        serviceMax: String,
        servicePrice: String,
        serviceUnity: String
    ) -> Unit,
    onBackClicked: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var min by remember { mutableStateOf("") }
    var max by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var unitySelected by remember { mutableIntStateOf(0) }

    var showValidationName by remember { mutableStateOf(false) }
    var showValidationDesc by remember { mutableStateOf(false) }
    var showValidationMin by remember { mutableStateOf(false) }
    var showValidationMax by remember { mutableStateOf(false) }
    var showValidationMinMax by remember { mutableStateOf(false) }
    var showValidationPrice by remember { mutableStateOf(false) }

    LaunchedEffect(name, description, min, max, price) {
        showValidationName = name.isBlank()
        showValidationDesc = description.isBlank()
        showValidationMin = min.isBlank()
        showValidationMax = max.isBlank()
        showValidationPrice = price.isBlank()
        val nMin = if (min.isBlank()) 0 else min.toInt()
        val nMax = if (max.isBlank() && min.isBlank()) {
            0
        } else if (min.isNotEmpty() && max.isBlank()) {
            nMin
        } else {
            max.toInt()
        }
        showValidationMinMax = nMin > nMax
    }

    GradientBackground(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                VerticalSpacer(height = 10.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    HorizontalProgressView(
                        totalBars = 8,
                        totalSelected = 5,
                        selectedColor = PinkFiestamas,
                        unselectedColor = Color.LightGray
                    )
                }

                TextBold(
                    modifier = Modifier.padding(vertical = 15.dp),
                    text = "${serviceCategory.name} - ${serviceType?.name}",
                    size = 20.sp,
                    verticalSpace = 18.sp,
                    color = PinkFiestamas
                )

                LazyColumn(
                    modifier = Modifier.sidePadding(25.dp)
                ) {
                    item {
                        RoundedEdittext(
                            isForV2 = true,
                            value = name,
                            placeholder = "Nombre",
                            textColorFocusedV2 = Color.Black,
                            textColorUnfocusedV2 = Color.DarkGray,
                            hintTextColorV2 = PinkFiestamas,
                            hintBackgroundColorV2 = Color.White,
                            focusedBorderColorV2 = PinkFiestamas,
                            unfocusedBorderColorV2 = Color.Gray,
                            onValueChange = { name = it }
                        )
                        ValidationText(
                            show = showValidationName,
                            color = Color.Red,
                            text = "Debes ingresar un nombre",
                        )
                    }
                    item {
                        VerticalSpacer(height = 8.dp)
                    }
                    item {
                        RoundedEdittext(
                            isForV2 = true,
                            value = description,
                            placeholder = "Descripción",
                            minLines = 5,
                            maxLines = 5,
                            textColorFocusedV2 = Color.Black,
                            textColorUnfocusedV2 = Color.DarkGray,
                            hintTextColorV2 = PinkFiestamas,
                            hintBackgroundColorV2 = Color.White,
                            focusedBorderColorV2 = PinkFiestamas,
                            unfocusedBorderColorV2 = Color.Gray,
                            onValueChange = { description = it }
                        )
                        ValidationText(
                            show = showValidationDesc,
                            color = Color.Red,
                            text = "Debes ingresar una descripción",
                        )
                    }
                    item {
                        VerticalSpacer(height = 25.dp)
                    }
                    item {
                        TextBold(
                            modifier = Modifier.padding(bottom = 5.dp),
                            text = "Capacidad",
                            size = 20.sp,
                            color = PinkFiestamas
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RoundedEdittext(
                                modifier = Modifier.weight(0.5f),
                                isForV2 = true,
                                value = min,
                                placeholder = "Mínimo",
                                textColorFocusedV2 = Color.Black,
                                textColorUnfocusedV2 = Color.DarkGray,
                                hintTextColorV2 = PinkFiestamas,
                                hintBackgroundColorV2 = Color.White,
                                focusedBorderColorV2 = PinkFiestamas,
                                unfocusedBorderColorV2 = Color.Gray,
                                keyboardType = KeyboardType.Number,
                                onValueChange = { min = it }
                            )
                            HorizontalSpacer(width = 10.dp)
                            RoundedEdittext(
                                modifier = Modifier.weight(0.5f),
                                isForV2 = true,
                                value = max,
                                placeholder = "Máximo",
                                textColorFocusedV2 = Color.Black,
                                textColorUnfocusedV2 = Color.DarkGray,
                                hintTextColorV2 = PinkFiestamas,
                                hintBackgroundColorV2 = Color.White,
                                focusedBorderColorV2 = PinkFiestamas,
                                unfocusedBorderColorV2 = Color.Gray,
                                keyboardType = KeyboardType.Number,
                                onValueChange = { max = it }
                            )
                        }
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.weight(0.5f)) {
                                ValidationText(
                                    show = showValidationMin,
                                    color = Color.Red,
                                    fillMaxWidth = false,
                                    text = "Debes ingresar un mínimo de asistentes",
                                )
                            }
                            Row(modifier = Modifier.weight(0.5f)) {
                                if (showValidationMinMax) {
                                    ValidationText(
                                        show = true,
                                        color = Color.Red,
                                        fillMaxWidth = false,
                                        text = "Debes ingresar un máximo de asistentes mayor al mínimo",
                                    )
                                } else {
                                    ValidationText(
                                        show = showValidationMax,
                                        color = Color.Red,
                                        fillMaxWidth = false,
                                        text = "Debes ingresar un máximo de asistentes",
                                    )
                                }
                            }
                        }
                    }
                    item {
                        VerticalSpacer(height = 8.dp)
                    }
                    item {
                        RoundedEdittext(
                            modifier = Modifier.weight(0.5f),
                            isForV2 = true,
                            value = price,
                            placeholder = "Precio desde",
                            textColorFocusedV2 = Color.Black,
                            textColorUnfocusedV2 = Color.DarkGray,
                            hintTextColorV2 = PinkFiestamas,
                            hintBackgroundColorV2 = Color.White,
                            focusedBorderColorV2 = PinkFiestamas,
                            unfocusedBorderColorV2 = Color.Gray,
                            keyboardType = KeyboardType.Number,
                            onValueChange = { price = it }
                        )
                        ValidationText(
                            show = showValidationPrice,
                            color = Color.Red,
                            text = "Debes ingresar un precio",
                        )
                    }
                    item {
                        VerticalSpacer(height = 30.dp)
                    }
                    item {
                        TextBold(
                            modifier = Modifier.padding(bottom = 10.dp),
                            text = "Selecciona uno",
                            size = 20.sp,
                            color = PinkFiestamas
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val hSpace = 10.dp
                            UnityViewV2(
                                modifier = Modifier.weight(0.25f),
                                icon = R.drawable.ic_unity_person,
                                text = BY_PERSON,
                                isSelected = unitySelected == 0,
                                onSelected = { unitySelected = 0 }
                            )
                            HorizontalSpacer(width = hSpace)
                            UnityViewV2(
                                modifier = Modifier.weight(0.25f),
                                icon = R.drawable.ic_unity_piece,
                                text = BY_PIECE,
                                isSelected = unitySelected == 1,
                                onSelected = { unitySelected = 1 }
                            )
                            HorizontalSpacer(width = hSpace)
                            UnityViewV2(
                                modifier = Modifier.weight(0.25f),
                                icon = R.drawable.ic_unity_kilogram,
                                text = BY_KG,
                                isSelected = unitySelected == 2,
                                onSelected = { unitySelected = 2 }
                            )
                            HorizontalSpacer(width = hSpace)
                            UnityViewV2(
                                modifier = Modifier.weight(0.25f),
                                icon = R.drawable.ic_unity_event,
                                text = BY_EVENT,
                                isSelected = unitySelected == 3,
                                onSelected = { unitySelected = 3 }
                            )
                        }
                    }
                    item {
                        VerticalSpacer(height = 30.dp)
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            ButtonPinkRoundedCornersV2(
                                verticalPadding = 14.dp,
                                horizontalPadding = 45.dp,
                                shape = allRoundedCornerShape24,
                                content = {
                                    TextBold(
                                        text = "Continuar".uppercase(),
                                        size = 17.sp,
                                        color = Color.White,
                                        fillMaxWidth = false
                                    )
                                },
                                onClick = {
                                    if (!showValidationName && !showValidationDesc
                                        && !showValidationMin && !showValidationMax
                                        && !showValidationMinMax && !showValidationPrice
                                        ) {
                                            val unity = when (unitySelected) {
                                                0 -> "Por Persona"
                                                1 -> "Por Pieza"
                                                2 -> "Por Kilogramo"
                                                else -> "Por Evento"
                                            }
                                            onContinue(name, description,
                                                min, max, price, unity
                                            )
                                        }
                                }
                            )
                        }
                    }
                    item {
                        VerticalSpacer(height = 20.dp)
                    }
                }
            }
        },
        titleScreen = "Información General",
        titleScreenColor = Color.White,
        isPinkBackground = true,
        showBackButton = true,
        backButtonColor = Color.White,
        showLogoFiestamas = false,
        showUserName = false,
        addBottomPadding = false,
        onBackButtonClicked = { onBackClicked() }
    )
}
