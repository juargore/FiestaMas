package com.universal.fiestamas.presentation.ui.bottom_sheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.MyPartyEvent
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape14
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape20
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape8
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.RegularEditText
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.TopDecorationBottomSheet
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.ViewDropDownMenuV2
import com.universal.fiestamas.presentation.ui.calendar.clickable
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.extensions.toTextUnit

@Composable
fun BottomSheetConfigureInvitation(
    clientEvent: MyPartyEvent,
    onSave: (
        newEventName: String,
        newEventColor: Color,
        newEventSize: TextUnit,
        newDateFormat: InvitationDateFormat,
        newDateColor: Color,
        newDateSize: TextUnit,
        newLocationName: String,
        newLocationColor: Color,
        newLocationSize: TextUnit,
    ) -> Unit
) {
    val optionsSize = listOf("12", "14", "16", "18", "21")
    val optionsDate = listOf("DD/MM/YY", "DD 'de' MMMM 'de' YYYY, HH:mm")

    var nameEvent by rememberSaveable { mutableStateOf("${clientEvent.name_event_type} ${clientEvent.name}") }
    var colorNameEvent by remember { mutableStateOf(Color.Black) }
    var sizeNameEvent by rememberSaveable { mutableStateOf(optionsSize[1]) }

    var formatDate by rememberSaveable { mutableStateOf(InvitationDateFormat.DDmmYYYY) }
    var colorDate by remember { mutableStateOf(Color.Black) }
    var sizeDate by rememberSaveable { mutableStateOf(optionsSize[1]) }

    var nameLocation by rememberSaveable { mutableStateOf(clientEvent.location) }
    var colorLocation by remember { mutableStateOf(Color.Black) }
    var sizeLocation by rememberSaveable { mutableStateOf(optionsSize[1]) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalSpacer(height = 5.dp)
        TopDecorationBottomSheet()
        VerticalSpacer(height = 15.dp)

        TextSemiBold(
            fillMaxWidth = false,
            text = "Configura tu Invitación",
            size = 19.sp
        )
        TextRegular(
            modifier = Modifier.sidePadding(20.dp),
            text = "Aqui puedes configurar tu invitación y visualizar cómo la verán tus invitados",
            color = Color.Gray,
            size = 15.sp
        )
        VerticalSpacer(height = 8.dp)

        InvitationConfigurationRow(
            headerTextForTextField = "Nombre del evento",
            initialValueForTextField = nameEvent,
            onTextFieldValueChange = { nameEvent = it },
            initialColor = colorNameEvent,
            onColorValueChange = { colorNameEvent = it },
            optionsForDropDown = optionsSize,
            initialOptionSelected = optionsSize[1],
            onOptionSelected = { sizeNameEvent = it }
        )
        InvitationDateConfigurationRow(
            optionsForMainDropDown = optionsDate,
            initialOptionMainSelected = optionsDate[0],
            onOptionMainSelected = {
                val format = if (it == optionsDate[0]) {
                    InvitationDateFormat.DDmmYYYY
                } else {
                    InvitationDateFormat.LongFormat
                }
                formatDate = format
            },
            initialColor = colorDate,
            onColorValueChange = { colorDate = it },
            optionsForDropDown = optionsSize,
            initialOptionSelected = optionsSize[0],
            onOptionSelected = { sizeDate = it }
        )
        InvitationConfigurationRow(
            headerTextForTextField = "Ubicación",
            initialValueForTextField = nameLocation,
            onTextFieldValueChange = { nameLocation = it },
            initialColor = colorLocation,
            onColorValueChange = { colorLocation = it },
            optionsForDropDown = optionsSize,
            initialOptionSelected = optionsSize[1],
            onOptionSelected = { sizeLocation = it }
        )
        VerticalSpacer(height = 16.dp)
        Box(
            modifier = Modifier.padding(start = 14.dp, end = 11.dp),
            contentAlignment = Alignment.Center
        ) {
            ButtonPinkRoundedCornersV2(
                verticalPadding = 10.dp,
                shape = allRoundedCornerShape12,
                content = {
                    TextSemiBold(
                        text = "Guardar",
                        size = 18.sp,
                        color = Color.White,
                        shadowColor = Color.Gray
                    )
                },
                onClick = {
                    onSave(
                        nameEvent,
                        colorNameEvent,
                        sizeNameEvent.toInt().toTextUnit(),
                        formatDate,
                        colorDate,
                        sizeDate.toInt().toTextUnit(),
                        nameLocation,
                        colorLocation,
                        sizeLocation.toInt().toTextUnit()
                    )
                }
            )
        }
    }
}

