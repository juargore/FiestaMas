package com.universal.fiestamas.presentation.ui.dialogs

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.universal.fiestamas.R
import com.universal.fiestamas.presentation.screens.location.AddressAutoCompleteViewModel
import com.universal.fiestamas.presentation.screens.location.PermissionEvent
import com.universal.fiestamas.presentation.screens.location.ViewState
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.hasLocationPermission

@SuppressLint("MissingPermission", "SuspiciousIndentation")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionsDialog(
    vm: AddressAutoCompleteViewModel = hiltViewModel(),
    isVisible: Boolean,
    isCancelable: Boolean = true,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        val context = LocalContext.current

        BaseDialog(
            isCancelable = isCancelable,
            onDismiss = onDismiss,
            addCloseIcon = false,
            content = {
                val permissionState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )

                val viewState by vm.viewState.collectAsState()

                LaunchedEffect(!context.hasLocationPermission()) {
                    permissionState.launchMultiplePermissionRequest()
                }

                when {
                    permissionState.allPermissionsGranted -> {
                        LaunchedEffect(Unit) {
                            vm.handle(PermissionEvent.Granted)
                        }
                    }
                    permissionState.shouldShowRationale -> {
                        /*RationaleAlert(onDismiss = { }) {
                            permissionState.launchMultiplePermissionRequest()
                        }*/
                    }
                    !permissionState.allPermissionsGranted && !permissionState.shouldShowRationale -> {
                        LaunchedEffect(Unit) {
                            vm.handle(PermissionEvent.Revoked)
                        }
                    }
                }

                with(viewState) {
                    when (this) {
                        ViewState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        ViewState.RevokedPermissions -> {
                            Column(
                                modifier = Modifier
                                    .height(300.dp)
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_location),
                                    contentDescription = "Image",
                                    modifier = Modifier
                                        .size(50.dp)
                                )

                                VerticalSpacer(height = 5.dp)
                                TextSemiBold(text = "Se necesitan permisos de ubicación para usar la app")

                                VerticalSpacer(height = 8.dp)
                                val text = """
                                    Con el fin de ofrecerte los mejores servicios a tu alrededor, es necesario que nos permitas acceder a la ubicación de tu teléfono.
                                    No te preocupes, la única finalidad de obtener tu ubicación es para filtrar los servicios cerca de tí.
                                """.trimIndent()
                                TextRegular(text = text, size = 14.sp)

                                VerticalSpacer(height = 16.dp)
                                Button(
                                    onClick = {
                                        startActivity(context, Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), null)
                                    },
                                    enabled = !context.hasLocationPermission()
                                ) {
                                    if (context.hasLocationPermission()) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            color = Color.White
                                        )
                                    } else {
                                        Text("Ir a los Ajustes")
                                    }
                                }
                            }
                        }
                        is ViewState.Success -> onDismiss()
                    }
                }
            }
        )
    }
}
