package com.universal.fiestamas.presentation.screens.auth

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape30
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.HorizontalProgressBar
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.RoundedPasswordEditText
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.ValidPasswordCharV2
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.isValidPassword
import com.universal.fiestamas.presentation.utils.showToast

@Composable
fun CreatePasswordScreenV2(
    isNewAccountFromGmail: Boolean,
    initialUserEmail: String,
    onBackClicked: () -> Unit,
    onRedirectToCreateBusiness: (password: String) -> Unit
) {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var userEmail by rememberSaveable { mutableStateOf(initialUserEmail.trim()) }

    var validEightChars by rememberSaveable { mutableStateOf(false) }
    var validOneLower by rememberSaveable { mutableStateOf(false) }
    var validOneSymbol by rememberSaveable { mutableStateOf(false) }
    var validOneNumber by rememberSaveable { mutableStateOf(false) }
    var validOneUpper by rememberSaveable { mutableStateOf(false) }
    var validPasswordsTheSame by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(password, confirmPassword) {
        validEightChars = password.length >= 8
        validOneLower = password.any { it.isLowerCase() }
        validOneSymbol = password.any { !it.isLetterOrDigit() }
        validOneNumber = password.any { it.isDigit() }
        validOneUpper = password.any { it.isUpperCase() }
        validPasswordsTheSame = password == confirmPassword
    }

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
                    HorizontalProgressBar(Modifier.weight(0.3f))
                    HorizontalProgressBar(Modifier.weight(0.3f))
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .sidePadding(30.dp)
                ) {
                    TextBold(
                        modifier = Modifier.padding(vertical = 60.dp),
                        text = "Bienvenido",
                        color = Color.White,
                        size = 28.sp
                    )

                    RoundedEdittext(
                        isForV2 = true,
                        placeholder = stringResource(id = R.string.login_email),
                        value = userEmail,
                        isEnabled = false
                    ) { userEmail = it }

                    VerticalSpacer(height = 30.dp)

                    RoundedPasswordEditText(
                        isForV2 = true,
                        placeholder = stringResource(id = R.string.login_password),
                        value = password,
                        onValueChange = { password = it }
                    )

                    VerticalSpacer(height = 20.dp)

                    RoundedPasswordEditText(
                        isForV2 = true,
                        placeholder = stringResource(id = R.string.login_confirm_password),
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it }
                    )

                    VerticalSpacer(height = 12.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .sidePadding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(0.5f)
                        ) {
                            ValidPasswordCharV2(text = "8 caracteres", isValid = validEightChars)
                            ValidPasswordCharV2(text = "1 minúscula", isValid = validOneLower)
                            ValidPasswordCharV2(text = "1 símbolo", isValid = validOneSymbol)
                        }
                        Column(
                            modifier = Modifier.weight(0.5f)
                        ) {
                            ValidPasswordCharV2(text = "1 número", isValid = validOneNumber)
                            ValidPasswordCharV2(text = "1 mayúscula", isValid = validOneUpper)
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .sidePadding(12.dp)
                    ) {
                        ValidPasswordCharV2(text = "Las contraseñas no coinciden", isValid = validPasswordsTheSame)
                    }

                    Box(
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 30.dp),
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
                                if (isNewAccountFromGmail) {
                                    onRedirectToCreateBusiness(password)
                                } else {
                                    if (isValidPassword(password)) {
                                        if (password == confirmPassword) {
                                            onRedirectToCreateBusiness(password)
                                        } else {
                                            showToast(context, context.getString(R.string.create_password_not_equal))
                                            return@ButtonPinkRoundedCornersV2
                                        }
                                    } else {
                                        showToast(context, context.getString(R.string.create_password_format_incorrect))
                                    }
                                }
                            }
                        )
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
        onBackButtonClicked = {
            if (!isNewAccountFromGmail) {
                onBackClicked()
            }
        }
    )
}
