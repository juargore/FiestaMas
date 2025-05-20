package com.universal.fiestamas.presentation.screens.home.services

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.ui.CardGeneralServiceV2
import com.universal.fiestamas.presentation.ui.HorizontalProgressView
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.utils.Constants.GRID_TWO_CELLS
import com.universal.fiestamas.presentation.utils.extensions.sidePadding

@Composable
fun ServicesCategoriesScreenV2(
    vm: ServicesCategoriesViewModel = hiltViewModel(),
    onNavigateServicesTypesV2: (
        serviceCategory: ServiceCategory
    ) -> Unit,
    onBackClicked: () -> Unit
) {

    vm.getAllServicesCategories()

    GradientBackground(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                VerticalSpacer(height = 10.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    HorizontalProgressView(
                        totalBars = 8,
                        totalSelected = 1,
                        selectedColor = PinkFiestamas,
                        unselectedColor = Color.LightGray
                    )
                }

                TextBold(
                    modifier = Modifier.padding(top = 15.dp),
                    text = "Selecciona el servicio",
                    size = 20.sp,
                    color = PinkFiestamas
                )

                vm.servicesByEvent.collectAsState().value?.let { events ->
                    LazyVerticalGrid(
                        columns = GRID_TWO_CELLS,
                        modifier = Modifier.sidePadding(50.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(events) { item ->
                            CardGeneralServiceV2(
                                image = item?.icon.orEmpty(),
                                text = item?.name.orEmpty(),
                                onItemClick = {
                                    item?.let { onNavigateServicesTypesV2(item) }
                                }
                            )
                        }
                    }
                }
            }
        },
        titleScreen = "Categoría - Servicio",
        titleScreenColor = Color.White,
        isPinkBackground = true,
        showBackButton = false,
        showLogoFiestamas = false,
        showUserName = false,
        addBottomPadding = false,
        onBackButtonClicked = { onBackClicked() }
    )
}
