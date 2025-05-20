package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.Event
import com.universal.fiestamas.domain.models.FirstQuestionsClientStored
import com.universal.fiestamas.domain.models.MyPartyEvent
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.domain.models.response.FirebaseUserDb
import com.universal.fiestamas.presentation.screens.home.main.MainParentClass
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.ServicesViewModel
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details.DetailsServiceViewModel
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape16
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape5
import com.universal.fiestamas.presentation.ui.HorizontalLine
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.calendar.clickable
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.toColor
import com.universal.fiestamas.presentation.utils.showToast
import com.universal.fiestamas.presentation.utils.showToastOnUiThread

@Composable
fun FavouritesDialog(
    vm: ServicesViewModel = hiltViewModel(),
    vmd: DetailsServiceViewModel = hiltViewModel(),
    isVisible: Boolean,
    horizontalList: List<MyPartyEvent?>,
    firebaseUserDb: FirebaseUserDb?,
    isCancelable: Boolean = true,
    onSeeAllClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        firebaseUserDb?.let {
            vm.getFavouriteServices(it)
        }

        val context = LocalContext.current
        val likedServices by vm.likedServices.collectAsState()

        var selectedService: Service? by remember { mutableStateOf(null) }
        var selectedEvent: MyPartyEvent? by remember { mutableStateOf(null) }
        var showConfirmationDialog by remember { mutableStateOf(false) }
        var showProgressDialog by remember { mutableStateOf(false) }
        var validateErrorDialog by remember { mutableStateOf(true) }
        var showEventsDialog by remember { mutableStateOf(false) }
        var showClientQuestionsDialog by remember { mutableStateOf(false) }
        var showAddressAutocompleteDialog by remember { mutableStateOf(false) }
        var savedQuestionsForClient: FirstQuestionsClientStored? by remember { mutableStateOf(null) }

        ProgressDialog(showProgressDialog, message = "Agregando Servicio...")

        with (vmd.getUserAddressIfExists()) {
            if (this != null) {
                savedQuestionsForClient = FirstQuestionsClientStored(
                    event = null,
                    festejadosNames = "",
                    date = "",
                    time = "",
                    numberOfGuests = "",
                    city = this.city.orEmpty(),
                    location = this.location
                )
            }
        }
        
        AddressAutoCompleteDialog(
            isVisible = showAddressAutocompleteDialog,
            onDismiss = { showAddressAutocompleteDialog = false },
            onAddressSelected = { address: Address? ->
                savedQuestionsForClient?.location = address?.location
                savedQuestionsForClient?.city = address?.city.orEmpty()
                showAddressAutocompleteDialog = false
                showClientQuestionsDialog = true
            }
        )

        if (showClientQuestionsDialog) {
            FirstQuestionsDialog(
                showEventsDropDown = true,
                serviceCategoryId = "",
                savedQuestions = savedQuestionsForClient,
                onDismiss = {
                    savedQuestionsForClient = null
                    showClientQuestionsDialog = false
                },
                onAddressClicked = { questions ->
                    savedQuestionsForClient = questions
                    showClientQuestionsDialog = false
                    showAddressAutocompleteDialog = true
                },
                onContinueClicked = { questions, event: Event? ->
                    showClientQuestionsDialog = false
                    showProgressDialog = true

                    val eventId = event!!.id

                    vmd.createEventByClient(
                        clientId = firebaseUserDb?.id.orEmpty(),
                        eventId = eventId,
                        questions = questions,
                        onSuccess = {
                            showToastOnUiThread(context, "Evento creado con éxito")
                            vmd.addServiceToExistingEvent(
                                eventId = it.data?.id.orEmpty(),
                                serviceId = selectedService?.id.orEmpty()
                            ) { success, message, serviceEventId ->
                                //println("AQUI: Response adding service to event -> ID: $serviceEventId")
                                showProgressDialog = false
                                if (success) {
                                    showToastOnUiThread(context, "Servicio agregado correctamente")
                                    onDismiss()
                                } else {
                                    showToastOnUiThread(context, message)
                                }
                            }
                        },
                        onFailure = {
                            showProgressDialog = false
                            showToastOnUiThread(context, it.message)
                        }
                    )
                }
            )
        }

        ConfirmationServiceToEventDialog(
            event = selectedEvent,
            isVisible = showConfirmationDialog,
            onAccept = {
                showConfirmationDialog = false
                vmd.serviceCanBeAddedToClientEvent(
                    clientEventId = selectedEvent!!.id,
                    serviceId = selectedService!!.id,
                    onResult = { isPossibleToAddService ->
                        showProgressDialog = true
                        if (isPossibleToAddService) {
                            validateErrorDialog = false
                            vmd.addServiceToExistingEvent(
                                eventId = selectedEvent!!.id,
                                serviceId = selectedService!!.id
                            ) { success, message, _ ->
                                if (success) {
                                    MainParentClass.userId?.let { userId ->
                                        selectedService?.id?.let { serviceId ->
                                            vm.likeService(userId, serviceId) { }
                                        }
                                    }
                                    showToastOnUiThread(context, "Servicio agregado!")
                                    showProgressDialog = false
                                    onDismiss()
                                } else {
                                    showProgressDialog = false
                                    showToastOnUiThread(context, message)
                                }
                            }
                        } else {
                            showProgressDialog = false
                            if (validateErrorDialog) {
                                showToastOnUiThread(context, "Este servicio ya fue agregado anteriormente a tu evento")
                            }
                        }
                    }
                )
            },
            onDismiss = {
                showConfirmationDialog = false
                onDismiss()
            }
        )

        ClientEventsDialog(
            isVisible = showEventsDialog,
            horizontalList = horizontalList,
            onDismiss = { showEventsDialog = false },
            onNewPartyClicked = {
                showEventsDialog = false
                showClientQuestionsDialog = true
            },
            onEventSelected = {
                selectedEvent = it
                showEventsDialog = false
                showConfirmationDialog = true
            }
        )

        BaseDialog(
            addCloseIcon = false,
            isCancelable = isCancelable,
            onDismiss = onDismiss,
            content = {
                TextSemiBold(
                    text = "Favoritos",
                    size = 18.sp.autoSize()
                )
                VerticalSpacer(height = 10.dp)
                Column(
                    modifier = Modifier
                        .height(400.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(0.85f)
                    ) {
                        if (likedServices.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                TextMedium(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = "Aún no has agregado favoritos",
                                    size = 16.sp.autoSize()
                                )
                            }
                        } else {
                            LazyColumn {
                                items(likedServices.take(3)) {
                                    CardFavouriteService(
                                        service = it,
                                        onContactClicked = {
                                            if (horizontalList.isEmpty()) {
                                                showToast(context, "Crea un evento para poder agregar este servicio")
                                            } else {
                                                selectedService = it
                                                showEventsDialog = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    if (likedServices.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(0.15f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(PinkFiestamas, shape = allRoundedCornerShape16)
                                    .clip(allRoundedCornerShape16)
                                    .clickable { onSeeAllClicked() }
                            ) {
                                TextMedium(
                                    modifier = Modifier.padding(
                                        vertical = 8.dp.autoSize(),
                                        horizontal = 18.dp.autoSize()
                                    ),
                                    text = "Ver todos",
                                    color = Color.White,
                                    fillMaxWidth = false,
                                    size = 16.sp.autoSize()
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun CardFavouriteService(
    service: Service?,
    onContactClicked: () -> Unit
) {
    if (service == null) return

    val context = LocalContext.current

    Column {
        Row (
            modifier = Modifier.height(100.dp.autoSize())
        ) {
            Image(
                painter = rememberAsyncImagePainter(service.image),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .padding(vertical = 8.dp.autoSize())
                    .clip(allRoundedCornerShape10)
                    .fillMaxHeight()
                    .width(85.dp.autoSize())
            )
            HorizontalSpacer(width = 10.dp)
            Column(
                modifier = Modifier.padding(vertical = 10.dp.autoSize())
            ) {
                Box(
                    modifier = Modifier
                        .clip(allRoundedCornerShape5)
                        .background("#E5A7F9".toColor())
                ) {
                    TextSemiBold(
                        text = service.name,
                        color = Color.White,
                        fillMaxWidth = false,
                        size = 13.sp.autoSize(),
                        maxLines = 1,
                        addThreeDots = true,
                        modifier = Modifier.padding(horizontal = 10.dp.autoSize())
                    )
                }
                TextSemiBold(
                    text = service.provider_name,
                    size = 13.sp.autoSize(),
                    fillMaxWidth = false
                )
                VerticalSpacer(height = 10.dp)
                Box(
                    modifier = Modifier
                        .background(
                            color = if (service.active == true) PinkFiestamas else Color.Gray,
                            shape = allRoundedCornerShape16
                        )
                        .clip(allRoundedCornerShape16)
                        .clickable {
                            if (service.active == true) {
                                onContactClicked()
                            } else {
                                showToast(
                                    context,
                                    "Este Servicio no puede ser contactado temporalmente"
                                )
                                showToast(context, "Intenta más tarde...")
                            }
                        }
                ) {
                    TextMedium(
                        modifier = Modifier.padding(
                            vertical = 6.dp.autoSize(),
                            horizontal = 15.dp.autoSize()
                        ),
                        text = if (service.active == true) {
                            stringResource(id = R.string.service_contact)
                        } else {
                            stringResource(id = R.string.service_unavailable)
                        },
                        color = Color.White,
                        fillMaxWidth = false,
                        size = 14.sp.autoSize()
                    )
                }
            }
        }
        VerticalSpacer(height = 5.dp)
        HorizontalLine(color = Color.LightGray)
        VerticalSpacer(height = 5.dp)
    }
}
