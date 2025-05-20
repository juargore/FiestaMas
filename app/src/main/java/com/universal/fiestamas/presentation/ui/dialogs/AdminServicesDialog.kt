package com.universal.fiestamas.presentation.ui.dialogs

import android.os.Handler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.MainPartyViewModel
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape14
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape16
import com.universal.fiestamas.presentation.ui.HorizontalLine
import com.universal.fiestamas.presentation.ui.LinkedStrings
import com.universal.fiestamas.presentation.ui.SwitchButton
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.calendar.clickable
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.convertTimestampToDateAndHourUTC
import com.universal.fiestamas.presentation.utils.extensions.or
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AdminServicesDialog(
    vm: MainPartyViewModel = hiltViewModel(),
    isCancelable: Boolean = true,
    providerId: String,
    servicesByEvents: List<MyPartyService?>?,
    onNewServiceClicked: () -> Unit,
    onEditServiceClicked: (Service) -> Unit,
    onDismiss: () -> Unit,
) {
    vm.getServicesByProviderId(providerId)

    val allServices by vm.allServicesProvider.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPendingServicesDialog by remember { mutableStateOf(false) }
    var selectedService: Service? by remember { mutableStateOf(null) }
    var pendingServices: List<MyPartyService?> by remember { mutableStateOf(emptyList()) }

    YesNoDialog(
        isVisible = showDeleteDialog,
        icon = R.drawable.ic_question_circled,
        message = "¿Confirma que desea eliminar el servicio seleccionado?",
        onDismiss = { showDeleteDialog = false },
        onOk = {
            showDeleteDialog = false
            selectedService?.id?.let {
                vm.deleteServiceById(it)
            }
        }
    )

    PendingServicesDialog(
        vm = vm,
        isVisible = showPendingServicesDialog,
        isActive = selectedService?.active,
        serviceId = selectedService?.id.orEmpty(),
        servicesByEvents = pendingServices,
        onDismiss = { showPendingServicesDialog = false }
    )

    BaseDialog(
        isCancelable = isCancelable,
        onDismiss = onDismiss,
        content = {
            TextMedium(
                text = "Administración de mis servicios",
                size = 16.sp.autoSize()
            )
            VerticalSpacer(height = 10.dp.autoSize())
            Column(
                modifier = Modifier
                    .height(200.dp.autoSize())
                    .fillMaxWidth()
                    .clip(allRoundedCornerShape16)
                    .background(Color.White)
                    .border(0.5.dp.autoSize(), Color.Gray, allRoundedCornerShape16)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(10.dp.autoSize()),
                    verticalArrangement = Arrangement.spacedBy(5.dp.autoSize())
                ) {
                    itemsIndexed(allServices) { index, item ->
                        CardServicesProvider(
                            vm = vm,
                            item = item,
                            onEdit = {
                                selectedService = it
                                onEditServiceClicked(it)
                            },
                            onDelete = { service ->
                                selectedService = service
                                if (servicesByEvents.isNullOrEmpty()) {
                                    showDeleteDialog = true
                                } else {
                                    servicesByEvents.filter {
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
            VerticalSpacer(height = 10.dp)
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { onNewServiceClicked() },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier.size(40.dp.autoSize()),
                        painter = painterResource(R.drawable.ic_add_fiestamas),
                        contentDescription = null
                    )
                    TextMedium(
                        text = "Nuevo Servicio",
                        fillMaxWidth = false,
                        size = 13.sp.autoSize()
                    )
                }
            }
        }
    )
}

@Composable
fun CardServicesProvider(
    vm: MainPartyViewModel,
    item: Service?,
    onEdit: (Service) -> Unit,
    onDelete: (Service) -> Unit,
) {
    if (item == null) return

    val scope = rememberCoroutineScope()
    var showProgressDialog by remember { mutableStateOf(false) }

    ProgressDialog(showProgressDialog)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(allRoundedCornerShape14)
            .background(Color.White)
            .padding(10.dp.autoSize())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(0.7f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SwitchButton(
                    startChecked = item.active == true,
                    onChecked = {
                        showProgressDialog = true
                        vm.enableOrDisableService(item.id, !item.active!!)
                        scope.launch {
                            delay(500L)
                            vm.alreadyEnabledOrDisabledService = false
                            showProgressDialog = false
                        }
                    }
                )
                TextMedium(
                    text = item.name,
                    size = 13.sp.autoSize(),
                    align = TextAlign.Start,
                    fillMaxWidth = false,
                    addThreeDots = true,
                    maxLines = 1
                )
            }
            Column(
                modifier = Modifier
                    .weight(0.15f)
                    .clickable { onEdit(item) },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(20.dp.autoSize()),
                    painter = painterResource(R.drawable.ic_edit),
                    colorFilter = ColorFilter.tint(PinkFiestamas),
                    contentDescription = null,
                )
                TextRegular(
                    text = "Editar",
                    size = 10.sp.autoSize()
                )
            }
            Column(
                modifier = Modifier
                    .weight(0.15f)
                    .clickable { onDelete(item) },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(20.dp.autoSize()),
                    painter = painterResource(R.drawable.ic_trash_can_filled),
                    colorFilter = ColorFilter.tint(PinkFiestamas),
                    contentDescription = null,
                )
                TextRegular(
                    text = "Eliminar",
                    size = 10.sp.autoSize()
                )
            }
        }
        LinkedStrings(
            smallest = true,
            separator = ">",
            strings = listOf(
                item.name_service_category.orEmpty(),
                item.name_service_type.orEmpty(),
                item.name_sub_service_type.orEmpty(),
                item.name
            )
        )
    }
}

