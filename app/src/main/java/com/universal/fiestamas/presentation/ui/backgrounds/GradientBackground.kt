package com.universal.fiestamas.presentation.ui.backgrounds

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.domain.models.response.FirebaseUserDb
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.auth.NetworkViewModel
import com.universal.fiestamas.presentation.theme.LightBlue
import com.universal.fiestamas.presentation.theme.LightGray
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.ui.ClickableHeart
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.RequireLoginScreenView
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.UserNameText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.extensions.toColor

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GradientBackground(
    vma: AuthViewModel = hiltViewModel(),
    networkViewModel: NetworkViewModel = hiltViewModel(),
    isPinkBackground: Boolean = false,
    validateOfflineMode: Boolean = false,
    isStatusBarForDetails: Boolean = false,
    showBackButton: Boolean = true,
    backButtonColor: Color = PinkFiestamas,
    showBackButtonV2: Boolean = false,
    backButtonStringV2: String? = null,
    showLogoFiestamas: Boolean = true,
    showUserName: Boolean = true,
    showWifiIcon: Boolean = false,
    endButton: Int? = null,
    notificationsCounter: String? = null,
    showSearchButton: Boolean = false,
    showShareButton: Boolean = false,
    showHeartIcon: Boolean = false,
    service: Service? = null,
    user: FirebaseUserDb? = null,
    titleScreen: String? = null,
    titleScreenColor: Color = PinkFiestamas,
    addBottomPadding: Boolean = true,
    rating: Int? = null,
    content: @Composable () -> Unit,
    onBackButtonClicked: (() -> Unit)? = null,
    onTitleScreenClicked: (() -> Unit)? = null,
    onEndButtonClicked: (() -> Unit)? = null,
    onSearchButtonClicked: (() -> Unit)? = null,
    onHeartButtonClicked: (() -> Unit)? = null,
    onShareButtonClicked: (() -> Unit)? = null,
    onNavigateAuthClicked: (() -> Unit)? = null,
    onWiFiIconClicked: ((NetworkViewModel.FiestamasConnectionState) -> Unit)? = null,
) {
    vma.checkIfUserIsSignedIn()

    var isUserSignedIn by remember { mutableStateOf(false) }
    val activeUser by vma.firebaseUser.collectAsState()
    val fiestamasConnectionState by networkViewModel.fiestamasConnectionState.collectAsState()

    LaunchedEffect(activeUser) {
        isUserSignedIn = activeUser != null && activeUser?.email != null
    }

    val context = LocalContext.current
    val statusBarColor = colorResource(id = R.color.status_bar_color).toArgb()

    DisposableEffect(isPinkBackground) {
        val activity = context as Activity
        activity.window.statusBarColor = if (isPinkBackground) {
            "#c70580".toColor().toArgb()
        } else {
            statusBarColor
        }

        WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
            isAppearanceLightStatusBars = !isPinkBackground
        }

        onDispose { }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isPinkBackground) {
                        listOf(
                            "#c70580".toColor(), // pink
                            "#58123e".toColor() // dark pink
                        )
                    } else {
                        listOf(
                            LightBlue,
                            LightGray
                        )
                    },
                    startY = 0f,
                    endY = 700f
                )
            )
    ) {
        // remove ripple effect
        val interactionSource = remember { MutableInteractionSource() }
        // hide keyboard when clicking outside
        val keyboardController = LocalSoftwareKeyboardController.current
        // bottom margin for menu navigation
        val bp = if (addBottomPadding) 57.dp else 0.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { keyboardController?.hide() }
        ) {
            VerticalSpacer(height = 5.dp)

            if (isStatusBarForDetails) {
                ServiceDetailsToolbar(
                    titleScreen = titleScreen,
                    rating = rating,
                    service = service,
                    user = user,
                    showShareButton = showShareButton,
                    onBackButtonClicked = { onBackButtonClicked?.invoke() },
                    onHeartButtonClicked = { onHeartButtonClicked?.invoke() },
                    onShareButtonClicked = { onShareButtonClicked?.invoke() }
                )
            } else {
                if (showBackButton || showLogoFiestamas || titleScreen != null || endButton != null || showUserName) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp.autoSize())
                            .padding(horizontal = 8.dp)
                    ) {
                        if (showBackButton || showLogoFiestamas || titleScreen != null) {
                            Row (
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .align(Alignment.CenterStart)
                            ) {
                                if (showBackButton) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_arrow_back),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(backButtonColor),
                                        modifier = Modifier
                                            .height(26.dp.autoSize())
                                            .clickable { onBackButtonClicked?.invoke() }
                                    )
                                }
                                if (showLogoFiestamas) {
                                    if (isPinkBackground) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Image(painterResource(id = R.drawable.logo_fiestamas),
                                                contentScale = ContentScale.FillHeight,
                                                contentDescription = null,
                                                colorFilter = ColorFilter.tint(Color.White),
                                                modifier = Modifier.height(25.dp.autoSize())
                                            )
                                        }
                                    } else {
                                        Box (
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                                .fillMaxHeight()
                                        ) {
                                            Image(painterResource(
                                                id = R.drawable.logo_fiestamas),
                                                contentScale = ContentScale.FillHeight,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .height(25.dp.autoSize())
                                                    .align(Alignment.CenterStart)
                                            )
                                        }
                                    }
                                }
                                if (titleScreen != null) {
                                    Column (
                                        modifier = Modifier.fillMaxHeight()
                                    ) {
                                        TextBold(
                                            text = titleScreen,
                                            size = 24.sp.autoSize(),
                                            color = titleScreenColor,
                                            shadowColor = Color.Gray,
                                            align = TextAlign.Start,
                                            includeFontPadding = false,
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .padding(start = 5.dp)
                                                .clickable { onTitleScreenClicked?.invoke() }
                                        )
                                    }
                                }
                            }
                        }
                        if (endButton != null || showUserName || showSearchButton || showHeartIcon) {
                            Row (
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .align(Alignment.CenterEnd),
                            ) {
                                if (showUserName) {
                                    Box (
                                        modifier = Modifier.fillMaxHeight()
                                    ) {
                                        UserNameText(modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(end = 7.dp))
                                    }
                                }

                                if (isUserSignedIn && showWifiIcon && fiestamasConnectionState != NetworkViewModel.FiestamasConnectionState.UNDETECTED) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .padding(end = 5.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(R.drawable.ic_wifi_on),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .align(Alignment.BottomCenter)
                                                .clickable {
                                                    onWiFiIconClicked?.invoke(
                                                        fiestamasConnectionState
                                                    )
                                                }
                                        )
                                        val (wifiIcon, wifiCircleColor, wifiTextColor) = when (fiestamasConnectionState) {
                                            NetworkViewModel.FiestamasConnectionState.CONNECTED -> Triple("✔", "#009d4f".toColor(), Color.White)
                                            NetworkViewModel.FiestamasConnectionState.DETECTED_BUT_DISCONNECTED -> Triple("!", "#ffb600".toColor(), Color.White)
                                            else -> Triple("x", Color.Red, Color.White)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(14.dp)
                                                .background(
                                                    color = wifiCircleColor,
                                                    shape = CircleShape
                                                )
                                        ) {
                                            TextMedium(
                                                modifier = Modifier.align(Alignment.Center),
                                                text = wifiIcon,
                                                size = 10.sp,
                                                color = wifiTextColor,
                                                includeFontPadding = false,
                                                fillMaxWidth = false
                                            )
                                        }

                                        if (!networkViewModel.wasWifiDialogAlreadyShownToUser() &&
                                            fiestamasConnectionState == NetworkViewModel.FiestamasConnectionState.DETECTED_BUT_DISCONNECTED)
                                        {
                                            onWiFiIconClicked?.invoke(fiestamasConnectionState)
                                        }
                                    }
                                }

                                if (showHeartIcon) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_heart_stroke),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .padding(vertical = 2.dp)
                                            .padding(end = 6.dp)
                                            .clickable { onHeartButtonClicked?.invoke() }
                                    )
                                }
                                if (showSearchButton) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_search),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(PinkFiestamas),
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .padding(end = 3.dp)
                                            .padding(vertical = 1.dp)
                                            .clickable { onSearchButtonClicked?.invoke() }
                                    )
                                }
                                if (endButton != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .padding(end = 5.dp)
                                    ) {
                                        //R.drawable.ic_bell
                                        val colorFilter = ColorFilter.tint(
                                            color = if (endButton == R.drawable.ic_bell) {
                                                PinkFiestamas
                                            } else {
                                                Color.Black
                                            }
                                        )
                                        Image(
                                            painter = painterResource(id = endButton),
                                            contentDescription = null,
                                            colorFilter = colorFilter,
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .align(Alignment.BottomCenter)
                                                .clickable { onEndButtonClicked?.invoke() }
                                        )
                                        if (notificationsCounter != null) {
                                            TextSemiBold(
                                                text = notificationsCounter,
                                                size = 11.sp.autoSize(),
                                                color = Color.White,
                                                fillMaxWidth = false,
                                                includeFontPadding = false,
                                                horizontalSpace = (-1).sp,
                                                modifier = Modifier
                                                    .background(
                                                        color = Color.Red,
                                                        shape = CircleShape
                                                    )
                                                    .align(Alignment.TopEnd)
                                                    .sidePadding(4.dp.autoSize())
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showBackButtonV2) {
                VerticalSpacer(height = 12.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sidePadding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_back_thicker),
                        contentDescription = null,
                        modifier = Modifier
                            .height(24.dp.autoSize())
                            .clickable { onBackButtonClicked?.invoke() }
                    )
                    HorizontalSpacer(width = 10.dp)
                    backButtonStringV2?.let {
                        TextBold(
                            text = it,
                            color = Color.White,
                            size = 24.sp,
                            fillMaxWidth = false
                        )
                    }
                }
            }

            VerticalSpacer(height = 8.dp)

            if (!isUserSignedIn && validateOfflineMode) {
                RequireLoginScreenView { onNavigateAuthClicked?.invoke() }
            } else {
                content()
            }
        }
    }
}

