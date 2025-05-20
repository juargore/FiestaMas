package com.universal.fiestamas.presentation.screens.home.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universal.fiestamas.domain.models.Event
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.domain.usecases.EventUseCase
import com.universal.fiestamas.domain.usecases.ServiceUseCase
import com.universal.fiestamas.domain.usecases.SharedPrefsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainHomeViewModel @Inject constructor(
    private val eventUseCase: EventUseCase,
    private val serviceUseCase: ServiceUseCase,
    private val sharedPrefsUseCase: SharedPrefsUseCase
) : ViewModel() {

    private var gotEventTypeList = false
    private var gotServiceCategories = false

    private val _eventTypesList = MutableStateFlow<List<Event?>?>(null)
    val eventList: StateFlow<List<Event?>?>
        get() = _eventTypesList

    private val _serviceCategoryList = MutableStateFlow<List<ServiceCategory?>?>(null)
    val serviceCategoryList: StateFlow<List<ServiceCategory?>?>
        get() = _serviceCategoryList

    fun getSharedServiceIdIfExists(): String {
        return sharedPrefsUseCase.getServiceIdShared()
    }

    fun resetSharedServiceId() = sharedPrefsUseCase.setServiceIdShared("")

    fun shouldRedirectToMyParty(): Boolean {
        resetVideoFromCameraInSharedPreferences()
        return sharedPrefsUseCase.getFirstTimeAppRunning()
    }

    private fun resetVideoFromCameraInSharedPreferences() {
        sharedPrefsUseCase.resetVideoFromCamera()
    }

    fun getEventTypeList() {
        if (!gotEventTypeList) {
            gotEventTypeList = true
            viewModelScope.launch(Dispatchers.IO) {
                eventUseCase.getEventTypesList().collectLatest { list ->
                    _eventTypesList.value = list
                }
            }
        }
    }

    fun getServiceCategories() {
        if (!gotServiceCategories) {
            gotServiceCategories = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.getServicesCategoriesList().collectLatest { list ->
                    _serviceCategoryList.value = list.sortedBy { it.name }
                }
            }
        }
    }
}
