package com.universal.fiestamas.presentation.screens.location

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.PlaceAutocomplete
import com.universal.fiestamas.presentation.ui.CardGooglePlace
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.ViewDropDownMenu
import com.universal.fiestamas.presentation.ui.calendar.clickable
import com.universal.fiestamas.presentation.ui.dialogs.MapDialog
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.getOptionsDistanceForDropDown
import com.universal.fiestamas.presentation.utils.showToast

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddressAutoCompleteScreen(
    vm: AddressAutoCompleteViewModel = hiltViewModel(),
    searchForCities: Boolean = false,
    showMapOption: Boolean = false,
    showDistanceDropDown: Boolean = false,
    onAddressSelected: (
        address: Address?,
        distance: Int
    ) -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var query by rememberSaveable { mutableStateOf("") }
    var showMapDialog by remember { mutableStateOf(false) }
    val places by vm.placesList.collectAsState()
    val address by vm.address.collectAsState()
    val optionsDistance = getOptionsDistanceForDropDown(false)
    var distance by rememberSaveable { mutableIntStateOf(optionsDistance.first().second) }

    address?.let {
        keyboardController?.hide()
        query = ""
        onAddressSelected(it, distance)
        vm.resetAddress()
    }

    MapDialog(
        isVisible = showMapDialog,
        onDismiss = { showMapDialog = false },
        onAddressSelected = {
            showMapDialog = false
            onAddressSelected(it, distance)
        }
    )

    Box {
        Column(modifier = Modifier
            .fillMaxSize()
            .align(Alignment.TopCenter)
            .sidePadding(25.dp)
        ) {
            VerticalSpacer(25.dp)

            TextMedium(
                text = stringResource(id = R.string.autocomplete_title),
                size = 16.sp.autoSize()
            )

            VerticalSpacer(5.dp)

            if (showDistanceDropDown) {
                ViewDropDownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = stringResource(id = R.string.service_distance_max),
                    options = optionsDistance.map { it.first },
                    startedIndexSelected = optionsDistance.indexOfFirst { pair ->
                        pair.second == distance
                    },
                    onItemSelected = { option ->
                        distance = optionsDistance.find { it.first == option }?.second!!
                    }
                )
                VerticalSpacer(15.dp)
            }

            RoundedEdittext(
                placeholder = stringResource(id = R.string.autocomplete_search),
                value = query
            ) {
                query = it
                if (searchForCities) {
                    vm.findCityByQuery(query)
                } else {
                    vm.findPlaceByQuery(query)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .background(Color.White),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(5.dp),
            ) {
                items(places) { place ->
                    CardGooglePlace(place) {
                        if (searchForCities) {
                            vm.getAddressByPlaceId(it.placeId)
                        } else {
                            val hasNumber = it.fullAddress.contains(Regex("\\d+"))
                            if (hasNumber) {
                                vm.getAddressByPlaceId(it.placeId)
                            } else {
                                showToast(context, "Seleccione una dirección completa del buscador")
                            }
                        }
                    }
                }
            }

            // popup that shows the map to select address
            if (showMapOption) {
                CardGooglePlace(
                    item = PlaceAutocomplete(
                        placeId = "",
                        primaryText = stringResource(R.string.autocomplete_map_item_title),
                        secondaryText = stringResource(R.string.autocomplete_map_item_subtitle),
                        fullAddress = ""
                    )
                ) {
                    showMapDialog = true
                }
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextSemiBold(
                fillMaxWidth = false,
                text = stringResource(id = R.string.gral_cancel),
                size = 16.sp.autoSize(),
                color = Color.Gray,
                modifier = Modifier.clickable {
                    onAddressSelected(null, distance)
                    vm.resetAddress()
                    query = ""
                }
            )
            VerticalSpacer(height = 20.dp)
            Image(
                painter = painterResource(id = R.drawable.img_powered_by_google),
                modifier = Modifier.height(16.dp.autoSize()),
                contentDescription = null
            )
            VerticalSpacer(32.dp)
        }
    }
}
