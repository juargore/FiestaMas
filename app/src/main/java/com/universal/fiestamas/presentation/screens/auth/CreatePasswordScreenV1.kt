package com.universal.fiestamas.presentation.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCorners
import com.universal.fiestamas.presentation.ui.RoundedPasswordEditText
import com.universal.fiestamas.presentation.ui.TermsAndConditionsClickable
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.ValidationText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.BlueBackgroundRoundedCorners
import com.universal.fiestamas.presentation.ui.backgrounds.CardAuthBackground
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.isValidPassword
import com.universal.fiestamas.presentation.utils.showToast

@Composable
fun CreatePasswordScreenV1(
    isNewAccountFromGmail: Boolean,
    initialUserEmail: String,
    onBackClicked: () -> Unit,
    refreshAppIfAccountIsCreated: Boolean,
    onRedirectToCreateProvider: (password: String, refresh: Boolean) -> Unit,
    onRedirectToCreateUser: (password: String, refresh: Boolean) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var termsChecked by rememberSaveable { mutableStateOf(false) }
    var providerChecked by rememberSaveable { mutableStateOf(false) }
    var showValidationText1 by remember { mutableStateOf(false) }
    var showValidationText2 by remember { mutableStateOf(false) }
    var showValidationText3 by remember { mutableStateOf(false) }

    GradientBackground(
        content = {
            CardAuthBackground(
                centerContent = false
            ) {
                if (!isNewAccountFromGmail) {
                    VerticalSpacer(11.dp)

                    TextBold(
                        text = stringResource(R.string.create_password_title),
                        size = 19.sp.autoSize(),
                        align = TextAlign.Start
                    )

                    TextRegular(
                        text = stringResource(R.string.create_password_to, initialUserEmail),
                        align = TextAlign.Start,
                        size = 16.sp.autoSize()
                    )

                    VerticalSpacer(21.dp)

                    TextRegular(
                        text = stringResource(R.string.create_password_conditions),
                        size = 13.sp.autoSize(),
                        align = TextAlign.Start,
                        verticalSpace = 17.sp.autoSize()
                    )

                    VerticalSpacer(20.dp)

                    RoundedPasswordEditText(
                        placeholder = stringResource(R.string.login_password),
                        value = password,
                        onNextAction = { focusManager.moveFocus(FocusDirection.Down) },
                        onValueChange = {
                            password = it
                            showValidationText1 = if (it.length > 3) {
                                !isValidPassword(it)
                            } else {
                                false
                            }
                        }
                    )

                    ValidationText(show = showValidationText1, text = context.getString(R.string.create_password_format_incorrect))

                    VerticalSpacer(5.dp)

                    RoundedPasswordEditText(
                        placeholder = stringResource(R.string.login_confirm_password),
                        value = confirmPassword,
                        onNextAction = {  },
                        onValueChange = {
                            confirmPassword = it
                            showValidationText2 = if (it.length > 3) {
                                !isValidPassword(it)
                            } else {
                                false
                            }
                            if (isValidPassword(it)) {
                                showValidationText3 = password != confirmPassword
                            }
                        }
                    )
                }

                if (showValidationText2 || showValidationText3) {
                    VerticalSpacer(height = 2.dp)
                    TextRegular(
                        text = if (showValidationText2) {
                            context.getString(R.string.create_password_format_incorrect)
                        } else {
                            context.getString(R.string.create_password_not_equal)
                        },
                        size = 13.sp,
                        color = Color.Red
                    )
                }

                VerticalSpacer(22.dp)

                BlueBackgroundRoundedCorners(
                    showOutline = false,
                    onChecked = { termsChecked = it },
                    content = {
                        TermsAndConditionsClickable(
                            firstLineStr = R.string.create_password_terms_one,
                            centerText = false
                        )
                    }
                )

                VerticalSpacer(18.dp)

                BlueBackgroundRoundedCorners(
                    showOutline = true,
                    onChecked = { providerChecked = it },
                    content = {
                        TextSemiBold(
                            modifier = Modifier.padding(vertical = 4.dp),
                            text = stringResource(R.string.create_password_to_be_provider),
                            size = 16.sp.autoSize(),
                            horizontalSpace = 0.sp,
                            align = TextAlign.Start
                        )
                    }
                )

                VerticalSpacer(36.dp)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ButtonPinkRoundedCorners(
                        text = stringResource(R.string.create_password_create_account)
                    ) {
                        if (!termsChecked) {
                            showToast(context, context.getString(R.string.create_password_accept_terms))
                            return@ButtonPinkRoundedCorners
                        }

                        if (isNewAccountFromGmail) {
                            if (providerChecked) {
                                onRedirectToCreateProvider(password, refreshAppIfAccountIsCreated)
                            } else {
                                onRedirectToCreateUser(password, refreshAppIfAccountIsCreated)
                            }
                        } else {
                            if (isValidPassword(password)) {
                                if (password == confirmPassword) {
                                    if (providerChecked) {
                                        onRedirectToCreateProvider(password, refreshAppIfAccountIsCreated)
                                    } else {
                                        onRedirectToCreateUser(password, refreshAppIfAccountIsCreated)
                                    }
                                } else {
                                    showToast(context, context.getString(R.string.create_password_not_equal))
                                    return@ButtonPinkRoundedCorners
                                }
                            } else {
                                showToast(context, context.getString(R.string.create_password_format_incorrect))
                            }
                        }
                    }
                }

                VerticalSpacer(15.dp)
            }
        },
        showUserName = false,
        addBottomPadding = false,
        onBackButtonClicked = {
            if (!isNewAccountFromGmail) {
                onBackClicked()
            }
        }
    )
}
