package com.universal.fiestamas.presentation.screens.home.services.types.sub_service

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
import com.universal.fiestamas.domain.models.ServiceType
import com.universal.fiestamas.domain.models.SubService
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.ui.CardGeneralServiceV2
import com.universal.fiestamas.presentation.ui.HorizontalProgressView
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.utils.Constants.GRID_TWO_CELLS
import com.universal.fiestamas.presentation.utils.extensions.sidePadding

@Composable
fun SubServicesScreenV2(
    vm: SubServicesViewModel = hiltViewModel(),
    serviceType: ServiceType,
    onNavigateAddServiceProvider1V2: (SubService) -> Unit,
    onBackClicked: () -> Unit
) {

    vm.getSubServicesByServiceTypeId(serviceType.id)

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
                        totalSelected = 3,
                        selectedColor = PinkFiestamas,
                        unselectedColor = Color.LightGray
                    )
                }

                TextBold(
                    modifier = Modifier.padding(top = 15.dp),
                    text = "Selecciona el subtipo de servicio",
                    size = 20.sp,
                    color = PinkFiestamas
                )

                vm.subServices.collectAsState().value?.let { services ->
                    LazyVerticalGrid(
                        columns = GRID_TWO_CELLS,
                        modifier = Modifier.sidePadding(50.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        items(services) { item ->
                            CardGeneralServiceV2(
                                image = item?.icon.orEmpty(),
                                text = item?.name.orEmpty(),
                                onItemClick = {
                                    item?.let { onNavigateAddServiceProvider1V2(it) }
                                }
                            )
                        }
                    }
                }
            }
        },
        titleScreen = "Subtipo - Servicio",
        titleScreenColor = Color.White,
        isPinkBackground = true,
        showBackButton = true,
        backButtonColor = Color.White,
        showLogoFiestamas = false,
        showUserName = false,
        addBottomPadding = false,
        onBackButtonClicked = { onBackClicked() }
    )
}
