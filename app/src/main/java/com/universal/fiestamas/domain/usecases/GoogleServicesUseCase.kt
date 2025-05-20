package com.universal.fiestamas.domain.usecases

import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class GoogleServicesUseCase @Inject constructor(
    private val googleRepository: IGoogleServicesRepository
) {
    fun findPlaceAutocomplete(query: String) = googleRepository.autocompleteFlow(query)

    fun findCityAutocomplete(query: String) = googleRepository.cityAutocompleteFlow(query)

    fun getAddressByPlaceId(placeId: String) = googleRepository.getAddressByPlaceId(placeId)

    fun requestLocationUpdates() = googleRepository.requestLocationUpdates()

    fun requestCurrentLocation() = googleRepository.requestCurrentLocation()

    fun getAddressByCoordinates(location: LatLng) = googleRepository.getAddressByCoordinates(location)
}
