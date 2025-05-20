package com.universal.fiestamas.presentation.ui.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import com.universal.fiestamas.domain.models.Event
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape30
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.isRunningOnTablet
import com.universal.fiestamas.presentation.utils.extensions.toColor

@Composable
fun CardHomeEventTypeCircled(
    item: Event?,
    onItemClick: () -> Unit
) {
    if (item == null) return

    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(SvgDecoder.Factory())
        }
        .build()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp.autoSize())
            .padding(vertical = 10.dp)
            .clickable { onItemClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = item.image,
                imageLoader = imageLoader
            ),
            colorFilter = ColorFilter.tint("#30567a".toColor()),
            modifier = Modifier
                .size(45.dp)
                .border(1.dp, "#30567a".toColor(), CircleShape)
                .clip(CircleShape)
                .background(Color.Transparent, CircleShape)
                .padding(10.dp)
            ,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        VerticalSpacer(height = 5.dp)
        TextRegular(
            text = item.name,
            align = TextAlign.Center,
            size = 11.sp,
            verticalSpace = 10.sp
        )
    }
}


@Composable
fun CardHomeEventType(
    item: Event?,
    index: Int,
    onItemClick: (Event) -> Unit
) {
    if (item == null) return

    val shape = allRoundedCornerShape10
    val padding = if (index % 2 != 0) {
        Modifier.padding(start = 7.dp, end = 15.dp)
    } else {
        Modifier.padding(end = 7.dp, start = 15.dp)
    }

    val configuration = LocalConfiguration.current
    val isTablet = isRunningOnTablet()
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val screenWidth = configuration.screenWidthDp.dp
    val cardHeight = if (isTablet) {
        ((screenHeight / 3) + 20).dp
    } else {
        (screenWidth / 2) + 10.dp
    }

    Column(
        modifier = padding
            .width((screenWidth / 2) - 23.dp)
            .height(cardHeight)
            .padding(vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(Color.White)
                .clickable { onItemClick(item) }
        ) {
            val image = if (item.icon.isBlank() || item.icon.isBlank()) {
                item.image
            } else {
                item.icon
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Gray)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = image,
                    contentDescription = null,
                    modifier = Modifier
                        .graphicsLayer(
                            scaleX = 1.4f,
                            scaleY = 1.4f,
                            translationY = 90f
                        )
                        .fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(10.dp)

            ) {
                TextMedium(
                    modifier = Modifier
                        .background(Color.White, allRoundedCornerShape10)
                        .padding(5.dp),
                    text = item.name.uppercase(),
                    size = 12.sp
                )
            }
        }
    }
}

enum class CardPosition {
    LEFT,
    CENTER,
    RIGHT,
    NONE
}

fun getCardPosition(columnCount: Int = 3, position: Int): CardPosition {
    val column = position % columnCount
    val isCenter = column == columnCount / 2
    return when {
        column == 0 -> CardPosition.LEFT
        column == columnCount - 1 -> CardPosition.RIGHT
        isCenter -> CardPosition.CENTER
        else -> CardPosition.NONE
    }
}

@Composable
fun CardHomeServiceCategory(
    item: ServiceCategory?,
    index: Int,
    onItemClick: (ServiceCategory) -> Unit
) {
    if (item == null) return

    val padding = if (index % 2 != 0) {
        Modifier.padding(start = 10.dp)
    } else {
        Modifier.padding(end = 10.dp)
    }

    val configuration = LocalConfiguration.current
    val isTablet = isRunningOnTablet()
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val screenWidth = configuration.screenWidthDp.dp
    val cardHeight = if (isTablet) {
        ((screenHeight / 3)).dp
    } else {
        (screenWidth / 2) - 10.dp
    }

    Column(
        modifier = padding
            .width((screenWidth / 2) - 50.dp)
            .height(cardHeight)
            .padding(vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(allRoundedCornerShape30)
                .background(Color.White)
                .clickable { onItemClick(item) }
        ) {
            val image = if (item.icon.isBlank() || item.icon.isBlank()) {
                item.image
            } else {
                item.icon
            }

            Box(
                modifier = Modifier
                    .clip(allRoundedCornerShape30)
                    .background(Color.Gray)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = image,
                    contentDescription = null,
                    modifier = Modifier
                        .graphicsLayer(
                            scaleX = 1.4f,
                            scaleY = 1.4f,
                            translationY = 90f
                        )
                        .fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(10.dp)

            ) {
                TextMedium(
                    modifier = Modifier
                        .background(Color.White, allRoundedCornerShape10)
                        .padding(5.dp),
                    text = item.name.uppercase(),
                    size = 12.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun CardHomeEventTypePreview() {
    CardHomeEventType(
        item = Event(),
        index = 0,
        onItemClick = { }
    )
}

@Preview
@Composable
fun CardHomeServiceCategoryPreview() {
    CardHomeServiceCategory(
        item = ServiceCategory(),
        index = 0,
        onItemClick = { }
    )
}
