package com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.hostess

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.invitations.InvitationsViewModel
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground

@Composable
fun HostessScreen(
    vm: InvitationsViewModel = hiltViewModel(),
    onBackClicked: () -> Unit
) {
    GradientBackground(
        content = {

        },
        addBottomPadding = false,
        showLogoFiestamas = false,
        titleScreen = "Hostess",
        onBackButtonClicked = { onBackClicked() }
    )
}
