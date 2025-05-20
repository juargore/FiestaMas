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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.home.main.MainParentClass
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape30
import com.universal.fiestamas.presentation.theme.sansBold
import com.universal.fiestamas.presentation.theme.sansExtraBold
import com.universal.fiestamas.presentation.theme.sansLight
import com.universal.fiestamas.presentation.theme.sansMedium
import com.universal.fiestamas.presentation.theme.sansRegular
import com.universal.fiestamas.presentation.theme.sansSemiBold
import com.universal.fiestamas.presentation.utils.OpenUrl
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.letterSpacing
import com.universal.fiestamas.presentation.utils.extensions.sidePadding

@Suppress("unused")
@Composable
fun TextLight(
    modifier: Modifier = Modifier,
    text: String,
    size: TextUnit = 16.sp,
    color: Color = Color.Black,
    align: TextAlign = TextAlign.Center,
    horizontalSpace: TextUnit = 0.sp,
    verticalSpace: TextUnit = 15.sp,
    isBold: Boolean = false,
    includeFontPadding: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    addThreeDots: Boolean = false,
    fillMaxWidth: Boolean = true,
    shadowColor: Color? = null
) {
    TextFiestaki(
        modifier, sansLight, text, size, color, align, horizontalSpace, verticalSpace,
        isBold, includeFontPadding, maxLines, minLines, addThreeDots, fillMaxWidth, shadowColor
    )
}

@Composable
fun TextRegular(
    modifier: Modifier = Modifier,
    text: String,
    size: TextUnit = 16.sp,
    color: Color = Color.Black,
    align: TextAlign = TextAlign.Center,
    horizontalSpace: TextUnit = 0.sp,
    verticalSpace: TextUnit = 15.sp,
    isBold: Boolean = false,
    includeFontPadding: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    addThreeDots: Boolean = false,
    fillMaxWidth: Boolean = true,
    shadowColor: Color? = null
) {
    TextFiestaki(
        modifier, sansRegular, text, size, color, align, horizontalSpace, verticalSpace,
        isBold, includeFontPadding, maxLines, minLines, addThreeDots, fillMaxWidth, shadowColor
    )
}

@Composable
fun TextMedium(
    modifier: Modifier = Modifier,
    text: String,
    size: TextUnit = 16.sp,
    color: Color = Color.Black,
    align: TextAlign = TextAlign.Center,
    horizontalSpace: TextUnit = 0.sp,
    verticalSpace: TextUnit = 15.sp,
    isBold: Boolean = false,
    includeFontPadding: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    addThreeDots: Boolean = false,
    fillMaxWidth: Boolean = true,
    shadowColor: Color? = null
) {
    TextFiestaki(
        modifier, sansMedium, text, size, color, align, horizontalSpace, verticalSpace,
        isBold, includeFontPadding, maxLines, minLines, addThreeDots, fillMaxWidth, shadowColor
    )
}

@Composable
fun TextSemiBold(
    modifier: Modifier = Modifier,
    text: String,
    size: TextUnit = 16.sp,
    color: Color = Color.Black,
    align: TextAlign = TextAlign.Center,
    horizontalSpace: TextUnit = 0.sp,
    verticalSpace: TextUnit = 15.sp,
    isBold: Boolean = false,
    includeFontPadding: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    addThreeDots: Boolean = false,
    fillMaxWidth: Boolean = true,
    shadowColor: Color? = null
) {
    TextFiestaki(
        modifier, sansSemiBold, text, size, color, align, horizontalSpace, verticalSpace,
        isBold, includeFontPadding, maxLines, minLines, addThreeDots, fillMaxWidth, shadowColor
    )
}

@Composable
fun TextBold(
    modifier: Modifier = Modifier,
    text: String,
    size: TextUnit = 16.sp,
    color: Color = Color.Black,
    align: TextAlign = TextAlign.Center,
    horizontalSpace: TextUnit = 0.sp,
    verticalSpace: TextUnit = 15.sp,
    isBold: Boolean = false,
    includeFontPadding: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    addThreeDots: Boolean = false,
    fillMaxWidth: Boolean = true,
    shadowColor: Color? = null
) {
    TextFiestaki(
        modifier, sansBold, text, size, color, align, horizontalSpace, verticalSpace,
        isBold, includeFontPadding, maxLines, minLines, addThreeDots, fillMaxWidth, shadowColor
    )
}

