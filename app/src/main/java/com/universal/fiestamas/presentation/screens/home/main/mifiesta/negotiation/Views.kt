package com.universal.fiestamas.presentation.screens.home.main.mifiesta.negotiation

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.QuoteProductsInformation
import com.universal.fiestamas.domain.models.request.ItemQuoteRequest
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.PurpleFiestaki
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.ui.ButtonStatusService
import com.universal.fiestamas.presentation.ui.ButtonWhiteRoundedCorners
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.RatingStar
import com.universal.fiestamas.presentation.ui.TextLight
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.cards.CardProductInformation
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.convertTimestampToDateAndHourUTC
import com.universal.fiestamas.presentation.utils.extensions.getStatus
import com.universal.fiestamas.presentation.utils.extensions.getStatusColor
import com.universal.fiestamas.presentation.utils.extensions.getStatusName
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.makePhoneCall

@Composable
fun TopEventInformation(myPartyService: MyPartyService) {
    val (date, hour) = convertTimestampToDateAndHourUTC(myPartyService.date)

    Box(modifier = Modifier.sidePadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = PurpleFiestaki, shape = allRoundedCornerShape10)
                .padding(vertical = 8.dp.autoSize())
        ) {
            Column(modifier = Modifier.weight(0.42f)) {
                myPartyService.event_data?.name?.let { festejadosName ->
                    myPartyService.event_data.name_event_type.let { eventName ->
                        TextSemiBold(
                            text = "$eventName $festejadosName",
                            color = Color.White,
                            size = 13.sp.autoSize()
                        )
                    }
                }

                VerticalSpacer(height = 3.dp)
                TextMedium(
                    text = myPartyService.address,
                    color = Color.White,
                    size = 10.sp.autoSize()
                )
            }
            Column(
                modifier = Modifier.weight(0.25f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(20.dp.autoSize())
                )
                VerticalSpacer(height = 3.dp)
                TextMedium(
                    text = date,
                    color = Color.White,
                    size = 10.sp.autoSize()
                )
            }
            Column(
                modifier = Modifier.weight(0.18f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_clock),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(20.dp.autoSize())
                )
                VerticalSpacer(height = 3.dp)
                TextMedium(
                    text = hour,
                    color = Color.White,
                    size = 10.sp.autoSize()
                )
            }
            Column(
                modifier = Modifier.weight(0.15f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_people),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(20.dp.autoSize())
                )
                VerticalSpacer(height = 3.dp)
                TextMedium(
                    text = "${myPartyService.event_data?.attendees} P",
                    color = Color.White,
                    size = 10.sp.autoSize()
                )
            }
        }
    }
}

@Composable
fun TopContactInformation(
    vma: AuthViewModel = hiltViewModel(),
    clientId: String?
) {
    vma.getFirebaseProviderDb(clientId.orEmpty())

    val context = LocalContext.current
    val provider by vma.firebaseProviderDb.collectAsState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextMedium(
            text = "${provider?.name} ${provider?.last_name}",
            size = 14.sp.autoSize()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp.autoSize()),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val phone = provider?.phone_one.orEmpty()
            val email = provider?.email.orEmpty()

            ButtonWhiteRoundedCorners(
                verticalPadding = 5.dp.autoSize(),
                horizontalSpacer = 5.dp.autoSize(),
                iconSize = 15.dp.autoSize(),
                text = phone
            ) {
                makePhoneCall(context, phone)
            }
            HorizontalSpacer(width = 8.dp)
            ButtonWhiteRoundedCorners(
                verticalPadding = 5.dp.autoSize(),
                horizontalSpacer = 5.dp.autoSize(),
                icon = R.drawable.ic_envelope,
                iconSize = 15.dp.autoSize(),
                text = email
            ) {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = "mailto:".toUri()
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                launcher.launch(intent)
            }
        }
    }
}

