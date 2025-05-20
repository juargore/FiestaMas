package com.universal.fiestamas.presentation.screens.home.services.types.sub_service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universal.fiestamas.domain.models.Screen
import com.universal.fiestamas.domain.models.ScreenInfo
import com.universal.fiestamas.domain.models.SubService
import com.universal.fiestamas.domain.usecases.ServiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubServicesViewModel @Inject constructor(
    private val serviceUseCase: ServiceUseCase
): ViewModel() {

    private var gotSubServicesByServiceTypeId = false

    private val _subServices = MutableStateFlow<List<SubService?>?>(null)
    val subServices: StateFlow<List<SubService?>?>
        get() = _subServices

    fun getSubServicesByServiceTypeId(serviceTypeId: String) {
        if (!gotSubServicesByServiceTypeId) {
            gotSubServicesByServiceTypeId = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.getSubServicesByServiceTypeId(serviceTypeId).collect { subServices ->
                    _subServices.value = subServices
                }
            }
        }
    }

    fun getLinkedStrings(screenInfo: ScreenInfo): List<String> {
        return listOf(
            screenInfo.serviceCategory?.name.orEmpty(),
            screenInfo.serviceType?.name.orEmpty()
        )
    }

    fun getNewScreenInfo(screenInfo: ScreenInfo, subService: SubService?): ScreenInfo {
        return ScreenInfo(
            role = screenInfo.role,
            startedScreen = screenInfo.startedScreen,
            prevScreen = Screen.SubServices,
            event = screenInfo.event,
            questions = screenInfo.questions,
            serviceCategory = screenInfo.serviceCategory,
            clientEventId = screenInfo.clientEventId,
            serviceType = screenInfo.serviceType,
            subService = subService
        )
    }
}
