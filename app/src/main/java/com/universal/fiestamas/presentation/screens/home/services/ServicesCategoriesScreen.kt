package com.universal.fiestamas.presentation.screens.home.services

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.Role
import com.universal.fiestamas.domain.models.Screen
import com.universal.fiestamas.domain.models.ScreenInfo
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.ui.CardServiceCategory
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.calendar.clickable
import com.universal.fiestamas.presentation.ui.dialogs.MapDialog
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.utils.Constants.GRID_THREE_CELLS
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.isProvider
import com.universal.fiestamas.presentation.utils.extensions.itemThreeColumns
import com.universal.fiestamas.presentation.utils.extensions.sidePadding

@Composable
fun ServicesCategoriesScreen (
    vm: ServicesCategoriesViewModel = hiltViewModel(),
    vma: AuthViewModel = hiltViewModel(),
    screenInfo: ScreenInfo,
    onNavigateServicesTypesClicked: (ScreenInfo) -> Unit,
    onAuthProcessStarted: () -> Unit,
    onBackClicked: () -> Unit
) {
    var showMapDialog by remember { mutableStateOf(false) }
    var citySelected: String? by remember { mutableStateOf(null) }
    var addressSelected: Address? by remember { mutableStateOf(null) }

    val firebaseUserDb by vma.firebaseUserDb.collectAsState()

    vma.getFirebaseUserDb()
    vm.getAllServicesCategories()
    vma.getUserLocation {
        citySelected = it?.city ?: "Desconocido"
        addressSelected = it
    }

    GradientBackground(
        content = {
            ProgressDialog(
                isVisible = vm.showProgressDialog.collectAsState().value,
                message = stringResource(R.string.progress_getting_info)
            )

            MapDialog(
                isVisible = showMapDialog,
                cameraZoom = 13f,
                onDismiss = { showMapDialog = false },
                onAddressSelected = {
                    vma.listenForLocationUpdates = false
                    val city = when (it?.city) {
                        null, "n/a" -> "Desconocido"
                        else -> it.city
                    }
                    showMapDialog = false
                    citySelected = city
                    addressSelected = it
                }
            )

            vm.servicesByEvent.collectAsState().value?.let { events ->
                LazyVerticalGrid(columns = GRID_THREE_CELLS, modifier = Modifier.sidePadding()) {
                    itemThreeColumns {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextMedium(
                                modifier = Modifier.align(Alignment.CenterStart),
                                text = stringResource(id = R.string.service_services),
                                align = TextAlign.Start,
                                size = 20.sp.autoSize()
                            )

                            if (!firebaseUserDb?.role.isProvider()) {
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 2.dp)
                                        .clickable { showMapDialog = true },
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (citySelected == null) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(15.dp.autoSize()),
                                            color = PinkFiestamas,
                                            strokeWidth = 3.dp
                                        )
                                        HorizontalSpacer(width = 6.dp)
                                        TextMedium(
                                            text = "Obteniendo ubicación...",
                                            fillMaxWidth = false,
                                            size = 15.sp.autoSize()
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_location),
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp.autoSize())
                                        )
                                        HorizontalSpacer(width = 6.dp)
                                        TextMedium(
                                            text = citySelected.orEmpty(),
                                            fillMaxWidth = false,
                                            size = 15.sp.autoSize()
                                        )
                                    }
                                }
                            }
                        }
                        VerticalSpacer(height = 15.dp)
                    }
                    itemsIndexed(events) { i, item ->
                        CardServiceCategory(
                            item = item,
                            index = i,
                            onItemClick = { serviceCategory ->
                                if (screenInfo.prevScreen == Screen.Mifiesta) {
                                    vma.listenForLocationUpdates = true
                                    vm.saveAddressSelectedInShPrefs(addressSelected)
                                    onNavigateServicesTypesClicked(
                                        vm.getNewScreenInfo(screenInfo, serviceCategory, screenInfo.questions, screenInfo.clientEventId)
                                    )
                                    return@CardServiceCategory
                                }
                                if (screenInfo.prevScreen == Screen.Home) {
                                    when (screenInfo.role) {
                                        Role.Provider -> {
                                            onNavigateServicesTypesClicked(
                                                vm.getNewScreenInfo(screenInfo, serviceCategory, null, null)
                                            )
                                        }
                                        Role.Unauthenticated, Role.Client -> {
                                            vma.listenForLocationUpdates = true
                                            vm.saveAddressSelectedInShPrefs(addressSelected)
                                            onNavigateServicesTypesClicked(
                                                vm.getNewScreenInfo(screenInfo, serviceCategory, screenInfo.questions, null)
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        },
        titleScreen = screenInfo.event.name,
        showLogoFiestamas = false,
        addBottomPadding = false,
        onNavigateAuthClicked = { onAuthProcessStarted() },
        onBackButtonClicked = { onBackClicked() }
    )
}
