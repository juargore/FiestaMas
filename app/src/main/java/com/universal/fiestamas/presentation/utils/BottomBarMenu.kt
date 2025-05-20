package com.universal.fiestamas.presentation.utils

import com.universal.fiestamas.R

sealed class BottomBarMenu(
    val route: String,
    var image: Int?
) {
    object Home : BottomBarMenu(
        route = "HOME",
        image = R.drawable.ic_home
    )
    object Party : BottomBarMenu(
        route = "PARTY",
        image = null
    )
    object Profile : BottomBarMenu(
        route = "PROFILE",
        image = null
    )
}
