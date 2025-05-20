package com.universal.fiestamas.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.ServiceStatus
import com.universal.fiestamas.presentation.theme.LightBlue
import com.universal.fiestamas.presentation.theme.NormalGray
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape16
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape20
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape24
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape30
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape6
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape8
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.getStatusColor
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.extensions.toColor

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ViewDropDownMenu(
    modifier: Modifier = Modifier,
    placeholder: String,
    options: List<String>,
    addWhiteBackground: Boolean = true,
    startedIndexSelected: Int = 0,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[startedIndexSelected]) }

    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp.autoSize())
                .background(
                    color = if (addWhiteBackground) Color.White else Color.Transparent,
                    shape = allRoundedCornerShape8
                )
                .align(Alignment.BottomCenter)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                readOnly = true,
                modifier = modifier,
                value = selectedOptionText,
                onValueChange = { },
                label = { Text(text = placeholder, fontSize = 16.sp.autoSize()) },
                placeholder = { Text(text = placeholder, fontSize = 16.sp.autoSize()) },
                shape = allRoundedCornerShape10,
                textStyle = TextStyle.Default.copy(fontSize = 16.sp.autoSize()),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PinkFiestamas, // selected
                    unfocusedBorderColor = NormalGray // unselected
                ),
                trailingIcon = {
                    Image(
                        modifier = Modifier.width(20.dp.autoSize()),
                        painter = painterResource(
                            id = R.drawable.ic_arrow_down),
                        contentDescription = null
                    )
                }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        onClick = {
                            selectedOptionText = selectionOption
                            onItemSelected(selectionOption)
                            expanded = false
                        }
                    ){
                        Text(text = selectionOption, fontSize = 16.sp.autoSize())
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun <T> ViewDropDownMenuV2(
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)?,
    fontSize: TextUnit,
    options: List<T>,
    initialValue: T? = null,
    onValueChange: (T) -> Unit,
    optionLabel: (T) -> String // label of each option,
) {
    val corners: RoundedCornerShape = allRoundedCornerShape12
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(initialValue) }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, allRoundedCornerShape8)
                .align(Alignment.BottomCenter)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, corners)
                    .border(1.dp, Color.Gray, corners)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .wrapContentHeight()
                ) {
                    Text(
                        modifier = Modifier.padding(4.dp),
                        text = optionLabel(selectedOption ?: options[0]),
                        fontSize = fontSize,
                    )
                    /*RegularEditText(
                        modifier = modifier,
                        textSize = fontSize,
                        readOnly = true,
                        value = optionLabel(selectedOption ?: options[0]),
                        placeholder = "MyPlaceholder",
                        onValueChange = { }
                    )*/
                }
                Box(
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    icon?.invoke()
                }
            }
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        onClick = {
                            selectedOption = selectionOption
                            onValueChange(selectionOption)
                            expanded = false
                        },
                        content = {
                            Text(text = optionLabel(selectionOption), fontSize = fontSize)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RoundedEdittext(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isForV2: Boolean = false,
    hintBackgroundColorV2: Color = PinkFiestamas,
    hintTextColorV2: Color = Color.White,
    textColorFocusedV2: Color = Color.Black,
    textColorUnfocusedV2: Color = Color.White,
    focusedBorderColorV2: Color = Color.White,
    unfocusedBorderColorV2: Color = Color.White,
    maxLines: Int = 1,
    minLines: Int = 1,
    isEnabled: Boolean = true,
    singleLine: Boolean = false,
    onClicked: (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    fun filterDecimalInput(text: String): String {
        val regex = Regex("""[0-9]*""")
        return regex.find(text)?.value.orEmpty()
    }

    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var text by remember { mutableStateOf(value) }
    val mShape = if (isForV2) allRoundedCornerShape30 else allRoundedCornerShape10

    val focusedBorderColor = if (isForV2) focusedBorderColorV2 else PinkFiestamas
    val unfocusedBorderColor = if (isForV2) unfocusedBorderColorV2 else NormalGray

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp.autoSize())
                .background(
                    color = if (isForV2) {
                        if (isFocused) Color.White else Color.Transparent
                    } else {
                        Color.White
                    },
                    shape = mShape
                )
                .align(Alignment.BottomCenter)
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClicked?.invoke() }
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            value = value,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            onValueChange = {
                val shouldFilterInput = keyboardType == KeyboardType.Number
                val filteredText = if (shouldFilterInput) filterDecimalInput(it) else it
                if (it != filteredText) onValueChange(filteredText) else onValueChange(it)
                text = filteredText
            },
            label = {
                PlaceHolder(
                    isForV2 = isForV2,
                    placeholder = placeholder,
                    isFocused = isFocused,
                    textOnEt = text,
                    hintBackgroundColorV2 = hintBackgroundColorV2,
                    hintTextColorV2 = hintTextColorV2
                )
            },
            maxLines = maxLines,
            minLines = minLines,
            shape = mShape,
            enabled = isEnabled,
            textStyle = TextStyle.Default.copy(
                fontSize = 16.sp.autoSize(),
                color = if (isForV2) {
                    if (isFocused) textColorFocusedV2 else textColorUnfocusedV2
                } else {
                    Color.Black
                }
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = focusedBorderColor, // selected
                unfocusedBorderColor = unfocusedBorderColor, // unselected
            )
        )
    }
}

