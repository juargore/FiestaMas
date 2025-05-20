package com.universal.fiestamas.presentation.screens.home.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.Event
import com.universal.fiestamas.domain.models.FirstQuestionsClient
import com.universal.fiestamas.domain.models.Role
import com.universal.fiestamas.domain.models.Screen
import com.universal.fiestamas.domain.models.ScreenInfo
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.domain.models.request.CreateEventResponse
import com.universal.fiestamas.domain.usecases.EventUseCase
import com.universal.fiestamas.domain.usecases.ServiceUseCase
import com.universal.fiestamas.domain.usecases.SharedPrefsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServicesCategoriesViewModel @Inject constructor(
    private val eventUseCase: EventUseCase,
    private val servicesUseCase: ServiceUseCase,
    private val sharedPrefsUseCase: SharedPrefsUseCase
) : ViewModel() {

    private var gotServicesByEventId = false

    private val _showProgressDialog = MutableStateFlow(false)
    val showProgressDialog: StateFlow<Boolean>
        get() = _showProgressDialog

    private val _eventsByService = MutableStateFlow<List<Event?>?>(null)
    val eventsByService: StateFlow<List<Event?>?>
        get() = _eventsByService

    private val _servicesByEvent = MutableStateFlow<List<ServiceCategory?>?>(null)
    val servicesByEvent: StateFlow<List<ServiceCategory?>?>
        get() = _servicesByEvent

    private val _eventCreated = MutableStateFlow<CreateEventResponse?>(null)
    val eventCreated: StateFlow<CreateEventResponse?>
        get() = _eventCreated


    fun getEventId(screenInfo: ScreenInfo) =
        if (screenInfo.role == Role.Provider) {
            "b26WVIm9RcEvXtqfiYDe"
        } else {
            screenInfo.event.id
        }

    fun getAllServicesCategories() {
        if (!gotServicesByEventId) {
            gotServicesByEventId = true
            viewModelScope.launch(Dispatchers.IO) {
                servicesUseCase.getServicesCategoriesList().collect { list ->
                    _servicesByEvent.value = list
                }
            }
        }
    }

    fun getEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            eventUseCase.getEventTypesList().collect { list ->
                _eventsByService.value = list
            }
        }
    }

    fun getNewScreenInfo(
        screenInfo: ScreenInfo,
        serviceCategory: ServiceCategory,
        questions: FirstQuestionsClient?,
        clientEventId: String?
    ): ScreenInfo {
        return ScreenInfo(
            role = screenInfo.role,
            startedScreen = screenInfo.startedScreen,
            prevScreen = Screen.ServiceCategories,
            event = screenInfo.event,
            serviceCategory = serviceCategory,
            questions = questions,
            clientEventId = clientEventId
        )
    }

    fun saveAddressSelectedInShPrefs(address: Address?) {
        if (address != null) {
            if (address.city != null && address.city != "Desconocido" && address.city != "n/a") {
                sharedPrefsUseCase.saveUserAddress(address)
            } else {
                sharedPrefsUseCase.resetUserAddress()
            }
        } else {
            sharedPrefsUseCase.resetUserAddress()
        }
    }
}
