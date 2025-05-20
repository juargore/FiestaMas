package com.universal.fiestamas.presentation.ui.bottom_sheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TopDecorationBottomSheet
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding

@Composable
fun BottomSheetCameraOrGallery(
    mediaSource: MediaSource? = MediaSource.Image,
    onItemSelected: (ImageSource) -> Unit
) {
    val optionsList = if (mediaSource == MediaSource.Image) {
        listOf(
            stringResource(id = R.string.add_service_camera),
            stringResource(id = R.string.add_service_gallery)
        )
    } else {
        listOf(
            stringResource(id = R.string.add_service_camera_video),
            stringResource(id = R.string.add_service_gallery_video)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalSpacer(height = 4.dp)
        TopDecorationBottomSheet()
        VerticalSpacer(height = 20.dp)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(optionsList) { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sidePadding(20.dp.autoSize())
                        .clickable {
                            val source = if (index == 0) ImageSource.Camera else ImageSource.Gallery
                            onItemSelected(source)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.width(45.dp.autoSize())) {
                        val icon = if (index == 0) R.drawable.ic_camera_filled else R.drawable.ic_image_filled
                        Image(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = PinkFiestamas),
                            modifier = Modifier.size(30.dp.autoSize()),
                        )
                    }
                    TextMedium(
                        text = item,
                        size = 17.sp.autoSize(),
                        fillMaxWidth = false
                    )
                }
                VerticalSpacer(height = 15.dp)
            }
        }
        VerticalSpacer(height = 21.dp)
    }
}

@Preview
@Composable
fun BottomSheetCameraOrGalleryPreview() {
    BottomSheetCameraOrGallery {

    }
}

enum class ImageSource {
    Camera,
    Gallery
}

enum class MediaSource {
    Image,
    Video
}
