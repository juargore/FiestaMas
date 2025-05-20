package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.autoSize

@Composable
fun YesNoDialog(
    isVisible: Boolean,
    title: String? = null,
    message: String? = null,
    isCancelable: Boolean = true,
    addCancelButton: Boolean = true,
    icon: Int? = R.drawable.ic_success_circled,
    colorFilter: Color? = null,
    onDismiss: () -> Unit,
    onOk: () -> Unit
) {
    if (isVisible) {
        BaseDialog(
            addCloseIcon = false,
            isCancelable = isCancelable,
            onDismiss = onDismiss,
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    icon?.let {
                        Image(
                            painter = painterResource(id = it),
                            colorFilter = if (colorFilter != null) ColorFilter.tint(colorFilter) else null,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp.autoSize())
                        )
                        VerticalSpacer(height = 5.dp)
                    }
                    title?.let {
                        TextSemiBold(
                            text = it,
                            size = 16.sp.autoSize()
                        )
                        VerticalSpacer(height = 5.dp)
                    }
                    message?.let {
                        TextMedium(
                            text = it,
                            size = 15.sp.autoSize(),
                            verticalSpace = 16.sp.autoSize()
                        )
                        VerticalSpacer(height = 10.dp)
                    }
                    VerticalSpacer(height = 5.dp)
                    Row(
                        modifier = Modifier
                            .width(200.dp.autoSize())
                            .align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (addCancelButton) {
                            TextMedium(
                                modifier = Modifier
                                    .width(100.dp.autoSize())
                                    .clickable { onDismiss() },
                                fillMaxWidth = false,
                                text = "Cancelar",
                                color = Color.Gray,
                                size = 16.sp.autoSize()
                            )
                        }
                        TextMedium(
                            modifier = Modifier
                                .width(100.dp.autoSize())
                                .clickable { onOk() },
                            fillMaxWidth = false,
                            text = "Aceptar",
                            color = PinkFiestamas,
                            size = 16.sp.autoSize()
                        )
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun YesNoDialogPreview() {
    YesNoDialog(
        isVisible = true,
        addCancelButton = false,
        title = "Email de restauracion enviado",
        message = "Verifica tu email para restaurar la contraseña",
        onDismiss = {  },
        onOk = { }
    )
}
