@file:Suppress("DEPRECATION")

package com.universal.fiestamas.presentation.ui

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.MediaItemService
import com.universal.fiestamas.domain.models.MyPartyEvent
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape14
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape8
import com.universal.fiestamas.presentation.theme.topRoundedCornerShape10
import com.universal.fiestamas.presentation.ui.calendar.models.CalendarDay
import com.universal.fiestamas.presentation.ui.cards.CardMyPartyHorizontal
import com.universal.fiestamas.presentation.ui.cards.CardMyPartyVertical
import com.universal.fiestamas.presentation.utils.extensions.scrollToDate
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.extensions.toUnitReadable
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.universal.fiestamas.domain.models.Role
import com.universal.fiestamas.domain.models.response.FirebaseUserDb
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape24
import com.universal.fiestamas.presentation.utils.OpenUrl
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.isRunningOnTablet
import com.universal.fiestamas.presentation.utils.extensions.toColor
import com.universal.fiestamas.presentation.utils.getRole
import kotlin.coroutines.CoroutineContext

@Composable
fun ViewHeaderHome(
    userDb: FirebaseUserDb?,
    onClick: () -> Unit,
    onSearch: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val isTablet = isRunningOnTablet()
    val cardHeight = if (isTablet) {
        (screenHeight / 4) + 25.dp
    } else {
        (screenHeight / 4) + 20.dp
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
    ) {
        Image(
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize(),
            painter = painterResource( id = R.drawable.fiestamas_banner)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Row {
                if (userDb?.role.getRole() == Role.Unauthenticated) {
                    Row(
                        modifier = Modifier
                            .background(PinkFiestamas, allRoundedCornerShape24)
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                            .clip(allRoundedCornerShape24)
                            .clickable { onClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextMedium(
                            text = "Ser proveedor",
                            color = Color.White,
                            size = 13.sp,
                            fillMaxWidth = false
                        )
                        HorizontalSpacer(width = 5.dp)
                        Image(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(id = R.drawable.ic_coin_hand),
                            colorFilter = ColorFilter.tint(Color.White),
                            contentDescription = null
                        )
                    }
                }
                if (userDb?.role.getRole() == Role.Client) {
                    HorizontalSpacer(width = 8.dp)
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color.White, CircleShape)
                            .clip(CircleShape)
                            .clickable { onSearch() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(PinkFiestamas),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .width(215.dp)
                .padding(5.dp)
        ) {
            TextSemiBold(
                text = "¡ARMA tu FIESTA!",
                color = "#611745".toColor(),
                size = 14.sp
            )
            TextMedium(
                text = "Encuentra proveedores, organiza\ny personaliza tu celebración",
                color = "#30567a".toColor(),
                size = 11.sp,
                verticalSpace = 11.sp
            )
        }
    }
}

@Composable
fun ViewFooterHome() {
    Column(
        modifier = Modifier.background(Color.White)
    ) {
        val socialMediaURL: MutableState<String?> = remember {
            mutableStateOf(null)
        }
        if (socialMediaURL.value != null) {
            OpenUrl(url = socialMediaURL.value!!)
            socialMediaURL.value = null
        }
        val mList = mapOf(
            R.drawable.ic_instagram to "https://www.instagram.com/fiestamasapp/",
            R.drawable.ic_facebook to "https://www.facebook.com/FiestamasApp",
            R.drawable.ic_pinterest to "https://www.pinterest.com.mx/FiestamasApp/",
            R.drawable.ic_twitch to "https://www.twitch.tv/fiestamas",
            R.drawable.ic_tiktok to "https://www.tiktok.com/@fiestamasapp"
        )
        TextMedium(
            text = stringResource(R.string.main_home_find_us),
            size = 13.sp.autoSize(),
            modifier = Modifier.padding(top = 10.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .sidePadding(50.dp.autoSize())
                .padding(bottom = 30.dp.autoSize(), top = 10.dp.autoSize())
        ) {
            // remove ripple effect
            val interactionSource = remember { MutableInteractionSource() }
            for (i in mList) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp.autoSize())
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            socialMediaURL.value = i.value
                        }
                ) {
                    Image(
                        painter = painterResource(id = i.key),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun RatingStar(
    modifier: Modifier = Modifier,
    rating: Float,
    maxRating: Int,
    starSize: Dp = 18.dp.autoSize(),
    onRatingChanged: (Float) -> Unit
) {
    Row(
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        val currentRating by mutableFloatStateOf(rating)
        repeat(maxRating) { index ->
            val color = if (index < currentRating) PinkFiestamas else Color.LightGray
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .clickable { onRatingChanged(index + 1f) }
                    .size(starSize)
            )
        }
    }
}

@Composable
fun VerticalSpacer(height: Dp) = Spacer(
    modifier = Modifier.height(height.autoSize())
)

@Composable
fun HorizontalSpacer(width: Dp) = Spacer(
    modifier = Modifier.width(width.autoSize())
)

@Composable
fun HorizontalLine(color: Color = Color.Black, thick: Dp = 1.dp) { // ---------------
    Divider(color = color, thickness = thick)
}

@Composable
fun HorizontalLineDecoration() { // ------- o -------
    Row(
        modifier = Modifier.height(38.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(modifier = Modifier.weight(1.35f), color = Color.Black, thickness = 1.dp)
        Text(modifier = Modifier.weight(0.3f), text = "○", textAlign = TextAlign.Center, fontSize = 25.sp)
        Divider(modifier = Modifier.weight(1.35f), color = Color.Black, thickness = 1.dp)
    }
}

@Composable
fun HorizontalProgressView(
    modifier: Modifier = Modifier,
    selectedColor: Color = Color.White,
    unselectedColor: Color? = null,
    totalBars: Int,
    totalSelected: Int
) {
    Row {
        for (i in 0 until totalBars) {
            HorizontalProgressBar(
                modifier = Modifier.weight(1/totalBars.toFloat()),
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                isSelected = i < totalSelected
            )
        }
    }
}

@Composable
fun HorizontalProgressBar(
    modifier: Modifier = Modifier,
    selectedColor: Color = Color.White,
    unselectedColor: Color? = null,
    isSelected: Boolean = false
) {
    Box(
        modifier = modifier
            .height(4.dp)
            .sidePadding(3.dp)
            .background(
                color = if (isSelected) selectedColor else unselectedColor ?: selectedColor.copy(
                    alpha = 0.5f
                ),
                shape = allRoundedCornerShape8
            )
    )
}

@Composable
fun ValidPasswordCharV2(
    text: String,
    isValid: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 3.dp)
    ) {
        val (res, color) = if (isValid) {
            Pair(R.drawable.ic_correct_validation, Color.Gray)
        } else {
            Pair(R.drawable.ic_incorrect_validation, "#F05053".toColor())
        }
        Image(
            painter = painterResource(res),
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
        HorizontalSpacer(width = 7.dp)
        TextRegular(
            text = text,
            color = color,
            fillMaxWidth = false
        )
    }
}

@Composable
fun ClickableHeart(
    modifier: Modifier = Modifier,
    isAlreadyClicked: Boolean,
    onClicked: () -> Unit
) {
    val (currentDrawable, setCurrentDrawable) = remember {
        mutableIntStateOf(R.drawable.ic_heart_stroke)
    }

    LaunchedEffect(isAlreadyClicked) {
        setCurrentDrawable(
            if (isAlreadyClicked) {
                R.drawable.ic_heart_filled
            } else {
                R.drawable.ic_heart_stroke
            }
        )
    }

    val painter = rememberAsyncImagePainter(model = currentDrawable)
    val interactionSource = remember { MutableInteractionSource() }

    Image(
        painter = painter,
        colorFilter = ColorFilter.tint(PinkFiestamas),
        contentDescription = null,
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) { onClicked() }
    )
}

@Composable
fun CarouselPhotosAndVideos (
    mediaList: List<MediaItemService>?,
    photoSelected: (String) -> Unit
) {
    if (mediaList.isNullOrEmpty())  return

    val cHeight = if (mediaList.size > 2) 225.dp.autoSize() else 123.75.dp.autoSize()

    Column(
        modifier = Modifier.height(cHeight)
    ) {
        Row(
            modifier = Modifier.weight(0.55f)
        ) {
            Box(
                modifier = Modifier.weight(0.58f)
            ) {
                CardPhotoCarousel(mediaList[0]) { photoSelected(it.url) }
            }
            HorizontalSpacer(width = 4.dp)
            Box(
                modifier = Modifier.weight(0.42f)
            ) {
                mediaList.getOrNull(1)?.let { mediaItem ->
                    CardPhotoCarousel(mediaItem) { photoSelected(it.url) }
                }
            }
        }
        if (mediaList.size > 2) {
            VerticalSpacer(height = 4.dp)
            Row(
                modifier = Modifier.weight(0.45f)
            ) {
                Box(
                    modifier = Modifier.weight(0.20f)
                ) {
                    mediaList.getOrNull(2)?.let { mediaItem ->
                        CardPhotoCarousel(mediaItem) { photoSelected(it.url) }
                    }
                }
                HorizontalSpacer(width = 4.dp)
                Box(
                    modifier = Modifier.weight(0.50f)
                ) {
                    mediaList.getOrNull(3)?.let { mediaItem ->
                        CardPhotoCarousel(mediaItem) { photoSelected(it.url) }
                    }
                }
                HorizontalSpacer(width = 4.dp)
                Box(
                    modifier = Modifier.weight(0.30f)
                ) {
                    mediaList.getOrNull(4)?.let { mediaItem ->
                        val restOfImages = mediaList.size - 5
                        if (restOfImages > 0) {
                            CardPhotoCarousel(mediaItem, restOfImages.toString()) {
                                photoSelected(it.url)
                            }
                        } else {
                            CardPhotoCarousel(mediaItem) { photoSelected(it.url) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServicePriceAndCapacity(
    service: Service?
) {
    Row {
        val shape = allRoundedCornerShape8
        Row(
            modifier = Modifier
                .weight(0.6f)
                .clip(shape)
                .background(Color.Transparent)
                .border(2.dp, Color.LightGray, shape)
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            TextSemiBold(
                text = "$ ${service?.price}",
                size = 24.sp,
                color = Color.Black,
                fillMaxWidth = false
            )
            HorizontalSpacer(width = 8.dp)
            TextSemiBold(
                size = 11.sp,
                text = stringResource(
                    id = R.string.service_per_unity_with_extra, service?.unit?.toUnitReadable() ?: "unidad"
                ),
                fillMaxWidth = false
            )
        }
        HorizontalSpacer(width = 12.dp)
        Column (modifier = Modifier.weight(0.4f)) {
            TextSemiBold(
                size = 14.sp,
                text = stringResource(id = R.string.service_capacity),
                fillMaxWidth = false,
                includeFontPadding = true
            )
            TextSemiBold(
                size = 20.sp,
                text = "${service?.min_attendees} - ${service?.max_attendees}",
                fillMaxWidth = false,
                includeFontPadding = false
            )
        }
    }
}

@Composable
fun IconSimpleClose(
    modifier: Modifier = Modifier,
    color: Color = Color.DarkGray,
    onClose: () -> Unit
) {
    Image(
        modifier = modifier
            .size(14.dp.autoSize())
            .clickable { onClose() },
        painter = painterResource(id = R.drawable.ic_close),
        colorFilter = ColorFilter.tint(color = color),
        contentDescription = null
    )
}

@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
fun ViewHorizontalMyParty(
    context: CoroutineContext,
    selectedDate: CalendarDay? = null,
    shouldShowArrows: Boolean,
    horizontalList: List<MyPartyEvent?>,
    onItemClicked: (MyPartyEvent) -> Unit,
    onNewPartyClicked: () -> Unit
) {
    val lazyListState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp.autoSize())
                .sidePadding(8.dp.autoSize())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(allRoundedCornerShape14)
                    .background(Color.White)
            ) {
                LazyRow(
                    state = lazyListState,
                    horizontalArrangement = Arrangement.spacedBy(6.dp.autoSize()),
                    contentPadding = PaddingValues(horizontal = 6.dp.autoSize(), vertical = 6.dp.autoSize()),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.7f)
                ) {
                    items(horizontalList) { attr ->
                        CardMyPartyHorizontal(attr) { onItemClicked(it) }
                    }
                }

                selectedDate?.let {
                    lazyListState.scrollToDate(context, horizontalList, it.date.toString())
                }

                val isTablet = isRunningOnTablet()
                val paddingVertical = if (isTablet) 10.dp.autoSize() else 0.dp

                Image(
                    painter = painterResource(id = R.drawable.img_new_party),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = paddingVertical, horizontal = 4.dp.autoSize())
                        .weight(0.3f)
                        .clickable { onNewPartyClicked() }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
                .sidePadding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(0.65f)) {
                if (shouldShowArrows && (lazyListState.firstVisibleItemScrollOffset > 0)) {
                    IconArrow(resource = R.drawable.ic_arrow_prev)
                }
            }
            Row(modifier = Modifier.weight(0.35f)) {
                if (shouldShowArrows && lazyListState.firstVisibleItemIndex == 0) {
                    IconArrow(resource = R.drawable.ic_arrow_next)
                }
            }
        }
    }
}

@Composable
fun IconArrow(resource: Int) {
    Box(
        modifier = Modifier
            .height(25.dp)
            .width(25.dp)
            .background(
                color = Color.Black.copy(alpha = 0.65f),
                shape = CircleShape
            )
    ) {
        Image(
            painter = painterResource(id = resource),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        )
    }
}

@Composable
fun IconArrowWhite(resource: Int) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .background(
                color = Color.White,
                shape = CircleShape
            )
            .clip(CircleShape)
            .border(1.dp, Color.LightGray, CircleShape)
    ) {
        Image(
            painter = painterResource(id = resource),
            contentDescription = null,
            colorFilter = ColorFilter.tint(PinkFiestamas),
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .padding(5.dp)
        )
    }
}

@Composable
fun ViewAddOrderMyParty(
    showMiddleItem: Boolean,
    onAddServicesClicked: () -> Unit,
    onOrderByClicked: () -> Unit,
    onSeeAllEventsClicked: () -> Unit,
    onShowBottomCalendar: () -> Unit,
) {
    VerticalSpacer(height = 10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .sidePadding(10.dp.autoSize()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ButtonPinkAddServices(modifier = Modifier.weight(0.35f)) {
            onAddServicesClicked()
        }
        Row(
            modifier = Modifier.weight(0.65f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                if (showMiddleItem) {
                    ButtonSeeAllEvents(modifier = Modifier.align(Alignment.Center)) {
                        onSeeAllEventsClicked()
                    }
                }
            }
            HorizontalSpacer(width = 12.dp)
            ButtonOrderBy {
                onOrderByClicked()
            }
            HorizontalSpacer(width = 12.dp)
            Image(
                painter = painterResource(id = R.drawable.ic_calendar),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp.autoSize())
                    .clickable { onShowBottomCalendar() }
            )
        }
    }
    VerticalSpacer(height = 10.dp)
}

@Composable
fun ViewVerticalMyParty(
    horizontalList: List<MyPartyEvent?>?,
    verticalList: List<MyPartyService?>,
    backgroundColor: Color,
    onItemClicked: (MyPartyService) -> Unit
) {
    val corners = topRoundedCornerShape10
    val padding = 8.dp.autoSize()

    if (verticalList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .sidePadding(padding)
                .clip(corners)
                .background(backgroundColor)
        ) {
            val text = if (horizontalList.isNullOrEmpty()) stringResource(id = R.string.mifiesta_no_events)
            else stringResource(id = R.string.mifiesta_no_services_by_events)

            TextMedium(
                modifier = Modifier
                    .align(Alignment.Center)
                    .sidePadding(15.dp.autoSize()),
                text = text,
                verticalSpace = 20.sp.autoSize()
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .sidePadding(padding)
            .clip(corners)
            .background(backgroundColor),
        verticalArrangement = Arrangement.spacedBy(8.dp.autoSize()),
        contentPadding = PaddingValues(horizontal = 6.dp.autoSize(), vertical = 6.dp.autoSize())
    ) {
        items(verticalList) {
            CardMyPartyVertical(item = it) { item ->
                onItemClicked(item)
            }
        }
    }
}

@Composable
fun RequireLoginScreenView(onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextMedium(
                text = "Inicia sesión para continuar",
                size = 16.sp.autoSize()
            )
            VerticalSpacer(height = 10.dp)
            Button(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(40.dp.autoSize()),
                onClick = { onClick() }
            ) {
                TextMedium(
                    text = "Iniciar sesión",
                    color = Color.White,
                    size = 16.sp.autoSize(),
                    fillMaxWidth = false
                )
            }
        }
    }
}

@Composable
fun TitleWithTopLine(
    text: String,
    icon: Int,
    iconSize: Dp = 20.dp,
    textColor: Color = PinkFiestamas,
    textSize: TextUnit = 18.sp,
    lineColor: Color = Color.Gray,
    lineThick: Dp = 1.dp
) {
    HorizontalLine(color = lineColor, thick = lineThick)
    VerticalSpacer(height = 5.dp)
    Row {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Gray),
            modifier = Modifier.height(iconSize.autoSize())
        )
        HorizontalSpacer(width = 10.dp)
        TextSemiBold(
            text = text,
            color = textColor,
            size = textSize.autoSize(),
            align = TextAlign.Start,
            includeFontPadding = false
        )
    }
    VerticalSpacer(height = 10.dp)
}


/*@Suppress("unused")
@Composable
private fun HorizontalBouncingIcon(infiniteTransition: InfiniteTransition) {
    val startColor = Color.White
    val endColor = PinkFiestamas

    val animatedColor by infiniteTransition.animateColor(
        initialValue = startColor,
        targetValue = endColor,
        animationSpec = infiniteRepeatable(
            tween(800, easing = FastOutLinearInEasing),
            RepeatMode.Reverse,
        )
    )

    val position by infiniteTransition.animateFloat(
        initialValue = -60f,
        targetValue = -50f,
        animationSpec = infiniteRepeatable(
            repeatMode = RepeatMode.Reverse,
            animation = tween(
                800,
                easing = FastOutLinearInEasing
            )
        )
    )

    Icon(
        imageVector = Icons.Default.ArrowCircleRight,
        contentDescription = null,
        tint = animatedColor,
        modifier = Modifier
            .size(30.dp)
            .offset(x = position.dp)
    )
}*/

@Suppress("DEPRECATION")
@SuppressLint("OpaqueUnitKey")
@Composable
fun MiniVideoPlayer(
    url: String,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    val context = LocalContext.current
    var volumeIcon by remember { mutableIntStateOf(R.drawable.ic_volume_off) }
    val i = MediaItem.fromUri(url)

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(i)
            prepare()
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
            volume = 0f
        }
    }

    Box(modifier = modifier.clickable { onClick(url) }) {
        DisposableEffect(
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = false
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                }
            )
        ) {
            onDispose {
                exoPlayer.release()
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(25.dp)
                    .background(color = Color.Black, shape = CircleShape)
                    .clickable {
                        exoPlayer.volume = if (exoPlayer.volume == 0f) 0.8f else 0f
                        volumeIcon =
                            if (volumeIcon == R.drawable.ic_volume_off) R.drawable.ic_volume_on else R.drawable.ic_volume_off
                    }
            ) {
                Image(
                    painter = painterResource(id = volumeIcon),
                    colorFilter = ColorFilter.tint(color = Color.White),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                )
            }
        }
    }
}

