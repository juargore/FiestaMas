package com.universal.fiestamas.presentation.ui.cards

import android.os.Handler
import android.os.Looper
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.MyPartyEvent
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.ServiceStatus
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.MainPartyViewModel
import com.universal.fiestamas.presentation.theme.LightGray
import com.universal.fiestamas.presentation.theme.NormalGray
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape5
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape6
import com.universal.fiestamas.presentation.theme.bottomRoundedCornerShape10
import com.universal.fiestamas.presentation.ui.CircleStatus
import com.universal.fiestamas.presentation.ui.GradientTopAndRightToBottom
import com.universal.fiestamas.presentation.ui.LinkedStrings
import com.universal.fiestamas.presentation.ui.RatingStar
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.convertTimestampToDate
import com.universal.fiestamas.presentation.utils.extensions.convertTimestampToDateAndHourUTC
import com.universal.fiestamas.presentation.utils.extensions.daysUntilDate
import com.universal.fiestamas.presentation.utils.extensions.getStatus
import com.universal.fiestamas.presentation.utils.extensions.getStatusColor
import com.universal.fiestamas.presentation.utils.extensions.isRunningOnTablet
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.toColor

@Composable
fun NewCardEventProvider(
    onClick: () -> Unit
) {

    val isTablet = isRunningOnTablet()
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val cardHeight = if (isTablet) {
        ((screenHeight / 4)).dp + 20.dp
    } else {
        190.dp
    }

    Column(
        modifier = Modifier
            .height(cardHeight)
            .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(allRoundedCornerShape10)
                .border(
                    width = 2.dp,
                    color = PinkFiestamas,
                    shape = allRoundedCornerShape10
                )
                .background("#FDE9F0".toColor())
                .clickable { onClick() }
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_new_event),
                contentDescription = null,
                modifier = Modifier
                    .height(85.dp.autoSize())
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun CardServiceProvider(
    item: MyPartyService?,
    savedServiceIdNotification: String,
    onClick: (MyPartyService) -> Unit
) {
    if (item == null) return

    val color = item.event_data?.color_hex?.toColor() ?: Color.Green

    val isTablet = isRunningOnTablet()
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val cardHeight = if (isTablet) {
        ((screenHeight / 4)).dp + 20.dp
    } else {
        190.dp
    }

    Box(
        modifier = Modifier.height(cardHeight)
    ) {

        // background image + gradient color
        Box(
            modifier = Modifier
                .padding(4.dp.autoSize())
                .clip(allRoundedCornerShape10)
                .fillMaxSize()
                .clickable { onClick(item) }
        ) {
            /*Image(
                painter = rememberAsyncImagePainter(item.image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
            )*/
            GradientTopAndRightToBottom(color)
        }

        Column(
            modifier = Modifier
                .sidePadding(4.dp.autoSize())
                .clip(bottomRoundedCornerShape10)
        ) {
            // top section
            Column(
                modifier = Modifier
                    .padding(4.dp.autoSize())
                    .weight(0.85f)
            ) {
                VerticalSpacer(height = 28.dp)
                TextSemiBold(
                    text = item.event_data?.name_event_type.orEmpty(), // Baby shower
                    color = Color.White,
                    size = 15.sp.autoSize(),
                    align = TextAlign.Start,
                    includeFontPadding = false,
                    maxLines = 2,
                    addThreeDots = true
                )
                TextMedium(
                    text = item.event_data?.name.orEmpty(), // Arturo
                    color = Color.White,
                    size = 14.sp.autoSize(),
                    align = TextAlign.Start,
                    includeFontPadding = false,
                    maxLines = 1,
                    addThreeDots = true
                )
                VerticalSpacer(height = if (isTablet) 10.dp else 4.dp)
                Row(
                    modifier = Modifier
                        .background(color = Color.White, shape = allRoundedCornerShape10)
                        .padding(horizontal = 5.dp.autoSize(), vertical = 5.dp.autoSize())
                ) {
                    val pairDate = convertTimestampToDate(item.date)
                    val nPariDate = convertTimestampToDateAndHourUTC(item.date)
                    Column(
                        modifier = Modifier.weight(0.55f)
                    ) {
                        val dateFormatted = pairDate.first.replace("-", "/") // 20-09-24
                        TextMedium(text = dateFormatted, size = 10.sp.autoSize())
                        TextMedium(text = pairDate.second, size = 10.sp.autoSize()) // viernes
                    }
                    Column(
                        modifier = Modifier.weight(0.45f)
                    ) {
                        TextMedium(text = nPariDate.second.substringBefore(" hrs"), size = 9.sp.autoSize())
                        VerticalSpacer(height = 2.dp)
                        TextMedium(text = "HRS", size = 9.sp.autoSize())
                    }
                }
                VerticalSpacer(height = 5.dp)
                Column(
                    modifier = Modifier
                        .background(color = Color.White, shape = allRoundedCornerShape10)
                        .padding(horizontal = 6.dp.autoSize(), vertical = 5.dp.autoSize())
                ) {
                    TextMedium(
                        text = item.service_category_name.orEmpty(),
                        size = 10.sp.autoSize(),
                        align = TextAlign.Start
                    )
                    Row {
                        TextMedium(
                            text = "✔ ",
                            size = 10.sp.autoSize(),
                            color = PinkFiestamas,
                            fillMaxWidth = false
                        )
                        TextMedium(
                            text = item.name,
                            size = 10.sp.autoSize(),
                            align = TextAlign.Start,
                            addThreeDots = true,
                            maxLines = 1
                        )
                    }
                }
            }
            // bottom section
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(0.15f)
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_clock),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = PinkFiestamas),
                    modifier = Modifier.size(11.dp.autoSize())
                )

                var pendingDays = daysUntilDate(item.date)
                val wording = if (pendingDays > -1) {
                    R.string.mifiesta_rest
                } else {
                    pendingDays *= (-1)
                    R.string.mifiesta_ago
                }

                TextSemiBold(text = stringResource(id = wording), size = 10.sp.autoSize(), fillMaxWidth = false)
                TextSemiBold(text = pendingDays.toString(), fillMaxWidth = false, size = 11.sp.autoSize(), color = PinkFiestamas)
                TextSemiBold(text = stringResource(id = R.string.mifiesta_days), size = 10.sp.autoSize(), fillMaxWidth = false)
            }
        }

        // Circle status color
        val status = item.status.getStatus()
        val statusColor = status.getStatusColor()
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(28.dp.autoSize())
                .background(color = statusColor, shape = CircleShape)
                .border(
                    width = 3.dp,
                    color = Color.White,
                    shape = CircleShape
                )
        )
    }

    // just created the serviceEvent on Db
    // User clicked in "chat" on details screen
    // Open the negotiation screen of this serviceEvent
    if (savedServiceIdNotification.isNotEmpty() && !alreadyClicked) {
        if (savedServiceIdNotification == item.id) {
            alreadyClicked = true
            Handler(Looper.getMainLooper()).postDelayed({
                onClick(item)
            }, 1000L)
        }
    }
}

