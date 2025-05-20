package com.universal.fiestamas.presentation.screens.home.main.mifiesta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universal.fiestamas.domain.models.MyPartyEvent
import com.universal.fiestamas.domain.models.MyPartyEventWithServices
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.Promotion
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.domain.models.ServiceStatus
import com.universal.fiestamas.domain.models.UriFile
import com.universal.fiestamas.domain.models.request.PromoRequest
import com.universal.fiestamas.domain.models.request.EditPromoRequest
import com.universal.fiestamas.domain.usecases.AuthUseCase
import com.universal.fiestamas.domain.usecases.EventUseCase
import com.universal.fiestamas.domain.usecases.ServiceUseCase
import com.universal.fiestamas.domain.usecases.SharedPrefsUseCase
import com.universal.fiestamas.presentation.utils.extensions.daysUntilDate
import com.universal.fiestamas.presentation.utils.extensions.getStatus
import com.universal.fiestamas.presentation.utils.extensions.hasBeenAlreadySentToServer
import com.universal.fiestamas.presentation.utils.extensions.or
import com.universal.fiestamas.presentation.utils.extensions.sortByServiceStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainPartyViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val eventUseCase: EventUseCase,
    private val serviceUseCase: ServiceUseCase,
    private val sharedPrefsUseCase: SharedPrefsUseCase
) : ViewModel() {

    var mustRefreshListClient = true
    var mustRefreshListFavorite = true
    var gotServicesByEventsByProvider = false
    var alreadyEnabledOrDisabledService = false
    private var gotServicesByProvider = false
    private var alreadyDeletedService = false
    private var alreadyCreatedPromo = false
    var alreadyUploadedMediaFiles = false

    private var originalVerticalListClient = listOf<MyPartyService>()
    private var originalParentListClient = listOf<MyPartyEventWithServices>()

    private val _horizontalListClient = MutableStateFlow<List<MyPartyEvent?>?>(null)
    val horizontalListClient: StateFlow<List<MyPartyEvent?>?>
        get() = _horizontalListClient

    private val _verticalListClient = MutableStateFlow<List<MyPartyService?>?>(null)
    val verticalListClient: StateFlow<List<MyPartyService?>?>
        get() = _verticalListClient

    private val _servicesListProvider = MutableStateFlow<List<MyPartyService?>?>(null)
    val servicesListProvider: StateFlow<List<MyPartyService?>?>
        get() = _servicesListProvider

    private val _allServicesProvider = MutableStateFlow<List<Service?>>(emptyList())
    val allServicesProvider: StateFlow<List<Service?>>
        get() = _allServicesProvider

    private val _mediaLinksAfterSuccessMediaUpload = MutableStateFlow<Pair<List<String>?, List<String>?>?>(null)
    val mediaLinksAfterSuccessMediaUpload: StateFlow<Pair<List<String>?, List<String>?>?>
        get() = _mediaLinksAfterSuccessMediaUpload

    private val _promosList = MutableStateFlow<List<Promotion>>(emptyList())
    val promosList: StateFlow<List<Promotion>>
        get() = _promosList

    init {
        Testing.clientId?.let {
            getEventsWithServices(it)
        }
    }

    fun resetRedirectionToMyPartyFromHome() {
        sharedPrefsUseCase.setFirstTimeAppRunning(false)

        viewModelScope.launch {
            authUseCase.getSsidCredentials().collectLatest { credentials ->
                credentials?.let { ssidCredentials ->
                    if (ssidCredentials.ssid.isNotEmpty() && ssidCredentials.password.isNotEmpty()) {
                        val lastKnownCredentials = Pair(ssidCredentials.ssid, ssidCredentials.password)
                        sharedPrefsUseCase.setLastKnownSsidAndPassword(lastKnownCredentials)
                    }
                }
            }
        }
    }

    fun resetUserAddressOnShPrefs() {
        sharedPrefsUseCase.resetUserAddress()
    }

    fun getMyPartyServicesByProvider(providerId: String) {
        if (!gotServicesByEventsByProvider) {
            gotServicesByEventsByProvider = true
            viewModelScope.launch(Dispatchers.IO) {
                eventUseCase.getMyPartyServicesByProvider(providerId).collectLatest { list ->
                    for (service in list) {
                        service.serviceStatus = service.status.getStatus()
                        _servicesListProvider.value = list
                    }
                }
            }
        }
    }

    fun getEventsByClientId(clientId: String) {
        if (mustRefreshListFavorite) {
            mustRefreshListFavorite = false
            viewModelScope.launch(Dispatchers.IO) {
                eventUseCase.getMyPartyEventsByClientId(clientId).collectLatest {
                    _horizontalListClient.value = it
                }
            }
        }
    }

    fun getEventsWithServices(id: String, onFinished: (() -> Unit)? = null) {
        if (mustRefreshListClient) {
            mustRefreshListClient = false
            viewModelScope.launch(Dispatchers.IO) {
                eventUseCase.getMyPartyEventsWithServices(id).collectLatest { parentList ->
                    originalParentListClient = parentList
                    val (tempEventList, tempServiceList) = transformParentList(parentList)
                    originalVerticalListClient = tempServiceList
                    _horizontalListClient.value = tempEventList
                    _verticalListClient.value = originalVerticalListClient

                    onFinished?.invoke()
                }
            }
        }
    }

    fun filterServiceListByEvent(eventId: String) {
        val parentList = originalParentListClient.filter { it.event?.id == eventId }
        val (_, tempServiceList) = transformParentList(parentList)
        _verticalListClient.value = tempServiceList
    }

    private fun transformParentList(
        parentList: List<MyPartyEventWithServices>
    ): Pair<List<MyPartyEvent>, List<MyPartyService>> {
        val tempEventList = mutableListOf<MyPartyEvent>()
        val tempServiceList = mutableListOf<MyPartyService>()

        parentList.forEach { parentObject ->
            parentObject.event?.let { event ->
                tempEventList.add(event)
                parentObject.servicesEvents?.forEach { service ->
                    service.serviceStatus = service.status.getStatus()
                    tempServiceList.add(service)
                }
            }
        }

        val sortedEventList = sortMyPartyEventsByDate(tempEventList)
        return Pair(sortedEventList, tempServiceList)
    }

    fun sortProviderServiceListByStatus(status: ServiceStatus) {
        if (status == ServiceStatus.All) {
            gotServicesByEventsByProvider = false
        } else {
            _servicesListProvider.value = _servicesListProvider.value?.sortByServiceStatus(status)
        }
    }

    fun sortClientServiceListByStatus(status: ServiceStatus) {
        if (status == ServiceStatus.All) {
            mustRefreshListClient = true
        } else {
            _verticalListClient.value = _verticalListClient.value?.sortByServiceStatus(status)
        }
    }

    private fun sortMyPartyEventsByDate(myPartyEvents: List<MyPartyEvent>): List<MyPartyEvent> {
        return myPartyEvents.sortedBy { it.date?.toDate() }
    }

    fun getServicesByProviderId(providerId: String) {
        if (!gotServicesByProvider) {
            gotServicesByProvider = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.getServicesByProviderId(providerId).collectLatest {
                    _allServicesProvider.value = it
                }
            }
        }
    }

    fun deleteServiceById(serviceId: String) {
        if (!alreadyDeletedService) {
            alreadyDeletedService = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.deleteServiceById(serviceId).collectLatest {
                    gotServicesByProvider = false
                    alreadyDeletedService = false
                }
            }
        }
    }

    fun enableOrDisableService(serviceId: String, isActive: Boolean) {
        if (!alreadyEnabledOrDisabledService) {
            alreadyEnabledOrDisabledService = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.enableOrDisableService(serviceId, isActive).collectLatest {
                    alreadyEnabledOrDisabledService = false
                }
            }
        }
    }

    fun getServicePathById(serviceId: String, onFinished: (List<String>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val pathList = mutableListOf<String>()
            val service = serviceUseCase.getServiceDetails(serviceId).firstOrNull()
            service?.name_service_category?.let {
                pathList.add(it)
            }
            service?.name_service_type?.let {
                pathList.add(it)
            }
            service?.name_sub_service_type?.let {
                pathList.add(it)
            }
            onFinished(pathList.toList())
        }
    }

    fun getServiceIdForNotification(): String {
        return sharedPrefsUseCase.getServiceIdNotification()
    }

    fun createNewPromo(request: PromoRequest?, onFinished: (Boolean) -> Unit) {
        alreadyUploadedMediaFiles = false
        if (!alreadyCreatedPromo && request != null) {
            alreadyCreatedPromo = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.createPromo(request).collectLatest {
                    onFinished(it)
                }
            }
        }
    }

    fun editExistingPromo(promoId: String, request: EditPromoRequest, onFinished: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = serviceUseCase.editExistingPromo(promoId, request).first()
            onFinished(response)
        }
    }

    fun uploadMediaFiles(images: List<UriFile?>, videos: List<UriFile?>, isEditing: Boolean) {
        if (!alreadyUploadedMediaFiles) {
            alreadyUploadedMediaFiles = true
            if (!isEditing) {
                viewModelScope.launch(Dispatchers.IO) {
                    serviceUseCase.uploadMediaFiles(images, videos).collectLatest { pair ->
                        _mediaLinksAfterSuccessMediaUpload.value = pair
                    }
                }
            } else {
                val alreadySentImages = mutableListOf<UriFile?>()
                val notSentImages = mutableListOf<UriFile?>()
                images.forEach {
                    if (it?.uri?.path.hasBeenAlreadySentToServer()) {
                        alreadySentImages.add(it)
                    } else {
                        notSentImages.add(it)
                    }
                }

                viewModelScope.launch(Dispatchers.IO) {
                    if (notSentImages.isEmpty()) {
                        val imagesUrl = alreadySentImages.map { it?.url ?: "" }
                        _mediaLinksAfterSuccessMediaUpload.value = Pair(imagesUrl, listOf())
                    } else {
                        serviceUseCase.uploadMediaFiles(notSentImages, listOf()).collectLatest { pair ->
                            val imagesUrl = pair.first + alreadySentImages.map { it?.url ?: "" }
                            _mediaLinksAfterSuccessMediaUpload.value = Pair(imagesUrl, listOf())
                        }
                    }
                }
            }
        }
    }

    fun getPromosForProvider(providerId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            serviceUseCase.getPromos(providerId).collectLatest {
                _promosList.value = it
            }
        }
    }

    fun deletePromo(promoId: String, onFinished: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            serviceUseCase.deletePromotion(promoId).collectLatest {
                onFinished(it)
            }
        }
    }
}