@Composable
fun PlaceHolder(
    placeholder: String,
    isForV2: Boolean = false,
    isFocused: Boolean = false,
    textOnEt: String = "",
    hintBackgroundColorV2: Color = PinkFiestamas,
    hintTextColorV2: Color = Color.White,
) {
    if (isForV2) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = if (isFocused) {
                        hintBackgroundColorV2
                    } else {
                        if (textOnEt.isNotEmpty()) hintBackgroundColorV2 else Color.Transparent
                    },
                    shape = allRoundedCornerShape10
                )
                .padding(
                    vertical = 2.dp,
                    horizontal = 7.dp
                )
        ) {
            TextSemiBold(
                text = placeholder,
                size = 12.sp.autoSize(),
                color = hintTextColorV2,
                align = TextAlign.Start,
                fillMaxWidth = false
            )
        }
    } else {
        TextRegular(
            text = placeholder,
            size = 14.sp.autoSize(),
            color = Color.DarkGray,
            align = TextAlign.Start,
            fillMaxWidth = false
        )
    }
}

@Composable
fun RoundedPhoneEdittext(
    modifier: Modifier = Modifier,
    value: String,
    isForV2: Boolean = false,
    hintBackgroundColorV2: Color = PinkFiestamas,
    hintTextColorV2: Color = Color.White,
    placeholder: String,
    singleLine: Boolean = false,
    onClicked: (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    val maxNumbersForPhone = 10

    val focusedBorderColor = if (isForV2) Color.White else PinkFiestamas
    val unfocusedBorderColor = if (isForV2) Color.White else NormalGray
    val mShape = if (isForV2) allRoundedCornerShape30 else allRoundedCornerShape10

    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }
    var text by remember { mutableStateOf(value) }

    fun formatPhoneNumber(text: String): String {
        val digits = text.filter { it.isDigit() }
        return when {
            digits.length >= 7 -> {
                val areaCode = digits.take(3)
                val middlePart = digits.drop(3).take(3)
                val lastPart = digits.drop(6).take(4)
                "($areaCode) $middlePart-$lastPart"
            }
            digits.length >= 4 -> {
                val areaCode = digits.take(3)
                val middlePart = digits.drop(3)
                "($areaCode) $middlePart"
            }
            digits.isNotEmpty() -> {
                "($digits"
            }
            else -> ""
        }
    }

    LaunchedEffect(value) {
        if (value.filter { it.isDigit() }.length == maxNumbersForPhone) {
            val formattedText = formatPhoneNumber(value)
            if (formattedText != value) {
                textFieldValue = TextFieldValue(
                    text = formattedText,
                    selection = TextRange(formattedText.length) // cursor position
                )
                onValueChange(formattedText)
            }
        }
    }

    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp.autoSize())
                .background(
                    color = if (isForV2) {
                        if (isFocused) Color.White else Color.Transparent
                    } else {
                        Color.White
                    },
                    shape = mShape
                )
                .align(Alignment.BottomCenter)
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClicked?.invoke() }
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            value = textFieldValue,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            onValueChange = { newValue ->
                val formattedText = formatPhoneNumber(newValue.text)

                if (formattedText.filter { it.isDigit() }.length > maxNumbersForPhone) {
                    return@OutlinedTextField
                }

                val cursorPosition = formattedText.length
                textFieldValue = TextFieldValue(
                    text = formattedText,
                    selection = TextRange(cursorPosition)
                )
                onValueChange(formattedText)
                text = formattedText
            },
            label = {
                PlaceHolder(
                    isForV2 = isForV2,
                    placeholder = placeholder,
                    isFocused = isFocused,
                    textOnEt = text,
                    hintBackgroundColorV2 = hintBackgroundColorV2,
                    hintTextColorV2 = hintTextColorV2
                )
            },
            shape = mShape,
            enabled = true,
            textStyle = TextStyle.Default.copy(
                fontSize = 16.sp.autoSize(),
                color = if (isForV2) {
                    if (isFocused) Color.Black else Color.White
                } else {
                    Color.Black
                }
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = focusedBorderColor, // selected
                unfocusedBorderColor = unfocusedBorderColor // unselected
            )
        )
    }
}

