package com.universal.fiestamas.presentation.screens.home.main.search

import android.annotation.SuppressLint
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape24
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.IconSimpleClose
import com.universal.fiestamas.presentation.ui.RegularEditText
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.extensions.toColor

@Composable
fun SearchTitle(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 5.dp)
    ) {
        TextSemiBold(
            modifier = Modifier.align(Alignment.Center),
            text = "Búsqueda de servicios",
            size = 18.sp,
            color = PinkFiestamas,
            shadowColor = Color.LightGray,
            fillMaxWidth = false
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        ) {
            IconSimpleClose(
                color = PinkFiestamas,
                onClose = { onClose() }
            )
        }
    }
}

@Composable
fun SearchBarAndFilters(
    vm: SearchViewModel = hiltViewModel(),
    onFilterClicked: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    Row(
        modifier = Modifier,
            //.padding(8.dp.autoSize())
            //.background(Color.White, allRoundedCornerShape24)
            //.clip(allRoundedCornerShape24),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RegularEditText(
            modifier = Modifier
                .height(40.dp.autoSize())
                .sidePadding(5.dp.autoSize())
                .weight(.8f),
            corners = allRoundedCornerShape24,
            paddingVertical = 2.dp,
            textSize = 14.sp.autoSize(),
            background = "#e7e6ea".toColor(),
            value = query,
            addBorder = false,
            placeholder = "  Buscar por nombre...",
            onValueChange = {
                query = it
                vm.findServiceByQuery(query)
            }
        )

        Row(modifier = Modifier.weight(.2f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchOrFilterIcon(
                parentModifier = Modifier.weight(0.5f),
                childModifier = Modifier.padding(8.dp.autoSize()),
                icon = R.drawable.ic_search
            )
            HorizontalSpacer(width = 5.dp)
            SearchOrFilterIcon(
                parentModifier = Modifier.weight(0.5f),
                childModifier = Modifier.padding(9.dp.autoSize()),
                icon = R.drawable.ic_filter,
                onClick = {
                    onFilterClicked()
                }
            )
        }
    }
}

@Composable
fun SearchOrFilterIcon(
    @SuppressLint("ModifierParameter")
    parentModifier: Modifier,
    childModifier: Modifier,
    icon: Int,
    onClick: (() -> Unit)? = null
) {
    Box (
        modifier = parentModifier
            .height(36.dp.autoSize())
            .background(PinkFiestamas, CircleShape)
            .clickable { onClick?.invoke() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = childModifier.fillMaxSize(),
            painter = painterResource(icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = Color.White)
        )
    }
}

@Composable
fun EmptyResults() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = PinkFiestamas)
            )
            TextRegular(
                text = "Sin resultados",
                color = Color.DarkGray,
                size = 16.sp.autoSize()
            )
        }
    }
}

@Composable
fun SeeMapOrGridButton(
    text: String,
    icon: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(PinkFiestamas, allRoundedCornerShape24)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(14.dp),
            painter = painterResource(id = icon),
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = null
        )
        HorizontalSpacer(width = 5.dp)
        TextRegular(
            text = text,
            color = Color.White,
            size = 12.sp,
            fillMaxWidth = false
        )
    }
}

enum class BottomSheetView {
    Filters,
    AutoComplete
}

enum class SearchView {
    GRID,
    MAP
}
