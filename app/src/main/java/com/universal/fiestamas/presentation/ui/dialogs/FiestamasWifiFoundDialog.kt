package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.presentation.screens.auth.NetworkViewModel
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCorners
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.calendar.clickable

@Composable
fun FiestamasWifiFoundDialog(
    isVisible: Boolean,
    networkState: NetworkViewModel.FiestamasConnectionState,
    isCancelable: Boolean = false,
    wasWifiDialogAlreadyShownToUser: Boolean,
    onDismiss: () -> Unit,
    onConnectToNetwork: () -> Unit,
    onRedirectToHome: () -> Unit,
    onConnectionRejected: () -> Unit
) {
    if (isVisible) {
        BaseDialog(
            addCloseIcon = wasWifiDialogAlreadyShownToUser,
            isCancelable = isCancelable,
            onDismiss = { onDismiss() },
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val (title, subTitle) = when (networkState) {
                        NetworkViewModel.FiestamasConnectionState.CONNECTED -> Pair("Conectado", "¡Sigue disfrutando GRATIS nuestra red!")
                        NetworkViewModel.FiestamasConnectionState.DETECTED_BUT_DISCONNECTED -> Pair("Red Fiestamas disponible", "¡Conéctate GRATIS!")
                        else -> Pair("No se ha encontrado la red aún", "Asegúrate de estar cerca de un hotspot")
                    }

                    TextSemiBold(text = title)
                    VerticalSpacer(height = 5.dp)

                    TextMedium(text = subTitle)
                    VerticalSpacer(height = 30.dp)

                    if (networkState == NetworkViewModel.FiestamasConnectionState.DETECTED_BUT_DISCONNECTED) {
                        ButtonPinkRoundedCorners(text = "Conectarme a la red") {
                            onConnectToNetwork()
                        }

                        if (!wasWifiDialogAlreadyShownToUser) {
                            VerticalSpacer(height = 10.dp)
                            Box(
                                modifier = Modifier
                                    .background(Color.White, shape = allRoundedCornerShape12)
                                    .border(1.dp, Color.Gray, allRoundedCornerShape12)
                                    .clickable { onConnectionRejected() }
                            ) {
                                TextMedium(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 15.dp),
                                    text = "No conectarme",
                                    color = Color.Black,
                                    fillMaxWidth = false,
                                    size = 14.sp
                                )
                            }
                        }

                        VerticalSpacer(height = 10.dp)
                        TextRegular(
                            text = "* Este proceso puede tardar varios segundos dependiendo de la velocidad de la conexión.",
                            size = 11.sp
                        )
                        VerticalSpacer(height = 7.dp)
                        TextRegular(
                            text = "* Si al cabo de 30 segundos no ha cambiado el icono amarillo a verde, por favor repite el proceso y presiona nuevamente el botón Conectarme a la red.",
                            size = 11.sp
                        )
                    }

                    if (networkState == NetworkViewModel.FiestamasConnectionState.CONNECTED) {
                        ButtonPinkRoundedCorners(text = "¡Nueva Fiesta¡") {
                            onRedirectToHome()
                        }
                    }

                    VerticalSpacer(height = 10.dp)
                }
            }
        )
    }
}

@Preview
@Composable
fun FiestamasWifiFoundDialogPreview() {
    FiestamasWifiFoundDialog(
        isVisible = true,
        networkState = NetworkViewModel.FiestamasConnectionState.CONNECTED,
        isCancelable = true,
        wasWifiDialogAlreadyShownToUser = true,
        onDismiss = {},
        onConnectToNetwork = {},
        onRedirectToHome = {},
        onConnectionRejected = {}
    )
}
