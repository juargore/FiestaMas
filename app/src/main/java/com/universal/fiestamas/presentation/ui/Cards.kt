package com.universal.fiestamas.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.LoginAccount
import com.universal.fiestamas.domain.models.MediaItemService
import com.universal.fiestamas.domain.models.PlaceAutocomplete
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.domain.models.ServiceType
import com.universal.fiestamas.domain.models.SubService
import com.universal.fiestamas.domain.models.UriFile
import com.universal.fiestamas.domain.models.response.FirebaseUserDb
import com.universal.fiestamas.presentation.theme.GreenFiestaki
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape20
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape24
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape30
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape5
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape7
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape8
import com.universal.fiestamas.presentation.theme.bottomRoundedCornerShape5
import com.universal.fiestamas.presentation.theme.topRoundedCornerShape5
import com.universal.fiestamas.presentation.utils.Constants
import com.universal.fiestamas.presentation.utils.Constants.BUTTON_ANIMATION_DURATION
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.isRunningOnTablet
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import kotlinx.coroutines.delay

@Composable
fun CardGooglePlace(
    item: PlaceAutocomplete,
    onAddressSelected: (PlaceAutocomplete) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAddressSelected(item) }
            .background(
                color = Color.Black.copy(alpha = 0.05f),
                shape = allRoundedCornerShape12
            )
            .padding(vertical = 10.dp.autoSize(), horizontal = 16.dp.autoSize())
    ) {
        Text(
            text = item.primaryText,
            fontSize = 16.sp.autoSize()
        )
        Text(text = item.secondaryText, fontSize = 12.sp.autoSize())
    }
}

@Composable
fun CardLoginAutoComplete(
    account: LoginAccount,
    onItemClick: (LoginAccount) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(account) }
    ) {
        VerticalSpacer(height = 7.dp.autoSize())
        Text(
            text = account.email,
            fontSize = 15.sp.autoSize(),
            color = Color.DarkGray
        )
        /*Text(
            text = "${account.password[0]}*******${account.password[account.password.length-1]}",
            fontSize = 13.sp,
            color = Color.Gray
        )*/
        VerticalSpacer(height = 6.dp.autoSize())
    }
}

@Composable
fun CardGeneralServiceV2(
    image: String,
    text: String,
    onItemClick: () -> Unit
) {

    val shape = allRoundedCornerShape24
    val isTablet = isRunningOnTablet()
    val screenHeight = LocalConfiguration.current.screenHeightDp

    Box(
        modifier = Modifier
            .height(if (isTablet) ((screenHeight / 4) - 5).dp else 165.dp)
            .fillMaxWidth()
            .clip(shape)
    ) {
        Image(
            painter = rememberAsyncImagePainter(image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationY = 60f
                }
                .shadow(8.dp, shape)
                .clip(shape)
                .clickable { onItemClick() }
        )
        Row(
            modifier = Modifier
                .sidePadding(10.dp)
                .padding(bottom = 5.dp)
                .background(Color.White, allRoundedCornerShape20)
                .align(Alignment.BottomCenter)
        ) {
            TextSemiBold(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .sidePadding(6.dp),
                text = text.uppercase(),
                horizontalSpace = (0).sp,
                verticalSpace = 12.sp,
                maxLines = 2,
                addThreeDots = true,
                size = 10.sp
            )
        }
    }
}