@Composable
fun RoundedPasswordEditText(
    value: String,
    placeholder: String,
    isForV2: Boolean = false,
    hintBackgroundColorV2: Color = PinkFiestamas,
    hintTextColorV2: Color = Color.White,
    onNextAction: (() -> Unit)? = null,
    onValueChange: (String) -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var passwordVisible by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(value) }
    val mShape = if (isForV2) allRoundedCornerShape30 else allRoundedCornerShape10

    val focusedBorderColor = if (isForV2) Color.White else PinkFiestamas
    val unfocusedBorderColor = if (isForV2) Color.White else NormalGray

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp.autoSize())
                .background(
                    color = if (isForV2) {
                        if (isFocused) Color.White else Color.Transparent
                    } else {
                        Color.White
                    },
                    shape = mShape
                )
                .align(Alignment.BottomCenter)
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            value = value,
            onValueChange = {
                onValueChange(it)
                text = it
            },
            label = {
                PlaceHolder(
                    isForV2 = isForV2,
                    placeholder = placeholder,
                    isFocused = isFocused,
                    textOnEt = text,
                    hintBackgroundColorV2 = hintBackgroundColorV2,
                    hintTextColorV2 = hintTextColorV2
                )
            },
            shape = mShape,
            keyboardActions = KeyboardActions(
                onNext = { onNextAction?.invoke() }
            ),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = if (isForV2) {
                            if (isFocused) Color.DarkGray else Color.White
                        } else {
                            Color.Black
                        }
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation("*".toCharArray().first()),
            textStyle = TextStyle.Default.copy(
                fontSize = 16.sp.autoSize(),
                color = if (isForV2) {
                    if (isFocused) Color.Black else Color.White
                } else {
                    Color.Black
                }
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = focusedBorderColor, // selected
                unfocusedBorderColor = unfocusedBorderColor // unselected
            )
        )
    }
}

@Composable
fun RegularEditText(
    modifier: Modifier,
    value: String,
    textSize: TextUnit = 14.sp,
    readOnly: Boolean = false,
    placeholder: String,
    background: Color = Color.White,
    corners: RoundedCornerShape = allRoundedCornerShape12,
    keyboardType: KeyboardType = KeyboardType.Text,
    paddingHorizontal: Dp = 8.dp,
    paddingVertical: Dp = 5.dp,
    addBorder: Boolean = true,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = modifier
            .background(background, corners)
            .then(if (addBorder) {
                Modifier.border(1.dp.autoSize(), Color.Gray, corners)
            } else {
                modifier
            }),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = paddingHorizontal,
                    vertical = paddingVertical
                ),
            value = value,
            readOnly = readOnly,
            onValueChange = onValueChange,
            textStyle = TextStyle.Default.copy(fontSize = textSize),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
        if (value.isBlank()) {
            TextRegular(
                modifier = Modifier.padding(
                    horizontal = paddingHorizontal,
                    vertical = paddingVertical
                ),
                text = placeholder,
                fillMaxWidth = false,
                color = Color.Gray,
                size = textSize
            )
        }
    }
}

@Composable
fun TopDecorationBottomSheet() {
    Box (
        modifier = Modifier
            .height(5.dp)
            .width(85.dp)
            .clip(allRoundedCornerShape24)
            .background(Color.LightGray)
    )
}

@Composable
fun CircleStatus(modifier: Modifier = Modifier, size: Dp = 11.dp.autoSize(), status: ServiceStatus) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(size)
            .background(status.getStatusColor())
    )
}

@Composable
fun GradientTopAndRightToBottom(color: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        color,
                        Color.Transparent
                    )
                )
            )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        color
                    )
                )
            )
    )
}

@Composable
fun CheckboxPink(
    content: @Composable () -> Unit,
    startChecked: Boolean = false,
    onChecked: (Boolean) -> Unit
) {
    var checkedState by remember { mutableStateOf(startChecked) }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp.autoSize()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            modifier = Modifier.size(20.dp.autoSize()),
            checked = checkedState,
            onCheckedChange = {
                checkedState = it
                onChecked(it)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = PinkFiestamas,
                uncheckedColor = PinkFiestamas,
                checkmarkColor = Color.White
            )
        )
        HorizontalSpacer(width = 5.dp)
        content()
    }
}

