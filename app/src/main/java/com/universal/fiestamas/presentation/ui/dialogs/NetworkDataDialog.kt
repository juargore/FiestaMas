package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.VerticalSpacer

@Composable
fun NetworkDataDialog(
    text: String,
    isVisible: Boolean,
    isCancelable: Boolean = true,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        BaseDialog(
            isCancelable = isCancelable,
            onDismiss = onDismiss,
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    VerticalSpacer(height = 5.dp)
                    TextMedium(text = text)
                    VerticalSpacer(height = 5.dp)
                }
            }
        )
    }
}