@Composable
fun CardMyPartyHorizontal(
    item: MyPartyEvent?,
    showPrice: Boolean = true,
    onClick: (MyPartyEvent) -> Unit
) {
    if (item == null) return
    val progress by remember {  mutableFloatStateOf((item.progress_event ?: 100).toFloat() * 0.01f) }
    val color = item.color_hex.toColor()
    val shape = allRoundedCornerShape10

    Box(
        modifier = Modifier
            .clip(shape)
            .fillMaxHeight()
            .width(140.dp.autoSize())
            .clickable { onClick(item) }
    ) {
        Image(
            painter = rememberAsyncImagePainter(item.image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
        )
        GradientTopAndRightToBottom(color)
        Column(modifier = Modifier.fillMaxHeight()) {
            // top section
            Column(
                modifier = Modifier
                    .weight(0.8f)
                    .padding(horizontal = 8.dp.autoSize(), vertical = 3.dp.autoSize())
            ) {
                TextSemiBold(
                    text = item.name_event_type,
                    size = 15.sp.autoSize(),
                    color = Color.White,
                    includeFontPadding = false,
                    maxLines = 1,
                    addThreeDots = true,
                    align = TextAlign.Start
                )
                VerticalSpacer(height = 3.dp)
                Row {
                    // left content
                    Column(modifier = Modifier.weight(0.5f)) {
                        TextSemiBold(
                            text = item.name,
                            size = 13.sp.autoSize(),
                            color = Color.White,
                            fillMaxWidth = false,
                            includeFontPadding = false,
                            align = TextAlign.Start
                        )
                        VerticalSpacer(height = 5.dp)

                        val (date, _) = convertTimestampToDateAndHourUTC(item.date)

                        TextSemiBold(
                            text = date,
                            size = 11.sp.autoSize(),
                            color = Color.White,
                            fillMaxWidth = false,
                            includeFontPadding = false,
                            align = TextAlign.Start,
                            horizontalSpace = (-1).sp,
                            maxLines = 1,
                            addThreeDots = true
                        )
                    }
                    // right content
                    Column(
                        modifier = Modifier.weight(0.5f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .clip(shape)
                                .background(Color.White)
                                .padding(5.dp)
                        ) {
                            TextSemiBold(
                                text = stringResource(id = R.string.mifiesta_progress),
                                size = 9.sp.autoSize(),
                                includeFontPadding = false
                            )
                            TextSemiBold(
                                text = "${item.progress_event ?: 0}%",
                                size = 17.sp.autoSize(),
                                color = Color.Black,
                                includeFontPadding = false,
                                horizontalSpace = (-0.5).sp
                            )
                            VerticalSpacer(height = 2.dp)
                            LinearProgressIndicator(
                                backgroundColor = NormalGray,
                                progress = progress,
                                color = "#008001".toColor()
                            )
                            VerticalSpacer(height = 2.dp)

                            if (showPrice) {
                                TextSemiBold(
                                    text = stringResource(id = R.string.mifiesta_cost, item.finalCost.toString()),
                                    size = 10.sp.autoSize(),
                                    horizontalSpace = (-0.5).sp
                                )
                            }
                        }
                    }
                }
            }
            // bottom section
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(0.2f)
                    .background(LightGray)
                    .fillMaxWidth()
            ) {
                var pendingDays = item.pendingDays ?: 0
                val wording = if (pendingDays > -1) {
                    R.string.mifiesta_rest
                } else {
                    pendingDays *= (-1)
                    R.string.mifiesta_ago
                }

                TextSemiBold(
                    text = stringResource(id = wording),
                    size = 10.sp.autoSize(),
                    fillMaxWidth = false
                )
                TextBold(
                    text = pendingDays.toString(),
                    fillMaxWidth = false,
                    size = 11.sp.autoSize(),
                    color = Color.Black
                )
                TextSemiBold(
                    text = stringResource(id = R.string.mifiesta_days),
                    size = 10.sp.autoSize(),
                    fillMaxWidth = false
                )
            }
        }
    }
}

