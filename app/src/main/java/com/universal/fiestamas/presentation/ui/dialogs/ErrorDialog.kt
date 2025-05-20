package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.common.ErrorResponse
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape20
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold

@Composable
fun ErrorDialog(
    error: ErrorResponse?,
    showStatus: Boolean = true,
    isCancelable: Boolean = true,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = isCancelable)
    ) {
        Box(
            modifier = Modifier
                .background(Color.White, shape = allRoundedCornerShape20)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_error),
                    contentDescription = "Image",
                    modifier = Modifier
                        .size(90.dp)
                        .align(Alignment.CenterHorizontally)
                )
                if (showStatus) {
                    TextSemiBold(
                        text = (error?.status ?: 500).toString(),
                        size = 20.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                TextMedium(
                    text = error?.message ?: "Unknown Server Error",
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun ErrorDialogPreview() {
    ErrorDialog(error = ErrorResponse(status = 400, message = "Error!!")) {
        
    }
}
