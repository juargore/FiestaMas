package com.universal.fiestamas.presentation.screens.home.main.mifiesta.admin_services

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.MainPartyViewModel
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.ui.FloatingButtonV2
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.dialogs.CardServicesProvider
import com.universal.fiestamas.presentation.ui.dialogs.PendingServicesDialog
import com.universal.fiestamas.presentation.ui.dialogs.YesNoDialogV2
import com.universal.fiestamas.presentation.ui.empty_screen.BaseEmptyScreen
import com.universal.fiestamas.presentation.utils.extensions.or
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.showToastOnUiThread

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminServicesScreen(
    vm: MainPartyViewModel = hiltViewModel(),
    vma: AuthViewModel = hiltViewModel(),
    providerId: String,
    onNewServiceClicked: () -> Unit,
    onEditServiceClicked: (Service) -> Unit,
    onBackClicked: () -> Unit
) {
    vma.setProviderShouldBeRedirectedToServices(false)

    vm.getMyPartyServicesByProvider(providerId)
    vm.getServicesByProviderId(providerId)

    val context = LocalContext.current
    val allServices by vm.allServicesProvider.collectAsState()
    val servicesByEvents by vm.servicesListProvider.collectAsState()

    var selectedService: Service? by remember { mutableStateOf(null) }
    var pendingServices: List<MyPartyService?> by remember { mutableStateOf(emptyList()) }
    var showPendingServicesDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    PendingServicesDialog(
        vm = vm,
        isVisible = showPendingServicesDialog,
        isActive = selectedService?.active,
        serviceId = selectedService?.id.orEmpty(),
        servicesByEvents = pendingServices,
        onDismiss = { showPendingServicesDialog = false }
    )

    YesNoDialogV2(
        isVisible = showDeleteDialog,
        title = stringResource(R.string.service_delete_service_title),
        message = stringResource(R.string.service_delete_service_body),
        textPrimaryButton = stringResource(R.string.gral_delete),
        onDismiss = { showDeleteDialog = false },
        onPrimaryButtonClicked = {
            showDeleteDialog = false
            selectedService?.id?.let {
                showToastOnUiThread(context, context.getString(R.string.service_delete_service_in_progress))
                vm.deleteServiceById(it)
            }
        }
    )

    GradientBackground(
        content = {
            if (allServices.isEmpty()) {
                BaseEmptyScreen(
                    grayText = stringResource(R.string.service_no_services),
                    buttonText = stringResource(R.string.service_add_service),
                    onAddClicked = { onNewServiceClicked() },
                    imageTop = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        ) {
                            Box (
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(PinkFiestamas, CircleShape)
                                    .align(Alignment.TopCenter)
                                    .padding(5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    modifier = Modifier.fillMaxSize(),
                                    painter = painterResource(R.drawable.ic_user_filled),
                                    colorFilter = ColorFilter.tint(color = Color.White),
                                    contentDescription = null
                                )
                            }
                            Image(
                                modifier = Modifier
                                    .size(150.dp)
                                    .align(Alignment.BottomCenter),
                                painter = painterResource(R.drawable.ic_hand_holding),
                                colorFilter = ColorFilter.tint(color = PinkFiestamas),
                                contentDescription = null
                            )
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        VerticalSpacer(height = 20.dp)
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .sidePadding()
                        ) {
                            FlowRow(
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                allServices.forEach { item ->
                                    CardServicesProvider(
                                        vm = vm,
                                        item = item,
                                        onEdit = { service ->
                                            selectedService = service
                                            onEditServiceClicked(service)
                                        },
                                        onDelete = { service ->
                                            selectedService = service
                                            if (servicesByEvents.isNullOrEmpty()) {
                                                showDeleteDialog = true
                                            } else {
                                                servicesByEvents!!.filter {
                                                    (it?.date ?: Timestamp.now()) >= Timestamp.now()
                                                }.let { list ->
                                                    list.find {
                                                        it?.id_service == selectedService?.id
                                                    }?.let {
                                                        pendingServices = list.filter { it?.id_service == selectedService?.id }
                                                        showPendingServicesDialog = true
                                                    }.or {
                                                        showDeleteDialog = true
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    FloatingButtonV2(
                        iconVector = Icons.Filled.Add,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(24.dp)
                    ) {
                        onNewServiceClicked()
                    }
                }
            }
        },
        addBottomPadding = false,
        showLogoFiestamas = false,
        titleScreen = stringResource(R.string.service_services),
        onBackButtonClicked = { onBackClicked() }
    )
}
