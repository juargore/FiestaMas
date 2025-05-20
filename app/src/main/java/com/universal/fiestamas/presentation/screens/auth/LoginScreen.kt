package com.universal.fiestamas.presentation.screens.auth

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.common.ErrorResponse
import com.universal.fiestamas.domain.models.LoginAccount
import com.universal.fiestamas.presentation.theme.LightGray
import com.universal.fiestamas.presentation.theme.LighterGray
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape14
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape30
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape8
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCorners
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.CheckboxPink
import com.universal.fiestamas.presentation.ui.ForgotPasswordClickable
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.RoundedPasswordEditText
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.TextExtraBold
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.ValidationText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.CardAuthBackground
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.dialogs.ErrorDialog
import com.universal.fiestamas.presentation.ui.dialogs.NoInternetConnectionDialog
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.ui.dialogs.YesNoDialog
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.isInternetAvailable
import com.universal.fiestamas.presentation.utils.extensions.resetApplication
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.isValidEmail
import com.universal.fiestamas.presentation.utils.isValidPassword
import com.universal.fiestamas.presentation.utils.showToast
import com.universal.fiestamas.presentation.utils.showToastOnUiThread

@Composable
fun LoginScreenV2(
    vm: AuthViewModel = hiltViewModel(),
    initialUserEmail: String,
    account: LoginAccount?,
    mustRefreshApp: Boolean = false,
    onBackClicked: () -> Unit,
    onSuccessLogin: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var userEmail by rememberSaveable { mutableStateOf(initialUserEmail.trim()) }
    var userPassword by rememberSaveable { mutableStateOf(account?.password.orEmpty()) }
    var showProgressDialog by remember { mutableStateOf(false) }
    var showValidationText by remember { mutableStateOf(false) }
    var showNoInternetDialog by remember { mutableStateOf(false) }
    var showIncorrectCredentialsView by remember { mutableStateOf(false) }
    var showForgotPasswordSuccessDialog by remember { mutableStateOf(false) }

    NoInternetConnectionDialog(
        isVisible = showNoInternetDialog,
        onOpenWifiSettings = { context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) },
        onDismiss = { showNoInternetDialog = false }
    )

    YesNoDialog(
        addCancelButton = false,
        isVisible = showForgotPasswordSuccessDialog,
        title = "Email de restauracion enviado",
        message = "Verifica tu email para restaurar la contraseña",
        onDismiss = { showForgotPasswordSuccessDialog = false },
        onOk = { showForgotPasswordSuccessDialog = false }
    )

    GradientBackground(
        content = {
            ProgressDialog(showProgressDialog)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .sidePadding(34.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
            ) {
                VerticalSpacer(15.dp)

                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(Color.White, allRoundedCornerShape14)
                        .padding(horizontal = 12.dp.autoSize())
                        .padding(vertical = 24.dp.autoSize())
                ) {
                    TextExtraBold(
                        text = "FIESTA\nMAS",
                        fillMaxWidth = false,
                        size = 28.sp,
                        verticalSpace = 35.sp,
                        align = TextAlign.Center,
                        color = PinkFiestamas
                    )
                }
                
                VerticalSpacer(height = 35.dp)

                RoundedEdittext(
                    isForV2 = true,
                    placeholder = stringResource(id = R.string.login_email),
                    value = userEmail,
                    isEnabled = false
                ) { userEmail = it }

                VerticalSpacer(30.dp)

                RoundedPasswordEditText(
                    isForV2 = true,
                    placeholder = stringResource(id = R.string.login_password),
                    value = userPassword,
                    onValueChange = {
                        showIncorrectCredentialsView = false
                        userPassword = it
                        showValidationText = if (it.length > 3) {
                            !isValidPassword(it)
                        } else {
                            false
                        }
                    }
                )

                ValidationText(
                    show = showValidationText,
                    isForV2 = true,
                    color = Color.White,
                    fillMaxWidth = false,
                    text = context.getString(R.string.create_password_format_incorrect),
                )

                VerticalSpacer(30.dp)

                if (showIncorrectCredentialsView) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_error_v2),
                            contentDescription = null,
                            modifier = Modifier.height(22.dp.autoSize())
                        )
                        HorizontalSpacer(width = 14.dp)
                        TextSemiBold(
                            text = "Credenciales\nincorrectas",
                            fillMaxWidth = false,
                            verticalSpace = 20.sp,
                            align = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }

                VerticalSpacer(30.dp)

                TextMedium(
                    text = "Recuperar Contraseña",
                    color = Color.White,
                    modifier = Modifier.clickable {
                        if (!isInternetAvailable(context)) {
                            showNoInternetDialog = true
                            return@clickable
                        }
                        if (userEmail.isBlank() || userEmail.isBlank() || !isValidEmail(userEmail)) {
                            showToast(context, "Ingrese un email válido")
                        } else {
                            showProgressDialog = true
                            vm.sendEmailForPasswordRecovery(userEmail) {
                                showProgressDialog = false
                                if (it) {
                                    showForgotPasswordSuccessDialog = true
                                } else {
                                    showToastOnUiThread(context, "Error al procesar la solicitud")
                                }
                            }
                        }
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 50.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    ButtonPinkRoundedCornersV2(
                        verticalPadding = 10.dp,
                        horizontalPadding = 35.dp,
                        shape = allRoundedCornerShape30,
                        addBorder = true,
                        borderColor = Color.White,
                        content = {
                            TextBold(
                                text = stringResource(R.string.login_sign_in).uppercase(),
                                size = 18.sp,
                                color = Color.White,
                                fillMaxWidth = false
                            )
                        },
                        onClick = {
                            if (!isInternetAvailable(context)) {
                                showNoInternetDialog = true
                                return@ButtonPinkRoundedCornersV2
                            }
                            if (userEmail.isBlank() || userPassword.isBlank()) {
                                showToast(context, context.getString(R.string.login_fill_fields))
                            } else {
                                showProgressDialog = true
                                vm.signInWithEmailAndPassword(
                                    email = userEmail.trim(),
                                    password = userPassword.trim(),
                                    validCredentials = { success ->
                                        if (success) {
                                            //if (rememberCredentials) {
                                            vm.saveAccountIntoInternalDb(LoginAccount(userEmail, userPassword))
                                            //}
                                            showToastOnUiThread(context, context.getString(R.string.login_welcome, userEmail))
                                            if (mustRefreshApp) {
                                                context.resetApplication()
                                            } else {
                                                onSuccessLogin()
                                            }
                                        } else {
                                            vm.alreadySignedInWithEmailAndPassword = false
                                            showIncorrectCredentialsView = true
                                        }
                                        showProgressDialog = false
                                    }
                                )
                            }
                        }
                    )
                }
            }
        },
        isPinkBackground = true,
        addBottomPadding = false,
        showUserName = false,
        showBackButton = false,
        showBackButtonV2 = true,
        backButtonStringV2 = "Inicio de Sesión",
        onBackButtonClicked = { onBackClicked() }
    )
}

