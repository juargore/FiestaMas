package com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universal.fiestamas.domain.common.BaseResult
import com.universal.fiestamas.domain.common.ErrorResponse
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.Attribute
import com.universal.fiestamas.domain.models.FirstQuestionsClient
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.domain.models.UserReview
import com.universal.fiestamas.domain.models.request.CreateEventResponseV2
import com.universal.fiestamas.domain.usecases.EventUseCase
import com.universal.fiestamas.domain.usecases.ServiceUseCase
import com.universal.fiestamas.domain.usecases.SharedPrefsUseCase
import com.universal.fiestamas.presentation.utils.Constants.ONE_SECOND
import com.universal.fiestamas.presentation.utils.extensions.openGoogleMaps
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsServiceViewModel @Inject constructor(
    private val serviceUseCase: ServiceUseCase,
    private val eventUseCase: EventUseCase,
    private val sharedPrefsUseCase: SharedPrefsUseCase
): ViewModel() {

    var alreadyAddedServiceToExistingEvent = false
    private var alreadyValidatedServiceCanBeAdded = false
    var alreadyLoggedServiceView = false
    var alreadyLoggedServiceContact = false
    var alreadyLoggedServiceClickContact = false
    private var alreadyCreatedEventByClient = false
    var alreadyLikedService = false
    private var gotReviewsByServiceId = false
    private var gotServiceDetails = false
    private var gotAttributes = false

    private val _service = MutableStateFlow<Service?>(null)
    val service: StateFlow<Service?>
        get() = _service

    private val _attributes = MutableStateFlow<List<Attribute?>?>(null)
    val attributes: StateFlow<List<Attribute?>?>
        get() = _attributes

    private val _reviews = MutableStateFlow<List<UserReview?>?>(null)
    //val reviews: StateFlow<List<UserReview?>?>
        //get() = _reviews

    fun getServiceDetails(serviceId: String, getAlsoAttributes: Boolean = true) {
        if (!gotServiceDetails) {
            gotServiceDetails = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.getServiceDetails(serviceId).collect { service ->
                    _service.value = service
                    if (getAlsoAttributes) {
                        getAttributes(service?.attributes.orEmpty())
                        //service?.id?.let { getReviewsByServiceId(it) }
                    }
                }
            }
        }
    }

    private fun getAttributes(ids: List<String>) {
        if (!gotAttributes) {
            gotAttributes = true
            if (ids.isEmpty()) {
                _attributes.value = emptyList()
            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    serviceUseCase.getAttributesByIds(ids).collect { attributes ->
                        _attributes.value = attributes
                    }
                }
            }
        }
    }

    @Suppress("unused")
    private fun getReviewsByServiceId(id: String) {
        if (!gotReviewsByServiceId) {
            gotReviewsByServiceId = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.getReviewsByServiceId(id).collect { reviews ->
                    _reviews.value = reviews
                }
            }
        }
    }

    fun likeService(userId: String, serviceId: String, onFinished: () -> Unit) {
        if (!alreadyLikedService) {
            alreadyLikedService = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.likeService(userId, serviceId).collectLatest { response ->
                    when (response) {
                        is BaseResult.Success -> println("Success: ${response.data}")
                        is BaseResult.Error -> println("Error: ${response.rawResponse.message}")
                    }; onFinished()
                }
            }
        }
    }

    fun addServiceToExistingEvent(eventId: String, serviceId: String, onFinished: (Boolean, String?, String) -> Unit) {
        if (!alreadyAddedServiceToExistingEvent) {
            alreadyAddedServiceToExistingEvent = true
            viewModelScope.launch(Dispatchers.IO) {
                eventUseCase.addServiceToEvent(eventId, serviceId).collectLatest { serviceEventId ->
                    if (serviceEventId.isBlank()) {
                        onFinished(false, "Error!", serviceEventId)
                    } else {
                        onFinished(true, null, serviceEventId)
                    }
                }
            }
        }
    }

    fun createEventByClient(
        clientId: String,
        eventId: String,
        questions: FirstQuestionsClient,
        onSuccess: (CreateEventResponseV2) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
        if (!alreadyCreatedEventByClient) {
            alreadyCreatedEventByClient =  true
            viewModelScope.launch(Dispatchers.IO) {
                eventUseCase.createEventByClient(clientId, eventId, questions).collectLatest { response ->
                    when (response) {
                        is BaseResult.Success -> onSuccess(response.data)
                        is BaseResult.Error -> onFailure(response.rawResponse)
                    }
                }
            }
        }
    }

    // clientEventId: Id of client event already created in server
    // serviceId: Id of service which client is trying to add to existing client event
    fun serviceCanBeAddedToClientEvent(clientEventId: String, serviceId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!alreadyValidatedServiceCanBeAdded) {
                alreadyValidatedServiceCanBeAdded = true
                val list = eventUseCase.getServicesEventByClientEventIdInThread(clientEventId).first()
                val coincidences = list.filter { myPartyService -> myPartyService.id_service == serviceId }
                onResult(coincidences.isEmpty())

                delay(ONE_SECOND)
                alreadyValidatedServiceCanBeAdded = false
            } else {
                onResult(false)

                delay(ONE_SECOND)
                alreadyValidatedServiceCanBeAdded = false
            }
        }
    }

    @Suppress("unused")
    fun getIntentForEmail(providerEmail: String) : Intent {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = "mailto:".toUri()
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(providerEmail))
        return intent
    }

    fun getIntentForWhatsApp(providerPhone: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = "https://api.whatsapp.com/send?phone=$providerPhone".toUri()
        return intent
    }

    fun openGoogleMaps(context: Context, lat: String?, lng: String?) {
        try {
            lat?.toDouble()?.let { latitude ->
                lng?.toDouble()?.let { longitude ->
                    context.openGoogleMaps(latitude, longitude)
                }
            }
        } catch (e: Exception) {
            Log.e("Error", e.localizedMessage.orEmpty())
        }
    }

    fun saveServiceIdForNotification(serviceId: String) {
        sharedPrefsUseCase.setServiceIdNotification(serviceId)
    }

    fun recordLogService(logType: LogServiceType, serviceId: String?) {
        if (serviceId != null) {
            val canLog = when (logType) {
                LogServiceType.VIEW -> !alreadyLoggedServiceView
                LogServiceType.CONTACT -> !alreadyLoggedServiceContact
                LogServiceType.CLICK_CONTACT -> !alreadyLoggedServiceClickContact
            }
            if (canLog) {
                when (logType) {
                    LogServiceType.VIEW -> alreadyLoggedServiceView = true
                    LogServiceType.CONTACT -> alreadyLoggedServiceContact = true
                    LogServiceType.CLICK_CONTACT -> alreadyLoggedServiceClickContact = true
                }
                viewModelScope.launch(Dispatchers.IO) {
                    val response = eventUseCase.logService(serviceId, logType).first()
                    println("Response from log $logType = ${response?.status}")
                }
            }
        }
    }

    fun getUserAddressIfExists(): Address? {
        val savedAddress = sharedPrefsUseCase.getUserAddress()
        return savedAddress
    }

    enum class LogServiceType {
        VIEW,
        CONTACT,
        CLICK_CONTACT
    }
}
