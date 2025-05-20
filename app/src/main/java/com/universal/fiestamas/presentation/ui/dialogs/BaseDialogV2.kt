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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.ui.IconSimpleClose
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding

@Composable
fun BaseDialogV2(
    isCancelable: Boolean = true,
    addCloseIcon: Boolean = true,
    icon: @Composable () -> Unit,
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
                .padding(horizontal = 45.dp.autoSize())
        ) {

            Box(
                modifier = Modifier
                    .padding(top = 20.dp.autoSize()) // transparent space top icon
                    .fillMaxWidth()
                    .background(Color.White, shape = allRoundedCornerShape10)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = allRoundedCornerShape10)
                        .padding(horizontal = 12.dp.autoSize())
                        .padding(bottom = 10.dp.autoSize(), top = 34.dp.autoSize())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .sidePadding(5.dp.autoSize())
                    ) {
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

            Box(
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                icon()
            }
        }
    }
}
