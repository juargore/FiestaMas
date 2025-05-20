package com.universal.fiestamas.presentation.ui.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.autoSize

@Composable
fun CardServiceReview(
    photo: String?,
    name: String?,
    message: String?
) {
    Row(
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        Box(
            modifier = Modifier.size(45.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(photo),
                contentDescription = null,
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(1.dp, Color.Gray, CircleShape)
            )
        }
        Column(
            modifier = Modifier.padding(start = 15.dp)
        ) {
            Text(
                text = name.orEmpty(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = message.orEmpty(),
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun CardServiceAttribute(
    icon: String?,
    name: String?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(90.dp.autoSize())
            .height(72.dp.autoSize())
    ) {
        Image(
            modifier = Modifier.weight(0.55f),
            painter = rememberAsyncImagePainter(icon.orEmpty()),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        TextRegular(
            modifier = Modifier.weight(0.45f),
            text = name.orEmpty(),
            align = TextAlign.Center,
            size = 11.sp,
            verticalSpace = 10.sp
        )
    }
}