@Composable
fun CardServiceCategory(
    item: ServiceCategory?,
    index: Int,
    onItemClick: (ServiceCategory) -> Unit
) {
    if (item == null) return

    val (isSelected, setIsSelected) = remember { mutableStateOf(false) }
    val shape = allRoundedCornerShape7
    val columnCount = Constants.THREE_COLUMNS
    val padding = when (index % columnCount) {
        0 -> Modifier.padding(end = 5.dp) // start
        columnCount - 1 -> Modifier.padding(start = 5.dp) // end
        else -> Modifier.padding(start = 2.5.dp, end = 2.5.dp) // middle
    }

    val isTablet = isRunningOnTablet()
    val screenHeight = LocalConfiguration.current.screenHeightDp

    Column(
        modifier = padding
            .fillMaxSize()
            .padding(vertical = 4.dp)
            .shadow(
                elevation = 5.dp,
                shape = shape,
                clip = true
            )
    ) {
        Column(
            modifier = Modifier
                .height(if (isTablet) ((screenHeight / 4) - 5).dp else 142.dp)
                .fillMaxWidth()
                .clip(shape)
                .background(if (isSelected) GreenFiestaki else Color.White)
                .clickable {
                    setIsSelected(true)
                    onItemClick(item)
                },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(item.icon),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
    LaunchedEffect(isSelected) {
        if (isSelected) {
            delay(BUTTON_ANIMATION_DURATION)
            setIsSelected(false)
        }
    }
}

@Composable
fun CardServiceType(
    item: ServiceType?,
    index: Int,
    onItemClick: (ServiceType) -> Unit
) {
    if (item == null) return
    val (isSelected, setIsSelected) = remember { mutableStateOf(false) }
    val shape = allRoundedCornerShape7
    val columnCount = Constants.THREE_COLUMNS
    val padding = when (index % columnCount) {
        0 -> Modifier.padding(end = 5.dp) // start
        columnCount - 1 -> Modifier.padding(start = 5.dp) // end
        else -> Modifier.padding(start = 2.5.dp, end = 2.5.dp) // middle
    }

    val isTablet = isRunningOnTablet()
    val screenHeight = LocalConfiguration.current.screenHeightDp

    Column(
        modifier = padding
            .fillMaxSize()
            .padding(vertical = 4.dp)
            .background(color = if (isSelected) PinkFiestamas else GreenFiestaki, shape = shape)
    ) {
        Column(
            modifier = Modifier
                .height(if (isTablet) ((screenHeight / 4) - 5).dp else 142.dp)
                .fillMaxWidth()
                .clip(shape)
                .background(Color.White)
                .clickable {
                    setIsSelected(true)
                    onItemClick(item)
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ///*
            Image(
                painter = rememberAsyncImagePainter(item.icon),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
    LaunchedEffect(isSelected) {
        if (isSelected) {
            delay(BUTTON_ANIMATION_DURATION)
            setIsSelected(false)
        }
    }
}

@Composable
fun CardSubService(
    item: SubService?,
    index: Int,
    onItemClick: (SubService) -> Unit
) {
    if (item == null) return
    val (isSelected, setIsSelected) = remember { mutableStateOf(false) }
    val shape = allRoundedCornerShape7
    val columnCount = Constants.THREE_COLUMNS
    val padding = when (index % columnCount) {
        0 -> Modifier.padding(end = 5.dp) // start
        columnCount - 1 -> Modifier.padding(start = 5.dp) // end
        else -> Modifier.padding(start = 2.5.dp, end = 2.5.dp) // middle
    }

    val isTablet = isRunningOnTablet()
    val screenHeight = LocalConfiguration.current.screenHeightDp

    Column(
        modifier = padding
            .fillMaxSize()
            .padding(vertical = 4.dp)
            .background(color = if (isSelected) Color.Black else PinkFiestamas, shape = shape)
    ) {
        Column(
            modifier = Modifier
                .height(if (isTablet) ((screenHeight / 4) - 5).dp else 142.dp)
                .fillMaxWidth()
                .clip(shape)
                .background(Color.White)
                .clickable {
                    setIsSelected(true)
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(item.icon),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
    LaunchedEffect(isSelected) {
        if (isSelected) {
            delay(BUTTON_ANIMATION_DURATION)
            setIsSelected(false)
            onItemClick(item)
        }
    }
}

@Composable
fun CardServiceOption(
    service: Service?,
    user: FirebaseUserDb?,
    index: Int,
    isFavourite: Boolean? = null,
    onItemClick: (Service) -> Unit,
    onHeartClick: (Service) -> Unit
) {
    if (service == null) return

    val shape = allRoundedCornerShape5
    val shapeTop = topRoundedCornerShape5
    val shapeBottom = bottomRoundedCornerShape5
    val padding = if (index % 2 != 0) {
        Modifier.padding(start = 5.dp, end = 10.dp)
    } else {
        Modifier.padding(end = 5.dp, start = 10.dp)
    }

    val isTablet = isRunningOnTablet()
    val screenHeight = LocalConfiguration.current.screenHeightDp

    Column(
        modifier = padding
            .fillMaxWidth()
            .height(if (isTablet) ((screenHeight / 3)).dp else 210.dp)
            .padding(vertical = 5.dp)
            .shadow(
                elevation = 4.dp,
                shape = shape,
                clip = true
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shapeTop)
                .weight(0.55f)
                .background(Color.White)
                .clickable { onItemClick(service) }
        ) {
            Image(
                painter = rememberAsyncImagePainter(service.image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(shapeBottom)
                .weight(0.45f)
                .background(Color.White)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
            ) {
                TextMedium(
                    text = service.name,
                    align = TextAlign.Start,
                    size = 17.sp.autoSize(),
                    verticalSpace = 15.sp.autoSize(),
                    includeFontPadding = false,
                    maxLines = 2,
                    minLines = 1,
                    addThreeDots = true,
                    modifier = Modifier.weight(0.85f)
                )
                val isClicked = isFavourite
                    ?: if (user?.likes.isNullOrEmpty()) {
                        false
                    } else {
                        user?.likes?.contains(service.id) == true
                    }

                ClickableHeart(
                    isAlreadyClicked = isClicked,
                    modifier = Modifier
                        .weight(0.15f)
                        .padding(bottom = 7.dp, top = 3.dp)
                        .sidePadding(2.dp)
                ) {
                    onHeartClick(service)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
            ) {
                RatingStar(
                    modifier = Modifier
                        .weight(0.85f)
                        .align(Alignment.CenterVertically),
                    rating = service.rating.toFloat(), // currentRating,
                    maxRating = service.rating, // maxRating,
                    onRatingChanged = { // newRating ->
                        // currentRating = newRating
                    }
                )
            }
        }
    }
}

@Composable
fun CardServiceOptionForHorizontalList(
    service: Service?,
    user: FirebaseUserDb?,
    isFavourite: Boolean? = null,
    onItemClick: (Service) -> Unit,
    onHeartClick: (Service) -> Unit
) {
    if (service == null) return

    val shape = allRoundedCornerShape5
    val shapeTop = topRoundedCornerShape5
    val shapeBottom = bottomRoundedCornerShape5
    val padding = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)

    val isTablet = isRunningOnTablet()
    val screenHeight = LocalConfiguration.current.screenHeightDp

    Column(
        modifier = padding
            .fillMaxWidth()
            .height(if (isTablet) ((screenHeight / 3)).dp else 230.dp)
            .padding(vertical = 5.dp)
            .shadow(
                elevation = 4.dp,
                shape = shape,
                clip = true
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shapeTop)
                .weight(0.55f)
                .background(Color.White)
                .clickable { onItemClick(service) }
        ) {
            Image(
                painter = rememberAsyncImagePainter(service.image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(shapeBottom)
                .weight(0.45f)
                .background(Color.White)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
            ) {
                TextMedium(
                    text = service.name,
                    align = TextAlign.Start,
                    size = 17.sp.autoSize(),
                    verticalSpace = 15.sp.autoSize(),
                    includeFontPadding = false,
                    maxLines = 2,
                    minLines = 1,
                    addThreeDots = true,
                    modifier = Modifier.weight(0.8f)
                )
                Box(
                    modifier = Modifier.weight(0.2f),
                    contentAlignment = Alignment.Center
                ) {
                    val isClicked = isFavourite
                        ?: if (user?.likes.isNullOrEmpty()) {
                            false
                        } else {
                            user?.likes?.contains(service.id) == true
                        }

                    ClickableHeart(
                        isAlreadyClicked = isClicked,
                        modifier = Modifier.size(20.dp),
                        onClicked = { onHeartClick(service) }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
            ) {
                RatingStar(
                    modifier = Modifier
                        .weight(0.85f)
                        .align(Alignment.CenterVertically),
                    rating = service.rating.toFloat(), // currentRating,
                    maxRating = service.rating, // maxRating,
                    onRatingChanged = { // newRating ->
                        // currentRating = newRating
                    }
                )
            }
        }
    }
}

@Composable
fun CardPhotoCarousel(
    item: MediaItemService?,
    pendingPhotos: String? = null,
    onItemClick: (MediaItemService) -> Unit
) {
    if (item == null) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(allRoundedCornerShape5)
            .shadow(
                shape = allRoundedCornerShape5,
                elevation = 4.dp,
                clip = true
            )
            .padding(2.dp.autoSize())
            .clickable {
                onItemClick(item)
            }
    ) {
        val imagePainter = if (item.isVideo) {
            rememberAsyncImagePainter(R.drawable.img_blurred_background)
        } else {
            rememberAsyncImagePainter(item.url)
        }
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .clip(allRoundedCornerShape5),
            contentScale = ContentScale.Crop
        )

        if (item.isVideo) {
            Image(
                painter = painterResource(id = R.drawable.ic_play_circle),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = Color.White),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp.autoSize())
            )
        }

        pendingPhotos?.let {
            Box (
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f))
                    .fillMaxSize()
            ) {
                Text(
                    text = "+$pendingPhotos",
                    color = Color.White,
                    fontSize = 20.sp.autoSize(),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun CardPhotoOrVideo(
    modifier: Modifier = Modifier,
    uriFile: UriFile?,
    isVideo: Boolean = false,
    addShadow: Boolean = true,
    showDeleteOption: Boolean = true,
    onItemClick: (UriFile) -> Unit
) {
    if (uriFile == null) return

    val shape = allRoundedCornerShape8

    val mModifier = if (addShadow) {
        Modifier.shadow(
            elevation = 4.dp,
            shape = shape,
            clip = true
        )
    } else Modifier

    Box(
        modifier = mModifier.then(modifier)
            .width(100.dp.autoSize())
            .height(120.dp.autoSize())
    ) {
        if (isVideo) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.ic_video_movie),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        } else {
            AsyncImage(
                model = uriFile.uri,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        }
        if (showDeleteOption) {
            Row (
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(vertical = 6.dp.autoSize())
                    .clickable { onItemClick(uriFile) },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextRegular(text = "Eliminar", fillMaxWidth = false, size = 12.sp.autoSize())
                HorizontalSpacer(width = 4.dp)
                Image(
                    painter = painterResource(id = R.drawable.ic_trash_can_no_background),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp.autoSize())
                )
            }
        }
    }
}

@Composable
fun CardPhotoV2(
    modifier: Modifier = Modifier,
    uriFile: UriFile?,
    onDelete: () -> Unit
) {
    if (uriFile == null) return

    val shape = allRoundedCornerShape30

    Box(
        modifier = modifier
            //.fillMaxWidth()
            .height(130.dp.autoSize())
            .shadow(
                elevation = 4.dp,
                shape = shape,
                clip = true
            )
    ) {
        AsyncImage(
            model = uriFile.url ?: uriFile.uri,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
        ) {
            Box (
                modifier = Modifier
                    .size(35.dp)
                    .background(Color.White, CircleShape)
                    .clip(CircleShape)
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(R.drawable.ic_trash_can_filled),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(PinkFiestamas),
                )
            }
        }
    }
}
