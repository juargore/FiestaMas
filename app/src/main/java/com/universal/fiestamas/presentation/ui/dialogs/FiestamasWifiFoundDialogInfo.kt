package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCorners
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.toColor

@Composable
fun FiestamasWifiFoundDialogInfo(
    isVisible: Boolean,
    isCancelable: Boolean = false,
    onDismiss: () -> Unit,
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
                    TextSemiBold(text = "Entendido")
                    VerticalSpacer(height = 5.dp)

                    TextMedium(text = "Recuerda que siempre puedes presionar el botón de wifi que se encuentra en la parte superior derecha de esta App.")
                    VerticalSpacer(height = 5.dp)

                    Box(modifier = Modifier.height(40.dp)) {
                        Image(
                            painter = painterResource(R.drawable.ic_wifi_on),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxHeight()
                                .align(Alignment.BottomCenter)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(15.dp)
                                .background(
                                    color = "#ffb600".toColor(),
                                    shape = CircleShape
                                )
                        ) {
                            TextMedium(
                                modifier = Modifier.align(Alignment.Center),
                                text = "!",
                                size = 11.sp,
                                color = PinkFiestamas,
                                includeFontPadding = false,
                                fillMaxWidth = false
                            )
                        }
                    }

                    VerticalSpacer(height = 5.dp)
                    TextMedium(text = "Y así conectarte a la red Fiestamas.")
                    TextMedium(text = "Gracias por tu preferencia!!")

                    VerticalSpacer(height = 15.dp)

                    ButtonPinkRoundedCorners(text = "Aceptar") {
                        onDismiss()
                    }

                    VerticalSpacer(height = 10.dp)
                }
            }
        )
    }
}

@Preview
@Composable
fun FiestamasWifiFoundDialogInfoPreview() {
    FiestamasWifiFoundDialogInfo(
        isCancelable = true,
        isVisible = true,
        onDismiss = {}
    )
}
