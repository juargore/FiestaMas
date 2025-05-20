package com.universal.fiestamas.presentation.screens.home.main.mifiesta.favourites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.Event
import com.universal.fiestamas.domain.models.FirstQuestionsClientStored
import com.universal.fiestamas.domain.models.MyPartyEvent
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.home.main.MainParentClass
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.MainPartyViewModel
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.ServicesViewModel
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details.DetailsServiceViewModel
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.CardAuthBackground
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.dialogs.AddressAutoCompleteDialog
import com.universal.fiestamas.presentation.ui.dialogs.CardFavouriteService
import com.universal.fiestamas.presentation.ui.dialogs.ClientEventsDialog
import com.universal.fiestamas.presentation.ui.dialogs.ConfirmationServiceToEventDialog
import com.universal.fiestamas.presentation.ui.dialogs.FirstQuestionsDialog
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.resetApplication
import com.universal.fiestamas.presentation.utils.showToast
import com.universal.fiestamas.presentation.utils.showToastOnUiThread

@Composable
fun FavouritesScreen(
    vm: ServicesViewModel = hiltViewModel(),
    vma: AuthViewModel = hiltViewModel(),
    vmp: MainPartyViewModel = hiltViewModel(),
    vmd: DetailsServiceViewModel = hiltViewModel(),
    onBackClicked: () -> Unit
) {
    vma.getFirebaseUserDb(MainParentClass.userId)

    val context = LocalContext.current
    val firebaseUserDb by vma.firebaseUserDb.collectAsState()
    val likedServices by vm.likedServices.collectAsState()
    val horizontalList by vmp.horizontalListClient.collectAsState()

    var showProgressDialog by remember { mutableStateOf(false) }
    var selectedService: Service? by remember { mutableStateOf(null) }
    var selectedEvent: MyPartyEvent? by remember { mutableStateOf(null) }
    var showEventsDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showClientQuestionsDialog by remember { mutableStateOf(false) }
    var validateErrorDialog by remember { mutableStateOf(true) }
    var showAddressAutocompleteDialog by remember { mutableStateOf(false) }
    var savedQuestionsForClient: FirstQuestionsClientStored? by remember { mutableStateOf(null) }

    firebaseUserDb?.let {
        vm.getFavouriteServices(it)
        vmp.getEventsByClientId(it.id)
    }

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
                        ) { success, message, /* serviceEventId */ _ ->
                            showProgressDialog = false
                            if (success) {
                                showToastOnUiThread(context, "Servicio agregado correctamente")
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
                                context.resetApplication(1000L)
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
        onDismiss = { showConfirmationDialog = false }
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

    GradientBackground(
        content = {
            CardAuthBackground(
                centerContent = false,
                addScroll = false
            ) {
                VerticalSpacer(5.dp)
                TextSemiBold(
                    text = "Favoritos",
                    size = 20.sp.autoSize()
                )
                VerticalSpacer(height = 10.dp)
                Column(
                    modifier = Modifier.fillMaxSize()
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
                            items(likedServices) {
                                CardFavouriteService(
                                    service = it,
                                    onContactClicked = {
                                        if (horizontalList.isNullOrEmpty()) {
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
            }
        },
        addBottomPadding = false,
        onBackButtonClicked = { onBackClicked() }
    )
}
