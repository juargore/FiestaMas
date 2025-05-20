package com.universal.fiestamas.presentation.ui.bottom_sheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.TopDecorationBottomSheet
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.calendar.clickable

@Composable
fun BottomSheetCustomizeInvitation(
    onImageSelected: (Int) -> Unit
) {
    val mList = listOf(
        R.drawable.invitation_test0,
        R.drawable.invitation_test1,
        R.drawable.invitation_test2,
        R.drawable.invitation_test3,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalSpacer(height = 5.dp)
        TopDecorationBottomSheet()
        VerticalSpacer(height = 15.dp)

        TextSemiBold(
            fillMaxWidth = false,
            text = "Edita el diseño de tu Invitación",
            size = 19.sp
        )
        VerticalSpacer(height = 12.dp)
        LazyRow(
            state = rememberLazyListState(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(mList) { i, img ->
                InvitationBackground(index = i, image = img) {
                    onImageSelected(img)
                }
            }
        }
    }
}

@Composable
fun InvitationBackground(
    index: Int,
    image: Int,
    onImageSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(250.dp)
            .width(140.dp)
            .border(1.dp, Color.LightGray)
            .clickable { onImageSelected() }
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(image),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(0.5.dp, Color.LightGray)
                .align(Alignment.BottomCenter)
        ) {
            TextSemiBold(
                modifier = Modifier.padding(6.dp),
                text = "Invitación ${index+1}"
            )
        }
    }
}

@Preview
@Composable
fun BottomSheetCustomizeInvitationPreview() {
    BottomSheetCustomizeInvitation {

    }
}
