package com.universal.fiestamas.presentation.screens.auth

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.ViewTreeObserver
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.universal.fiestamas.BuildConfig
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.common.ErrorResponse
import com.universal.fiestamas.domain.models.GoogleUserData
import com.universal.fiestamas.domain.models.LoginAccount
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape30
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCorners
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.ButtonSocialMediaWhiteRoundedCorners
import com.universal.fiestamas.presentation.ui.ButtonSocialMediaWhiteRoundedCornersV1
import com.universal.fiestamas.presentation.ui.CardLoginAutoComplete
import com.universal.fiestamas.presentation.ui.HorizontalLine
import com.universal.fiestamas.presentation.ui.HorizontalLineDecoration
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.TermsAndConditionsClickable
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.ValidationText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.CardAuthBackground
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.dialogs.ErrorDialog
import com.universal.fiestamas.presentation.ui.dialogs.NoInternetConnectionDialog
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.isInternetAvailable
import com.universal.fiestamas.presentation.utils.extensions.resetApplication
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.isValidEmail
import com.universal.fiestamas.presentation.utils.showToast

@Composable
fun StartEmailScreen(
    fullScreen: Boolean,
    refreshAppIfAccountIsCreated: Boolean,
    onEmailValidated: (
        isNewAccountFromGmail: Boolean,
        googleUserData: GoogleUserData?,
        emailExists: Boolean,
        email: String,
        account: LoginAccount?,
        refreshAppIfAccountIsCreated: Boolean
    ) -> Unit,
    onBackClicked: (() -> Unit)? = null,
) {
    if (!fullScreen) {
        StartEmailContentV1(
            refreshAppIfAccountIsCreated = refreshAppIfAccountIsCreated
        ) { fromGmail, googleUserData, exists, email, account, refresh ->
            onEmailValidated(fromGmail, googleUserData, exists, email, account, refresh)
        }; return
    }

    GradientBackground(
        content = {
            StartEmailContentV2(
                refreshAppIfAccountIsCreated = refreshAppIfAccountIsCreated
            ) { fromGmail, googleUserData, exists, email, account, refresh ->
                onEmailValidated(fromGmail, googleUserData, exists, email, account, refresh)
            }
        },
        isPinkBackground = true,
        addBottomPadding = false,
        showLogoFiestamas = true,
        backButtonColor = Color.White,
        showUserName = false,
        onBackButtonClicked = { onBackClicked?.invoke() }
    )
}

