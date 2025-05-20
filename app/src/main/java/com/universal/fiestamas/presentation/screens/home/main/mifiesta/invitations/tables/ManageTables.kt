package com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.tables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Guest
import com.universal.fiestamas.domain.usecases.EventUseCase
import com.universal.fiestamas.domain.usecases.GuestUseCase
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.CardGuestStatus
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.InvitationsViewModel
import com.universal.fiestamas.presentation.theme.GradientBackgroundFiestamas
import com.universal.fiestamas.presentation.theme.LightBlue
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.ui.CircleAvatarWithInitials
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.RegularEditText
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.dialogs.IEventRepositoryTest
import com.universal.fiestamas.presentation.ui.dialogs.IGuestRepositoryTest
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding

@Composable
fun ManageTablesScreen(
    vm: InvitationsViewModel = hiltViewModel(),
    idClientEvent: String,
    onBackClicked: () -> Unit
) {
    vm.getGuestsListForTables(idClientEvent)
    //vm.getGuestsList(idClientEvent)

    val guestsListForTables by vm.guestsListForTables.collectAsState()
    val guestsList by vm.guestsList.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = GradientBackgroundFiestamas)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp),
            color = Color.Transparent
        ) {
            LongPressDraggable(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(bottom = 175.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    guestsListForTables?.let {
                        items(it) { guest ->
                            CardGuestCondensed(guest = guest)
                        }
                    }
                }
                TableListContainer(guestsList.orEmpty())
            }
        }

        SearchSection()
        HeaderScreen { onBackClicked() }
    }
}

@Composable
fun SearchSection() {
    var searchTerm by rememberSaveable { mutableStateOf("") }

    Row(
        modifier = Modifier
            .padding(top = 45.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RegularEditText(
            modifier = Modifier
                .height(40.dp)
                .sidePadding(5.dp)
                .weight(.7f),
            value = searchTerm,
            placeholder = "  Buscar invitado",
            onValueChange = {
                searchTerm = it
            }
        )
    }
}

@Composable
fun HeaderScreen(
    onBackClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)
            .padding(horizontal = 8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = null,
            colorFilter = ColorFilter.tint(PinkFiestamas),
            modifier = Modifier
                .height(26.dp)
                .clickable { onBackClicked() }
        )
        TextBold(
            text = "Administrar mesas",
            size = 24.sp,
            color = PinkFiestamas,
            shadowColor = Color.Gray,
            align = TextAlign.Start,
            includeFontPadding = false,
            modifier = Modifier
                .wrapContentHeight()
                .padding(start = 5.dp)
        )
    }
}

@Composable
fun BoxScope.TableListContainer(guestList: List<Guest>) {

    val tableList = listOf(
        TableItem(1, "Mesa 1", mutableListOf()),
        TableItem(2, "Mesa 2", mutableListOf()),
        TableItem(3, "Mesa 3", mutableListOf()),
        TableItem(4, "Mesa 4", mutableListOf()),
    )

    //val addedGuests = mutableListOf<Guest>()

    guestList.forEach { guest ->
        tableList.find { table -> table.id == guest.num_table }?.let { table ->
            table.guests.add(guest)
            // todo: add guest to table on vm here...
            //addedGuests.add(guest)
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.LightGray,
                shape = RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp)
            )
            .padding(vertical = 10.dp)
            .align(Alignment.BottomCenter),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        items(items = tableList) { table ->
            CardTable(table)
        }
    }
}

@Composable
fun CardGuestCondensed(
    guest: Guest
) {
    DragTarget(
        modifier = Modifier.fillMaxWidth(),
        dataToDrop = guest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(allRoundedCornerShape12)
                .background(Color.White)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(0.7f),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleAvatarWithInitials(
                        name = guest.name.orEmpty(),
                        circleSize = 35.dp,
                        textSize = 14.sp,
                        backgroundColor = LightBlue
                    )
                    HorizontalSpacer(width = 8.dp)
                    TextMedium(
                        text = guest.name.orEmpty(),
                        size = 13.sp.autoSize(),
                        align = TextAlign.Start,
                        fillMaxWidth = false,
                        addThreeDots = true,
                        maxLines = 1
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(start = 5.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    CardGuestStatus(guest.status)
                }
            }
        }
    }
}

@Composable
fun CardTable(
    tableItem: TableItem
) {
    DropTarget<Guest>(
        modifier = Modifier.padding(8.dp)
    ) { isInBound, guestItem ->

        val bgColor = if (isInBound) {
            PinkFiestamas
        } else {
            Color.White
        }

        guestItem?.let {
            if (isInBound) {
                tableItem.guests.add(guestItem)
            }
        }

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(130.dp)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Column {
                TextSemiBold(
                    text = tableItem.name,
                    size = 18.sp
                )
                tableItem.guests.forEach {
                    TextRegular(
                        text = it.name.orEmpty(),
                        size = 8.sp
                    )
                }
            }
        }
    }
}

data class TableItem(
    val id: Int,
    val name: String,
    val guests: MutableList<Guest>
)


///*
@Preview
@Composable
fun ManageTablesScreenPreview() {
    ManageTablesScreen(
        idClientEvent = "",
        vm = InvitationsViewModel(
            GuestUseCase(IGuestRepositoryTest()),
            EventUseCase(IEventRepositoryTest())
        ),
        onBackClicked = { }
    )
}
//*/
