package com.universal.fiestamas.presentation.screens.auth

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.GoogleUserData
import com.universal.fiestamas.domain.models.LoginAccount
import com.universal.fiestamas.domain.models.request.GoogleProviderRequest
import com.universal.fiestamas.domain.models.request.ProviderRequest
import com.universal.fiestamas.presentation.screens.location.AddressAutoCompleteScreen
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCorners
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.RoundedPhoneEdittext
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.ValidationText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.CardAuthBackground
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.dialogs.PhoneAlreadyExistsDialog
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.utils.Constants.HALF_SECOND
import com.universal.fiestamas.presentation.utils.Constants.PROVIDER
import com.universal.fiestamas.presentation.utils.cleanPhoneNumberNewFormat
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.resetApplication
import com.universal.fiestamas.presentation.utils.isValidPhoneNumberNewFormat
import com.universal.fiestamas.presentation.utils.isValidRFC
import com.universal.fiestamas.presentation.utils.showToast
import com.universal.fiestamas.presentation.utils.showToastOnUiThread
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateProviderScreen(
    vm: AuthViewModel = hiltViewModel(),
    @Suppress("UNUSED_PARAMETER") refreshAppIfAccountIsCreated: Boolean,
    email: String,
    googleUserData: GoogleUserData?,
    password: String,
    @Suppress("UNUSED_PARAMETER") onSuccessLogin: () -> Unit,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var address by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var state by rememberSaveable { mutableStateOf("") }
    var postalCode by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf("") }
    var latitude by rememberSaveable { mutableStateOf("") }
    var longitude by rememberSaveable { mutableStateOf("") }

    var showValidationBusinessName by remember { mutableStateOf(false) }
    var showValidationTextName by remember { mutableStateOf(false) }
    var showValidationTextLastName by remember { mutableStateOf(false) }
    var showValidationTextAddress by remember { mutableStateOf(false) }
    var showValidationTextPhone1 by remember { mutableStateOf(false) }
    var showValidationTextPhone2 by remember { mutableStateOf(false) }

    var businessName by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf(googleUserData?.userName.orEmpty()) }
    var lastName by rememberSaveable { mutableStateOf(googleUserData?.userLastName.orEmpty()) }
    var mobilePhone by rememberSaveable { mutableStateOf("") }
    var homePhone by rememberSaveable { mutableStateOf("") }
    var rfc by rememberSaveable { mutableStateOf("") }
    var showProgressDialog by remember { mutableStateOf(false) }
    var showPhoneAlreadyExistsDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    var currentOnStop by remember { mutableStateOf(false) }
    var currentOnCreate by remember { mutableStateOf(false) }
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.Expanded },
        skipHalfExpanded = true
    )

    /** Store and retrieve the data from client */

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            currentOnStop = event == Lifecycle.Event.ON_STOP
            currentOnCreate = event == Lifecycle.Event.ON_RESUME
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(currentOnStop, currentOnCreate) {
        if (currentOnStop) {
            vm.storeProviderDataInShPrefs(
                ProviderRequest(
                    role = PROVIDER,
                    name = name,
                    email = email,
                    phone_one = mobilePhone.cleanPhoneNumberNewFormat(),
                    phone_two = homePhone.cleanPhoneNumberNewFormat(),
                    business_name = businessName,
                    state = state,
                    city = city,
                    cp = postalCode,
                    rfc = rfc.trim(),
                    country = "Mexico",
                    password = password,
                    last_name = lastName,
                    lat = latitude,
                    lng = longitude,
                    address = address
                )
            )
        }
        if (currentOnCreate) {
            vm.getStoredProviderDataInShPrefs(email)?.let { storedData ->
                businessName = storedData.business_name
                name = storedData.name
                lastName = storedData.last_name
                mobilePhone = storedData.phone_one
                homePhone = storedData.phone_two
                rfc = storedData.rfc
            }
        }
    }

    BackHandler(modalSheetState.isVisible) {
        coroutineScope.launch { modalSheetState.hide() }
    }

    PhoneAlreadyExistsDialog(
        isVisible = showPhoneAlreadyExistsDialog,
        icon = R.drawable.ic_exclamation_mark,
        title = "Ups!",
        body = "El número de teléfono ya está registrado",
        buttonString = "Aceptar",
        onOk = { showPhoneAlreadyExistsDialog = false }
    )

    GradientBackground(
        content = {
            ModalBottomSheetLayout(
                sheetState = modalSheetState,
                sheetContent = {
                    AddressAutoCompleteScreen(
                        showMapOption = true
                    ) { mAddress: Address?, _ ->
                        if (mAddress != null) {
                            address = mAddress.line1.orEmpty()
                            city = mAddress.city.orEmpty()
                            state = mAddress.state.orEmpty()
                            postalCode = mAddress.zipcode.orEmpty()
                            country = mAddress.country.orEmpty()
                            latitude = mAddress.location?.lat.toString()
                            longitude = mAddress.location?.lng.toString()
                        }
                        coroutineScope.launch { modalSheetState.hide() }
                    }
                }
            ) {
                Scaffold {
                    ProgressDialog(showProgressDialog, message = "Creando cuenta, espere...")

                    CardAuthBackground(
                        centerContent = false
                    ) {
                        VerticalSpacer(5.dp)

                        Text(
                            text = stringResource(R.string.business_name),
                            fontSize = 16.sp.autoSize()
                        )

                        RoundedEdittext(
                            placeholder = stringResource(R.string.business_contact_business_name),
                            value = businessName
                        ) {
                            businessName = it
                            showValidationBusinessName = it.isBlank()
                        }

                        ValidationText(show = showValidationBusinessName, text = context.getString(R.string.gral_error_empty, "El nombre de negocio"))

                        VerticalSpacer(14.dp)

                        Text(
                            text = stringResource(R.string.business_contact_info),
                            fontSize = 14.sp.autoSize()
                        )

                        RoundedEdittext(
                            placeholder = stringResource(R.string.business_contact_name),
                            value = name
                        ) {
                            name = it
                            showValidationTextName = it.isBlank()
                        }

                        ValidationText(show = showValidationTextName, text = context.getString(R.string.gral_error_empty, "El nombre"))

                        VerticalSpacer(8.dp)

                        RoundedEdittext(
                            placeholder = stringResource(R.string.business_contact_last_name),
                            value = lastName
                        ) {
                            lastName = it
                            showValidationTextLastName = it.isBlank()
                        }

                        ValidationText(show = showValidationTextLastName, text = context.getString(R.string.gral_error_empty, "El apellido"))

                        VerticalSpacer(8.dp)

                        RoundedEdittext(
                            placeholder = stringResource(R.string.business_contact_address),
                            value = address,
                            isEnabled = false,
                            onClicked = {
                                showValidationTextAddress = false
                                coroutineScope.launch {
                                    modalSheetState.show()
                                }
                            },
                            onValueChange = {
                                address = it
                                showValidationTextAddress = it.isBlank()
                            }
                        )

                        ValidationText(show = showValidationTextAddress, text = context.getString(R.string.gral_error_empty, "El domicilio"))

                        VerticalSpacer(8.dp)

                        Row(
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            RoundedEdittext(
                                modifier = Modifier.width(90.dp.autoSize()),
                                placeholder = stringResource(R.string.business_contact_country),
                                value = stringResource(R.string.business_contact_country_code),
                                isEnabled = false
                            ) {  }
                            HorizontalSpacer(width = 5.dp)
                            RoundedPhoneEdittext(
                                placeholder = stringResource(R.string.business_contact_mobile_phone),
                                value = mobilePhone
                            ) {
                                mobilePhone = it
                                showValidationTextPhone1 = it.isBlank()
                                showValidationTextPhone2 = it.isNotEmpty() && !isValidPhoneNumberNewFormat(it)
                            }
                        }

                        if (showValidationTextPhone1 || showValidationTextPhone2) {
                            VerticalSpacer(height = 2.dp)
                            TextRegular(
                                text = if (showValidationTextPhone1) {
                                    context.getString(R.string.gral_error_empty, "El teléfono móvil")
                                } else {
                                    context.getString(R.string.gral_error_phone_formatted, "teléfono móvil")
                                },
                                size = 13.sp.autoSize(),
                                color = Color.Red
                            )
                        }

                        VerticalSpacer(30.dp)

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ButtonPinkRoundedCorners(
                                text = stringResource(R.string.gral_continue)
                            ) {
                                if (businessName.isBlank()) {
                                    showToast(context, context.getString(R.string.gral_error_empty, "El nombre de negocio"))
                                    return@ButtonPinkRoundedCorners
                                }
                                if (name.isBlank()) {
                                    showToast(context, context.getString(R.string.gral_error_empty, "El nombre"))
                                    return@ButtonPinkRoundedCorners
                                }
                                if (lastName.isBlank()) {
                                    showToast(context, context.getString(R.string.gral_error_empty, "El apellido"))
                                    return@ButtonPinkRoundedCorners
                                }
                                if (address.isBlank()) {
                                    showToast(context, context.getString(R.string.gral_error_empty, "El domicilio"))
                                    return@ButtonPinkRoundedCorners
                                }
                                if (mobilePhone.isBlank()) {
                                    showToast(context, context.getString(R.string.gral_error_empty, "El teléfono móvil"))
                                    return@ButtonPinkRoundedCorners
                                }
                                if (!isValidPhoneNumberNewFormat(mobilePhone)) {
                                    showToast(context, context.getString(R.string.gral_error_phone_formatted, "teléfono móvil"))
                                    return@ButtonPinkRoundedCorners
                                }
                                if (homePhone.isNotEmpty()) {
                                    if (!isValidPhoneNumberNewFormat(homePhone)) {
                                        showToast(context, context.getString(R.string.gral_error_phone_formatted, "teléfono fijo"))
                                        return@ButtonPinkRoundedCorners
                                    }
                                }
                                if (rfc.isNotEmpty()) {
                                    if (!isValidRFC(rfc.trim())) {
                                        showToast(context, context.getString(R.string.gral_error_invalid, "El RFC"))
                                        return@ButtonPinkRoundedCorners
                                    }
                                }

                                showProgressDialog = true

                                val providerRequest = ProviderRequest(
                                    role = PROVIDER,
                                    name = name,
                                    email = email,
                                    phone_one = mobilePhone.cleanPhoneNumberNewFormat(),
                                    phone_two = homePhone.cleanPhoneNumberNewFormat(),
                                    business_name = businessName,
                                    state = state,
                                    city = city,
                                    cp = postalCode,
                                    rfc = rfc.trim(),
                                    country = "Mexico",
                                    password = password,
                                    last_name = lastName,
                                    lat = latitude,
                                    lng = longitude,
                                    address = address
                                )

                                if (googleUserData?.uid == null) {
                                    // traditional registration
                                    vm.createNewProviderOnServer(
                                        uid = null,
                                        googleProviderRequest = null,
                                        providerRequest = providerRequest,
                                        onFinished = { response ->
                                            if (response.status == 200) {
                                                showToastOnUiThread(context, context.getString(R.string.business_contact_account_created))
                                                vm.resetStoredUserDataInShPrefs()
                                                vm.signInWithEmailAndPassword(
                                                    email = email,
                                                    password = password,
                                                    validCredentials = {
                                                        context.resetApplication(HALF_SECOND)
                                                        showProgressDialog = false
                                                    }
                                                )
                                                vm.saveAccountIntoInternalDb(LoginAccount(email, password))
                                            } else {
                                                showProgressDialog = false
                                                showPhoneAlreadyExistsDialog = true
                                                vm.alreadyCreatedNewProviderOnServer = false
                                            }
                                        }
                                    )
                                } else {
                                    // google registration
                                    vm.createNewProviderOnServer(
                                        uid = googleUserData.uid,
                                        googleProviderRequest = GoogleProviderRequest(
                                            role = PROVIDER,
                                            name = name,
                                            phone_one = mobilePhone.cleanPhoneNumberNewFormat(),
                                            phone_two = homePhone.cleanPhoneNumberNewFormat(),
                                            business_name = businessName,
                                            state = state,
                                            city = city,
                                            cp = postalCode,
                                            rfc = rfc.trim(),
                                            country = "Mexico",
                                            last_name = lastName,
                                            lat = latitude,
                                            lng = longitude,
                                            address = address
                                        ),
                                        providerRequest = providerRequest,
                                        onFinished = { response ->
                                            if (response.status == 200) {
                                                showToastOnUiThread(context, context.getString(R.string.business_contact_account_created))
                                                vm.resetStoredUserDataInShPrefs()
                                                vm.signInWithEmailAndPassword(
                                                    email = email,
                                                    password = password,
                                                    validCredentials = {
                                                        context.resetApplication(HALF_SECOND)
                                                        showProgressDialog = false
                                                    }
                                                )
                                                vm.saveAccountIntoInternalDb(LoginAccount(email, password))
                                            } else {
                                                showProgressDialog = false
                                                showToastOnUiThread(context, "Error al crear la cuenta. El teléfono ya fue registrado previamente")
                                                vm.alreadyCreatedNewProviderOnServer = false
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        VerticalSpacer(30.dp)
                    }
                }
            }
        },
        isPinkBackground = true,
        showUserName = false,
        addBottomPadding = false,
        onBackButtonClicked = { onBackClicked() }
    )
}
