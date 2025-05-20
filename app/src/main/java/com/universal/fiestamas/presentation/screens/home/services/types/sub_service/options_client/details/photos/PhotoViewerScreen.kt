package com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details.photos

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.utils.extensions.AllowLandscapeOrientation
import com.universal.fiestamas.presentation.utils.extensions.SetBlackStatusBar
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.getIntentShareSheet
import kotlinx.coroutines.launch

@Composable
fun PhotoViewerScreen(
    photosList: List<String>,
    selectedPhoto: String,
    onBackClicked: () -> Unit
) {
    SetBlackStatusBar()
    AllowLandscapeOrientation()

    val context = LocalContext.current
    val initialIndex = photosList.indexOf(selectedPhoto)
    val index by remember { mutableIntStateOf(initialIndex) }

    ImageViewer(
        photos = photosList,
        initialIndex = index,
        onCloseClicked = { onBackClicked() },
        onShareClicked = { context.startActivity(getIntentShareSheet("Example")) }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageViewer(
    photos: List<String>,
    initialIndex: Int,
    onCloseClicked: () -> Unit,
    onShareClicked: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { photos.size }
    )
    val scope = rememberCoroutineScope()
    var scale by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    scale *= zoom
                }
            }
    ) {
        HorizontalPager(
            state = pagerState,
            key = { photos[it] },
            pageSize = PageSize.Fill,
            modifier = Modifier.align(Alignment.Center).fillMaxSize()
        ) { i ->
            Image(
                painter = rememberAsyncImagePainter(photos[i]),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .graphicsLayer(
                        scaleX = maxOf(1f, minOf(4f, scale)),
                        scaleY = maxOf(1f, minOf(4f, scale))
                    )
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        ) {
            Box(modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(18.dp)
                .clickable { onCloseClicked() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = Color.White),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .height(18.dp.autoSize())
                )
            }
        }

        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .clickable {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                pagerState.currentPage - 1
                            )
                        }
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_left_arrow),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = Color.White),
                    modifier = Modifier
                        .height(18.dp.autoSize())
                        .align(Alignment.Center)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(35.dp).padding(horizontal = 18.dp)
            ) {
                HorizontalSpacer(width = 70.dp)
                Text(
                    text = stringResource(id = R.string.service_image_counter, (pagerState.currentPage+1), photos.size),
                    color = Color.White,
                    fontSize = 16.sp.autoSize()
                )
                HorizontalSpacer(width = 70.dp)
            }
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .clickable {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                pagerState.currentPage + 1
                                //if (pagerState.currentPage == photos.size-1) 0
                                //else pagerState.currentPage + 1
                            )
                        }
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_right_arrow),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = Color.White),
                    modifier = Modifier
                        .height(17.dp.autoSize())
                        .align(Alignment.Center)
                )
            }
        }
    }
}
