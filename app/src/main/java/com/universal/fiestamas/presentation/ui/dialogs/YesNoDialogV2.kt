package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape14
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.autoSize

@Composable
fun YesNoDialogV2(
    isVisible: Boolean,
    isCancelable: Boolean = true,
    addAcceptButton: Boolean = true,
    addCancelButton: Boolean = false,
    textPrimaryButton: String = "Aceptar",
    textSecondaryButton: String = "Cancelar",
    icon: Int = R.drawable.ic_alert_filled,
    title: String? = null,
    message: String? = null,
    iconColor: Color? = PinkFiestamas,
    onDismiss: () -> Unit,
    onPrimaryButtonClicked: () -> Unit
) {
    if (isVisible) {
        BaseDialogV2(
            icon = {
                Image(
                    painter = painterResource(id = icon),
                    colorFilter = if (iconColor != null) ColorFilter.tint(iconColor) else null,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp.autoSize())
                )
            },
            addCloseIcon = false,
            isCancelable = isCancelable,
            onDismiss = onDismiss,
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    VerticalSpacer(height = 25.dp)

                    title?.let {
                        TextSemiBold(
                            text = it,
                            size = 18.sp.autoSize()
                        )
                        VerticalSpacer(height = 5.dp)
                    }

                    VerticalSpacer(height = 10.dp)

                    message?.let {
                        TextMedium(
                            text = it,
                            size = 16.sp.autoSize(),
                            verticalSpace = 16.sp.autoSize()
                        )
                        VerticalSpacer(height = 10.dp)
                    }

                    VerticalSpacer(height = 18.dp)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (addCancelButton) {
                            ButtonForDialogV2(text = textSecondaryButton) {
                                onDismiss()
                            }
                            HorizontalSpacer(width = 10.dp)
                        }
                        if (addAcceptButton) {
                            ButtonForDialogV2(text = textPrimaryButton) {
                                onPrimaryButtonClicked()
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun ButtonForDialogV2(
    text: String,
    onClick: () -> Unit
) {
    ButtonPinkRoundedCornersV2(
        verticalPadding = 8.dp,
        horizontalPadding = 22.dp,
        shape = allRoundedCornerShape14,
        onClick = onClick,
        content = {
            TextSemiBold(
                text = text,
                size = 15.sp.autoSize(),
                color = Color.White,
                shadowColor = Color.Gray,
                fillMaxWidth = false
            )
        }
    )
}

@Preview
@Composable
fun YesNoDialogV2Preview() {
    YesNoDialogV2(
        isVisible = true,
        addCancelButton = false,
        addAcceptButton = false,
        title = "Email de restauracion enviado",
        message = "Verifica tu email para restaurar la contraseña",
        onDismiss = {  },
        onPrimaryButtonClicked = { }
    )
}