@Composable
fun CircleAvatarWithInitials(
    name: String,
    circleSize: Dp,
    backgroundColor: Color,
    textSize: TextUnit = 14.sp,
    textColor: Color = Color.Black,
    isBold: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(circleSize)
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(5.dp.autoSize()),
        contentAlignment = Alignment.Center
    ) {
        if (name.isNotEmpty()) {
            val initials = extractInitials(name)
            TextInCircle(initials, textSize.autoSize(), textColor, isBold)
        }
    }
}

@Composable
fun TextInCircle(
    text: String,
    textSize: TextUnit,
    textColor: Color,
    isBold: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isBold) {
            TextSemiBold(
                modifier = Modifier.align(Alignment.Center),
                text = text,
                size = textSize,
                color = textColor
            )
        } else {
            TextRegular(
                modifier = Modifier.align(Alignment.Center),
                text = text,
                size = textSize,
                color = textColor
            )
        }
    }
}

fun extractInitials(name: String): String {
    val names = name.split(" ")
    return when (names.size) {
        0 -> ""
        1 -> names[0].take(2).uppercase()
        else -> names[0].take(1).uppercase() + names[1].take(1).uppercase()
    }
}

@Composable
fun TitleTopDescriptionBottomTextV2(
    contentTop: @Composable () -> Unit,
    contentBottom: @Composable () -> Unit,
) {
    Column {
        contentTop()
        contentBottom()
    }
}

@Composable
fun ImageLeftTextRightV2(
    imageLeft: @Composable () -> Unit,
    contentText: @Composable () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        imageLeft()
        HorizontalSpacer(3.dp)
        contentText()
    }
}

@Composable
fun BottomShadow() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Black.copy(alpha = 0.1f),
                    Color.Transparent,
                )
            )
        )
    )
}