@Composable
fun CardMyPartyVertical(
    vm: MainPartyViewModel = hiltViewModel(),
    item: MyPartyService?,
    onClick: (MyPartyService) -> Unit
) {
    if (item == null) return

    val pathList = remember { mutableStateListOf<String>() }
    val color = if (!item.event_data?.color_hex.isNullOrEmpty()) {
        item.event_data!!.color_hex.toColor()
    } else {
        Color.Blue
    }

    item.id_service?.let {
        vm.getServicePathById(item.id_service) { list ->
            if (pathList.isEmpty()) {
                pathList.addAll(list)
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp.autoSize())
            .clip(allRoundedCornerShape10)
            .background(Color.White)
            .clickable { onClick(item) }
    ) {
        // circle at start/left
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(15.dp.autoSize())
        ) {
            CircleStatus(
                modifier = Modifier.align(Alignment.Center),
                status = item.serviceStatus ?: ServiceStatus.Hired
            )
        }
        // middle image
        Image(
            painter = rememberAsyncImagePainter(item.image),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .padding(vertical = 8.dp.autoSize())
                .clip(allRoundedCornerShape10)
                .fillMaxHeight()
                .width(85.dp.autoSize())
        )
        // third space for information
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp.autoSize())
        ) {
            Box(
                modifier = Modifier
                    .clip(allRoundedCornerShape5)
                    .background(color)
            ) {
                item.event_data?.name?.let { festejadosName ->
                    item.event_data.name_event_type.let { eventName ->
                        TextSemiBold(
                            text = "$eventName $festejadosName", // Bautizo Mauricio
                            color = Color.White,
                            fillMaxWidth = false,
                            size = 13.sp.autoSize(),
                            maxLines = 1,
                            addThreeDots = true,
                            modifier = Modifier.padding(horizontal = 8.dp.autoSize())
                        )
                    }
                }
            }
            // Taquerias
            if (pathList.isNotEmpty()) {
                LinkedStrings(
                    strings = pathList,
                    modifier = Modifier.padding(start = 0.dp),
                    smallest = true,
                    separator = "<"
                )
            } else {
                TextMedium(
                    text = item.service_category_name.orEmpty(),
                    size = 12.sp.autoSize(),
                    fillMaxWidth = false,
                    maxLines = 1,
                    addThreeDots = true
                )
            }
            Row {
                TextBold(
                    modifier = Modifier.weight(0.62f),
                    text = item.name,
                    size = 14.sp.autoSize(),
                    align = TextAlign.Start,
                    horizontalSpace = (-1).sp,
                    maxLines = 1,
                    addThreeDots = true,
                    includeFontPadding = false
                )
                Box(modifier = Modifier.weight(0.38f)) {
                    RatingStar(
                        modifier = Modifier.align(Alignment.Center),
                        rating = item.rating.toFloat(),
                        starSize = 11.dp.autoSize(),
                        maxRating = item.rating,
                        onRatingChanged = { }
                    )
                }
            }
            Row {
                Column (modifier = Modifier.weight(0.6f)) {
                    TextSemiBold(
                        modifier = Modifier.padding(end = 5.dp.autoSize()),
                        text = stringResource(id = R.string.mifiesta_contact, item.provider_contact_name),
                        size = 11.sp.autoSize(),
                        fillMaxWidth = false,
                        maxLines = 1,
                        addThreeDots = true
                    )
                    Row(
                        modifier = Modifier
                            .border(0.5.dp.autoSize(), Color.Gray, allRoundedCornerShape12)
                            .padding(vertical = 3.dp.autoSize(), horizontal = 10.dp.autoSize())
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_phone_calling),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp.autoSize())
                        )
                        TextSemiBold(
                            text = "  ${item.provider_contact_phone}",
                            size = 12.sp.autoSize(),
                            color = PinkFiestamas,
                            fillMaxWidth = false,
                            includeFontPadding = false
                        )
                    }
                }
                Column (
                    modifier = Modifier
                        .weight(0.4f)
                        .border(0.5.dp.autoSize(), Color.Gray, allRoundedCornerShape6)
                        .padding(2.dp.autoSize()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TextSemiBold(
                        text = stringResource(id = R.string.mifiesta_cost_per_event),
                        size = 8.sp.autoSize(),
                        verticalSpace = 12.sp.autoSize(),
                        fillMaxWidth = false
                    )
                    TextSemiBold(
                        text = stringResource(id = R.string.mifiesta_cost, item.price),
                        size = 13.sp.autoSize()
                    )
                }
            }
            // notes at bottom
            VerticalSpacer(height = 2.dp.autoSize())
            Row {
                TextBold(
                    modifier = Modifier.padding(end = 4.dp.autoSize()),
                    text = "●",
                    color = PinkFiestamas,
                    size = 10.sp.autoSize(),
                    fillMaxWidth = false
                )
                TextRegular(
                    text = item.description,
                    size = 10.sp.autoSize(),
                    maxLines = 1,
                    addThreeDots = true,
                    align = TextAlign.Start
                )
            }
        }
    }

    // just created the serviceEvent on Db
    // User clicked in "chat" on details screen
    // Open the negotiation screen of this serviceEvent
    /*if (savedServiceIdNotification.isNotEmpty() && !alreadyClicked) {
        if (savedServiceIdNotification == item.id) {
            alreadyClicked = true
            Handler(Looper.getMainLooper()).postDelayed({
                onClick(item)
            }, 1000L)
        }
    }*/
}

private var alreadyClicked = false
