package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.ui.IconSimpleClose
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding

@Composable
fun BaseDialog(
    title: String? = null,
    isCancelable: Boolean = true,
    sidePadding: Dp = 15.dp,
    addCloseIcon: Boolean = true,
    content: @Composable () -> Unit,
    onDismiss: () -> Unit,
) {
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
                .padding(sidePadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = allRoundedCornerShape10)
                    .padding(sidePadding)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sidePadding(5.dp.autoSize())
                ) {
                    if (!title.isNullOrEmpty()) {
                        TextSemiBold(
                            modifier = Modifier.align(Alignment.Center),
                            fillMaxWidth = false,
                            text = title,
                            size = 22.sp.autoSize()
                        )
                    }

                    if (addCloseIcon) {
                        IconSimpleClose(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            onClose = { onDismiss() }
                        )
                    }
                }

                content()
            }
        }
    }
}
