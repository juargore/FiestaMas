package com.universal.fiestamas.presentation.ui.bottom_sheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.domain.models.ServiceType
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.home.main.search.SearchViewModel
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape16
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.RegularEditText
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.TopDecorationBottomSheet
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.ViewDropDownMenuV2
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.getOptionsDistanceForDropDown
import com.universal.fiestamas.presentation.utils.getOptionsForUnity
import com.universal.fiestamas.presentation.utils.showToast


data class StoredValuesForFilters (
    val category: ServiceCategory?,
    val type: ServiceType?,
    val addressFromMap: Address?,
    val distance: Pair<String, Int>?,
    val unity: String?,
    val minCapacity: String?,
    val maxCapacity: String?
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BottomSheetSearchFilters(
    vm: SearchViewModel = hiltViewModel(),
    vma: AuthViewModel = hiltViewModel(),
    storedValues: StoredValuesForFilters,
    onFilterApplied: (
        category: ServiceCategory?,
        type: ServiceType?,
        distance: Pair<String, Int>,
        unity: String,
        minCapacity: String,
        maxCapacity: String
    ) -> Unit,
    onShowAutoComplete: (StoredValuesForFilters) -> Unit,
    onReset: () -> Unit
) {
    vm.getAllServicesCategories()

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val servicesByEvent by vm.servicesByEvent.collectAsState()
    val servicesByCategory by vm.servicesByCategory.collectAsState()

    val optionsDistanceMax = getOptionsDistanceForDropDown(true)
    val optionsUnity = getOptionsForUnity(true)
    val emptyCategory = ServiceCategory("0000", "Seleccionar")
    var citySelected: String? by remember { mutableStateOf(storedValues.addressFromMap?.city) }
    var addressSelected: Address? by remember { mutableStateOf(storedValues.addressFromMap) }
    var selectedCategory: ServiceCategory? by remember { mutableStateOf(storedValues.category) }
    var selectedType: ServiceType? by remember { mutableStateOf(storedValues.type ?: servicesByCategory.firstOrNull()) }
    var selectedDistance: Pair<String, Int>? by remember { mutableStateOf(storedValues.distance ?: optionsDistanceMax.first()) }
    var selectedUnity: String? by remember { mutableStateOf(storedValues.unity ?: optionsUnity.first()) }
    var minCapacity by remember { mutableStateOf(storedValues.minCapacity) }
    var maxCapacity by remember { mutableStateOf(storedValues.maxCapacity) }

    if (storedValues.addressFromMap == null) {
        vma.getUserLocation {
            citySelected = it?.city ?: "Desconocido"
            addressSelected = it
        }
    }

    LaunchedEffect(servicesByEvent) {
        if (selectedCategory == null || selectedCategory == ServiceCategory()) {
            selectedCategory = servicesByEvent.firstOrNull()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalSpacer(4.dp.autoSize())
        TopDecorationBottomSheet()

        TextSemiBold(
            modifier = Modifier.padding(vertical = 15.dp),
            text = "Filtros", color = PinkFiestamas
        )

        FilterRow(
            leftString = "Categoría",
            rightString = selectedCategory?.name.orEmpty(),
            initialOptionSelected = selectedCategory ?: servicesByEvent.firstOrNull(),
            options = servicesByEvent,
            onOptionSelected = {
                selectedCategory = it
                if (selectedCategory != emptyCategory) {
                    vm.getServicesByCategoryId(it?.id.orEmpty())
                }
            }
        )

        if (selectedCategory != emptyCategory) {
            FilterRow(
                leftString = "Tipo",
                options = servicesByCategory,
                initialOptionSelected = selectedType ?: servicesByCategory.firstOrNull(),
                rightString = selectedType?.name.orEmpty(),
                onOptionSelected = { selectedType = it }
            )
        }

        FilterRowLocation(
            rightString = if (citySelected == null) "Obteniendo ubicación..." else citySelected.orEmpty(),
            onLocationClicked = {
                onShowAutoComplete(
                    StoredValuesForFilters(
                        selectedCategory,
                        selectedType,
                        addressSelected,
                        selectedDistance,
                        selectedUnity,
                        minCapacity,
                        maxCapacity
                    )
                )
            }
        )

        if (citySelected != null) {
            FilterRow(
                leftString = "Distancia",
                rightString = selectedDistance?.first.orEmpty(),
                options = optionsDistanceMax,
                initialOptionSelected = selectedDistance ?: optionsDistanceMax.first()
            ) {
                selectedDistance = it
            }
        }

        FilterRow(
            leftString = "Unidad",
            rightString = optionsUnity.first(),
            options = optionsUnity,
            initialOptionSelected = selectedUnity ?: optionsUnity.first()
        ) {
            selectedUnity = it
        }

        FilterRowDoubleEt(
            leftString = "Capacidad",
            savedString1 = "",
            savedString2 = "",
            onValueChanged1 = { minCapacity = it },
            onValueChanged2 = { maxCapacity = it }
        )
        
        VerticalSpacer(height = 14.dp)

        Row(modifier = Modifier.sidePadding()) {
            ButtonCleanFilter(
                modifier = Modifier.weight(0.5f)
            ) {
                selectedCategory = servicesByEvent.firstOrNull()
                selectedType = null
                selectedDistance = optionsDistanceMax.first()
                onReset()
            }
            HorizontalSpacer(10.dp)
            ButtonApplyFilter(
                modifier = Modifier.weight(0.5f)
            ) {
                val mMin = minCapacity?.toIntOrNull() ?: 0
                val mMax = maxCapacity?.toIntOrNull() ?: 0

                if (mMin > mMax) {
                    showToast(context, "El valor mínimo no puede ser mayor al máximo")
                } else {
                    keyboardController?.hide()
                    onFilterApplied(
                        selectedCategory,
                        selectedType,
                        selectedDistance ?: optionsDistanceMax.first(),
                        selectedUnity.orEmpty(),
                        minCapacity.orEmpty(),
                        maxCapacity.orEmpty()
                    )
                }
            }
        }

        VerticalSpacer(height = 12.dp)
    }
}

@Composable
fun <T> FilterRow(
    leftString: String,
    rightString: String,
    options: List<T>,
    initialOptionSelected: T,
    onOptionSelected: ((T) -> Unit)? = null
) {

    var optionSelected: T by remember { mutableStateOf(initialOptionSelected) }

    Row(modifier = Modifier.fillMaxWidth()) {
        TextMedium(
            modifier = Modifier
                .weight(0.4f)
                .sidePadding(),
            text = leftString,
            color = Color.DarkGray,
            align = TextAlign.Start
        )
        Row(
            modifier = Modifier
                .weight(0.6f)
                .padding(end = 9.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (options.isNotEmpty()) {
                ViewDropDownMenuV2(
                    modifier = Modifier.height(40.dp),
                    fontSize = 15.sp,
                    initialValue = initialOptionSelected,
                    icon = {
                        Image(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(id = R.drawable.ic_arrows_up_down),
                            contentDescription = null
                        )
                    },
                    options = options,
                    onValueChange = {
                        optionSelected = it
                        onOptionSelected?.invoke(it)
                    },
                    optionLabel = {
                        when (it) {
                            is ServiceCategory -> it.name
                            is ServiceType -> it.name
                            is Pair<*, *> -> it.first as String
                            else -> it.toString()
                        }
                    }
                )
            }

            if (leftString == "Ubicación") {
                TextMedium(
                    modifier = Modifier
                        .weight(0.4f)
                        .sidePadding(),
                    text = rightString,
                    align = TextAlign.End
                )
                Image(
                    contentDescription = null,
                    painter = painterResource(id = R.drawable.ic_location),
                    modifier = Modifier.size(16.dp.autoSize())
                )
            }

            HorizontalSpacer(12.dp)
        }
    }

    VerticalSpacer(10.dp)
}

@Composable
fun FilterRowDoubleEt(
    leftString: String,
    savedString1: String,
    savedString2: String,
    placeHolder1: String = "Mínimo",
    placeHolder2: String = "Máximo",
    onValueChanged1: (String) -> Unit,
    onValueChanged2: (String) -> Unit
) {

    var stringOne by remember { mutableStateOf(savedString1) }
    var stringTwo by remember { mutableStateOf(savedString2) }

    Row(modifier = Modifier.fillMaxWidth()) {
        TextMedium(
            modifier = Modifier
                .weight(0.4f)
                .sidePadding(),
            text = leftString,
            color = Color.DarkGray,
            align = TextAlign.Start
        )
        Row(
            modifier = Modifier
                .weight(0.6f)
                .padding(end = 9.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RegularEditText(
                modifier = Modifier.weight(.5f),
                textSize = 15.sp,
                value = stringOne,
                placeholder = placeHolder1,
                keyboardType = KeyboardType.Number,
                paddingVertical = 7.dp,
                onValueChange = {
                    stringOne = it
                    onValueChanged1(it)
                }
            )
            HorizontalSpacer(width = 6.dp)
            RegularEditText(
                modifier = Modifier.weight(.5f),
                textSize = 15.sp,
                value = stringTwo,
                placeholder = placeHolder2,
                keyboardType = KeyboardType.Number,
                paddingVertical = 7.dp,
                onValueChange = {
                    stringTwo = it
                    onValueChanged2(it)
                }
            )
        }
    }
}

@Composable
fun FilterRowLocation(
    rightString: String,
    onLocationClicked: () -> Unit
) {

    Row(modifier = Modifier.fillMaxWidth()) {
        TextMedium(
            modifier = Modifier
                .weight(0.4f)
                .sidePadding(),
            text = "Ubicación",
            color = Color.DarkGray,
            align = TextAlign.Start
        )
        Row(
            modifier = Modifier
                .weight(0.6f)
                .clickable {
                    onLocationClicked()
                },
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextMedium(
                modifier = Modifier
                    .weight(0.4f)
                    .sidePadding(),
                text = rightString,
                align = TextAlign.End
            )
            Image(
                contentDescription = null,
                painter = painterResource(id = R.drawable.ic_location),
                modifier = Modifier.size(16.dp.autoSize())
            )

            HorizontalSpacer(12.dp)
        }
    }

    VerticalSpacer(10.dp)
}

@Composable
fun ButtonCleanFilter(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(allRoundedCornerShape16)
            .border(1.dp, PinkFiestamas, allRoundedCornerShape16)
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        TextSemiBold(
            text = "LIMPIAR",
            maxLines = 1,
            color = PinkFiestamas,
            fillMaxWidth = false
        )
    }
}

@Composable
fun ButtonApplyFilter(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(allRoundedCornerShape16)
            .border(1.dp, PinkFiestamas, allRoundedCornerShape16)
            .background(PinkFiestamas)
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        TextSemiBold(
            text = "FILTRAR",
            maxLines = 1,
            color = Color.White,
            fillMaxWidth = false
        )
    }
}
