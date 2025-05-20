package com.universal.fiestamas.presentation.ui.cards

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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Notification
import com.universal.fiestamas.domain.models.NotificationStatus
import com.universal.fiestamas.presentation.theme.LightBlue
import com.universal.fiestamas.presentation.theme.LightGray
import com.universal.fiestamas.presentation.theme.LighterGray
import com.universal.fiestamas.presentation.theme.OrangeFiestaki
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape6
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape8
import com.universal.fiestamas.presentation.ui.CircleAvatarWithInitials
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding

enum class MessageType {
    MESSAGE,
    NOTIFICATION,
    APPROVAL,
    IMAGE,
    VIDEO,
    URL
}

fun String?.toMessageType(): MessageType {
    if (this.isNullOrEmpty()) return MessageType.MESSAGE

    return when (this.toUpperCase(Locale.current)) {
        "NOTIFICATION" -> MessageType.NOTIFICATION
        "APPROVAL" -> MessageType.APPROVAL
        "IMAGE" -> MessageType.IMAGE
        "VIDEO" -> MessageType.VIDEO
        "URL" -> MessageType.URL
        else -> MessageType.MESSAGE
    }
}

fun String?.verifyTypeForMessage(): MessageType {
    if (this.isNullOrEmpty()) return MessageType.MESSAGE

    val url = this.lowercase()
    val imageExtensions = listOf(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp")
    val isImage = imageExtensions.any { extension -> url.contains(extension, ignoreCase = true) }

    return if (isImage) {
        MessageType.IMAGE
    } else {
        MessageType.MESSAGE
    }
}


@Composable
fun ChatCard(
    date: String,
    photo: String,
    name: String,
    message: String,
    isReceived: Boolean,
    type: MessageType
) {
    if (type == MessageType.NOTIFICATION || type == MessageType.APPROVAL) {
        CardChatNotification(message)
    }
    if (type == MessageType.IMAGE || type == MessageType.MESSAGE) {
        if (!isReceived) {
            CardChatReceivedMessage(
                date = date,
                photo = photo,
                name = name,
                message = message,
                type = type
            )
        } else {
            CardChatSentMessage(
                date = date,
                photo = photo,
                name = name,
                message = message,
                type = type
            )
        }
    }
}

@Composable
fun CardChatApproval(
    text: String,
    onResponse: (accepted: Boolean) -> Unit
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .clip(allRoundedCornerShape8)
            .border(1.dp.autoSize(), LightGray, allRoundedCornerShape8)
            .background(LighterGray)
            .padding(10.dp.autoSize())
    ) {
        // Se requiere editar cotización
        TextRegular(
            text = text,
            size = 13.sp.autoSize()
        )
        VerticalSpacer(height = 5.dp)
        // buttons Accept | Decline
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(PinkFiestamas, shape = allRoundedCornerShape6)
                    .height(30.dp.autoSize())
                    .clickable { onResponse(true) }
            ) {
                TextRegular(
                    modifier = Modifier.sidePadding(),
                    text = "Aceptar",
                    size = 14.sp.autoSize(),
                    color = Color.White,
                    fillMaxWidth = false
                )
            }
            HorizontalSpacer(width = 5.dp)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(Color.Gray, shape = allRoundedCornerShape6)
                    .height(30.dp.autoSize())
                    .clickable { onResponse(false) }
            ) {
                TextRegular(
                    modifier = Modifier.sidePadding(),
                    text = "Declinar",
                    size = 14.sp.autoSize(),
                    color = Color.White,
                    fillMaxWidth = false
                )
            }
        }
    }
}

@Composable
fun CardChatNotification(text: String) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clip(allRoundedCornerShape8)
            .border(0.5.dp.autoSize(), PinkFiestamas, allRoundedCornerShape8)
            .background(Color.White)
            .padding(10.dp.autoSize())
    ) {
        Image(
            modifier = Modifier.size(20.dp.autoSize()),
            painter = painterResource(id = R.drawable.ic_alert_circled),
            contentDescription = null,
            colorFilter = ColorFilter.tint(OrangeFiestaki)
        )
        HorizontalSpacer(6.dp)
        TextRegular(
            text = text,
            size = 13.sp.autoSize(),
            align = TextAlign.Start,
            fillMaxWidth = false
        )
    }
}

@Composable
fun CardChatReceivedMessage(
    date: String,
    photo: String,
    name: String,
    message: String,
    type: MessageType
) {
    Row {
        if (photo.isBlank()) {
            CircleAvatarWithInitials(name, 36.dp.autoSize(), LightBlue)
        } else {
            Image(
                painter = rememberAsyncImagePainter(photo),
                contentDescription = null,
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .size(36.dp.autoSize())
                    .clip(CircleShape)
                    .border(0.5.dp.autoSize(), Color.Gray, CircleShape)
            )
        }
        HorizontalSpacer(12.dp)
        Column {
            TextMedium(
                text = name,
                size = 13.sp.autoSize(),
                align = TextAlign.Start
            )
            BodyMessageForImageAndText(text = message, sent = false, type = type)
        }
    }
}

