package com.universal.fiestamas.presentation.screens.home.services.types.sub_service.add_edit_provider

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.AddressData
import com.universal.fiestamas.domain.models.Event
import com.universal.fiestamas.domain.models.Role
import com.universal.fiestamas.domain.models.Screen
import com.universal.fiestamas.domain.models.ScreenInfo
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.domain.models.ServiceProviderData
import com.universal.fiestamas.domain.models.ServiceType
import com.universal.fiestamas.domain.models.SubService
import com.universal.fiestamas.presentation.screens.location.AddressAutoCompleteScreen
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.home.main.MainParentClass
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.ServicesViewModel
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details.DetailsServiceViewModel
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCorners
import com.universal.fiestamas.presentation.ui.CheckboxAttributes
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.LinkedStrings
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.ValidationText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.ViewDropDownMenu
import com.universal.fiestamas.presentation.ui.backgrounds.CardServicesBackground
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.or
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.getOptionsForUnity
import com.universal.fiestamas.presentation.utils.showToast
import kotlinx.coroutines.launch
import java.util.Locale

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddEditProviderFirstStepScreen (
    vm: DetailsServiceViewModel = hiltViewModel(),
    serviceId: String?,
    screenInfo: ScreenInfo?,
    onBackClicked: () -> Unit,
    onNavigateToAddOrEditServiceProviderSecondScreenClicked: (
        screenInfo: ScreenInfo,
        ServiceProviderData,
        images: List<String>,
        videos: List<String>,
        isEditing: Boolean
    ) -> Unit
) {
    serviceId?.let { vm.getServiceDetails(it) }

    val mList = listOf(
        screenInfo?.serviceCategory?.name.orEmpty(),
        screenInfo?.serviceType?.name.orEmpty(),
        screenInfo?.subService?.name.orEmpty()
    )

    val coroutineScope = rememberCoroutineScope()
    val service: Service? by vm.service.collectAsState()
    var screenInfo2: ScreenInfo? by remember { mutableStateOf(null) }
    var addressData by rememberSaveable { mutableStateOf(AddressData()) }
    var addressDataWasModified by rememberSaveable { mutableStateOf(false) }
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.Expanded },
        skipHalfExpanded = true
    )

    BackHandler(modalSheetState.isVisible) {
        coroutineScope.launch { modalSheetState.hide() }
    }

    GradientBackground(
        content = {
            ModalBottomSheetLayout(
                sheetState = modalSheetState,
                sheetContent = {
                    AddressAutoCompleteScreen { mAddress, _ ->
                        if (mAddress != null) {
                            addressData = AddressData(
                                address = mAddress.line1.orEmpty(),
                                city = mAddress.city.orEmpty(),
                                state = mAddress.state.orEmpty(),
                                postalCode = mAddress.zipcode.orEmpty(),
                                country = mAddress.country.orEmpty(),
                                latitude = mAddress.location?.lat.toString(),
                                longitude = mAddress.location?.lng.toString()
                            )
                            addressDataWasModified = true
                        }
                        coroutineScope.launch { modalSheetState.hide() }
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .padding(bottom = 15.dp)
                        .sidePadding()
                ) {
                    if (service != null) {
                        LinkedStrings(listOf(service!!.name), Modifier.padding(start = 10.dp), small = true)
                    } else {
                        LinkedStrings(mList, Modifier.padding(start = 10.dp), small = true)
                    }

                    VerticalSpacer(10.dp)

                    if (service != null && !addressDataWasModified) {
                        with(addressData) {
                            latitude = service!!.lat.toString()
                            longitude = service!!.lng.toString()
                            address = service!!.address
                            city = ""
                            state = ""
                            postalCode = ""
                            country = ""
                        }
                    }

                    CardServicesBackground {
                        if (service != null) {
                            val subService = if (service!!.id_sub_service_type.isNullOrEmpty()) null
                            else SubService(id = service!!.id_sub_service_type!!)

                            val serviceType = if (service!!.id_service_type.isNullOrEmpty()) null
                            else ServiceType(id = service!!.id_service_type!!)

                            val serviceCategory = if (service!!.id_service_category.isBlank()) null
                            else ServiceCategory(id = service!!.id_service_category)

                            screenInfo2 = ScreenInfo(
                                role = Role.Provider,
                                startedScreen = Screen.EditServiceProvider,
                                prevScreen = Screen.EditServiceProvider,
                                event = Event(""),
                                questions = null,
                                serviceCategory = serviceCategory,
                                clientEventId = null,
                                serviceType = serviceType,
                                subService = subService,
                                service = service
                            )
                            ProviderView(
                                isEditing = true,
                                screenInfo = screenInfo2!!,
                                addressData = addressData,
                                onAddressClicked = {
                                    coroutineScope.launch { modalSheetState.show() }
                                },
                                onNavigateToAddServiceProviderClicked = { serviceProviderData ->
                                    val images = screenInfo2!!.service?.images ?: emptyList()
                                    val videos = screenInfo2!!.service?.videos ?: emptyList()

                                    screenInfo2!!.service = null
                                    screenInfo2!!.service = Service(service!!.id, service!!.name)
                                    onNavigateToAddOrEditServiceProviderSecondScreenClicked(
                                        screenInfo2!!,
                                        serviceProviderData,
                                        images,
                                        videos,
                                        true
                                    )
                                }
                            )
                        } else {
                            screenInfo?.let {
                                ProviderView(
                                    isEditing = false,
                                    screenInfo = it,
                                    addressData = addressData,
                                    onAddressClicked = {
                                        coroutineScope.launch { modalSheetState.show() }
                                    },
                                    onNavigateToAddServiceProviderClicked = { serviceProviderData ->
                                        onNavigateToAddOrEditServiceProviderSecondScreenClicked(
                                            it,
                                            serviceProviderData,
                                            emptyList(),
                                            emptyList(),
                                            false
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        titleScreen = if (service == null) "Agregar Servicio" else "Editar Servicio",
        showLogoFiestamas = false,
        addBottomPadding = false,
        onBackButtonClicked = { onBackClicked() }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProviderView(
    vm: ServicesViewModel = hiltViewModel(),
    vma: AuthViewModel = hiltViewModel(),
    screenInfo: ScreenInfo,
    addressData: AddressData,
    isEditing: Boolean,
    onAddressClicked: () -> Unit,
    onNavigateToAddServiceProviderClicked: (ServiceProviderData) -> Unit
) {
    vm.getAllServicesByProvider(MainParentClass.userId.orEmpty())

    screenInfo.service?.id?.let {
        vm.checkIfServiceHasActiveEvents(it)
    }

    screenInfo.service?.id_provider?.let {
        vma.getFirebaseProviderDb(it)
    }

    val context = LocalContext.current
    val service: Service? = screenInfo.service
    val optionsUnity = getOptionsForUnity()
    val optionsDistanceMax = listOf(
        Pair("Distancia máxima 40km", 40),
        Pair("Distancia máxima 80km", 80),
        Pair("Distancia máxima 120km", 120),
        Pair("Distancia máxima 160km", 160),
        Pair("Distancia máxima 200km", 200),
        Pair("Distancia máxima 240km", 240),
        Pair("Distancia máxima 280km", 280),
        Pair("Distancia máxima 320km", 320),
        Pair("Distancia máxima 360km", 360),
        Pair("Distancia máxima 400km", 400)
    )

    var suggestedAttribute by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable {
        mutableStateOf(if (isEditing) service?.name.orEmpty() else "")
    }

    var description by rememberSaveable {
        mutableStateOf(if (isEditing) service?.description.orEmpty() else "")
    }
    var min by rememberSaveable {
        mutableStateOf(if (isEditing) service?.min_attendees.toString() else "")
    }
    var max by rememberSaveable {
        mutableStateOf(if (isEditing) service?.max_attendees.toString() else "")
    }
    var cost by rememberSaveable {
        mutableStateOf(if (isEditing) service?.price.toString() else "")
    }

    var providerAddress by remember { mutableStateOf("") }
    var providerLat by remember { mutableStateOf("") }
    var providerLng by remember { mutableStateOf("") }

    // pz | person | kg | event
    var unit by rememberSaveable { mutableStateOf(optionsUnity.first()) }

    var distance by rememberSaveable {
        mutableIntStateOf(
            service?.distance?.takeIf { isEditing && it > 0 }
                ?: optionsDistanceMax.first().second
        )
    }

    val serviceName = if (screenInfo.subService != null) {
        vm.getAttributesBySubServiceId(screenInfo.subService.id)
        screenInfo.subService.name
    } else if (screenInfo.serviceType != null) {
        vm.getAttributesByServiceTypeId(screenInfo.serviceType.id)
        screenInfo.serviceType.name
    } else {
        screenInfo.serviceCategory?.id?.let { idServiceCategory ->
            vm.getAttributesByServiceCategoryId(idServiceCategory)
        }.or {
            showToast(context, "Error: Id es null!")
        }
        screenInfo.serviceCategory?.name.orEmpty()
    }

    var showValidationName by remember { mutableStateOf(false) }
    var showValidationDescription by remember { mutableStateOf(false) }
    var showValidationMin by remember { mutableStateOf(false) }
    var showValidationMax by remember { mutableStateOf(false) }
    var showValidationMinMax by remember { mutableStateOf(false) }
    var showValidationCost by remember { mutableStateOf(false) }
    var textValidationCost by remember { mutableStateOf("") }
    val attributes by vm.attributes.collectAsState()
    val providerDb by vma.firebaseProviderDb.collectAsState()
    val allServices by vm.allServicesProvider.collectAsState()
    val serviceHasActiveEvents by vm.serviceHasActiveEvents.collectAsState()

    providerDb?.let { provider ->
        provider.address?.let { providerAddress = it }
        provider.lat?.let { providerLat = it }
        provider.lng?.let { providerLng = it }
    }

    Column(modifier = Modifier.padding(15.dp)) {
        VerticalSpacer(height = 10.dp)
        val string = if (isEditing) {
            stringResource(R.string.add_service_type)
        } else {
            stringResource(R.string.add_services_types, serviceName)
        }
        TextSemiBold(
            text = string,
            color = PinkFiestamas,
            size = 18.sp.autoSize(),
            verticalSpace = 28.sp
        )
        VerticalSpacer(height = 5.dp)

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .sidePadding(12.dp)
                .padding(vertical = 15.dp)
        ) {
            FlowRow {
                attributes?.forEachIndexed { _, attribute ->
                    var startSelected = false
                    if (isEditing) {
                        startSelected = service?.attributes?.contains(attribute.id) ?: false
                        if (startSelected) {
                            vm.attributesSelected.add(attribute.id)
                        }
                    }

                    if (!startSelected) {
                        startSelected = vm.attributesSelected.contains(attribute.name)
                    }

                    CheckboxAttributes(
                        startChecked = startSelected,
                        content = {
                            TextRegular(
                                text = attribute.name,
                                fillMaxWidth = false,
                                align = TextAlign.Start,
                                size = 16.sp.autoSize(),
                                verticalSpace = 16.sp.autoSize()
                            )
                        }
                    ) { checked ->
                        if (checked) {
                            attribute.id.let {
                                vm.attributesSelected.add(it)
                            }
                        } else {
                            attribute.id.let {
                                vm.attributesSelected.remove(it)
                            }
                        }
                    }
                }
            }

            VerticalSpacer(10.dp)

            TextRegular(
                size = 14.sp.autoSize(),
                text = stringResource(id = R.string.add_service_suggested_attribute)
            )
            RoundedEdittext(
                placeholder = "",
                value = suggestedAttribute
            ) { suggestedAttribute = it }

            VerticalSpacer(25.dp)
            TextSemiBold(
                text = stringResource(id = R.string.add_service_info),
                color = PinkFiestamas,
                size = 18.sp.autoSize()
            )
            VerticalSpacer(10.dp)

            RoundedEdittext(
                placeholder = stringResource(id = R.string.business_contact_business_name),
                value = name,
                onValueChange = {
                    name = it
                    showValidationName = it.isBlank()
                }
            )

            ValidationText(show = showValidationName, text = context.getString(R.string.gral_error_empty, "El nombre"))

            VerticalSpacer(7.dp)

            if (serviceHasActiveEvents == true) {
                Row(modifier = Modifier
                    .sidePadding()
                    .padding(vertical = 12.dp)
                ) {
                    Image(
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        painter = painterResource( id = R.drawable.ic_info)
                    )
                    HorizontalSpacer(width = 10.dp)
                    TextRegular(
                        text = stringResource(R.string.profile_to_modify_finalize_services),
                        size = 12.sp.autoSize(),
                        verticalSpace = 16.sp.autoSize()
                    )
                }
            } else {
                RoundedEdittext(
                    placeholder = stringResource(R.string.business_contact_address),
                    value = addressData.address,
                    isEnabled = false,
                    singleLine = true,
                    onClicked = { onAddressClicked() },
                    onValueChange = { addressData.address = it }
                )
            }

            VerticalSpacer(7.dp)

            RoundedEdittext(
                placeholder = stringResource(id = R.string.add_service_description),
                value = description
            ) {
                description = it
                showValidationDescription = it.isBlank()
            }

            ValidationText(show = showValidationDescription, text = context.getString(R.string.gral_error_empty, "El campo descripción"))

            VerticalSpacer(7.dp)

            RoundedEdittext(
                placeholder = stringResource(id = R.string.add_service_min_people),
                keyboardType = KeyboardType.Number,
                value = min
            ) {
                min = it
                showValidationMin = it.isBlank()
                if (max.isNotEmpty() && min.isNotEmpty()) {
                    val maxInt = max.toInt()
                    val minInt = min.toInt()
                    showValidationMinMax = minInt > maxInt
                }
            }

            if (showValidationMin || showValidationMinMax) {
                VerticalSpacer(height = 2.dp)
                TextRegular(
                    text = if (showValidationMin) {
                        context.getString(R.string.gral_error_empty, "El mínimo de asistentes")
                    } else {
                        context.getString(R.string.add_service_max_error)
                    },
                    size = 13.sp.autoSize(),
                    color = Color.Red
                )
            }

            VerticalSpacer(7.dp)

            RoundedEdittext(
                placeholder = stringResource(id = R.string.add_service_max_people),
                keyboardType = KeyboardType.Number,
                value = max
            ) {
                max = it
                showValidationMax = it.isBlank()
                if (max.isNotEmpty() && min.isNotEmpty()) {
                    val maxInt = max.toInt()
                    val minInt = min.toInt()
                    showValidationMinMax = minInt > maxInt
                }
            }

            if (showValidationMax || showValidationMinMax) {
                VerticalSpacer(height = 2.dp)
                TextRegular(
                    text = if (showValidationMax) {
                        context.getString(R.string.gral_error_empty, "El máximo de asistentes")
                    } else {
                        context.getString(R.string.add_service_max_error)
                    },
                    size = 13.sp.autoSize(),
                    color = Color.Red
                )
            }

            VerticalSpacer(14.dp)

            RoundedEdittext(
                placeholder = stringResource(id = R.string.add_service_price),
                keyboardType = KeyboardType.Number,
                value = cost
            ) {
                cost = it
                showValidationCost = it.isBlank()
                textValidationCost = context.getString(R.string.gral_error_empty, "El costo")
                if (it.isNotEmpty() && it.toInt() < 1) {
                    textValidationCost = context.getString(R.string.gral_error_zero, "El costo")
                    showValidationCost = true
                }
            }

            ValidationText(show = showValidationCost, text = textValidationCost)

            VerticalSpacer(7.dp)

            ViewDropDownMenu(
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(id = R.string.service_per_unity),
                options = optionsUnity
            ) { unit = it }

            VerticalSpacer(7.dp)

            ViewDropDownMenu(
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(id = R.string.service_distance_max),
                options = optionsDistanceMax.map { it.first },
                startedIndexSelected = optionsDistanceMax.indexOfFirst { pair ->
                    pair.second == distance
                },
                onItemSelected = { option ->
                    distance = optionsDistanceMax.find { it.first == option }?.second!!
                }
            )

            VerticalSpacer(32.dp)

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ButtonPinkRoundedCorners(text = stringResource(R.string.gral_continue)) {
                    allServices.forEach { service ->
                        val cServiceType = service.id_service_type.orEmpty()
                        val cServiceCategory = service.id_service_category
                        val cSubService = service.id_sub_service_type.orEmpty()
                        val cServiceName = service.name
                            .trim()
                            .lowercase(Locale.getDefault())
                            .replace("  ", " ")

                        val serviceType = screenInfo.serviceType?.id.orEmpty()
                        val serviceCategory = screenInfo.serviceCategory?.id.orEmpty()
                        val subService = screenInfo.subService?.id.orEmpty()
                        val serviceNameFromTextField = name
                            .trim()
                            .lowercase(Locale.getDefault())
                            .replace("  ", " ")

                        if (cServiceName == serviceNameFromTextField &&
                            cServiceType == serviceType &&
                            cServiceCategory == serviceCategory &&
                            cSubService == subService
                        ) {
                            if (!isEditing) {
                                showToast(context, context.getString(R.string.add_service_choose_other_service_name))
                                return@ButtonPinkRoundedCorners
                            }
                        }
                    }

                    if (name.isBlank()) {
                        showToast(context, context.getString(R.string.gral_error_empty, "El nombre"))
                        return@ButtonPinkRoundedCorners
                    }
                    if (addressData.address.isBlank()) {
                        showToast(context, context.getString(R.string.gral_error_empty, "El campo dirección"))
                        return@ButtonPinkRoundedCorners
                    }
                    if (description.isBlank()) {
                        showToast(context, context.getString(R.string.gral_error_empty, "El campo descripción"))
                        return@ButtonPinkRoundedCorners
                    }
                    if (min.isBlank()) {
                        showToast(context, context.getString(R.string.gral_error_empty, "El mínimo de asistentes"))
                        return@ButtonPinkRoundedCorners
                    }
                    if (max.isBlank()) {
                        showToast(context, context.getString(R.string.gral_error_empty, "El máximo de asistentes"))
                        return@ButtonPinkRoundedCorners
                    }
                    if (min.ifEmpty { "0" }.toInt() > max.ifEmpty { "0" }.toInt()) {
                        showToast(context, context.getString(R.string.add_service_max_error))
                        return@ButtonPinkRoundedCorners
                    }
                    if (cost.isBlank()) {
                        showToast(context, context.getString(R.string.gral_error_empty, "El costo"))
                        return@ButtonPinkRoundedCorners
                    }
                    if (cost.isNotEmpty() && cost.toInt() < 1) {
                        textValidationCost = context.getString(R.string.gral_error_zero, "El costo")
                        showToast(context, textValidationCost)
                        return@ButtonPinkRoundedCorners
                    }
                    if (vm.attributesSelected.isEmpty()) {
                        showToast(context, context.getString(R.string.add_service_select_attribute))
                        return@ButtonPinkRoundedCorners
                    }

                    val data = ServiceProviderData(
                        name = name,
                        addressData = addressData,
                        description = description,
                        minCapacity = min,
                        maxCapacity = max,
                        cost = cost,
                        unit = unit,
                        distance = distance,
                        attributes = vm.attributesSelected.toList(),
                        suggestedAttribute = suggestedAttribute
                    )
                    onNavigateToAddServiceProviderClicked(data)
                }
            }
        }
    }
}
