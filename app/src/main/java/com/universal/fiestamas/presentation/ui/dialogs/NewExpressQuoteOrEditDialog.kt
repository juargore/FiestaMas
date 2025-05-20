package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.ui.HorizontalLine
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.ValidationText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.calendar.clickable
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.showToast

@Composable
fun NewExpressQuoteOrEditDialog(
    isVisible: Boolean,
    isCancelable: Boolean = true,
    isEditingData: Boolean,
    originalTotal: String,
    originalNotes: String,
    onSendNewQuoteClicked: (
        notes: String,
        total: Int
    ) -> Unit,
    onEditQuoteClicked: (
        notes: String,
        total: Int
    ) -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {

        val context = LocalContext.current
        var importantNotes by rememberSaveable { mutableStateOf(originalNotes) }
        var total by rememberSaveable { mutableStateOf(if (isEditingData) originalTotal else "") }
        var showValidationPrice by remember { mutableStateOf(total.isBlank()) }
        var textValidationPrice by remember { mutableStateOf("") }
        var showValidationNotes by remember { mutableStateOf(importantNotes.isBlank()) }

        BaseDialog(
            isCancelable = isCancelable,
            onDismiss = onDismiss,
            content = {
                TextMedium(
                    text = if (isEditingData) "Editar Cotización Express" else "Nueva Cotización Express",
                    size = 18.sp.autoSize()
                )

                VerticalSpacer(height = 10.dp)
                HorizontalLine(color = Color.Gray, thick = 0.5.dp)
                VerticalSpacer(height = 15.dp)

                Column(modifier = Modifier.fillMaxWidth()) {
                    TextRegular(
                        modifier = Modifier.padding(
                            start = 5.dp.autoSize(),
                            end = 10.dp.autoSize()
                        ),
                        text = "Notas importantes",
                        size = 12.sp.autoSize(),
                        align = TextAlign.Start,
                        color = Color.Gray
                    )
                    VerticalSpacer(height = 2.dp)
                    Row(
                        modifier = Modifier
                            .height(115.dp.autoSize())
                            .fillMaxWidth()
                            .background(Color.White, allRoundedCornerShape12)
                            .border(0.5.dp, Color.Gray, allRoundedCornerShape12),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        BasicTextField(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp.autoSize()),
                            value = importantNotes,
                            onValueChange = {
                                importantNotes = it
                                showValidationNotes = it.isBlank() || it.isBlank()
                            },
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 16.sp.autoSize()
                            ),
                            singleLine = false
                        )
                    }
                    ValidationText(show = showValidationNotes, text = "Agregue alguna nota a la cotización")
                }

                VerticalSpacer(height = 10.dp)

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(0.65f)) {
                        TextRegular(
                            modifier = Modifier.padding(
                                start = 5.dp.autoSize(),
                                end = 10.dp.autoSize()
                            ),
                            text = "Precio",
                            size = 12.sp.autoSize(),
                            align = TextAlign.Start,
                            color = Color.Gray
                        )
                        VerticalSpacer(height = 2.dp)
                        Row(
                            modifier = Modifier
                                .height(50.dp.autoSize())
                                .fillMaxWidth()
                                .padding(end = 10.dp.autoSize())
                                .background(Color.White, allRoundedCornerShape12)
                                .border(0.5.dp, Color.Gray, allRoundedCornerShape12),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            BasicTextField(
                                value = total,
                                onValueChange = {
                                    total = it
                                    showValidationPrice = it.isBlank()
                                    textValidationPrice = context.getString(R.string.gral_error_empty, "El Precio")
                                    if (it.isNotEmpty() && it.toInt() < 1) {
                                        textValidationPrice = context.getString(R.string.gral_error_zero, "El Precio")
                                        showValidationPrice = true
                                    }
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = LocalTextStyle.current.copy(
                                    textAlign = TextAlign.Center,
                                    fontSize = 16.sp.autoSize()
                                )
                            )
                        }
                        ValidationText(show = showValidationPrice, text = textValidationPrice)
                    }

                    Box(
                        modifier = Modifier
                            .weight(0.35f)
                            .height(68.dp.autoSize()),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .background(PinkFiestamas, shape = allRoundedCornerShape12)
                                .height(49.dp.autoSize())
                                .fillMaxWidth()
                                .clickable {
                                    if (importantNotes.isBlank() || importantNotes.isBlank()) {
                                        showToast(context, "Agregue alguna nota a la cotización")
                                        return@clickable
                                    }
                                    if (total.isBlank()) {
                                        showToast(
                                            context,
                                            context.getString(
                                                R.string.gral_error_empty,
                                                "El Precio"
                                            )
                                        )
                                        return@clickable
                                    }
                                    if (total.isNotEmpty() && total.toInt() < 1) {
                                        textValidationPrice =
                                            context.getString(R.string.gral_error_zero, "El Precio")
                                        return@clickable
                                    }
                                    if (isEditingData) {
                                        onEditQuoteClicked(importantNotes, total.toInt())
                                    } else {
                                        onSendNewQuoteClicked(importantNotes, total.toInt())
                                    }
                                }
                        ) {
                            TextMedium(
                                text = "Enviar",
                                color = Color.White,
                                size = 18.sp.autoSize()
                            )
                        }
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun NewExpressQuoteOrEditDialogPreview() {
    NewExpressQuoteOrEditDialog(
        isVisible = true,
        isCancelable = false,
        isEditingData = false,
        originalTotal = "0",
        originalNotes = "",
        onSendNewQuoteClicked = { _, _ -> },
        onEditQuoteClicked = { _, _ -> },
        onDismiss = { }
    )
}
