package com.universal.fiestamas.presentation.screens.home.services.types.sub_service.add_edit_provider

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.domain.models.Attribute
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.domain.models.ServiceType
import com.universal.fiestamas.domain.models.SubService
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.ServicesViewModel
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape24
import com.universal.fiestamas.presentation.theme.topRoundedCornerShape15
import com.universal.fiestamas.presentation.ui.AttributeViewV2
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.HorizontalProgressView
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.bottom_sheets.BottomSheetAttributeSuggestion
import com.universal.fiestamas.presentation.utils.Constants.GRID_TWO_CELLS
import com.universal.fiestamas.presentation.utils.extensions.itemTwoColumns
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.showToast
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddServiceProviderScreen1V2(
    vm: ServicesViewModel = hiltViewModel(),
    serviceCategory: ServiceCategory,
    serviceType: ServiceType?,
    subService: SubService?,
    onContinue: (
        selectedAttributes: List<Attribute>,
        suggestedAttributes: List<String>,
    ) -> Unit,
    onBackClicked: () -> Unit
) {

    /*if (subService != null) {
        vm.getAttributesBySubServiceId(subService.id)
    } else*/
    if (serviceType != null) {
        vm.getAttributesByServiceTypeId(serviceType.id)
    } else {
        vm.getAttributesByServiceCategoryId(serviceCategory.id)
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var suggestedAttributes: List<String> = listOf()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    val attributes = vm.attributes.collectAsState()
    val serviceName = subService?.name ?: serviceType?.name ?: serviceCategory.name

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = topRoundedCornerShape15,
        sheetContent = {
            BottomSheetAttributeSuggestion(
                title = serviceName,
                onContinue = { attributesList ->
                    suggestedAttributes = attributesList
                },
                onClose = {
                    coroutineScope.launch { modalSheetState.hide() }
                }
            )
        }
    ) {
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
                            totalSelected = 4,
                            selectedColor = PinkFiestamas,
                            unselectedColor = Color.LightGray
                        )
                    }

                    TextBold(
                        modifier = Modifier.padding(top = 15.dp),
                        text = "Selecciona el tipo de $serviceName que ofreces",
                        size = 20.sp,
                        verticalSpace = 18.sp,
                        color = PinkFiestamas
                    )

                    attributes.value?.let { attributes ->
                        LazyVerticalGrid(
                            columns = GRID_TWO_CELLS,
                            modifier = Modifier.sidePadding(35.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement =  Arrangement.spacedBy(12.dp)
                        ) {
                            items(attributes) { item ->
                                AttributeViewV2(
                                    text = item.name,
                                    isChecked = item.isSelected,
                                    onClicked = {
                                        vm.markAttributeAsSelected(item)
                                    }
                                )
                            }
                            itemTwoColumns {
                                VerticalSpacer(height = 15.dp)
                            }
                            itemTwoColumns {
                                TextMedium(
                                    text = "No encontraste tu tipo...",
                                    size = 14.sp
                                )
                            }
                            itemTwoColumns {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .sidePadding(20.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .height(50.dp)
                                            .background(Color.White, allRoundedCornerShape24)
                                            .border(1.dp, PinkFiestamas, allRoundedCornerShape24)
                                            .clip(allRoundedCornerShape24)
                                            .clickable {
                                                coroutineScope.launch { modalSheetState.show() }
                                            },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextMedium(
                                            modifier = Modifier.padding(end = 4.dp),
                                            text = "Hacer sugerencia".uppercase(),
                                            size = 16.sp,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                            itemTwoColumns {
                                VerticalSpacer(height = 30.dp)
                            }
                            itemTwoColumns {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    ButtonPinkRoundedCornersV2(
                                        verticalPadding = 14.dp,
                                        horizontalPadding = 45.dp,
                                        shape = allRoundedCornerShape24,
                                        content = {
                                            TextBold(
                                                text = "Continuar".uppercase(),
                                                size = 17.sp,
                                                color = Color.White,
                                                fillMaxWidth = false
                                            )
                                        },
                                        onClick = {
                                            val attributesFromList = attributes.filter { it.isSelected }
                                            if (attributesFromList.isEmpty() && suggestedAttributes.isEmpty()) {
                                                showToast(context, "Selecciona al menos un atributo o sugiere uno para continuar")
                                                return@ButtonPinkRoundedCornersV2
                                            }
                                            onContinue(attributesFromList, suggestedAttributes)
                                        }
                                    )
                                }
                            }
                            itemTwoColumns {
                                VerticalSpacer(height = 20.dp)
                            }
                        }
                    }
                }
            },
            titleScreen = "Servicio - Tipo",
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
}