@Composable
fun StartEmailContentV2(
    vm: AuthViewModel = hiltViewModel(),
    refreshAppIfAccountIsCreated: Boolean,
    onEmailValidated: (
        isNewAccountFromGmail: Boolean,
        googleUserData: GoogleUserData?,
        emailExists: Boolean,
        email: String,
        account: LoginAccount?,
        refreshAppIfAccountIsCreated: Boolean
    ) -> Unit
) {
    val context = LocalContext.current
    //val accounts by vm.accountsList.collectAsState()

    var showProgressDialog by remember { mutableStateOf(false) }
    var showAutoCompleteTextField by remember { mutableStateOf(true) }
    var showValidationText by remember { mutableStateOf(false) }
    var showNoInternetDialog by remember { mutableStateOf(false) }
    var userEmail by rememberSaveable { mutableStateOf(vm.allAccounts.firstOrNull()?.email?.trim().orEmpty()) }
    var selectedAccount: LoginAccount? by remember { mutableStateOf(vm.allAccounts.firstOrNull()) }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage: ErrorResponse? by remember { mutableStateOf(null) }

    if (showErrorDialog) {
        ErrorDialog(error = errorMessage) { showErrorDialog = false }
    }

    NoInternetConnectionDialog(
        isVisible = showNoInternetDialog,
        onOpenWifiSettings = { context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) },
        onDismiss = { showNoInternetDialog = false }
    )

    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = {
            val uid = it.user?.uid
            val email = it.user?.email
            val name = it.additionalUserInfo?.profile?.getOrDefault("given_name", "").toString()
            val lastName = it.additionalUserInfo?.profile?.getOrDefault("family_name", "").toString()

            vm.checkIfUserExistsInDb(it.user?.email.orEmpty()) { exists: Boolean ->
                if (exists) {
                    // user already signed in with Google -> refresh app to see changes
                    it.user?.let { user ->
                        vm.setFirebaseUser(user)
                        context.resetApplication()
                    }
                } else {
                    // user not signed in -> redirect to create user/provider
                    Handler(Looper.getMainLooper()).postDelayed({
                        onEmailValidated(
                            true,
                            GoogleUserData(
                                uid = uid,
                                userName = name,
                                userLastName = lastName
                            ),
                            false,
                            email.toString().trim(),
                            selectedAccount,
                            refreshAppIfAccountIsCreated
                        )
                    }, 10L)
                }
            }
        },
        onAuthError = {
            errorMessage = ErrorResponse(
                message = "Firebase Auth: ${it.localizedMessage}",
                status = 400
            )
            showProgressDialog = false
            showErrorDialog = true
        }
    )

    ProgressDialog(showProgressDialog)

    val focusManager = LocalFocusManager.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(18.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        VerticalSpacer(14.dp)

        TextBold(
            text = stringResource(R.string.login_connect_more_clients),
            size = 20.sp.autoSize(),
            color = Color.White,
            verticalSpace = 40.sp.autoSize()
        )

        VerticalSpacer(30.dp)

        TextSemiBold(
            text = stringResource(R.string.login_sign_in),
            size = 15.sp.autoSize(),
            color = Color.White
        )

        VerticalSpacer(20.dp)

        TextSemiBold(
            text = stringResource(R.string.login_create_account_for_free),
            size = 15.sp.autoSize(),
            color = Color.White,
            verticalSpace = 20.sp.autoSize()
        )

        VerticalSpacer(20.dp)

        RoundedEdittext(
            isForV2 = true,
            value = userEmail,
            modifier = Modifier.sidePadding(30.dp),
            keyboardType = KeyboardType.Email,
            placeholder = stringResource(R.string.login_email),
            onValueChange = {
                userEmail = it
                vm.getStoredAccountsFromInternalDb(it)
                showAutoCompleteTextField = it.length >= 3
                showValidationText = if (it.length > 6) {
                    !isValidEmail(it)
                } else {
                    false
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .sidePadding(30.dp)
        ) {
            ValidationText(
                isForV2 = true,
                show = showValidationText,
                color = Color.White,
                fillMaxWidth = false,
                text = context.getString(R.string.login_email_invalid_ii)
            )
        }

        VerticalSpacer(35.dp)

        TextSemiBold(
            text = stringResource(R.string.login_login_with),
            size = 15.sp.autoSize(),
            color = Color.White
        )

        VerticalSpacer(35.dp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ButtonSocialMediaWhiteRoundedCorners(
                iconDrawable = R.drawable.ic_google_v2,
                text = "Google",
                onGoogleIconClicked = {
                    if (!isInternetAvailable(context)) {
                        showNoInternetDialog = true
                        return@ButtonSocialMediaWhiteRoundedCorners
                    }
                    showProgressDialog = true
                    val gso = GoogleSignInOptions
                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(BuildConfig.SERVER_CLIENT_ID)
                        .requestEmail()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    launcher.launch(googleSignInClient.signInIntent)
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 30.dp),
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
                        text = stringResource(R.string.gral_continue).uppercase(),
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
                    if (isValidEmail(userEmail.trim())) {
                        showProgressDialog = true
                        vm.getSingedInMethodsFromEmailInFirebase(
                            email = userEmail.trim(),
                            signInMethods = { signInMethods ->
                                val userExists = signInMethods.isNotEmpty()
                                if (userExists) {
                                    // user already registered -> check if was via google or password
                                    val userSignedViaGoogle = signInMethods.any { it == "google.com" }
                                    if (userSignedViaGoogle) {
                                        println("User Signed via Google")
                                        // registered via google -> show popup from google
                                        val gso = GoogleSignInOptions
                                            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                            .requestIdToken(BuildConfig.SERVER_CLIENT_ID)
                                            .requestEmail()
                                            .build()
                                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                                        launcher.launch(googleSignInClient.signInIntent)
                                        userEmail = ""
                                    } else {
                                        println("User Signed via Email + Password")
                                        // registered via email + password -> go to normal login process
                                        onEmailValidated(
                                            false,
                                            null,
                                            true,
                                            userEmail.trim(),
                                            selectedAccount,
                                            refreshAppIfAccountIsCreated
                                        )
                                        showProgressDialog = false
                                    }
                                } else {
                                    // user not registered -> go to normal registration process
                                    onEmailValidated(
                                        false,
                                        null,
                                        false,
                                        userEmail.trim(),
                                        selectedAccount,
                                        refreshAppIfAccountIsCreated
                                    )
                                    showProgressDialog = false
                                }
                            }
                        )
                    } else {
                        showToast(context, context.getString(R.string.login_email_invalid))
                    }
                }
            )
        }
    }
}