@Composable
fun ServiceDetailsToolbar(
    titleScreen: String?,
    rating: Int? = null,
    service: Service?,
    user: FirebaseUserDb?,
    showShareButton: Boolean,
    onBackButtonClicked: () -> Unit,
    onHeartButtonClicked: () -> Unit,
    onShareButtonClicked: () -> Unit
) {
    if (service == null) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp.autoSize())
            .padding(horizontal = 8.dp.autoSize())
    ) {
        Row (
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.8f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = null,
                colorFilter = ColorFilter.tint(PinkFiestamas),
                modifier = Modifier
                    .height(26.dp.autoSize())
                    .clickable { onBackButtonClicked() }
            )
            Column (
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextSemiBold(
                    text = titleScreen.orEmpty(),
                    size = 21.sp.autoSize(),
                    align = TextAlign.Start,
                    includeFontPadding = false,
                    horizontalSpace = (-1).sp,
                    maxLines = 1,
                    addThreeDots = true,
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(start = 5.dp)
                )
            }
        }
        Row (
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.2f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            /*RatingStar(
                rating = rating?.toFloat() ?: 0f,
                maxRating = rating ?: 0,
                onRatingChanged = { },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 5.dp)
            )*/

            val isClicked = if (user?.likes.isNullOrEmpty()) {
                false
            } else {
                user?.likes?.contains(service.id) == true
            }

            ClickableHeart(
                isAlreadyClicked = isClicked,
                modifier = Modifier
                    .height(21.dp.autoSize())
                    .padding(end = 6.dp.autoSize())
            ) {
                onHeartButtonClicked()
            }
            if (showShareButton) {
                Image(
                    painter = painterResource(id = R.drawable.ic_share_ios),
                    contentDescription = null,
                    modifier = Modifier
                        .height(26.dp.autoSize())
                        .padding(end = 5.dp.autoSize())
                        .clickable { onShareButtonClicked() }
                )
            }
        }
    }
}