@Composable
fun CardChatSentMessage(
    date: String,
    photo: String,
    name: String,
    message: String,
    type: MessageType
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            horizontalAlignment = Alignment.End
        ) {
            TextMedium(
                text = name,
                size = 13.sp.autoSize(),
                align = TextAlign.End,
                fillMaxWidth = false
            )
            BodyMessageForImageAndText(text = message, sent = true, type = type)
        }
        HorizontalSpacer(12.dp)
        if (photo.isBlank()) {
            CircleAvatarWithInitials(name, 36.dp.autoSize(), LightGray)
        } else {
            Image(
                painter = rememberAsyncImagePainter(photo),
                contentDescription = null,
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .size(36.dp.autoSize())
                    .clip(CircleShape)
                    .border(0.5.dp.autoSize(), Color.Gray, CircleShape)
            )
        }
    }
}

@Composable
fun BodyMessageForImageAndText(
    text: String,
    sent: Boolean,
    type: MessageType
) {
    Column {
        if (type == MessageType.MESSAGE) {
            TextRegular(
                modifier = if (sent) Modifier.padding(end = 5.dp.autoSize()) else Modifier,
                text = text,
                size = 13.sp.autoSize(),
                color = PinkFiestamas,
                align = if (sent) TextAlign.End else TextAlign.Start,
                fillMaxWidth = false
            )
        }

        if (type == MessageType.IMAGE) {
            Box(
                modifier = Modifier
                    .height(100.dp.autoSize())
                    .then(if (sent) Modifier.wrapContentWidth() else Modifier.fillMaxWidth()),
                contentAlignment = if (sent) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Image(
                    painter = rememberAsyncImagePainter(text),
                    contentDescription = null,
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentWidth()
                        .clip(allRoundedCornerShape8)
                )
            }
        }
    }
}

@Composable
fun CardNotification(
    item: Notification?,
    onClick: (Notification) -> Unit
) {
    if (item == null) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp.autoSize())
            .background(Color.White)
            .clickable { onClick(item) },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // circle at start/left
        /*val modifier = if (item.status == NotificationStatus.Read) {
            Modifier.border(1.dp.autoSize(), Color.LightGray, CircleShape)
        }  else {
            Modifier
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(18.dp.autoSize())
        ) {
            Box(
                modifier = modifier
                    .clip(CircleShape)
                    .size(10.dp.autoSize())
                    .align(Alignment.Center)
                    .background(if (item.status == NotificationStatus.Read) Color.White else Color.Red)
            )
        }*/
        // middle image
        Image(
            painter = rememberAsyncImagePainter(item.icon),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(60.dp.autoSize())
                .border(0.2.dp.autoSize(), Color.LightGray, CircleShape)
        )
        // third space for information
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextSemiBold(
                    modifier = Modifier.weight(0.7f),
                    text = "${item.eventType} ${item.festejadosName}",
                    size = 14.sp.autoSize(),
                    maxLines = 1,
                    horizontalSpace = (-1).sp,
                    addThreeDots = true,
                    fillMaxWidth = true,
                    align = TextAlign.Start
                )
                TextRegular(
                    modifier = Modifier.weight(0.3f),
                    size = 11.sp.autoSize(),
                    horizontalSpace = (-1).sp,
                    text = item.date.orEmpty(),
                    align = TextAlign.End
                )
            }
            Row {
                Column (
                    modifier = Modifier
                        .padding(2.dp.autoSize())
                        .weight(0.8f)
                ) {
                    TextSemiBold(
                        text = "${item.eventName} - ${item.serviceName}",
                        size = 12.sp.autoSize(),
                        fillMaxWidth = false,
                        align = TextAlign.Start,
                        includeFontPadding = false,
                        maxLines = 1,
                        addThreeDots = true,
                        verticalSpace = 15.sp.autoSize()
                    )
                    val msg = if (item.message.startsWith("https://")) {
                        "Imagen multimedia"
                    } else {
                        item.message
                    }
                    TextMedium(
                        text = msg,
                        size = 13.sp.autoSize(),
                        color = PinkFiestamas,
                        maxLines = 1,
                        addThreeDots = true,
                        fillMaxWidth = false
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxHeight()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_right_arrow),
                        contentDescription = null,
                        modifier = Modifier
                            .size(15.dp.autoSize())
                            .align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}
