package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape20
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersWithImage
import com.universal.fiestamas.presentation.ui.HorizontalLine
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer

@Composable
fun SuccessDialogCustom(
    serviceType: String,
    serviceName: String,
    contactName: String,
    image: String,
    phone: String,
    email: String,
    whatsapp: String,
    onDismiss: () -> Unit,
    onPhoneClicked: () -> Unit,
    onEmailClicked: () -> Unit,
    onWhatsappClicked: () -> Unit,
    onOkClicked: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false)
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
                Row(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_check),
                        colorFilter = ColorFilter.tint(color = Color.Green),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    HorizontalSpacer(width = 4.dp)
                    TextMedium(
                        text = "Has agregado un nuevo servicio de $serviceType a tu evento",
                        size = 15.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 12.dp)
                ) {
                    HorizontalLine(color = Color.LightGray)
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .height(100.dp)
                            .width(80.dp)
                            .clip(allRoundedCornerShape10)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(image),
                            contentDescription = null,
                            modifier = Modifier.fillMaxHeight(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    HorizontalSpacer(width = 8.dp)
                    Column {
                        TextSemiBold(
                            text = serviceName,
                            size = 17.sp,
                            align = TextAlign.Start
                        )
                        Row {
                            TextMedium(
                                text = "Contacto: ",
                                size = 13.sp,
                                color = PinkFiestamas,
                                fillMaxWidth = false
                            )
                            TextMedium(
                                text = contactName,
                                size = 14.sp,
                                fillMaxWidth = false
                            )
                        }
                    }
                }

                VerticalSpacer(height = 15.dp)

                Column(modifier = Modifier.fillMaxWidth()) {
                    /*Row(modifier = Modifier.fillMaxWidth()) {
                        ButtonPinkRoundedCornersWithImage(
                            modifier = Modifier.weight(0.5f),
                            icon = R.drawable.ic_phone_calling,
                            text = phone
                        ) { onPhoneClicked() }

                        HorizontalSpacer(width = 5.dp)

                        ButtonPinkRoundedCornersWithImage(
                            modifier = Modifier.weight(0.5f),
                            icon = R.drawable.ic_whatsapp,
                            text = whatsapp
                        ) { onWhatsappClicked() }
                    }

                    VerticalSpacer(height = 5.dp)*/

                    Row(modifier = Modifier.fillMaxWidth()) {
                        ButtonPinkRoundedCornersWithImage(
                            modifier = Modifier.weight(0.5f),
                            icon = R.drawable.ic_envelope,
                            //text = email
                            text = "Iniciar Chat"
                        ) { onEmailClicked() }

                        HorizontalSpacer(width = 5.dp)

                        ButtonPinkRoundedCornersWithImage(
                            modifier = Modifier.weight(0.5f),
                            icon = null,
                            text = "OK"
                        ) { onOkClicked() }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SuccessDialogCustomPreview() {
    SuccessDialogCustom(
        serviceType = "Terraza",
        serviceName = "La Fuente Rosa",
        contactName = "Octavio López",
        image = "",
        phone = "123 4567 6789",
        email = "Mensaje",
        whatsapp = "Whatsapp",
        onDismiss = { },
        onPhoneClicked = { },
        onEmailClicked = { },
        onWhatsappClicked = { },
        onOkClicked = { }
    )
}
