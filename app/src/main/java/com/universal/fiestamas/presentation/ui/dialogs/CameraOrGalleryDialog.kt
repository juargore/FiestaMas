package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.ui.HorizontalLine
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.bottom_sheets.ImageSource
import com.universal.fiestamas.presentation.ui.bottom_sheets.MediaSource
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding

@Composable
fun CameraOrGalleryDialog(
    isVisible: Boolean,
    isCancelable: Boolean = true,
    mediaSource: MediaSource? = MediaSource.Image,
    onItemSelected: (ImageSource) -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
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

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnClickOutside = isCancelable,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = allRoundedCornerShape10)
                        .padding(15.dp)
                ) {
                    TextMedium(
                        text = "Agregar foto vía",
                        size = 14.sp.autoSize(),
                        color = Color.DarkGray
                    )

                    VerticalSpacer(height = 10.dp)
                    HorizontalLine(color = Color.Gray, thick = 0.5.dp)
                    VerticalSpacer(height = 18.dp)

                    optionsList.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .sidePadding(20.dp.autoSize())
                                .clickable {
                                    val source =
                                        if (index == 0) ImageSource.Camera else ImageSource.Gallery
                                    onItemSelected(source)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.width(45.dp.autoSize())) {
                                val icon = if (index == 0) R.drawable.ic_camera_filled else R.drawable.ic_image_filled
                                Image(
                                    painter = painterResource(id = icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp.autoSize()),
                                    colorFilter = ColorFilter.tint(color = PinkFiestamas)
                                )
                            }
                            TextMedium(
                                text = item,
                                size = 17.sp.autoSize(),
                                fillMaxWidth = false
                            )
                        }
                        if (index == 0)
                            VerticalSpacer(height = 18.dp)
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun CameraOrGalleryDialogPreview() {
    CameraOrGalleryDialog(
        isVisible = true,
        isCancelable = false,
        mediaSource = MediaSource.Image,
        onItemSelected = {},
        onDismiss = { }
    )
}
