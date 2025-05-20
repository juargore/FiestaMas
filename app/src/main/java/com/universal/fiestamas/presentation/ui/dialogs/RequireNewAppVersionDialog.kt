package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape14
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer

@Composable
fun RequireNewAppVersionDialog(
    isVisible: Boolean,
    isCancelable: Boolean = true,
    onLinkClicked: (link: String) -> Unit
) {
    if (isVisible) {
        BaseDialog(
            isCancelable = isCancelable,
            addCloseIcon = false,
            onDismiss = { },
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TextSemiBold(
                        text = "Actualización disponible",
                        size = 18.sp
                    )
                    VerticalSpacer(10.dp)

                    TextMedium(text = "La nueva versión de FiESTAMAS está aquí con mejoras de rendimiento y funciones increíbles.")
                    VerticalSpacer(25.dp)

                    ButtonPinkRoundedCornersV2(
                        verticalPadding = 8.dp,
                        horizontalPadding = 22.dp,
                        shape = allRoundedCornerShape14,
                        onClick = {
                            onLinkClicked("https://play.google.com/store/apps/details?id=com.universal.fiestamas&hl=en&gl=US")
                        },
                        content = {
                            TextSemiBold(
                                text = "¡Actualiza ahora!",
                                size = 14.sp,
                                color = Color.White,
                                shadowColor = Color.Gray,
                                fillMaxWidth = false
                            )
                        }
                    )
                    VerticalSpacer(10.dp)
                }
            }
        )
    }
}
