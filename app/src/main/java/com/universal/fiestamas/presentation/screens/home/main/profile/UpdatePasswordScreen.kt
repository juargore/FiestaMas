package com.universal.fiestamas.presentation.screens.home.main.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCorners
import com.universal.fiestamas.presentation.ui.RoundedPasswordEditText
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.CardAuthBackground
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.utils.Constants
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.resetApplication
import com.universal.fiestamas.presentation.utils.isValidPassword
import com.universal.fiestamas.presentation.utils.showToast
import com.universal.fiestamas.presentation.utils.showToastOnUiThread

@Composable
fun UpdatePasswordScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    userEmail: String,
    userId: String,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var showProgressDialog by remember { mutableStateOf(false) }

    GradientBackground(
        content = {
            ProgressDialog(
                isVisible = showProgressDialog,
                message = stringResource(R.string.progress_updating_password)
            )

            CardAuthBackground(
                centerContent = false
            ) {
                VerticalSpacer(11.dp)

                TextBold(
                    text = stringResource(R.string.update_password_title),
                    size = 19.sp.autoSize(),
                    align = TextAlign.Start
                )

                TextRegular(
                    text = stringResource(R.string.create_password_to, userEmail),
                    align = TextAlign.Start,
                    size = 16.sp.autoSize()
                )

                VerticalSpacer(21.dp)

                TextRegular(
                    text = stringResource(R.string.create_password_conditions),
                    size = 13.sp.autoSize(),
                    align = TextAlign.Start,
                    verticalSpace = 14.sp.autoSize()
                )

                VerticalSpacer(25.dp)

                RoundedPasswordEditText(
                    placeholder = stringResource(R.string.login_new_password),
                    value = password,
                    onNextAction = { focusManager.moveFocus(FocusDirection.Down) }
                ) { password = it }

                VerticalSpacer(5.dp)

                RoundedPasswordEditText(
                    placeholder = stringResource(R.string.login_confirm_password),
                    value = confirmPassword,
                    onNextAction = {  }
                ) { confirmPassword = it }

                VerticalSpacer(30.dp)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ButtonPinkRoundedCorners(
                        text = stringResource(R.string.update_password_title)
                    ) {
                        if (isValidPassword(password)) {
                            if (password == confirmPassword) {
                                showProgressDialog = true
                                authViewModel.updatePasswordOnServer(userEmail, password) { success ->
                                    showProgressDialog = false
                                    if (success) {
                                        showToastOnUiThread(context, context.getString(R.string.profile_password_updated))
                                        authViewModel.signOutFromAccount()
                                        authViewModel.unregisterTokenForPushNotification(userId)
                                        context.resetApplication(delay = Constants.ONE_SECOND)
                                    } else {
                                        showToastOnUiThread(context, context.getString(R.string.profile_error_updating_password))
                                    }
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
                VerticalSpacer(15.dp)
            }
        },
        addBottomPadding = false,
        showUserName = false,
        onBackButtonClicked = { onBackClicked() }
    )
}
