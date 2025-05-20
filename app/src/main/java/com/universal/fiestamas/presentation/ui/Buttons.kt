package com.universal.fiestamas.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape14
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape16
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape20
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape24
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape30
import com.universal.fiestamas.presentation.utils.Constants.BUTTON_ANIMATION_DURATION
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import kotlinx.coroutines.delay

@Composable
fun ButtonSocialMediaWhiteRoundedCorners(
    iconDrawable: Int,
    text: String,
    onGoogleIconClicked: () -> Unit
) {
    val shape = allRoundedCornerShape30
    Column(
        modifier = Modifier.height(46.dp.autoSize())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()
                .clip(shape)
                .background(Color.Transparent)
                .border(1.dp, Color.White, shape)
                .sidePadding(35.dp)
                .clickable { onGoogleIconClicked() }
        ) {
            Image(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(25.dp.autoSize()),
                painter = painterResource(id = iconDrawable),
                contentDescription = null
            )
            HorizontalSpacer(width = 20.dp)
            TextMedium(
                text = text,
                color = Color.White,
                fillMaxWidth = false,
                size = 16.sp.autoSize()
            )
        }
    }
}

@Composable
fun ButtonSocialMediaWhiteRoundedCornersV1(
    modifier: Modifier,
    iconDrawable: Int,
    onGoogleIconClicked: () -> Unit
) {
    val shape = allRoundedCornerShape30
    Column(
        modifier = modifier
            .wrapContentWidth()
            .height(65.dp.autoSize())
            .shadow(
                elevation = 7.dp,
                shape = shape,
                clip = true
            )
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .clip(shape)
                .background(Color.White)
                .border(0.3.dp, Color.LightGray, shape)
                .clickable { onGoogleIconClicked() }
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(32.dp.autoSize())
                        .sidePadding(30.dp.autoSize()),
                    painter = painterResource(id = iconDrawable),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun ButtonPinkRoundedCorners(
    modifier: Modifier = Modifier,
    isBigButton: Boolean = false,
    text: String,
    onItemClick: () -> Unit
) {
    val (isSelected, setIsSelected) = remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .wrapContentWidth()
            .height(if (isBigButton) 65.dp.autoSize() else 52.dp.autoSize())
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .clip(allRoundedCornerShape30)
                .background(if (isSelected) Color.Black else PinkFiestamas)
                .clickable { setIsSelected(true) }
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                TextBold(
                    text = text,
                    size = 18.sp.autoSize(),
                    color = Color.White,
                    fillMaxWidth = false,
                    modifier = Modifier.sidePadding(if (isBigButton) 45.dp.autoSize() else 55.dp.autoSize())
                )
            }
        }
    }
    LaunchedEffect(isSelected) {
        if (isSelected) {
            delay(BUTTON_ANIMATION_DURATION)
            onItemClick()
            setIsSelected(false)
        }
    }
}

@Composable
fun ButtonPinkRoundedCornersWithImage(
    modifier: Modifier = Modifier,
    icon: Int?,
    text: String,
    onItemClick: () -> Unit
) {
    val (isSelected, setIsSelected) = remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .clip(allRoundedCornerShape14)
            .background(if (isSelected) Color.Black else PinkFiestamas)
            .padding(6.dp)
            .clickable { setIsSelected(true) },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = Color.White),
                modifier = Modifier.size(16.dp)
            )
            
            HorizontalSpacer(width = 5.dp)
        }

        TextMedium(
            text = text,
            size = 13.sp,
            color = Color.White,
            maxLines = 1,
            fillMaxWidth = false
        )
    }

    LaunchedEffect(isSelected) {
        if (isSelected) {
            delay(BUTTON_ANIMATION_DURATION)
            onItemClick()
            setIsSelected(false)
        }
    }
}

@Composable
fun ButtonWhiteRoundedCornersPinkText(
    isSelected: Boolean,
    text: String,
    modifier: Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .clip(allRoundedCornerShape16)
            .border(0.1.dp, Color.LightGray, allRoundedCornerShape16)
            .background(if (isSelected) PinkFiestamas else Color.White)
    ) {
        TextBold(
            text = text,
            size = 16.sp,
            color = if (isSelected) Color.White else PinkFiestamas,
            verticalSpace = 18.sp,
            horizontalSpace = (0).sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp)
        )
    }
    LaunchedEffect(isSelected) {
        if (isSelected) delay(BUTTON_ANIMATION_DURATION)
    }
}

@Composable
fun ButtonPinkAddServices(
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(allRoundedCornerShape24)
            .border(1.dp.autoSize(), PinkFiestamas, allRoundedCornerShape24)
            .background(Color.White)
            .clickable { onItemClick() }
    ) {
        TextSemiBold(
            text = "Agregar Servicios",
            size = 12.sp.autoSize(),
            color = PinkFiestamas,
            fillMaxWidth = false,
            modifier = Modifier
                .padding(vertical = 6.dp.autoSize())
                .padding(start = 4.dp.autoSize(), end = 3.dp.autoSize())
        )
        Image(
            painter = painterResource(id = R.drawable.ic_add_green),
            contentDescription = null,
            modifier = Modifier
                .size(25.dp.autoSize())
                .padding(end = 2.dp.autoSize())
        )
    }
}