@Composable
fun StartEmailContentV1(
    vm: AuthViewModel = hiltViewModel(),
    refreshAppIfAccountIsCreated: Boolean,
    onEmailValidated: (
        isNewAccountFromGmail: Boolean,
        googleUserData: GoogleUserData?,
        emailExists: Boolean,
        email: String,
        account: LoginAccount?,
        refreshAppIfAccountIsCreated: Boolean
    ) -> Unit
) {
    val view = LocalView.current
    val context = LocalContext.current
    val viewTreeObserver = view.viewTreeObserver
    val focusRequester = remember { FocusRequester() }
    var showProgressDialog by remember { mutableStateOf(false) }
    var showAutoCompleteTextField by remember { mutableStateOf(true) }
    var showValidationText by remember { mutableStateOf(false) }
    var showBottomDisclaimer by remember { mutableStateOf(true) }
    var showNoInternetDialog by remember { mutableStateOf(false) }
    val accounts by vm.accountsList.collectAsState()
    var userEmail by rememberSaveable { mutableStateOf(vm.allAccounts.firstOrNull()?.email?.trim().orEmpty()) }
    var selectedAccount: LoginAccount? by remember { mutableStateOf(vm.allAccounts.firstOrNull()) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage: ErrorResponse? by remember { mutableStateOf(null) }

    if (showErrorDialog) {
        ErrorDialog(error = errorMessage) { showErrorDialog = false }
    }

    NoInternetConnectionDialog(
        isVisible = showNoInternetDialog,
        onOpenWifiSettings = { context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) },
        onDismiss = { showNoInternetDialog = false }
    )

    DisposableEffect(viewTreeObserver) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val isKeyboardOpen = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
            showBottomDisclaimer = !isKeyboardOpen
        }
        viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = {
            val uid = it.user?.uid
            val email = it.user?.email
            val name = it.additionalUserInfo?.profile?.getOrDefault("given_name", "").toString()
            val lastName = it.additionalUserInfo?.profile?.getOrDefault("family_name", "").toString()

            vm.checkIfUserExistsInDb(it.user?.email.orEmpty()) { exists: Boolean ->
                if (exists) {
                    // user already signed in with Google -> refresh app to see changes
                    it.user?.let { user ->
                        vm.setFirebaseUser(user)
                        context.resetApplication()
                    }
                } else {
                    // user not signed in -> redirect to create user/provider
                    Handler(Looper.getMainLooper()).postDelayed({
                        onEmailValidated(
                            true,
                            GoogleUserData(
                                uid = uid,
                                userName = name,
                                userLastName = lastName
                            ),
                            false,
                            email.toString().trim(),
                            selectedAccount,
                            refreshAppIfAccountIsCreated
                        )
                    }, 10L)
                }
            }
        },
        onAuthError = {
            errorMessage = ErrorResponse(
                message = "Firebase Auth: ${it.localizedMessage}",
                status = 400
            )
            showProgressDialog = false
            showErrorDialog = true
        }
    )

    ProgressDialog(showProgressDialog)

    CardAuthBackground(addScroll = false) {

        VerticalSpacer(14.dp)

        TextBold(
            text = stringResource(R.string.login_sign_in_or_create_account),
            size = 22.sp.autoSize(),
            color = PinkFiestamas,
            verticalSpace = 25.sp.autoSize()
        )

        VerticalSpacer(14.dp)

        RoundedEdittext(
            value = userEmail,
            modifier = Modifier.focusRequester(focusRequester),
            keyboardType = KeyboardType.Email,
            placeholder = stringResource(R.string.login_email),
            onValueChange = {
                userEmail = it
                vm.getStoredAccountsFromInternalDb(it)
                showAutoCompleteTextField = it.length >= 3
                showValidationText = if (it.length > 6) {
                    !isValidEmail(it)
                } else {
                    false
                }
            }
        )

        ValidationText(show = showValidationText, text = context.getString(R.string.login_email_invalid))

        if (showAutoCompleteTextField) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 5.dp.autoSize())
                    .background(Color.White)
            ) {
                items(accounts) {account ->
                    CardLoginAutoComplete(account) {
                        selectedAccount = it
                        userEmail = it.email
                        showAutoCompleteTextField = false
                        showValidationText = false
                    }
                }
            }
        }

        VerticalSpacer(37.dp)

        ButtonPinkRoundedCorners(
            text = stringResource(R.string.gral_continue)
        ) {
            if (!isInternetAvailable(context)) {
                showNoInternetDialog = true
                return@ButtonPinkRoundedCorners
            }
            if (isValidEmail(userEmail.trim())) {
                showProgressDialog = true
                vm.getSingedInMethodsFromEmailInFirebase(
                    email = userEmail.trim(),
                    signInMethods = { signInMethods ->
                        val userExists = signInMethods.isNotEmpty()
                        if (userExists) {
                            // user already registered -> check if was via google or password
                            val userSignedViaGoogle = signInMethods.any { it == "google.com" }
                            if (userSignedViaGoogle) {
                                println("User Signed via Google")
                                // registered via google -> show popup from google
                                val gso = GoogleSignInOptions
                                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(BuildConfig.SERVER_CLIENT_ID)
                                    .requestEmail()
                                    .build()
                                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                                launcher.launch(googleSignInClient.signInIntent)
                                userEmail = ""
                            } else {
                                println("User Signed via Email + Password")
                                // registered via email + password -> go to normal login process
                                onEmailValidated(
                                    false,
                                    null,
                                    true,
                                    userEmail.trim(),
                                    selectedAccount,
                                    refreshAppIfAccountIsCreated
                                )
                                showProgressDialog = false
                            }
                        } else {
                            // user not registered -> go to normal registration process
                            onEmailValidated(
                                false,
                                null,
                                false,
                                userEmail.trim(),
                                selectedAccount,
                                refreshAppIfAccountIsCreated
                            )
                            showProgressDialog = false
                        }
                    }
                )
            } else {
                showToast(context, context.getString(R.string.login_email_invalid))
            }
        }

        VerticalSpacer(15.dp)

        HorizontalLineDecoration() // -------- O --------


        // =============== Starts Gmail | Facebook | Apple Login ===============

        VerticalSpacer(10.dp)

        TextMedium(
            text = stringResource(R.string.login_connect_with),
            size = 15.sp.autoSize(),
            horizontalSpace = 1.sp
        )

        VerticalSpacer(14.dp)

        Row {
            ButtonSocialMediaWhiteRoundedCornersV1(
                modifier = Modifier.weight(1f),
                iconDrawable = R.drawable.ic_google_colored,
                onGoogleIconClicked = {
                    if (!isInternetAvailable(context)) {
                        showNoInternetDialog = true
                        return@ButtonSocialMediaWhiteRoundedCornersV1
                    }
                    showProgressDialog = true
                    val gso = GoogleSignInOptions
                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(BuildConfig.SERVER_CLIENT_ID)
                        .requestEmail()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    launcher.launch(googleSignInClient.signInIntent)
                }
            )
        }

        VerticalSpacer(20.dp)

        HorizontalLine()

        // =============== Ends Gmail | Facebook | Apple Login ===============

        VerticalSpacer(16.dp)

        if (showBottomDisclaimer) {
            TermsAndConditionsClickable(
                firstLineStr = R.string.login_terms_and_conditions_one,
                centerText = true
            )

            VerticalSpacer(18.dp)

            HorizontalLine()

            VerticalSpacer(20.dp)

            TextRegular(
                text = stringResource(R.string.login_all_rights_reserved1),
                size = 12.sp.autoSize()
            )
            TextRegular(
                text = stringResource(R.string.login_all_rights_reserved2),
                size = 12.sp.autoSize()
            )

            VerticalSpacer(7.dp)
        }
    }
}

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (FirebaseAuthException) -> Unit
): ActivityResultLauncher<Intent> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        onAuthComplete(authTask.result!!)
                    } else {
                        onAuthError(authTask.exception as FirebaseAuthException)
                    }
                }
        } catch (e: ApiException) {
            onAuthError(FirebaseAuthException("google_signin_failed", e.message.orEmpty()))
        }
    }
}
