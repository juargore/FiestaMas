package com.universal.fiestamas.presentation.screens.home.main.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.GoogleUserData
import com.universal.fiestamas.domain.models.LoginAccount
import com.universal.fiestamas.domain.models.UriFile
import com.universal.fiestamas.domain.models.response.FirebaseUserDb
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.auth.NetworkViewModel
import com.universal.fiestamas.presentation.screens.auth.StartEmailScreen
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.PurpleFiestaki
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.dialogs.FiestamasWifiFoundDialog
import com.universal.fiestamas.presentation.ui.dialogs.FiestamasWifiFoundDialogInfo
import com.universal.fiestamas.presentation.ui.dialogs.YesNoDialog
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.isProvider
import com.universal.fiestamas.presentation.utils.extensions.resetApplication
import com.universal.fiestamas.presentation.utils.extensions.sidePadding

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainProfileScreen(
    vm: AuthViewModel = hiltViewModel(),
    networkViewModel: NetworkViewModel = hiltViewModel(),
    onAuthProcessStarted: (
        isNewAccountFromGmail: Boolean,
        googleUserData: GoogleUserData?,
        emailExists: Boolean,
        email: String,
        account: LoginAccount?,
        refreshAppIfAccountIsCreated: Boolean
    ) -> Unit,
    onNavigateUpdatePasswordClicked: (email: String, userId: String) -> Unit,
    onRedirectToHome: () -> Unit
) {
    vm.checkIfUserIsSignedIn()

    val context = LocalContext.current
    val activeUser by vm.firebaseUser.collectAsState()
    val firebaseUserDb by vm.firebaseUserDb.collectAsState()
    var isUserSignedIn by remember { mutableStateOf(false) }
    var isPinkBackground by remember { mutableStateOf(false) }
    var showFiestamasWifiDialog by remember { mutableStateOf(false) }
    var showFiestamasWifiDialogInfo by remember { mutableStateOf(false) }
    var networkState: NetworkViewModel.FiestamasConnectionState by remember { mutableStateOf(
        NetworkViewModel.FiestamasConnectionState.UNDETECTED
    ) }

    LaunchedEffect(activeUser) {
        isUserSignedIn = activeUser != null && activeUser?.email != null
        vm.getFirebaseUserDb(activeUser?.uid)
    }

    FiestamasWifiFoundDialog(
        isVisible = showFiestamasWifiDialog,
        networkState = networkState,
        wasWifiDialogAlreadyShownToUser = networkViewModel.wasWifiDialogAlreadyShownToUser(),
        onDismiss = { showFiestamasWifiDialog = false },
        onConnectToNetwork = {
            showFiestamasWifiDialog = false
            networkViewModel.connectToWifi(context)
        },
        onConnectionRejected = {
            showFiestamasWifiDialog = false
            showFiestamasWifiDialogInfo = true
            networkViewModel.informThatWifiDialogWasAlreadyShownToUser()
        },
        onRedirectToHome = {
            showFiestamasWifiDialog = false
            onRedirectToHome()
        }
    )

    FiestamasWifiFoundDialogInfo(
        isVisible = showFiestamasWifiDialogInfo,
        onDismiss = { showFiestamasWifiDialogInfo = false }
    )

    GradientBackground(
        content = {
            if (isUserSignedIn && firebaseUserDb != null) {
                isPinkBackground = false
                if (firebaseUserDb?.role.isProvider()) {
                    ProfileProvider(
                        firebaseUserDb = firebaseUserDb!!,
                        onNavigateUpdatePassword = onNavigateUpdatePasswordClicked
                    )
                } else {
                    ProfileClient(
                        //firebaseUserDb = vm.firebaseUserDb.collectAsState(),
                        onNavigateUpdatePassword = onNavigateUpdatePasswordClicked,
                    )
                }
            } else {
                StartEmailScreen(
                    fullScreen = false,
                    refreshAppIfAccountIsCreated = true,
                    onEmailValidated = { isNewAccountFromGmail, googleUserData, exists, email, account, refresh ->
                        onAuthProcessStarted(isNewAccountFromGmail, googleUserData, exists, email, account, refresh)
                    }
                )
            }
        },
        isPinkBackground = isPinkBackground,
        showBackButton = false,
        addBottomPadding = true,
        showWifiIcon = true,
        onWiFiIconClicked = {
            networkState = it
            showFiestamasWifiDialog = true
        }
    )
}

@Composable
fun ProfilePhoto(firebaseUserDb: FirebaseUserDb?, uriFile: UriFile?) {
    if (uriFile?.uri != null) {
        AsyncImage(
            model = uriFile.uri,
            modifier = Modifier
                .size(110.dp.autoSize())
                .clip(CircleShape)
                .border(4.dp, Color.Gray, CircleShape),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    } else {
        val photo =
            if (firebaseUserDb?.photo.isNullOrEmpty()) {
                painterResource(id = R.drawable.ic_user)
            } else {
                rememberAsyncImagePainter(firebaseUserDb?.photo)
            }
        Image(
            painter = photo,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(110.dp.autoSize())
                .clip(CircleShape)
                .border(4.dp, Color.Gray, CircleShape)
        )
    }
}

@Composable
fun ProfileLogOutButton(
    vm: AuthViewModel,
    modifier: Modifier = Modifier,
    uid: String?,
    onStartedLogOutProcess: () -> Unit,
    onFinishedLogOutProcess: () -> Unit
) {
    var showYesNoDialogToLogOut by remember { mutableStateOf(false) }
    val context = LocalContext.current

    YesNoDialog(
        isVisible = showYesNoDialogToLogOut,
        icon = R.drawable.ic_question_circled,
        message = stringResource(id = R.string.profile_confirm_log_out),
        onDismiss = { showYesNoDialogToLogOut = false },
        onOk = {
            showYesNoDialogToLogOut = false
            onStartedLogOutProcess()
            vm.signOutFromAccount()
            vm.unregisterTokenForPushNotification(uid)
            context.resetApplication()
        }
    )


    Row(
        modifier = modifier.then(Modifier.clickable {
            showYesNoDialogToLogOut = true
        }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.width(16.dp.autoSize()),
            painter = painterResource(id = R.drawable.ic_log_out),
            colorFilter = ColorFilter.tint(color = PurpleFiestaki),
            contentDescription = null
        )
        HorizontalSpacer(width = 5.dp)
        TextMedium(
            text = stringResource(id = R.string.profile_log_out),
            fillMaxWidth = false,
            size = 15.sp.autoSize(),
            color = PurpleFiestaki,
            horizontalSpace = (-1).sp
        )
    }
}

@Composable
fun OptionsProfileButton(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .background(
                color = Color.Transparent,
                shape = allRoundedCornerShape12
            )
            .border(0.5.dp.autoSize(), PinkFiestamas, allRoundedCornerShape12)
            .clickable { onClick() }
    ) {
        TextRegular(
            modifier = Modifier
                .sidePadding()
                .padding(vertical = 4.dp),
            text = text,
            size = 14.sp.autoSize(),
            color = PinkFiestamas,
            fillMaxWidth = false
        )
    }
}
