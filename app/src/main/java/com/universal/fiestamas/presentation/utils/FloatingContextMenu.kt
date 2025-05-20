package com.universal.fiestamas.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FloatingContextMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    menuData: List<MenuData>,
    onDismissRequest: (Boolean) -> Unit
) {
    Box(modifier = modifier) {
        DropdownMenu(
            modifier = Modifier.background(Color.White),
            expanded = expanded,
            onDismissRequest = { onDismissRequest(false) }
        ) {
            menuData.forEach { menuData ->
                DropdownMenuItem(onClick = { menuData.onClick.invoke() }) {
                    Row(modifier = Modifier.wrapContentSize()) {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .align(alignment = Alignment.CenterVertically),
                            text = menuData.name,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

data class MenuData(
    val name: String,
    val onClick: () -> Unit
)
