package com.universal.fiestamas.presentation.ui.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.BidForQuote
import com.universal.fiestamas.domain.models.QuoteProductsInformation
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.StatusCanceled
import com.universal.fiestamas.presentation.theme.StatusHired
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape16
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape20
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape8
import com.universal.fiestamas.presentation.ui.ButtonStatusService
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.TextLight
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.calendar.clickable
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.getStatus
import com.universal.fiestamas.presentation.utils.extensions.getStatusColor
import com.universal.fiestamas.presentation.utils.extensions.getStatusName
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.extensions.toColor

@Composable
fun CardLeftGreenBid(
    item: BidForQuote?,
    showPinkButton: Boolean,
    isButtonEnabled: Boolean,
    isEdittextEnabled: Boolean,
    showCostOnEdittext: Boolean,
    isAcceptedItem: Boolean? = false,
    onButtonClicked: (BidForQuote) -> Unit,
    onPinkButtonClicked: () -> Unit
) {
    if (item == null) return

    val heightUI = 32.dp
    val greenStrong = "#27AD5F".toColor()
    var bid by rememberSaveable { mutableStateOf(if (showCostOnEdittext) item.bid.toString() else "") }

    Row (modifier = Modifier
        .fillMaxWidth()
        .sidePadding()) {
        Row(
            modifier = Modifier
                .weight(.65f)
                .background(
                    color = if (isButtonEnabled) greenStrong else greenStrong.copy(alpha = 0.6f),
                    shape = allRoundedCornerShape10
                )
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(.55f)
                    .height(heightUI)
                    .clickable {
                        if (isButtonEnabled) {
                            onButtonClicked(item)
                        }
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_check_rounded),
                    colorFilter = ColorFilter.tint(color = Color.White),
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )
                HorizontalSpacer(width = 5.dp)
                TextMedium(
                    text = if (isAcceptedItem == true) "Aceptado" else "Acepto",
                    fillMaxWidth = false,
                    size = 14.sp,
                    color = Color.White
                )
            }
            Row(
                modifier = Modifier
                    .weight(.45f)
                    .padding(end = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(heightUI)
                        .background(Color.White, allRoundedCornerShape8)
                        .border(1.dp, Color.Gray, allRoundedCornerShape8)
                ) {
                    TextRegular(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 6.dp),
                        fillMaxWidth = false,
                        text = "$",
                        size = 14.sp,
                        color = Color.Gray
                    )
                    BasicTextField(
                        modifier = Modifier.align(Alignment.Center),
                        value = bid,
                        readOnly = !isEdittextEnabled,
                        onValueChange = {
                            val filteredText = filterDecimalInput(it)
                            bid = if (it != filteredText) filteredText else it
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            color = if (isEdittextEnabled) Color.Black else Color.Gray
                        )
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .weight(.35f)
                .height(40.dp),
            contentAlignment = Alignment.Center
        ) {
            if (showPinkButton) {
                Box(
                    modifier = Modifier
                        .background(PinkFiestamas, shape = allRoundedCornerShape16)
                        .clip(allRoundedCornerShape20)
                        .clickable { onPinkButtonClicked() }
                ) {
                    TextBold(
                        modifier = Modifier.padding(horizontal = 30.dp),
                        text = "···",
                        color = Color.White,
                        fillMaxWidth = false,
                        size = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CardBothAcceptedBid(item: BidForQuote?) {
    if (item == null) return

    val heightUI = 32.dp
    val greenStrong = "#27AD5F".toColor()
    var bid by rememberSaveable { mutableStateOf(item.bid.toString()) }

    Row (modifier = Modifier
        .fillMaxWidth()
        .sidePadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = greenStrong.copy(alpha = 0.6f), shape = allRoundedCornerShape10)
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(.55f)
                    .height(heightUI),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_check_rounded),
                    colorFilter = ColorFilter.tint(color = Color.White),
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )
                HorizontalSpacer(width = 5.dp)
                TextMedium(
                    text = "Aceptado",
                    fillMaxWidth = false,
                    horizontalSpace = (-1).sp,
                    size = 14.sp,
                    color = Color.White
                )
            }
            Row(
                modifier = Modifier
                    .weight(.45f)
                    .padding(end = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(heightUI)
                        .background(Color.White, allRoundedCornerShape8)
                        .border(1.dp, Color.Gray, allRoundedCornerShape8),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    BasicTextField(
                        value = bid,
                        readOnly = true,
                        onValueChange = { bid = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    )
                }
            }
            Row(
                modifier = Modifier
                    .weight(.55f)
                    .height(heightUI),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_check_rounded),
                    colorFilter = ColorFilter.tint(color = Color.White),
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )
                HorizontalSpacer(width = 5.dp)
                TextMedium(
                    text = "Aceptado",
                    fillMaxWidth = false,
                    horizontalSpace = (-1).sp,
                    size = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun CardAcceptOrDeclineBidPink(
    item: BidForQuote?,
    isButtonEnabled: Boolean,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
) {
    if (item == null) return

    val heightUI = 32.dp.autoSize()
    var bid by rememberSaveable { mutableStateOf(item.bid.toString()) }

    Row (modifier = Modifier
        .fillMaxWidth()
        .sidePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (isButtonEnabled) PinkFiestamas else PinkFiestamas.copy(alpha = 0.6f),
                    shape = allRoundedCornerShape10
                )
                .padding(vertical = 4.dp.autoSize())
        ) {
            Row(
                modifier = Modifier
                    .weight(.55f)
                    .height(heightUI)
                    .clickable { if (isButtonEnabled) onAccept() },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_check_rounded),
                    colorFilter = ColorFilter.tint(color = Color.White),
                    contentDescription = null,
                    modifier = Modifier.size(17.dp.autoSize())
                )
                HorizontalSpacer(width = 5.dp)
                TextMedium(
                    text = "Acepto",
                    fillMaxWidth = false,
                    horizontalSpace = (-1).sp,
                    size = 14.sp.autoSize(),
                    color = Color.White
                )
            }
            Row(
                modifier = Modifier
                    .weight(.45f)
                    .padding(end = 4.dp.autoSize())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(heightUI)
                        .background(Color.White, allRoundedCornerShape8)
                        .border(1.dp, Color.Gray, allRoundedCornerShape8)
                ) {
                    TextRegular(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 6.dp.autoSize()),
                        fillMaxWidth = false,
                        text = "$",
                        size = 14.sp.autoSize(),
                        color = Color.Gray
                    )
                    BasicTextField(
                        modifier = Modifier.align(Alignment.Center),
                        value = bid,
                        readOnly = true,
                        onValueChange = {
                            val filteredText = filterDecimalInput(it)
                            bid = if (it != filteredText) filteredText else it
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            color = if (isButtonEnabled) Color.Black else Color.Gray,
                            fontSize = 16.sp.autoSize()
                        )
                    )
                }
            }
            Row(
                modifier = Modifier
                    .weight(.55f)
                    .height(heightUI)
                    .clickable { if (isButtonEnabled) onDecline() },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_close_record),
                    contentDescription = null,
                    modifier = Modifier.size(17.dp.autoSize())
                )
                HorizontalSpacer(width = 5.dp)
                TextMedium(
                    text = "Rechazo",
                    fillMaxWidth = false,
                    horizontalSpace = (-1).sp,
                    size = 14.sp.autoSize(),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun CardGreenAccepted(
    item: BidForQuote?
) {
    if (item == null) return

    Row (modifier = Modifier
        .fillMaxWidth()
        .sidePadding()
        .padding(vertical = 6.dp.autoSize())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp.autoSize())
                .background(
                    color = StatusHired,
                    shape = allRoundedCornerShape10
                )
                .padding(vertical = 4.dp.autoSize()),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_check_rounded),
                colorFilter = ColorFilter.tint(color = Color.White),
                contentDescription = null,
                modifier = Modifier.size(17.dp.autoSize())
            )
            HorizontalSpacer(width = 5.dp)
            TextMedium(
                text = "Oferta aceptada por $ ${item.bid}",
                fillMaxWidth = false,
                size = 15.sp.autoSize(),
                color = Color.White
            )
        }
    }
}