@Composable
fun LoginScreenV1(
    vm: AuthViewModel = hiltViewModel(),
    initialUserEmail: String,
    account: LoginAccount?,
    mustRefreshApp: Boolean = false,
    onBackClicked: () -> Unit,
    onSuccessLogin: () -> Unit
) {
    val context = LocalContext.current
    var userEmail by rememberSaveable { mutableStateOf(initialUserEmail.trim()) }
    var userPassword by rememberSaveable { mutableStateOf(account?.password.orEmpty()) }
    var showProgressDialog by remember { mutableStateOf(false) }
    var showValidationText by remember { mutableStateOf(false) }
    var showNoInternetDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var rememberCredentials by remember { mutableStateOf(true) }
    var showForgotPasswordSuccessDialog by remember { mutableStateOf(false) }
    val errorMessage = ErrorResponse(
        message = context.getString(R.string.login_wrong_credentials),
        status = 401
    )

    NoInternetConnectionDialog(
        isVisible = showNoInternetDialog,
        onOpenWifiSettings = { context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) },
        onDismiss = { showNoInternetDialog = false }
    )

    YesNoDialog(
        addCancelButton = false,
        isVisible = showForgotPasswordSuccessDialog,
        title = "Email de restauracion enviado",
        message = "Verifica tu email para restaurar la contraseña",
        onDismiss = { showForgotPasswordSuccessDialog = false },
        onOk = { showForgotPasswordSuccessDialog = false }
    )

    GradientBackground(
        content = {
            if (showErrorDialog) {
                ErrorDialog(error = errorMessage) { showErrorDialog = false }
            }

            ProgressDialog(showProgressDialog)

            CardAuthBackground {

                VerticalSpacer(15.dp)

                TextBold(
                    text = stringResource(R.string.login_sign_in),
                    size = 22.sp.autoSize(),
                    color = PinkFiestamas
                )

                VerticalSpacer(13.dp)

                RoundedEdittext(
                    placeholder = stringResource(id = R.string.login_email),
                    value = userEmail,
                    isEnabled = false
                ) { userEmail = it }

                VerticalSpacer(5.dp)

                RoundedPasswordEditText(
                    placeholder = stringResource(id = R.string.login_password),
                    value = userPassword,
                    onValueChange = {
                        userPassword = it
                        showValidationText = if (it.length > 3) {
                            !isValidPassword(it)
                        } else {
                            false
                        }
                    }
                )

                ValidationText(show = showValidationText, text = context.getString(R.string.create_password_format_incorrect))

                VerticalSpacer(15.dp)

                CheckboxPink(
                    startChecked = true,
                    content = {
                        TextMedium(
                            text = stringResource(id = R.string.login_save_password),
                            fillMaxWidth = false,
                            size = 16.sp.autoSize()
                        )
                    },
                    onChecked = { rememberCredentials = it }
                )

                VerticalSpacer(15.dp)

                ButtonPinkRoundedCorners(
                    text = stringResource(id = R.string.gral_continue)
                ) {
                    if (!isInternetAvailable(context)) {
                        showNoInternetDialog = true
                        return@ButtonPinkRoundedCorners
                    }
                    if (userEmail.isBlank() || userPassword.isBlank()) {
                        showToast(context, context.getString(R.string.login_fill_fields))
                    } else {
                        showProgressDialog = true
                        vm.signInWithEmailAndPassword(
                            email = userEmail.trim(),
                            password = userPassword.trim(),
                            validCredentials = { success ->
                                if (success) {
                                    if (rememberCredentials) {
                                        vm.saveAccountIntoInternalDb(LoginAccount(userEmail, userPassword))
                                    }
                                    showToastOnUiThread(context, context.getString(R.string.login_welcome, userEmail))
                                    if (mustRefreshApp) {
                                        context.resetApplication()
                                    } else {
                                        onSuccessLogin()
                                    }
                                } else {
                                    vm.alreadySignedInWithEmailAndPassword = false
                                    showErrorDialog = true
                                }
                                showProgressDialog = false
                            }
                        )
                    }
                }

                VerticalSpacer(35.dp)

                ForgotPasswordClickable {
                    if (!isInternetAvailable(context)) {
                        showNoInternetDialog = true
                        return@ForgotPasswordClickable
                    }
                    if (userEmail.isBlank() || userEmail.isBlank() || !isValidEmail(userEmail)) {
                        showToast(context, "Ingrese un email válido")
                    } else {
                        showProgressDialog = true
                        vm.sendEmailForPasswordRecovery(userEmail) {
                            showProgressDialog = false
                            if (it) {
                                showForgotPasswordSuccessDialog = true
                            } else {
                                showToastOnUiThread(context, "Error al procesar la solicitud")
                            }
                        }
                    }
                }

                VerticalSpacer(12.dp)
            }
        },
        addBottomPadding = false,
        showUserName = false,
        onBackButtonClicked = { onBackClicked() }
    )
}
