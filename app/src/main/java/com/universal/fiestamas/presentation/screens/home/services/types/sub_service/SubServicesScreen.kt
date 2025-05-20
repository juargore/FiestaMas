package com.universal.fiestamas.presentation.screens.home.services.types.sub_service

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.domain.models.ScreenInfo
import com.universal.fiestamas.presentation.ui.CardSubService
import com.universal.fiestamas.presentation.ui.LinkedStrings
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.utils.Constants.GRID_THREE_CELLS
import com.universal.fiestamas.presentation.utils.extensions.itemThreeColumns
import com.universal.fiestamas.presentation.utils.extensions.sidePadding

@Composable
fun SubServicesScreen(
    vm: SubServicesViewModel = hiltViewModel(),
    screenInfo: ScreenInfo,
    onNavigateServicesOptionsClicked: (ScreenInfo) -> Unit,
    onTitleScreenClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    screenInfo.serviceType?.id?.let {
        vm.getSubServicesByServiceTypeId(it)
    }

    GradientBackground(
        content = {
            vm.subServices.collectAsState().value?.let { services ->
                if (services.isEmpty()) {
                    onNavigateServicesOptionsClicked(
                        vm.getNewScreenInfo(screenInfo, null)
                    )
                } else {
                    LazyVerticalGrid(columns = GRID_THREE_CELLS, modifier = Modifier.sidePadding()) {
                        itemThreeColumns {
                            LinkedStrings(
                                strings = vm.getLinkedStrings(screenInfo),
                                modifier = Modifier.padding(start = 0.dp),
                                separator = "<"
                            )
                        }
                        itemsIndexed(services) { i, item ->
                            CardSubService(item = item, index = i) {
                                onNavigateServicesOptionsClicked(
                                    vm.getNewScreenInfo(screenInfo, it)
                                )
                            }
                        }
                    }
                }
            }
        },
        titleScreen = screenInfo.event.name,
        showLogoFiestamas = false,
        addBottomPadding = false,
        onBackButtonClicked = { onBackClicked() },
        onTitleScreenClicked = {
            if (screenInfo.event.name.isNotEmpty()) {
                onTitleScreenClicked()
            }
        }
    )
}