@Composable
fun CardRedDeclined() {
    Row (modifier = Modifier
        .fillMaxWidth()
        .sidePadding()
        .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .background(
                    color = StatusCanceled,
                    shape = allRoundedCornerShape10
                )
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_close_record),
                contentDescription = null,
                modifier = Modifier.size(17.dp)
            )
            HorizontalSpacer(width = 5.dp)
            TextMedium(
                text = "Oferta rechazada",
                fillMaxWidth = false,
                size = 15.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun CardRightYellowBid(
    item: BidForQuote?,
    isButtonEnabled: Boolean,
    showCostOnEdittext: Boolean,
    text: String,
    onButtonClicked: (BidForQuote) -> Unit
) {
    if (item == null) return

    val heightUI = 32.dp.autoSize()
    val yellowStrong = "#FFF200".toColor()
    var bid by rememberSaveable { mutableStateOf(if (showCostOnEdittext) item.bid.toString() else "") }

    Row (modifier = Modifier
        .fillMaxWidth()
        .sidePadding()
    ) {
        Box(modifier = Modifier.weight(.35f))
        Box(modifier = Modifier.weight(.65f)) {
            Row(
                modifier = Modifier
                    .background(
                        color = if (isButtonEnabled) yellowStrong else yellowStrong.copy(
                            alpha = 0.3f
                        ), shape = allRoundedCornerShape10
                    )
                    .padding(vertical = 4.dp.autoSize())
            ) {
                Row(
                    modifier = Modifier
                        .weight(.45f)
                        .padding(start = 4.dp.autoSize())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(heightUI)
                            .background(Color.White, allRoundedCornerShape8)
                            .border(1.dp, Color.Gray, allRoundedCornerShape8)
                    ) {
                        TextRegular(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 6.dp),
                            fillMaxWidth = false,
                            text = "$",
                            size = 14.sp.autoSize(),
                            color = Color.Gray
                        )
                        BasicTextField(
                            modifier = Modifier.align(Alignment.Center),
                            value = bid,
                            readOnly = !isButtonEnabled,
                            onValueChange = {
                                val filteredText = filterDecimalInput(it)
                                bid = if (it != filteredText) filteredText else it
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                color = if (isButtonEnabled) Color.Black else Color.Gray,
                                fontSize = 16.sp.autoSize()
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .weight(.55f)
                        .height(heightUI)
                        .clickable {
                            if (isButtonEnabled) {
                                onButtonClicked(
                                    item.also {
                                        it.bid = if (bid.isBlank()) 0 else bid
                                            .replace("$", "")
                                            .toInt()
                                    }
                                )
                            }
                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextMedium(
                        text = text,
                        fillMaxWidth = false,
                        size = 14.sp.autoSize()
                    )
                }
            }
        }
    }
}

@Composable
fun CardProductInformation(
    item: QuoteProductsInformation,
    isTitle: Boolean = false,
    addRemoveButton: Boolean = false,
    addEditButton: Boolean = false,
    onRemoveClicked: ((QuoteProductsInformation) -> Unit)? = null,
    onEditClicked: ((QuoteProductsInformation) -> Unit)? = null,
) {
    var size0 = if (addRemoveButton) 0.05f else 0.0f
    var size1 = if (addRemoveButton) 0.15f else 0.15f
    var size2 = if (addRemoveButton) 0.47f else 0.50f
    var size3 = if (addRemoveButton) 0.15f else 0.15f
    var size4 = if (addRemoveButton) 0.18f else 0.20f

    if (addEditButton && addRemoveButton) {
        size0 = 0.10f
        size1 = 0.15f
        size2 = 0.42f
        size3 = 0.15f
        size4 = 0.18f
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp.autoSize()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (addRemoveButton && !isTitle) {
            Image(
                painter = painterResource(id = R.drawable.ic_remove_filled),
                contentDescription = null,
                colorFilter = ColorFilter.tint("#F39C12".toColor()),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(if (addEditButton) size0 / 2 else size0)
                    .clickable { onRemoveClicked?.invoke(item) }
            )
            if (addEditButton) {
                HorizontalSpacer(width = 2.dp.autoSize())
                Image(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint("#F39C12".toColor()),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(size0 / 2)
                        .clickable { onEditClicked?.invoke(item) }
                )
            }
        }
        if (addRemoveButton && isTitle) {
            Box(modifier = Modifier.weight(size0))
        }

        TextMedium(
            modifier = Modifier.weight(size1),
            text = item.quantity,
            color = if (isTitle) Color.Gray else Color.Black,
            size = 12.sp.autoSize()
        )
        TextMedium(
            modifier = Modifier.weight(size2),
            text = item.description,
            color = if (isTitle) Color.Gray else Color.Black,
            align = TextAlign.Start,
            size = 12.sp.autoSize()
        )
        TextMedium(
            modifier = Modifier.weight(size3),
            text = if (isTitle) item.price else "$${item.price}" ,
            color = if (isTitle) Color.Gray else Color.Black,
            size = 12.sp.autoSize()
        )
        TextMedium(
            modifier = Modifier.weight(size4),
            text = if (isTitle) item.subtotal else "$${item.subtotal}",
            maxLines = 1,
            addThreeDots = true,
            color = if (isTitle) Color.Gray else Color.Black,
            size = 12.sp.autoSize()
        )
    }
}

fun filterDecimalInput(text: String): String {
    val regex = Regex("""[0-9]*""")
    return regex.find(text)?.value.orEmpty()
}

@Composable
fun RequestQuoteEmptyView(
    onRequestClicked: () -> Unit,
    status: String,
    onStatusClicked: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextMedium(
                text = stringResource(R.string.mifiesta_quote_non_existent),
                size = 14.sp.autoSize(),
                fillMaxWidth = false,
                verticalSpace = 17.sp.autoSize()
            )
            VerticalSpacer(height = 10.dp)
            Box(modifier = Modifier
                .background(
                    color = PinkFiestamas,
                    shape = allRoundedCornerShape12
                )
                .sidePadding()
                .wrapContentWidth()
                .clickable { onRequestClicked() }
            ) {
                TextRegular(
                    modifier = Modifier.padding(vertical = 8.dp.autoSize()),
                    text = stringResource(id = R.string.mifiesta_quote_request),
                    color = Color.White,
                    size = 12.sp.autoSize(),
                    fillMaxWidth = false
                )
            }
        }
        Column(modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(12.dp.autoSize())
        ) {
            TextLight(text = "Estado", size = 11.sp.autoSize())
            VerticalSpacer(height = 10.dp)
            ButtonStatusService(
                modifier = Modifier.fillMaxWidth(),
                text = status.getStatusName(),
                color = status.getStatus().getStatusColor()
            ) {
                onStatusClicked()
            }
        }
    }
}

@Composable
fun ProviderQuoteEmptyView(
    status: String,
    onStatusClicked: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextMedium(
                text = stringResource(R.string.mifiesta_quote_provider_non_existent),
                size = 14.sp.autoSize(),
                fillMaxWidth = false,
                verticalSpace = 15.sp.autoSize()
            )
            VerticalSpacer(height = 20.dp)
        }
        Column(modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(12.dp)
        ) {
            TextLight(text = "Estado", size = 11.sp.autoSize())
            VerticalSpacer(height = 5.dp.autoSize())
            ButtonStatusService(
                modifier = Modifier.fillMaxWidth(),
                text = status.getStatusName(),
                color = status.getStatus().getStatusColor()
            ) {
                onStatusClicked()
            }
        }
    }
}