@Composable
fun TextExtraBold(
    modifier: Modifier = Modifier,
    text: String,
    size: TextUnit = 16.sp,
    color: Color = Color.Black,
    align: TextAlign = TextAlign.Center,
    horizontalSpace: TextUnit = 0.sp,
    verticalSpace: TextUnit = 15.sp,
    isBold: Boolean = false,
    includeFontPadding: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    addThreeDots: Boolean = false,
    fillMaxWidth: Boolean = true,
    shadowColor: Color? = null
) {
    TextFiestaki(
        modifier, sansExtraBold, text, size, color, align, horizontalSpace, verticalSpace,
        isBold, includeFontPadding, maxLines, minLines, addThreeDots, fillMaxWidth, shadowColor
    )
}

@Composable
fun TextFiestaki(
    modifier: Modifier,
    fontFamily: FontFamily,
    text: String,
    size: TextUnit,
    color: Color,
    align: TextAlign,
    horizontalSpace: TextUnit,
    verticalSpace: TextUnit,
    isBold: Boolean,
    includeFontPadding: Boolean,
    maxLines: Int,
    minLines: Int,
    addThreeDots: Boolean,
    fillMaxWidth: Boolean,
    shadowColor: Color?
) {
    val width = if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier.wrapContentWidth()
    val shadow = if (shadowColor != null) Shadow(
        color = shadowColor,
        offset = Offset(2f, 2f),
        blurRadius = 2f
    ) else null

    Text(
        modifier = modifier.then(width),
        text = text,
        fontSize = size,
        color = color,
        textAlign = align,
        fontFamily = fontFamily,
        maxLines = maxLines,
        minLines = minLines,
        style = TextStyle(
            letterSpacing = horizontalSpace,
            platformStyle = PlatformTextStyle(includeFontPadding = includeFontPadding),
            shadow = shadow
        ),
        lineHeight = verticalSpace,
        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
        overflow = if (addThreeDots) TextOverflow.Ellipsis else TextOverflow.Visible
    )
}

@Composable
fun TermsAndConditionsClickable(
    firstLineStr: Int,
    centerText: Boolean
) {
    val link: MutableState<String?> = remember { mutableStateOf(null) }
    if (link.value != null) {
        OpenUrl(url = link.value!!)
        link.value = null
    }

    val annotatedString = buildAnnotatedString {
        append("${stringResource(id = firstLineStr)} ")
        pushStringAnnotation(tag = "terms_and_conditions", annotation = "https://fiestamas.com/privacy-policy")
        withStyle(style = SpanStyle(color = PinkFiestamas)) {
            append(stringResource(id = R.string.login_terms_and_conditions_two))
        }
        pop()
        append(" ${stringResource(id = R.string.login_terms_and_conditions_three)} ")
        pushStringAnnotation(tag = "privacy_policy", annotation = "https://fiestamas.com/privacy-policy")
        withStyle(style = SpanStyle(color = PinkFiestamas)) {
            append(stringResource(id = R.string.login_terms_and_conditions_four))
        }
        pop()
    }

    ClickableText(
        modifier = Modifier.padding(vertical = 4.dp.autoSize()),
        text = annotatedString,
        style = TextStyle(
            color = Color.Black,
            fontSize = 14.sp.autoSize(),
            fontFamily = FontFamily.Default,
            textAlign = if (centerText) TextAlign.Center else TextAlign.Start
        ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(
                tag = "terms_and_conditions",
                start = offset,
                end = offset
            ).firstOrNull()?.let {
                link.value = it.item
            }
            annotatedString.getStringAnnotations(
                tag = "privacy_policy",
                start = offset,
                end = offset
            ).firstOrNull()?.let {
                link.value = it.item
            }
        }
    )
}

@Composable
fun ForgotPasswordClickable(onClick: () -> Unit) {
    val annotatedString = buildAnnotatedString {
        append("¿Olvidaste tu contraseña? ")
        pushStringAnnotation(tag = "myTag", annotation = "myAnnotation")
        withStyle(style = SpanStyle(color = Color.Blue)) {
            append("Haz click aquí")
        }
        pop()
    }

    ClickableText(
        text = annotatedString,
        style = TextStyle(
            color = Color.Black,
            fontSize = 14.sp.autoSize(),
            fontFamily = FontFamily.Default,
            textAlign = TextAlign.Center
        ), onClick = { onClick() }
    )
}

