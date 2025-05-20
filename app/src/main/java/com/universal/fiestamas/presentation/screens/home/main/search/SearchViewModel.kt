package com.universal.fiestamas.presentation.screens.home.main.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.Location
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.domain.models.ServiceType
import com.universal.fiestamas.domain.usecases.ServiceUseCase
import com.universal.fiestamas.domain.usecases.SharedPrefsUseCase
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.calculateDistanceBetweenPoints
import com.universal.fiestamas.presentation.utils.extensions.toUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val serviceUseCase: ServiceUseCase,
    private val sharedPrefsUseCase: SharedPrefsUseCase
): ViewModel() {

    private val _servicesList = MutableStateFlow<List<Service>>(emptyList())
    val servicesList: StateFlow<List<Service>>
        get() = _servicesList

    private val _filteredServicesList = MutableStateFlow<List<Service>?>(null)
    val filteredServicesList: StateFlow<List<Service>?>
        get() = _filteredServicesList

    private val _servicesByEvent = MutableStateFlow<List<ServiceCategory?>>(emptyList())
    val servicesByEvent: StateFlow<List<ServiceCategory?>>
        get() = _servicesByEvent

    private val _servicesByCategory = MutableStateFlow<List<ServiceType?>>(emptyList())
    val servicesByCategory: StateFlow<List<ServiceType?>>
        get() = _servicesByCategory

    fun getAllServices() {
        viewModelScope.launch(Dispatchers.IO) {
            val allServices = serviceUseCase.getAllServices().first().filter { service ->
                service.name != "FIESTAMAS"
            }
            _servicesList.value = allServices
            _filteredServicesList.value = _servicesList.value
        }
    }

    fun findServiceByQuery(query: String) {
        if (query.isBlank()) {
            _filteredServicesList.value = _servicesList.value
        } else {
            _filteredServicesList.value = servicesList.value.filter { service ->
                service.name != "FIESTAMAS" && service.name.contains(query, ignoreCase = true)
            }
        }
    }

    fun filterServicesByServiceCategory(serviceCategory: ServiceCategory) {
        if (serviceCategory.id == "0000") { //All
            _filteredServicesList.value = servicesList.value
        } else {
            _filteredServicesList.value = servicesList.value.filter { service ->
                service.id_service_category == serviceCategory.id
            }
        }
    }

    fun filterServicesByParameters(
        category: ServiceCategory?,
        type: ServiceType?,
        distance: Pair<String, Int>,
        userLocation: Location?,
        unity: String,
        minCapacity: String,
        maxCapacity: String
    ) {
        val userLatitude = userLocation?.lat?.toDouble() ?: 0.0
        val userLongitude = userLocation?.lng?.toDouble() ?: 0.0
        val distanceSelected = distance.second

        _filteredServicesList.value = servicesList.value.filter { service ->
            val matchesCategory = if (category?.id != "0000") {
                service.id_service_category == category?.id && service.name != "FIESTAMAS"
            } else true

            /*
            val matchesCategoryAndType = if (category?.id != "0000") {
                (service.id_service_category == category?.id && (type?.id == "0000" || service.id_service_type == type?.id)) &&
                        service.name != "FIESTAMAS"
            } else true
            */

            val matchesDistance = if (userLocation != null) { run {
                val serviceLatitude = service.lat?.toDouble() ?: 0.0
                val serviceLongitude = service.lng?.toDouble() ?: 0.0
                val distanceBetweenUserAndService = calculateDistanceBetweenPoints(
                    lat1 = userLatitude,
                    lng1 = userLongitude,
                    lat2 = serviceLatitude,
                    lng2 = serviceLongitude
                )
                distanceBetweenUserAndService <= distanceSelected
            } } else true

            val matchesUnity = if (unity.isNotEmpty() && unity != "Cualquiera") {
                service.unit == unity.toUnit()
            } else true

            val matchesMinCapacity = if (minCapacity.isNotEmpty()) {
                service.min_attendees >= minCapacity.toInt()
            } else true

            val matchesMaxCapacity = if (maxCapacity.isNotEmpty()) {
                service.max_attendees <= maxCapacity.toInt()
            } else true

            matchesCategory &&
                    matchesDistance &&
                    matchesUnity &&
                    matchesMinCapacity &&
                    matchesMaxCapacity
        }
    }

    fun getAllServicesCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            serviceUseCase.getServicesCategoriesList().collect { list ->
                val mList: MutableList<ServiceCategory> = mutableListOf()
                mList.clear()
                mList.addAll(list)
                mList.add(0, ServiceCategory("0000", "Seleccionar"))
                _servicesByEvent.value = mList
            }
        }
    }

    fun getServicesByCategoryId(serviceCategoryId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            serviceUseCase.getServicesTypesByServiceCategoryId(serviceCategoryId).collect { services ->
                val mList: MutableList<ServiceType> = mutableListOf()
                mList.clear()
                mList.addAll(services)
                mList.add(0, ServiceType("0000", "Seleccionar"))
                _servicesByCategory.value = mList
            }
        }
    }

    fun getUserAddressIfExists(): Address? = sharedPrefsUseCase.getUserAddress()
}
