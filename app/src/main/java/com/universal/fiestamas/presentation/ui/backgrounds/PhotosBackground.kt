package com.universal.fiestamas.presentation.ui.backgrounds

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.domain.models.response.FirebaseUserDb
import com.universal.fiestamas.presentation.theme.LightGray
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.sansBold
import com.universal.fiestamas.presentation.ui.ClickableHeart
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.autoSize

@Composable
fun PhotosBackground(
    titleScreen: String,
    service: Service?,
    user: FirebaseUserDb?,
    content: @Composable () -> Unit,
    onHeartButtonClicked: () -> Unit,
    onShareButtonClicked: () -> Unit,
    onBackButtonClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            VerticalSpacer(height = 5.dp)

            // Item Start
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp.autoSize())
                    .padding(horizontal = 8.dp.autoSize())
            ) {
                Box(
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(PinkFiestamas),
                        modifier = Modifier.clickable { onBackButtonClicked() }
                    )
                }

                // Item Middle
                Box(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = titleScreen,
                        fontSize = 18.sp.autoSize(),
                        fontFamily = sansBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                // Item End
                Box(
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Row (
                        modifier = Modifier.fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isClicked = if (user?.likes.isNullOrEmpty()) {
                            false
                        } else {
                            user?.likes?.contains(service?.id) == true
                        }
                        ClickableHeart(
                            isAlreadyClicked = isClicked,
                            modifier = Modifier
                                .height(20.dp.autoSize())
                                .padding(end = 5.dp.autoSize())
                        ) {
                            onHeartButtonClicked()
                        }
                        /*Image(
                            painter = painterResource(id = R.drawable.ic_share_ios),
                            contentDescription = null,
                            modifier = Modifier
                                .height(26.dp)
                                .padding(end = 5.dp)
                                .clickable { onShareButtonClicked() }
                        )*/
                    }
                }
            }
            content()
        }
    }
}
