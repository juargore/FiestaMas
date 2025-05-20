package com.universal.fiestamas.presentation.ui.dialogs

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.presentation.screens.location.AddressAutoCompleteViewModel
import com.universal.fiestamas.presentation.screens.location.PermissionEvent
import com.universal.fiestamas.presentation.screens.location.ViewState
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape16
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.hasLocationPermission
import kotlinx.coroutines.delay

@SuppressLint("MissingPermission", "SuspiciousIndentation")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapDialog(
    vm: AddressAutoCompleteViewModel = hiltViewModel(),
    isVisible: Boolean,
    cameraZoom: Float = 58f,
    isCancelable: Boolean = true,
    onDismiss: () -> Unit,
    onAddressSelected: (Address?) -> Unit
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
                                    .fillMaxSize()
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
                                TextSemiBold(text = stringResource(R.string.autocomplete_location_permissions_title))

                                VerticalSpacer(height = 8.dp)
                                TextRegular(text = stringResource(R.string.autocomplete_location_permissions_subtitle), size = 14.sp)

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
                                        Text(stringResource(R.string.autocomplete_location_permissions_settings))
                                    }
                                }
                            }
                        }

                        is ViewState.Success -> {
                            val currentLoc =
                                LatLng(
                                    location?.latitude ?: 0.0,
                                    location?.longitude ?: 0.0
                                )
                            MainScreen(
                                vm = vm,
                                currentPosition = LatLng(
                                    currentLoc.latitude,
                                    currentLoc.longitude
                                ),
                                cameraZoom = cameraZoom,
                                onClose = onDismiss,
                                onAddressCompleted = onAddressSelected
                            )
                        }
                    }
                }

            }
        )
    }
}

@Composable
fun MainScreen(
    vm: AddressAutoCompleteViewModel,
    currentPosition: LatLng,
    cameraZoom: Float,
    onClose: () -> Unit,
    onAddressCompleted: (Address?) -> Unit
) {
    val markerPosition = LatLng(currentPosition.latitude, currentPosition.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, cameraZoom) // bigger number -> closer camera
    }
    var lastCameraPosition by remember { mutableStateOf(cameraPositionState.position) }
    var currentAddress: Address? by remember { mutableStateOf(null) }

    LaunchedEffect(cameraPositionState.position) {
        while (true) {
            val currentCameraPosition = cameraPositionState.position
            delay(1000)

            if (lastCameraPosition == currentCameraPosition) {
                vm.getAddressByCoordinates(
                    LatLng(
                        currentCameraPosition.target.latitude,
                        currentCameraPosition.target.longitude
                    )
                ) { currentAddress = it }
            }
            lastCameraPosition = currentCameraPosition
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = true
            )
        )
        Image(
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.Center),
            painter = painterResource(id = R.drawable.ic_location),
            contentDescription = "marker",
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .clip(allRoundedCornerShape16)
                    .background(Color.White)
                    .border(0.5.dp, Color.Gray, allRoundedCornerShape16)
            ) {
                VerticalSpacer(height = 4.dp)
                TextMedium(
                    text = stringResource(R.string.autocomplete_location_selected_location),
                    size = 16.sp.autoSize()
                )

                VerticalSpacer(height = 5.dp)

                val city = currentAddress?.city.orEmpty()
                val state = currentAddress?.state.orEmpty()

                TextRegular(
                    text = currentAddress?.line1.orEmpty(),
                    size = 16.sp.autoSize()
                )
                TextRegular(
                    text = "$city, $state",
                    size = 16.sp.autoSize()
                )
                TextRegular(
                    text = currentAddress?.zipcode.orEmpty(),
                    size = 16.sp.autoSize()
                )

                VerticalSpacer(height = 7.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            onClose()
                        }) {
                        Text(
                            text = stringResource(id = R.string.gral_cancel),
                            fontSize = 16.sp.autoSize()
                        )
                    }
                    HorizontalSpacer(width = 10.dp)
                    Button(
                        onClick = {
                            // showToast(context, "Obteniendo ubicación...", duration = Toast.LENGTH_SHORT)
                            vm.getAddressByCoordinates(
                                LatLng(
                                    cameraPositionState.position.target.latitude,
                                    cameraPositionState.position.target.longitude
                                )
                            ) { onAddressCompleted(it) }
                        }) {
                        Text(
                            text = stringResource(id = R.string.gral_accept),
                            fontSize = 16.sp.autoSize()
                        )
                    }
                }
                VerticalSpacer(height = 3.dp)
            }
        }
    }
}

@Suppress("unused")
@Composable
fun RationaleAlert(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "We need location permissions to use this app",
                fontSize = 16.sp.autoSize()
            )
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
            ) {
                Text("OK")
            }
        }
    }
}

@Suppress("unused")
private suspend fun CameraPositionState.centerOnLocation(location: LatLng) = animate(
    update = CameraUpdateFactory.newLatLngZoom(
        location,
        15f
    ),
    durationMs = 1500
)
