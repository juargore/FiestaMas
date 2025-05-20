package com.universal.fiestamas.presentation.screens.home.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import com.universal.fiestamas.BuildConfig
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.response.FirebaseUserDb
import com.universal.fiestamas.presentation.navigation.MainNavGraph
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.home.main.MainParentClass.userId
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.ui.BottomMenuMiFiesta
import com.universal.fiestamas.presentation.ui.BottomMenuProfileOnline
import com.universal.fiestamas.presentation.ui.dialogs.LocationPermissionsDialog
import com.universal.fiestamas.presentation.ui.dialogs.RequireNewAppVersionDialog
import com.universal.fiestamas.presentation.utils.BottomBarMenu
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.isRunningOnTablet
import com.universal.fiestamas.presentation.utils.openUrl

object MainParentClass {
    var userId: String? = null
}

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainParentScreen(
    vm: AuthViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    vm.checkIfUserIsSignedIn()

    val firebaseUser by vm.firebaseUser.collectAsState()
    val userDb by vm.firebaseUserDb.collectAsState()

    var showNewVersionDialog by remember { mutableStateOf(false) }
    var showLocationPermissionDialog by remember { mutableStateOf(false) }

    RequireNewAppVersionDialog(
        isVisible = showNewVersionDialog,
        isCancelable = false,
        onLinkClicked = { link -> openUrl(context, link) }
    )

    LocationPermissionsDialog(
        isVisible = showLocationPermissionDialog,
        isCancelable = false,
        onDismiss = { showLocationPermissionDialog = false }
    )

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            if (!it.value) showLocationPermissionDialog = true
        }
    }

    vm.userHasNewestVersion(BuildConfig.VERSION_CODE) {
        if (!it) showNewVersionDialog = true
    }

    LaunchedEffect(firebaseUser) {
        userId = firebaseUser?.uid

        if (firebaseUser != null) {
            // refresh token for push notifications if needed
            vm.updateTokenForPushNotificationIfNeeded()
            vm.getFirebaseUserDb(userId)

            // ask for push notification and location permissions if needed
            val permissions = permissionsToRequest(context)

            if (permissions.isNotEmpty()) {
                requestPermissionsLauncher.launch(permissions.toTypedArray())
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Scaffold(bottomBar = { BottomBar(navController, firebaseUser, userDb) }) {
        MainNavGraph(navController = navController)
    }
}


private fun permissionsToRequest(context: Context): List<String> {
    val permissionsToRequest = mutableListOf<String>()
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
        permissionsToRequest.add(Manifest.permission.ACCESS_WIFI_STATE)
    }
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
        permissionsToRequest.add(Manifest.permission.CHANGE_WIFI_STATE)
    }
    return permissionsToRequest
}

@Composable
fun BottomBar(
    navController: NavHostController,
    firebaseUser: FirebaseUser?,
    userDb: FirebaseUserDb?
) {
    val screens = listOf(
        BottomBarMenu.Home,
        BottomBarMenu.Party,
        BottomBarMenu.Profile.also {
            it.image = if (firebaseUser != null) {
                R.drawable.ic_user_online
            } else {
                R.drawable.ic_user_offline
            }
        }
    )
    val isTablet = isRunningOnTablet()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomBarDestination = screens.any { it.route == currentDestination?.route }
    val bottomNavigationHeight = if (isTablet) Modifier.height(90.dp) else Modifier
    if (bottomBarDestination) {
        BottomNavigation(
            modifier = bottomNavigationHeight,
            backgroundColor = Color.White
        ) {
            screens.forEach { screen ->
                AddItem(
                    userDb = userDb,
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    userDb: FirebaseUserDb?,
    screen: BottomBarMenu,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    BottomNavigationItem(
        icon = {
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            if (screen.route == "PARTY") {
                val colorFilter = if (isSelected) PinkFiestamas else PinkFiestamas.copy(alpha = 0.3f)
                BottomMenuMiFiesta(colorFilter)
            } else if (screen.route == "PROFILE" && userDb != null) {
                BottomMenuProfileOnline(userDb.photo.orEmpty())
            } else {
                val height = if (screen.route == "PROFILE") 31.dp.autoSize() else 27.dp.autoSize()
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        modifier = Modifier
                            .height(height)
                            .align(Alignment.Center),
                        painter = painterResource(screen.image!!),
                        contentDescription = screen.route,
                        colorFilter = null
                    )
                    if (!isSelected) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.8f)))
                    }
                }
            }
        },
        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
        unselectedContentColor = LocalContentColor.current.copy(ContentAlpha.disabled),
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }
    )
}