@Composable
fun BottomMenuMiFiesta(
    colorBackground: Color
) {
    Column(
        modifier = Modifier
            .wrapContentWidth()
            .height(31.dp.autoSize())
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .clip(allRoundedCornerShape30)
                .background(colorBackground)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Mifiesta",
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp.autoSize(),
                    fontWeight = FontWeight.Bold,
                    lineHeight = 15.sp,
                    color = Color.White,
                    style = letterSpacing(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .sidePadding(9.dp)
                )
            }
        }
    }
}

@Composable
fun BottomMenuProfileOnline(
    photo: String
) {
    Box(
        modifier = Modifier.size(31.dp.autoSize())
    ) {
        Image(
            painter = rememberAsyncImagePainter(photo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(1.dp, Color.Gray, CircleShape)
        )
    }
}

@Composable
fun UserNameText(
    modifier: Modifier = Modifier,
    vm: AuthViewModel = hiltViewModel()
) {
    vm.getFirebaseUserDb(MainParentClass.userId)

    val userDb by vm.firebaseUserDb.collectAsState()
    val name = userDb?.name.orEmpty()
    if (name.isNotEmpty()) {
        TextRegular(
            text = name,
            size = 12.sp.autoSize(),
            fillMaxWidth = false,
            modifier = modifier
        )
    }
}

@Composable
fun LinkedStrings(
    strings: List<String>,
    modifier: Modifier = Modifier,
    small: Boolean = false,
    smallest: Boolean = false,
    separator: String = "-"
) {
    val nonEmptyStrings = strings.filter { it.isNotEmpty() }
    val formattedString = buildAnnotatedString {
        nonEmptyStrings.forEachIndexed { index, string ->
            val largeSize = if (small) 14.sp.autoSize() else if (smallest) 10.sp.autoSize() else 16.sp.autoSize()
            val shortSize = if (small) 12.sp.autoSize() else if (smallest) 10.sp.autoSize() else 13.sp.autoSize()
            val fontSize = if (index == nonEmptyStrings.size - 1) largeSize else shortSize
            val fontFamily = if (index == nonEmptyStrings.size - 1) sansSemiBold else sansMedium
            val fontColor = (if (index == nonEmptyStrings.size - 1) Color.Black else Color.DarkGray)
            val mStyle = SpanStyle(fontSize = fontSize, fontFamily = fontFamily, color = fontColor)

            withStyle(mStyle) { append(string) }

            if (index < nonEmptyStrings.size - 1) {
                withStyle(SpanStyle(color = Color.DarkGray)) {
                    append(" $separator ")
                }
            }
        }
    }

    Text(
        text = formattedString,
        fontSize = 16.sp.autoSize(),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        letterSpacing = 0.sp,
        modifier = modifier
            //.then(Modifier.wrapContentHeight(align = Alignment.CenterVertically))
    )
}

@Composable
fun ExpandableText(service: Service?) {
    val maxLines = 1
    var expandedState by remember { mutableStateOf(false) }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        val readMore = stringResource(id = R.string.service_read_more)
        val readLess = stringResource(id = R.string.service_read_less)
        Text(
            text = service?.description.orEmpty(),
            maxLines = if (expandedState) Int.MAX_VALUE else maxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult = it },
            fontSize = 15.sp.autoSize(),
            modifier = Modifier.clickable { expandedState = !expandedState }
        )
        // 44 characters
        if ((service?.description?.length ?: 0) > 44) {
            Text(
                text = if (expandedState) readLess else readMore,
                fontSize = 13.sp.autoSize(),
                fontFamily = sansBold,
                color = PinkFiestamas,
                modifier = Modifier
                    .align(Alignment.Start)
                    .clickable { expandedState = !expandedState }
            )
        }
    }
}

@Composable
fun ValidationText(
    show: Boolean,
    text: String,
    isForV2: Boolean = false,
    color: Color = Color.Red,
    fillMaxWidth: Boolean = true
) {
    if (show) {
        VerticalSpacer(height = 2.dp.autoSize())
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isForV2) {
                Image(
                    modifier = Modifier.size(15.dp),
                    painter = painterResource(R.drawable.ic_close_circled),
                    colorFilter = ColorFilter.tint(Color.White),
                    contentDescription = null
                )
                HorizontalSpacer(width = 6.dp)
            }
            TextRegular(
                text = text,
                size = 13.sp.autoSize(),
                color = color,
                fillMaxWidth = fillMaxWidth
            )
        }
    }
}
