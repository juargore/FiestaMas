package com.universal.fiestamas.presentation.screens.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.PlaceAutocomplete
import com.universal.fiestamas.domain.usecases.GoogleServicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressAutoCompleteViewModel @Inject constructor(
    private val googleUseCase: GoogleServicesUseCase
): ViewModel() {

    private val _placesList = MutableStateFlow<List<PlaceAutocomplete>>(emptyList())
    val placesList: StateFlow<List<PlaceAutocomplete>>
        get() = _placesList

    private val _address = MutableStateFlow<Address?>(null)
    val address: StateFlow<Address?>
        get() = _address

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Loading)
    val viewState = _viewState.asStateFlow()

    fun findCityByQuery(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            googleUseCase.findCityAutocomplete(query).collectLatest { list ->
                _placesList.value = list
            }
        }
    }

    fun findPlaceByQuery(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            googleUseCase.findPlaceAutocomplete(query).collectLatest { list ->
                _placesList.value = list
            }
        }
    }

    fun getAddressByPlaceId(placeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            googleUseCase.getAddressByPlaceId(placeId).collectLatest { res: Address ->
                _address.value = res
            }
        }
    }

    fun resetAddress() {
        _placesList.value = emptyList()
        _address.value = null
    }

    /* This function is responsible for updating the ViewState based
       on the event coming from the view */
    fun handle(event: PermissionEvent) {
        when (event) {
            PermissionEvent.Granted -> {
                viewModelScope.launch {
                    googleUseCase.requestLocationUpdates().collect { location ->
                        _viewState.value = ViewState.Success(location)
                    }
                }
            }
            PermissionEvent.Revoked -> {
                _viewState.value = ViewState.RevokedPermissions
            }
        }
    }

    fun getAddressByCoordinates(location: LatLng, onFinished: (Address?) -> Unit) {
        viewModelScope.launch {
            googleUseCase.getAddressByCoordinates(location).collectLatest {
                onFinished(it)
            }
        }
    }
}

sealed interface ViewState {
    object Loading : ViewState
    data class Success(val location: LatLng?) : ViewState
    object RevokedPermissions : ViewState
}

sealed interface PermissionEvent {
    object Granted : PermissionEvent
    object Revoked : PermissionEvent
}
