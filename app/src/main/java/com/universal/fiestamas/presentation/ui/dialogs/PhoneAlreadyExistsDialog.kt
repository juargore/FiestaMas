package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.universal.fiestamas.presentation.theme.OrangeFiestaki
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape14
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.toColor

@Composable
fun PhoneAlreadyExistsDialog(
    isVisible: Boolean,
    icon: Int,
    title: String,
    body: String,
    buttonString: String,
    onOk: () -> Unit
) {
    if (isVisible) {
        BaseDialog(
            isCancelable = true,
            addCloseIcon = false,
            onDismiss = { },
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Box(
                        modifier = Modifier
                            .size(55.dp)
                            .border(2.dp, OrangeFiestaki, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(OrangeFiestaki),
                            modifier = Modifier.size(35.dp)
                        )
                    }


                    VerticalSpacer(20.dp)

                    TextSemiBold(
                        text = title,
                        size = 22.sp
                    )
                    VerticalSpacer(10.dp)

                    TextMedium(text = body)
                    VerticalSpacer(25.dp)

                    ButtonPinkRoundedCornersV2(
                        verticalPadding = 8.dp,
                        horizontalPadding = 22.dp,
                        shape = allRoundedCornerShape14,
                        onClick = {
                            onOk()
                        },
                        content = {
                            TextSemiBold(
                                text = buttonString,
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


@Preview
@Composable
fun PhoneAlreadyExistsDialogPreview() {
    PhoneAlreadyExistsDialog(
        isVisible = true,
        icon = R.drawable.ic_exclamation_mark,
        title = "Ups!",
        body = "El número de teléfono ya está registrado",
        buttonString = "Aceptar",
        onOk = {

        }
    )
}