@Composable
fun CheckboxAttributes(
    content: @Composable () -> Unit,
    startChecked: Boolean = false,
    onChecked: (Boolean) -> Unit
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    var checkedState by remember { mutableStateOf(startChecked) }

    Row (
        modifier = Modifier
            .width((screenWidth / 2) - 50.dp)
            .padding(vertical = 2.dp.autoSize()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            modifier = Modifier.size(20.dp.autoSize()),
            checked = checkedState,
            onCheckedChange = {
                checkedState = it
                onChecked(it)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = PinkFiestamas,
                uncheckedColor = PinkFiestamas,
                checkmarkColor = Color.White
            )
        )
        HorizontalSpacer(width = 5.dp)
        content()
    }
}

@Composable
fun AttributeViewV2(
    modifier: Modifier = Modifier,
    text: String,
    isChecked: Boolean,
    onClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(if (isChecked) PinkFiestamas else Color.White, allRoundedCornerShape16)
            .border(1.dp, PinkFiestamas, allRoundedCornerShape16)
            .clip(allRoundedCornerShape16)
            .clickable { onClicked() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .sidePadding(8.dp)
                .size(16.dp)
                .background(Color.White, CircleShape)
                .border(1.dp, PinkFiestamas, CircleShape)
        )
        TextMedium(
            modifier = Modifier.padding(end = 4.dp),
            text = text,
            size = 14.sp,
            maxLines = 2,
            addThreeDots = true,
            align = TextAlign.Start,
            color = if (isChecked) Color.White else Color.Black
        )
    }
}

@Composable
fun UnityViewV2(
    modifier: Modifier = Modifier,
    icon: Int,
    text: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Column(
        modifier = modifier
            .height(95.dp)
            .background(if (isSelected) LightBlue else Color.Transparent, allRoundedCornerShape20)
            .border(2.dp, PinkFiestamas, allRoundedCornerShape20)
            .clip(allRoundedCornerShape20)
            .clickable { onSelected() },
    ) {
        Box(
            modifier = Modifier
                .weight(0.65f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
                    .padding(top = 8.dp)
                ,
                painter = painterResource(icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.DarkGray)
            )
        }
        Box(
            modifier = Modifier
                .weight(0.35f)
                .fillMaxWidth()
                .sidePadding(5.dp)
            ,
            contentAlignment = Alignment.Center
        ) {
            TextMedium(
                text = text,
                color = Color.DarkGray,
                size = 11.sp,
                verticalSpace = 10.sp
            )
        }
    }
}

@Composable
fun SwitchButton(
    startChecked: Boolean = false,
    onChecked: (Boolean) -> Unit
) {
    var checked by remember { mutableStateOf(startChecked) }

    Switch(
        checked = checked,
        onCheckedChange = {
            checked = it
            onChecked(it)
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = PinkFiestamas,
            uncheckedThumbColor = Color.Gray
        )
    )
}

@Composable
fun TabNewOrEditQuote(
    text: String,
    icon: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                color = "#98999C".toColor(),
                shape = allRoundedCornerShape12
            )
            .fillMaxWidth()
            .padding(vertical = 6.dp.autoSize())
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box (
            modifier = Modifier
                .size(20.dp.autoSize())
                .clip(CircleShape)
                .background(PinkFiestamas, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box (
                modifier = Modifier
                    .size(13.dp.autoSize())
                    .clip(CircleShape)
                    .background(Color.White, CircleShape)
            )
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(icon),
                contentDescription = null,
                colorFilter = null
            )
        }

        HorizontalSpacer(width = 6.dp)

        TextRegular(
            text = text,
            size = 12.sp.autoSize(),
            color = Color.White,
            fillMaxWidth = false
        )
    }
}

@Composable
fun MessagesFromNegotiationButton(
    counterUnreadMessages: Int,
    onNavigateNotificationsClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                color = "#98999C".toColor(),
                shape = allRoundedCornerShape12
            )
            .fillMaxWidth()
            .padding(vertical = 3.dp.autoSize())
            .clickable {
                onNavigateNotificationsClicked()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .wrapContentHeight()
                .padding(end = 5.dp.autoSize())
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_envelope_black),
                contentDescription = null,
                modifier = Modifier
                    .wrapContentHeight()
                    .align(Alignment.BottomCenter)
            )
            TextSemiBold(
                text = counterUnreadMessages.toString(),
                size = 9.sp.autoSize(),
                color = Color.White,
                fillMaxWidth = false,
                includeFontPadding = false,
                horizontalSpace = (-1).sp,
                modifier = Modifier
                    .background(
                        color = Color.Red,
                        shape = CircleShape
                    )
                    .align(Alignment.TopEnd)
                    .sidePadding(4.dp)
            )
        }

        HorizontalSpacer(width = 6.dp)

        TextRegular(
            text = "Mensajes",
            size = 12.sp.autoSize(),
            color = Color.White,
            fillMaxWidth = false
        )
    }
}