@Composable
fun TopServiceInformation(myPartyService: MyPartyService) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp.autoSize())
    ) {
        Box(modifier = Modifier.weight(0.25f)) {
            val photo = myPartyService.image
            Image(
                painter = rememberAsyncImagePainter(photo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp.autoSize())
                    .clip(CircleShape)
                    .border(0.5.dp.autoSize(), Color.Gray, CircleShape)
                    .align(Alignment.Center)
            )
        }
        Column(modifier = Modifier.weight(0.75f)) {
            val type = myPartyService.service_category_name
            val name = myPartyService.name
            Column {
                TextSemiBold(
                    text = "$type - $name",
                    size = 14.sp.autoSize(),
                    fillMaxWidth = false,
                    align = TextAlign.Start,
                    verticalSpace = 17.sp.autoSize()
                )
                RatingStar(
                    rating = 5f,
                    maxRating = 5,
                    starSize = 12.dp.autoSize(),
                    onRatingChanged = { }
                )
            }
            Row {
                Image(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp.autoSize())
                )
                HorizontalSpacer(5.dp)
                TextMedium(
                    text = myPartyService.address,
                    color = PinkFiestamas,
                    align = TextAlign.Start,
                    size = 12.sp.autoSize()
                )
            }
        }
    }
}

@Composable
fun ProductsInformationDetails(list: List<ItemQuoteRequest>?) {
    Column(modifier = Modifier.padding(horizontal = 4.dp.autoSize(), vertical = 4.dp.autoSize())) {
        CardProductInformation(
            QuoteProductsInformation(
                quantity = "Cant.",
                description = "Descripción",
                price = "Precio",
                subtotal = "Subtotal"
            ), isTitle = true
        )
        list?.forEach { itemQuoteRequest ->
            CardProductInformation(
                QuoteProductsInformation(
                quantity = itemQuoteRequest.qty.toString(),
                description = itemQuoteRequest.description,
                price = itemQuoteRequest.price.toString(),
                subtotal = itemQuoteRequest.subTotal.toString()
            )
            )
        }
    }
}

@Composable
fun BottomData(
    providerNotes: String,
    noteBook: String,
    alreadyAccepted: Boolean,
    finalEventCost: Int,
    status: String,
    onOptionsClicked: () -> Unit,
    onNoteBookClicked: (String) -> Unit,
    onNotesQuoteClicked: (String) -> Unit
) {
    val textSize = 11.sp.autoSize()
    val uiHeight = 40.dp.autoSize()
    val uiSidePadding = 4.dp.autoSize()

    VerticalSpacer(height = 5.dp)

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.weight(0.55f)) {
            Column(modifier = Modifier.weight(.50f)) {
                TextLight(text = "Estado", size = textSize)
                ButtonStatusService(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(uiHeight)
                        .sidePadding(uiSidePadding),
                    text = status.getStatusName(),
                    color = status.getStatus().getStatusColor()
                ) {
                    onOptionsClicked()
                }
            }

            if (alreadyAccepted) {
                Column(modifier = Modifier.weight(.50f)) {
                    TextLight(text = "Costo evento", size = textSize)
                    Row(
                        modifier = Modifier
                            .height(uiHeight)
                            .sidePadding(uiSidePadding)
                            .background(Color.White, allRoundedCornerShape12)
                            .border(1.dp, Color.Gray, allRoundedCornerShape12),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        BasicTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = "$$finalEventCost",
                            onValueChange = { },
                            singleLine = true,
                            readOnly = true,
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp.autoSize()
                            )
                        )
                    }
                }
            }
        }

        VerticalSpacer(height = 5.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.45f)
        ) {
            Column(modifier = Modifier
                .weight(0.495f)
                .sidePadding()
                .clickable { onNotesQuoteClicked(providerNotes) }
            ) {
                TextRegular(text = "Notas importantes", size = 12.sp.autoSize())
                TextRegular(
                    text = providerNotes,
                    size = 12.sp.autoSize(),
                    color = Color.Gray,
                    maxLines = 1,
                    align = TextAlign.Start,
                    addThreeDots = true
                )
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Color.LightGray)
            )
            Column(modifier = Modifier
                .weight(0.495f)
                .sidePadding()
                .clickable { onNoteBookClicked(noteBook) }
            ) {
                TextRegular(text = "Block de Notas", size = 12.sp.autoSize())
                TextRegular(
                    text = noteBook,
                    size = 12.sp.autoSize(),
                    color = Color.Gray,
                    maxLines = 1,
                    align = TextAlign.Start,
                    addThreeDots = true
                )
            }
        }
    }
}
