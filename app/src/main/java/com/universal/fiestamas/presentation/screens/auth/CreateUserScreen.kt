package com.universal.fiestamas.presentation.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.universal.fiestamas.domain.models.GoogleUserData
import com.universal.fiestamas.domain.models.LoginAccount
import com.universal.fiestamas.domain.models.request.GoogleUserRequest
import com.universal.fiestamas.domain.models.request.UserRequest
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
import com.universal.fiestamas.presentation.utils.Constants.CLIENT
import com.universal.fiestamas.presentation.utils.Constants.HALF_SECOND
import com.universal.fiestamas.presentation.utils.cleanPhoneNumberNewFormat
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.resetApplication
import com.universal.fiestamas.presentation.utils.isValidPhoneNumberNewFormat
import com.universal.fiestamas.presentation.utils.showToast
import com.universal.fiestamas.presentation.utils.showToastOnUiThread

@Composable
fun CreateUserScreen(
    vm: AuthViewModel = hiltViewModel(),
    email: String,
    googleUserData: GoogleUserData?,
    password: String,
    @Suppress("UNUSED_PARAMETER") refreshAppIfAccountIsCreated: Boolean,
    @Suppress("UNUSED_PARAMETER") onSuccessLogin: () -> Unit,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var name by rememberSaveable { mutableStateOf(googleUserData?.userName.orEmpty()) }
    var lastName by rememberSaveable { mutableStateOf(googleUserData?.userLastName.orEmpty()) }
    var mobilePhone by rememberSaveable { mutableStateOf("") }
    var homePhone by rememberSaveable { mutableStateOf("") }
    var showProgressDialog by remember { mutableStateOf(false) }
    var showPhoneAlreadyExistsDialog by remember { mutableStateOf(false) }

    var showValidationTextName by remember { mutableStateOf(false) }
    var showValidationTextLastName by remember { mutableStateOf(false) }
    var showValidationTextPhone1 by remember { mutableStateOf(false) }
    var showValidationTextPhone2 by remember { mutableStateOf(false) }
    var showValidationTextHomePhone by remember { mutableStateOf(false) }

    var currentOnStop by remember { mutableStateOf(false) }
    var currentOnCreate by remember { mutableStateOf(false) }


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
            vm.storeUserDataInShPrefs(
                UserRequest(
                    role = CLIENT,
                    name = name,
                    last_name = lastName,
                    phone_one = mobilePhone,
                    phone_two = homePhone,
                    email = email,
                    password = password
                )
            )
        }
        if (currentOnCreate) {
            vm.getStoredUserDataInShPrefs(email)?.let { storedData ->
                name = storedData.name
                lastName = storedData.last_name
                mobilePhone = storedData.phone_one
                homePhone = storedData.phone_two
            }
        }
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
            ProgressDialog(showProgressDialog, message = "Creando cuenta, espere...")

            CardAuthBackground(
                centerContent = false
            ) {
                VerticalSpacer(5.dp)

                Text(
                    text = stringResource(R.string.business_contact_info),
                    fontSize = 16.sp.autoSize()
                )

                VerticalSpacer(32.dp)

                RoundedEdittext(
                    placeholder = stringResource(R.string.business_contact_name),
                    value = name,
                    onValueChange = {
                        name = it
                        showValidationTextName = it.isBlank()
                    }
                )

                ValidationText(show = showValidationTextName, text = context.getString(R.string.gral_error_empty, "El nombre"))

                VerticalSpacer(8.dp)

                RoundedEdittext(
                    placeholder = stringResource(R.string.business_contact_last_name),
                    value = lastName,
                    onValueChange = {
                        lastName = it
                        showValidationTextLastName = it.isBlank()
                    }
                )

                ValidationText(show = showValidationTextLastName, text = context.getString(R.string.gral_error_empty, "El apellido"))

                VerticalSpacer(8.dp)

                Row(
                    modifier = Modifier.fillMaxWidth()
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
                        value = mobilePhone,
                        onValueChange = {
                            mobilePhone = it
                            showValidationTextPhone1 = it.isBlank()
                            showValidationTextPhone2 = it.isNotEmpty() && !isValidPhoneNumberNewFormat(it)
                        }
                    )
                }

                if (showValidationTextPhone1 || showValidationTextPhone2) {
                    VerticalSpacer(height = 2.dp)
                    TextRegular(
                        text = if (showValidationTextPhone1) {
                            context.getString(R.string.gral_error_empty, "El teléfono móvil")
                        } else {
                            context.getString(R.string.gral_error_phone_formatted, "teléfono móvil")
                        },
                        size = 13.sp,
                        color = Color.Red
                    )
                }

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
                        placeholder = stringResource(R.string.business_contact_home_phone),
                        value = homePhone,
                        onValueChange = {
                            homePhone = it
                            showValidationTextHomePhone = it.isNotEmpty() && !isValidPhoneNumberNewFormat(homePhone)
                        }
                    )
                }

                ValidationText(show = showValidationTextHomePhone, text = context.getString(R.string.gral_error_phone_formatted, "teléfono fijo"))

                VerticalSpacer(40.dp)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ButtonPinkRoundedCorners(
                        text = stringResource(R.string.gral_continue)
                    ) {
                        if (name.isBlank()) {
                            showToast(context, context.getString(R.string.gral_error_empty, "El nombre"))
                            return@ButtonPinkRoundedCorners
                        }
                        if (lastName.isBlank()) {
                            showToast(context, context.getString(R.string.gral_error_empty, "El apellido"))
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

                        showProgressDialog = true

                        val userRequest = UserRequest(
                            role = CLIENT,
                            name = name,
                            last_name = lastName,
                            phone_one = mobilePhone.cleanPhoneNumberNewFormat(),
                            phone_two = homePhone.cleanPhoneNumberNewFormat(),
                            email = email,
                            password = password
                        )

                        if (googleUserData?.uid == null) {
                            // traditional registration
                            vm.createNewUserOnServer(
                                uid = null,
                                googleUserRequest = null,
                                userRequest = userRequest,
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
                                        vm.alreadyCreatedNewUserOnServer = false
                                    }
                                }
                            )
                        } else {
                            // google registration
                            vm.createNewUserOnServer(
                                uid = googleUserData.uid,
                                googleUserRequest = GoogleUserRequest(
                                    role = CLIENT,
                                    name = name,
                                    last_name = lastName,
                                    phone_one = mobilePhone.cleanPhoneNumberNewFormat(),
                                    phone_two = homePhone.cleanPhoneNumberNewFormat()
                                ),
                                userRequest = userRequest,
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
                                    } else {
                                        showProgressDialog = false
                                        showToastOnUiThread(context, "Error al crear la cuenta. Email o Teléfono ya fue registrado previamente")
                                        vm.alreadyCreatedNewUserOnServer = false
                                    }
                                }
                            )
                        }
                    }
                }

                VerticalSpacer(33.dp)
            }
        },
        isPinkBackground = true,
        showUserName = false,
        addBottomPadding = false,
        onBackButtonClicked = { onBackClicked() }
    )
}
