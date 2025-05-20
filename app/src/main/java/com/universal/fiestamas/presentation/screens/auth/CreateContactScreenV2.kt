package com.universal.fiestamas.presentation.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.GoogleUserData
import com.universal.fiestamas.domain.models.LoginAccount
import com.universal.fiestamas.domain.models.request.ProviderRequest
import com.universal.fiestamas.presentation.screens.home.main.MainParentClass
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape30
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.HorizontalProgressBar
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.RoundedPhoneEdittext
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.ValidationText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.dialogs.PhoneAlreadyExistsDialog
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.utils.Constants.PROVIDER
import com.universal.fiestamas.presentation.utils.cleanPhoneNumberNewFormat
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.isValidPhoneNumberNewFormat
import com.universal.fiestamas.presentation.utils.openUrl

@Composable
fun CreateContactScreenV2(
    vm: AuthViewModel = hiltViewModel(),
    googleUserData: GoogleUserData?,
    email: String,
    password: String,
    businessName: String,
    businessAddress: Address?,
    businessPhotoUrl: String,
    onSuccessProviderAccountCreated: () -> Unit,
    onBackClicked: () -> Unit
) {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var contactName by rememberSaveable { mutableStateOf("") }
    var contactLastName by rememberSaveable { mutableStateOf("") }
    var contactPhonePrefix by rememberSaveable { mutableStateOf("+52") }
    var contactPhone by rememberSaveable { mutableStateOf("") }
    var showValidationName by remember { mutableStateOf(false) }
    var showValidationLastName by remember { mutableStateOf(false) }
    var showValidationPhone by remember { mutableStateOf(false) }
    var showProgressDialog by remember { mutableStateOf(false) }
    var showPhoneAlreadyExistsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(contactName, contactLastName, contactPhone) {
        showValidationName = contactName.isBlank()
        showValidationLastName = contactLastName.isBlank()
        showValidationPhone = !isValidPhoneNumberNewFormat(contactPhone)
    }

    ProgressDialog(showProgressDialog)

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    HorizontalProgressBar(Modifier.weight(0.3f), isSelected = true)
                    HorizontalProgressBar(Modifier.weight(0.3f), isSelected = true)
                    HorizontalProgressBar(Modifier.weight(0.3f), isSelected = true)
                }

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxWidth()
                            .sidePadding(40.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        TextMedium(
                            text = "Contacto",
                            size = 25.sp,
                            color = Color.White
                        )

                        VerticalSpacer(height = 10.dp)

                        RoundedEdittext(
                            isForV2 = true,
                            value = contactName,
                            placeholder = stringResource(R.string.business_contact_business_name),
                            onValueChange = { contactName = it }
                        )

                        ValidationText(
                            show = showValidationName,
                            isForV2 = true,
                            color = Color.White,
                            fillMaxWidth = false,
                            text = "Ingrese un nombre de contacto",
                        )

                        VerticalSpacer(height = 20.dp)

                        RoundedEdittext(
                            isForV2 = true,
                            value = contactLastName,
                            placeholder = stringResource(R.string.business_contact_last_name),
                            onValueChange = { contactLastName = it }
                        )

                        ValidationText(
                            show = showValidationLastName,
                            isForV2 = true,
                            color = Color.White,
                            fillMaxWidth = false,
                            text = "Ingrese un apellido(s) de contacto",
                        )

                        VerticalSpacer(height = 20.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RoundedEdittext(
                                modifier = Modifier.weight(0.3f),
                                isForV2 = true,
                                value = contactPhonePrefix,
                                isEnabled = false,
                                placeholder = stringResource(R.string.business_contact_confirm_country),
                                onValueChange = { contactPhonePrefix = it }
                            )
                            HorizontalSpacer(width = 10.dp)
                            RoundedPhoneEdittext(
                                modifier = Modifier.weight(0.7f),
                                isForV2 = true,
                                value = contactPhone,
                                placeholder = "Teléfono",
                                onValueChange = { contactPhone = it }
                            )
                        }

                        ValidationText(
                            show = showValidationPhone,
                            isForV2 = true,
                            color = Color.White,
                            fillMaxWidth = false,
                            text = "Ingrese un teléfono válido",
                        )

                        VerticalSpacer(height = 10.dp)
                    }

                    Column(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxWidth()
                            .sidePadding(30.dp),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ButtonPinkRoundedCornersV2(
                            verticalPadding = 10.dp,
                            horizontalPadding = 35.dp,
                            shape = allRoundedCornerShape30,
                            addBorder = true,
                            borderColor = Color.White,
                            content = {
                                TextBold(
                                    text = stringResource(R.string.gral_continue).uppercase(),
                                    size = 18.sp,
                                    color = Color.White,
                                    fillMaxWidth = false
                                )
                            },
                            onClick = {
                                if (!showValidationName && !showValidationLastName && !showValidationPhone) {
                                    showProgressDialog = true

                                    val providerRequest = ProviderRequest(
                                        role = PROVIDER,
                                        name = contactName,
                                        email = email,
                                        phone_one = contactPhone.cleanPhoneNumberNewFormat(),
                                        phone_two = "",
                                        business_name = businessName,
                                        state = businessAddress?.state.orEmpty(),
                                        city = businessAddress?.city.orEmpty(),
                                        cp = businessAddress?.zipcode.orEmpty(),
                                        rfc = "",
                                        country = "Mexico",
                                        password = password,
                                        last_name = contactLastName,
                                        lat = businessAddress?.location?.lat ?: "0.0",
                                        lng = businessAddress?.location?.lng ?: "0.0",
                                        address = businessAddress?.line1.orEmpty(),
                                        photo = businessPhotoUrl.ifEmpty { null }
                                    )

                                    if (googleUserData?.uid == null) {
                                        // traditional registration
                                        vm.createNewProviderOnServer(
                                            uid = null,
                                            googleProviderRequest = null,
                                            providerRequest = providerRequest,
                                            onFinished = { response ->
                                                if (response.status == 200) {
                                                    vm.resetStoredUserDataInShPrefs()
                                                    vm.signInWithEmailAndPassword(
                                                        email = email,
                                                        password = password,
                                                        validCredentials = {
                                                            showProgressDialog = false
                                                            MainParentClass.userId = response.data?.id
                                                            onSuccessProviderAccountCreated()
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
                                    }
                                }
                            }
                        )

                        VerticalSpacer(height = 20.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextRegular(
                                text = "Al crear cuenta acepto ",
                                color = Color.LightGray,
                                fillMaxWidth = false,
                                size = 11.sp
                            )
                            TextMedium(
                                modifier = Modifier.clickable {
                                    openUrl(context, "https://fiestamas.com/privacy-policy")
                                },
                                text = "Términos y condiciones",
                                color = Color.White,
                                fillMaxWidth = false,
                                size = 11.sp
                            )
                        }
                        VerticalSpacer(height = 60.dp)
                    }
                }

            }
        },
        titleScreen = "Registro",
        titleScreenColor = Color.White,
        isPinkBackground = true,
        showBackButton = true,
        showLogoFiestamas = false,
        backButtonColor = Color.White,
        showUserName = false,
        addBottomPadding = false,
        onBackButtonClicked = { onBackClicked() }
    )
}
