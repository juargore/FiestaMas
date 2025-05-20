package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCorners
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.calendar.clickable

@Composable
fun NoInternetConnectionDialog(
    isVisible: Boolean,
    isCancelable: Boolean = false,
    onOpenWifiSettings: () -> Unit,
    onDismiss: () -> Unit
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
                        text = "Sin conexión a Internet",
                        size = 21.sp
                    )

                    Image(
                        painter = painterResource(R.drawable.image_no_internet),
                        contentDescription = null,
                        modifier = Modifier.size(200.dp)
                    )

                    TextMedium(text = "Sin conexión a internet no es posible utilizar la App Fiestamas.\n\nPor favor, ve a ajustes y revisa que tienes los datos móviles o una conexión WiFi establecida.")

                    VerticalSpacer(height = 25.dp)

                    ButtonPinkRoundedCorners(text = "Abrir ajustes") {
                        onOpenWifiSettings()
                        onDismiss()
                    }
                    VerticalSpacer(height = 18.dp)
                    TextSemiBold(
                        modifier = Modifier.clickable { onDismiss() },
                        text = "Salir",
                        color = Color.Blue
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun NoInternetConnectionDialogPreview() {
    NoInternetConnectionDialog(
        isCancelable = true,
        isVisible = true,
        onDismiss = {},
        onOpenWifiSettings = {}
    )
}
