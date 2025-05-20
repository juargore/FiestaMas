package com.universal.fiestamas.presentation.ui.backgrounds

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.universal.fiestamas.presentation.theme.LightBlue
import com.universal.fiestamas.presentation.theme.LighterBlue
import com.universal.fiestamas.presentation.theme.LowPinkFiestaki
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape8
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding

@Composable
fun BlueBackgroundRoundedCorners(
    showOutline: Boolean,
    content: @Composable () -> Unit,
    onChecked: (Boolean) -> Unit
) {
    val shape = allRoundedCornerShape10
    val checkedState = remember { mutableStateOf(false) }
    val outline = if (showOutline) {
        Modifier.border(2.dp, LightBlue, shape)
    } else {
        Modifier
    }
    Row (
        modifier = outline
            .background(color = if (showOutline) Color.White else LighterBlue, shape = shape)
            .fillMaxWidth()
            .padding(vertical = 4.dp.autoSize())
            .sidePadding(8.dp.autoSize()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            modifier = Modifier.size(20.dp.autoSize()),
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = it
                onChecked(it)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = PinkFiestamas,
                uncheckedColor = PinkFiestamas,
                checkmarkColor = Color.White
            )
        )
        HorizontalSpacer(width = 5.dp)
        content()
    }
}

@Composable
fun CardAuthBackground(
    centerContent: Boolean = true,
    addScroll: Boolean = true,
    bottomPadding: Dp = 30.dp.autoSize(),
    backgroundColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    Card(
        elevation = 10.dp,
        shape = allRoundedCornerShape12,
        backgroundColor = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .sidePadding()
            .padding(
                top = 10.dp.autoSize(),
                bottom = bottomPadding
            )
    ) {
        val modifier = if (addScroll) {
            Modifier.verticalScroll(rememberScrollState())
        } else Modifier

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = if (centerContent) Alignment.CenterHorizontally else Alignment.Start,
            modifier = modifier
                .sidePadding()
                .padding(vertical = 15.dp.autoSize())
        ) {
            content()
        }
    }
}

@Composable
fun CardServicesBackground(
    isForDetailsService: Boolean = false,
    content: @Composable () -> Unit
) {
    val shape = allRoundedCornerShape8

    Card(
        elevation = 12.dp,
        shape = shape,
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, if (isForDetailsService) Color.LightGray else PinkFiestamas, shape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isForDetailsService) Color.White else LowPinkFiestaki)
                .padding(10.dp.autoSize())
        ) {
            content()
        }
    }
}
