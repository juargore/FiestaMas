package com.universal.fiestamas.presentation.ui.empty_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape16
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.autoSize

@Composable
fun BaseEmptyScreen(
    imageTop: @Composable () -> Unit,
    grayText: String,
    buttonText: String,
    onAddClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        imageTop()

        VerticalSpacer(height = 10.dp)

        TextRegular(
            text = grayText,
            color = Color.DarkGray,
            size = 16.sp.autoSize(),
            verticalSpace = 16.sp.autoSize()
        )

        VerticalSpacer(height = 6.dp)

        ButtonPinkRoundedCornersV2(
            shape = allRoundedCornerShape16,
            horizontalPadding = 18.dp.autoSize(),
            content = {
                TextSemiBold(
                    text = buttonText,
                    color = Color.White,
                    shadowColor = Color.Gray,
                    fillMaxWidth = false,
                    size = 16.sp.autoSize()
                )
            },
            onClick = { onAddClicked() }
        )
    }
}
