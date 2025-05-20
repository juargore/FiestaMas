package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape16
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.autoSize

@Composable
fun ProgressDialog(isVisible: Boolean, message: String = "Procesando...") {
    if (isVisible) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(dismissOnClickOutside = false)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White, shape = allRoundedCornerShape16)
                    .padding(16.dp.autoSize())
            ) {
                Column(
                    modifier = Modifier.width(200.dp.autoSize()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(58.dp.autoSize())
                            .padding(vertical = 18.dp.autoSize()),
                        color = PinkFiestamas,
                        strokeWidth = 6.dp
                    )
                    VerticalSpacer(20.dp)
                    Text(
                        text = message,
                        style = TextStyle(fontSize = 16.sp.autoSize()),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 18.dp.autoSize())
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ProgressDialogPreview() {
    ProgressDialog(
        isVisible = true,
    )
}