@Composable
fun PendingServicesDialog(
    vm: MainPartyViewModel,
    isVisible: Boolean,
    isActive: Boolean?,
    isCancelable: Boolean = true,
    serviceId: String,
    servicesByEvents: List<MyPartyService?>?,
    onDismiss: () -> Unit,
) {
    if (isVisible) {
        BaseDialog(
            isCancelable = isCancelable,
            onDismiss = onDismiss,
            addCloseIcon = false,
            content = {
                TextSemiBold(
                    text = "Servicios pendientes",
                    size = 18.sp.autoSize()
                )

                VerticalSpacer(height = 14.dp)

                TextMedium(
                    text = "No es posible eliminar el servicio porque que aún tienes los siguientes eventos pendientes:",
                    size = 16.sp.autoSize(),
                    verticalSpace = 16.sp.autoSize()
                )

                VerticalSpacer(height = 10.dp)

                LazyColumn(
                    contentPadding = PaddingValues(12.dp.autoSize()),
                    verticalArrangement = Arrangement.spacedBy(5.dp.autoSize())
                ) {
                    itemsIndexed(servicesByEvents.orEmpty()) { index, item ->
                        CardPendingService(item = item, index = index)
                    }
                }
                VerticalSpacer(height = 10.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SwitchButton(
                        startChecked = isActive != true,
                        onChecked = {
                            if (serviceId.isNotEmpty()) {
                                vm.enableOrDisableService(serviceId, !isActive!!)
                            }
                        }
                    )
                    TextRegular(
                        text = "Deshabilitar función para futuros eventos.",
                        size = 13.sp.autoSize(),
                        fillMaxWidth = false,
                        align = TextAlign.Start
                    )
                }
            }
        )
    }
}

@Composable
fun CardPendingService(
    item: MyPartyService?,
    index: Int,
) {
    if (item == null) return

    Column(modifier = Modifier.fillMaxWidth()) {
        if (index != 0) {
            HorizontalLine(color = Color.LightGray, thick = 0.5.dp)
            VerticalSpacer(height = 12.dp)
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(0.5f)) {
                TextMedium(
                    text = "${item.event_data?.name_event_type} ${item.event_data?.name}",
                    size = 13.sp.autoSize(),
                    fillMaxWidth = false
                )
            }
            val pairDate = convertTimestampToDateAndHourUTC(item.date)
            Column(
                modifier = Modifier
                    .weight(0.25f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextRegular(
                    text = pairDate.first,
                    color = PinkFiestamas,
                    size = 11.sp.autoSize()
                )
            }
            Column(
                modifier = Modifier
                    .weight(0.15f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextRegular(
                    text = pairDate.second,
                    color = PinkFiestamas,
                    size = 11.sp.autoSize()
                )
            }
        }
    }
}