@Composable
fun <T> InvitationConfigurationRow(
    headerTextForTextField: String,
    initialValueForTextField: String,
    onTextFieldValueChange: (String) -> Unit,
    initialColor: Color,
    onColorValueChange: (Color) -> Unit,
    optionsForDropDown: List<T>,
    initialOptionSelected: T,
    onOptionSelected: (T) -> Unit
) {
    var internalTextFieldValue by rememberSaveable { mutableStateOf(initialValueForTextField) }
    var showColorPicker by remember { mutableStateOf(false) }
    var colorPicked by remember { mutableStateOf(initialColor) }
    var optionSelected: T by remember { mutableStateOf(initialOptionSelected) }

    if (showColorPicker) {
        ClassicColorPicker(
            modifier = Modifier.height(180.dp),
            color = HsvColor.from(color = colorPicked),
            showAlphaBar = false,
            onColorChanged = { color: HsvColor ->
                colorPicked = color.toColor()
                onColorValueChange(color.toColor())
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Column(modifier = Modifier.weight(0.70f)) {
            TextRegular(
                text = headerTextForTextField,
                color = Color.Gray,
                size = 13.sp,
                fillMaxWidth = false
            )
            RegularEditText(
                modifier = Modifier.height(40.dp),
                value = internalTextFieldValue,
                placeholder = "",
                onValueChange = {
                    internalTextFieldValue = it
                    onTextFieldValueChange(it)
                }
            )
        }
        Column(
            modifier = Modifier.weight(0.12f)
        ) {
            TextRegular(
                text = "Color",
                color = Color.Gray,
                size = 13.sp,
                fillMaxWidth = false
            )
            ColorPicker(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
                initialColor = colorPicked,
                onComposerClicked = {
                    showColorPicker = !showColorPicker
                }
            )
        }
        Column(
            modifier = Modifier.weight(0.18f)
        ) {
            TextRegular(
                text = "Tamaño",
                color = Color.Gray,
                size = 13.sp,
                fillMaxWidth = false
            )
            ViewDropDownMenuV2(
                modifier = Modifier.height(40.dp),
                fontSize = 15.sp,
                icon = {
                    Image(
                        modifier = Modifier.size(25.dp),
                        painter = painterResource(id = R.drawable.ic_arrows_up_down),
                        contentDescription = null
                    )
                },
                options = optionsForDropDown,
                onValueChange = {
                    optionSelected = it
                    onOptionSelected(it)
                                },
                optionLabel = { it.toString() }
            )
        }
    }
}

@Composable
fun <T> InvitationDateConfigurationRow(
    optionsForMainDropDown: List<T>,
    initialOptionMainSelected: T,
    onOptionMainSelected: (T) -> Unit,
    initialColor: Color,
    onColorValueChange: (Color) -> Unit,
    optionsForDropDown: List<T>,
    initialOptionSelected: T,
    onOptionSelected: (T) -> Unit
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var colorPicked by remember { mutableStateOf(initialColor) }
    var optionSelected: T by remember { mutableStateOf(initialOptionSelected) }
    var optionMainSelected: T by remember { mutableStateOf(initialOptionMainSelected) }

    if (showColorPicker) {
        ClassicColorPicker(
            modifier = Modifier.height(180.dp),
            color = HsvColor.from(color = colorPicked),
            showAlphaBar = false,
            onColorChanged = { color: HsvColor ->
                colorPicked = color.toColor()
                onColorValueChange(color.toColor())
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Column(modifier = Modifier.weight(0.70f)) {
            TextRegular(
                text = "Formato de fecha",
                color = Color.Gray,
                size = 13.sp,
                fillMaxWidth = false
            )
            ViewDropDownMenuV2(
                modifier = Modifier.height(40.dp),
                fontSize = 14.sp,
                icon = {
                    Image(
                        modifier = Modifier.size(25.dp),
                        painter = painterResource(id = R.drawable.ic_arrows_up_down),
                        contentDescription = null
                    )
                },
                options = optionsForMainDropDown,
                onValueChange = {
                    optionMainSelected = it
                    onOptionMainSelected(it)
                },
                optionLabel = { it.toString() }
            )
        }
        Column(
            modifier = Modifier.weight(0.12f)
        ) {
            TextRegular(
                text = "Color",
                color = Color.Gray,
                size = 13.sp,
                fillMaxWidth = false
            )
            ColorPicker(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
                initialColor = colorPicked,
                onComposerClicked = {
                    showColorPicker = !showColorPicker
                }
            )
        }
        Column(
            modifier = Modifier.weight(0.18f)
        ) {
            TextRegular(
                text = "Tamaño",
                color = Color.Gray,
                size = 13.sp,
                fillMaxWidth = false
            )
            ViewDropDownMenuV2(
                modifier = Modifier.height(40.dp),
                fontSize = 15.sp,
                icon = {
                    Image(
                        modifier = Modifier.size(25.dp),
                        painter = painterResource(id = R.drawable.ic_arrows_up_down),
                        contentDescription = null
                    )
                },
                options = optionsForDropDown,
                onValueChange = {
                    optionSelected = it
                    onOptionSelected(it)
                },
                optionLabel = { it.toString() }
            )
        }
    }
}

@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    initialColor: Color,
    onComposerClicked: () -> Unit
) {
    Box(
        modifier = modifier
            .background(Color.White, allRoundedCornerShape10)
            .border(1.dp, Color.Gray, allRoundedCornerShape10),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(initialColor, allRoundedCornerShape8)
                .padding(14.dp)
                .clickable { onComposerClicked() }
        )
    }
}

enum class InvitationDateFormat {
    DDmmYYYY,
    LongFormat
}

@Preview
@Composable
fun BottomSheetConfigureInvitationPreview() {
    BottomSheetConfigureInvitation(
        clientEvent = MyPartyEvent(),
        onSave = { _, _, _, _, _, _, _, _, _ ->

        }
    )
}