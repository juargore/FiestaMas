package com.universal.fiestamas.presentation.screens.home.services.types.sub_service.add_edit_provider

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.AddressData
import com.universal.fiestamas.presentation.screens.location.AddressAutoCompleteScreen
import com.universal.fiestamas.presentation.theme.LightBlue
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape24
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.HorizontalProgressView
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.ValidationText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddServiceProviderScreen3V2(
    onContinue: (
        address: AddressData,
        distance: Int
    ) -> Unit,
    onBackClicked: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var addressData by rememberSaveable { mutableStateOf(AddressData()) }
    var address by rememberSaveable { mutableStateOf("") }
    var showValidationAddress by remember { mutableStateOf(false) }
    var maxDistance by rememberSaveable { mutableIntStateOf(0) }
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.Expanded },
        skipHalfExpanded = true
    )

    LaunchedEffect(address) {
        showValidationAddress = address.isBlank()
    }

    GradientBackground(
        content = {
            ModalBottomSheetLayout(
                sheetState = modalSheetState,
                sheetContent = {
                    AddressAutoCompleteScreen(
                        showDistanceDropDown = true
                    ) { mAddress, distance ->
                        if (mAddress != null) {
                            address = mAddress.line1.orEmpty()
                            maxDistance = distance
                            addressData = AddressData(
                                address = mAddress.line1.orEmpty(),
                                city = mAddress.city.orEmpty(),
                                state = mAddress.state.orEmpty(),
                                postalCode = mAddress.zipcode.orEmpty(),
                                country = mAddress.country.orEmpty(),
                                latitude = mAddress.location?.lat.toString(),
                                longitude = mAddress.location?.lng.toString()
                            )
                        }
                        coroutineScope.launch { modalSheetState.hide() }
                    }
                }
            ) {
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
                            totalSelected = 6,
                            selectedColor = PinkFiestamas,
                            unselectedColor = Color.LightGray
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(0.55f)
                            .sidePadding(50.dp),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box (
                            modifier = Modifier
                                .size(130.dp)
                                .background(LightBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier.size(60.dp),
                                painter = painterResource(R.drawable.ic_map),
                                contentDescription = null
                            )
                        }
                        TextBold(
                            modifier = Modifier.padding(vertical = 15.dp),
                            text = "Ubicación",
                            size = 20.sp,
                            color = PinkFiestamas
                        )
                        RoundedEdittext(
                            placeholder = stringResource(R.string.business_address),
                            value = address,
                            isForV2 = true,
                            isEnabled = false,
                            singleLine = true,
                            textColorFocusedV2 = Color.Black,
                            textColorUnfocusedV2 = Color.DarkGray,
                            hintTextColorV2 = PinkFiestamas,
                            hintBackgroundColorV2 = Color.White,
                            focusedBorderColorV2 = PinkFiestamas,
                            unfocusedBorderColorV2 = Color.Gray,
                            onClicked = {
                                coroutineScope.launch {
                                    modalSheetState.show()
                                }
                            },
                            onValueChange = { address = it }
                        )
                        ValidationText(
                            show = showValidationAddress,
                            color = Color.Red,
                            text = "Debes ingresar una dirección",
                        )
                    }
                    Column(
                        modifier = Modifier.weight(0.45f),
                        verticalArrangement = Arrangement.Center
                    ) {
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
                                    if (!showValidationAddress) {
                                        onContinue(addressData, maxDistance)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        titleScreen = "Nuevo Servicio",
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
