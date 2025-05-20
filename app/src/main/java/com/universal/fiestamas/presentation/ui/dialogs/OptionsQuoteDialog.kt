package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.universal.fiestamas.presentation.theme.StatusCanceled
import com.universal.fiestamas.presentation.theme.StatusHired
import com.universal.fiestamas.presentation.theme.StatusPending
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.ui.ButtonStatusService
import com.universal.fiestamas.presentation.ui.HorizontalLine
import com.universal.fiestamas.presentation.ui.IconSimpleClose
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.extensions.toColor

@Composable
fun OptionsQuoteDialog(
    isVisible: Boolean,
    isCancelable: Boolean = true,
    onActionSelected: (OptionsQuote) -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        val uiHeight = 40.dp.autoSize()
        val uiSidePadding = 4.dp.autoSize()
        val divider = 0.33f

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
                            onDismiss()
                        }
                    }

                    TextMedium(
                        text = "Estado del servicio",
                        size = 16.sp.autoSize()
                    )

                    VerticalSpacer(height = 10.dp)
                    HorizontalLine(color = Color.Gray, thick = 0.5.dp)
                    VerticalSpacer(height = 15.dp)

                    Row (modifier = Modifier.fillMaxWidth()) {
                        ButtonStatusService(
                            modifier = Modifier
                                .weight(divider)
                                .height(uiHeight)
                                .sidePadding(uiSidePadding),
                            text = "Contratado",
                            color = StatusHired
                        ) {
                            onActionSelected(OptionsQuote.Hired)
                        }
                        ButtonStatusService(
                            modifier = Modifier
                                .weight(divider)
                                .height(uiHeight)
                                .sidePadding(uiSidePadding),
                            text = "Pendiente",
                            color = StatusPending
                        ) {
                            onActionSelected(OptionsQuote.Pending)
                        }
                        ButtonStatusService(
                            modifier = Modifier
                                .weight(divider)
                                .height(uiHeight)
                                .sidePadding(uiSidePadding),
                            text = "Cancelar",
                            color = StatusCanceled
                        ) {
                            onActionSelected(OptionsQuote.Cancel)
                        }
                    }
                }
            }
        }
    }
}

enum class OptionsQuote {
    Hired,
    Pending,
    Cancel
}

fun OptionsQuote.toStringStatus(): String {
    return when (this) {
        OptionsQuote.Hired -> "CONTACTED"
        OptionsQuote.Pending -> "PENDING"
        OptionsQuote.Cancel -> "CANCELLED"
    }
}

@Preview
@Composable
fun OptionsQuoteDialogPreview() {
    OptionsQuoteDialog(
        isVisible = true,
        isCancelable = false,
        onActionSelected = { },
        onDismiss = { }
    )
}
