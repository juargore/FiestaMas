package com.universal.fiestamas.presentation.screens.home.services.types

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universal.fiestamas.domain.models.Screen
import com.universal.fiestamas.domain.models.ScreenInfo
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.domain.models.ServiceType
import com.universal.fiestamas.domain.usecases.ServiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServicesTypesViewModel @Inject constructor(
    private val serviceUseCase: ServiceUseCase
) : ViewModel() {

    private var gotServicesByCategoryId = false

    private val _showProgressDialog = MutableStateFlow(false)
    val showProgressDialog: StateFlow<Boolean>
        get() = _showProgressDialog

    private val _serviceCategory = MutableStateFlow<ServiceCategory?>(null)
    val serviceCategory: StateFlow<ServiceCategory?>
        get() = _serviceCategory

    private val _servicesByCategory = MutableStateFlow<List<ServiceType?>?>(null)
    val servicesByCategory: StateFlow<List<ServiceType?>?>
        get() = _servicesByCategory

    fun getServicesByCategoryId(serviceCategoryId: String) {
        if (!gotServicesByCategoryId) {
            gotServicesByCategoryId = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.getServicesTypesByServiceCategoryId(serviceCategoryId).collect { services ->
                    _servicesByCategory.value = services
                }
            }
        }
    }

    fun getNewScreenInfo(screenInfo: ScreenInfo, serviceType: ServiceType?): ScreenInfo {
        return ScreenInfo(
            role = screenInfo.role,
            startedScreen = screenInfo.startedScreen,
            prevScreen = Screen.ServiceTypes,
            event = screenInfo.event,
            questions = screenInfo.questions,
            serviceCategory = screenInfo.serviceCategory,
            clientEventId = screenInfo.clientEventId,
            serviceType = serviceType
        )
    }
}