@Composable
fun ButtonSeeAllEvents(
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(allRoundedCornerShape24)
            .clickable { onItemClick() }
    ) {
        TextMedium(
            text = "Ver todos\nmis eventos",
            size = 12.sp.autoSize(),
            color = PinkFiestamas,
            verticalSpace = 11.sp.autoSize(),
            horizontalSpace = (0).sp,
            fillMaxWidth = false,
            modifier = Modifier
                .padding(vertical = 8.dp.autoSize())
                .padding(horizontal = 5.dp.autoSize())
        )
    }
}

@Composable
fun ButtonOrderBy(
    modifier: Modifier = Modifier,
    text: String = stringResource(id = R.string.service_order_by),
    onItemClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = modifier
            .wrapContentHeight()
            .clickable { onItemClick() }
    ) {
        TextMedium(
            text = text,
            fillMaxWidth = false,
            size = 14.sp.autoSize(),
            horizontalSpace = (-1).sp
        )
        HorizontalSpacer(width = 8.dp)
        Image(
            contentDescription = null,
            painter = painterResource(id = R.drawable.ic_arrow_down_filled),
            colorFilter = ColorFilter.tint(color = PinkFiestamas),
            modifier = Modifier.size(16.dp.autoSize())
        )
    }
}

@Composable
fun ButtonAddMediaFile(
    text: String = "Agregar",
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .sidePadding(24.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_add_fiestamas),
            contentDescription = null,
            modifier = Modifier.size(40.dp.autoSize())
        )
        TextMedium(
            text = text,
            size = 12.sp.autoSize(),
            color = Color.Gray,
            fillMaxWidth = false
        )
    }
}

@Composable
fun ButtonWhiteRoundedCorners(
    modifier: Modifier = Modifier,
    iconSize: Dp = 30.dp.autoSize(),
    verticalPadding: Dp = 0.dp.autoSize(),
    horizontalSpacer: Dp = 0.dp.autoSize(),
    text: String,
    icon: Int = R.drawable.ic_phone_calling,
    onItemClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(allRoundedCornerShape24)
            .border(1.dp.autoSize(), Color.Gray, allRoundedCornerShape24)
            .background(Color.White)
            .padding(vertical = verticalPadding)
            .sidePadding(8.dp.autoSize())
            .clickable { onItemClick() }
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(iconSize)
        )
        HorizontalSpacer(width = horizontalSpacer)
        TextSemiBold(
            text = text,
            size = 12.sp.autoSize(),
            color = PinkFiestamas,
            fillMaxWidth = false,
            maxLines = 1,
            addThreeDots = true
        )
    }
}

@Composable
fun ButtonStatusService(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
    onItemClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(allRoundedCornerShape12)
            .background(color = color, shape = allRoundedCornerShape12)
            .clickable { onItemClick() }
    ) {
        TextMedium(
            text = text,
            size = 13.sp.autoSize(),
            fillMaxWidth = false,
            horizontalSpace = (-1).sp,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun ButtonPinkRoundedCornersV2(
    content: @Composable () -> Unit,
    backgroundColor: Color = PinkFiestamas,
    shape: RoundedCornerShape = allRoundedCornerShape20,
    verticalPadding: Dp = 6.dp,
    horizontalPadding: Dp = 12.dp,
    addBorder: Boolean = false,
    borderColor: Color? = null,
    onClick: () -> Unit
) {
    val modifier = if (addBorder) {
        Modifier.border(1.5.dp, borderColor ?: Color.White, shape)
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .background(color = backgroundColor, shape = shape)
            .padding(
                vertical = verticalPadding.autoSize(),
                horizontal = horizontalPadding.autoSize()
            )
            .clip(shape)
            .clickable { onClick() }
    ) {
        content()
    }
}


@Composable
fun CircleButtonPinkV2(
    icon: Int,
    size: Dp = 35.dp,
    backgroundColor: Color = PinkFiestamas,
    iconColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box (
        modifier = Modifier
            .size(size.autoSize())
            .clip(CircleShape)
            .background(backgroundColor, CircleShape)
            .padding(10.dp.autoSize())
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = iconColor),
        )
    }
}

@Composable
fun ButtonAlphaBackgroundV2(
    icon: Int,
    size: Dp = 35.dp,
    alpha: Float = 0.3f,
    iconColor: Color = PinkFiestamas,
    backgroundColor: Color = PinkFiestamas,
    shape: RoundedCornerShape = allRoundedCornerShape10,
    onClick: (() -> Unit)? = null
) {
    Box (
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor.copy(alpha = alpha), shape)
            .padding(8.dp)
            .clickable { onClick?.invoke() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = iconColor),
        )
    }
}

@Composable
fun FloatingButtonV2(
    modifier: Modifier = Modifier,
    backgroundColor: Color = PinkFiestamas,
    contentColor: Color = Color.White,
    iconVector: ImageVector? = null,
    icon: Int? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        FloatingActionButton(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            onClick = onClick,
        ) {
            iconVector?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp.autoSize())
                )
            }
            icon?.let {
                Image(
                    modifier = Modifier.size(30.dp.autoSize()),
                    painter = painterResource(it),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = contentColor)
                )
            }
        }
    }
}
