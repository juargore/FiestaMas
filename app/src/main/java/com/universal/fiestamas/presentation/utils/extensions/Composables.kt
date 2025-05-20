package com.universal.fiestamas.presentation.utils.extensions

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.presentation.utils.Constants

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.sidePadding(size: Dp = 15.dp) : Modifier {
    return padding(horizontal = size)
}

fun letterSpacing(space: TextUnit = 1.sp) : TextStyle {
    return TextStyle(letterSpacing = space)
}

fun LazyGridScope.itemTwoColumns(content: @Composable () -> Unit) {
    item(span = { GridItemSpan(Constants.TWO_COLUMNS) }) {
        content()
    }
}

fun LazyGridScope.itemThreeColumns(content: @Composable () -> Unit) {
    item(span = { GridItemSpan(Constants.THREE_COLUMNS) }) {
        content()
    }
}

@Composable
fun TextUnit.autoSize(): TextUnit {
    val value = this
    val isTablet = isRunningOnTablet()
    return if (isTablet) {
        value * 1.6
    } else {
        value
    }
}

@Composable
fun Dp.autoSize(): Dp {
    val value = this
    val isTablet = isRunningOnTablet()
    return if (isTablet) {
        value * 1.6f
    } else {
        value
    }
}

