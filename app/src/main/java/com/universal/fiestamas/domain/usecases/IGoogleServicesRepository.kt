package com.universal.fiestamas.domain.usecases

import com.google.android.gms.maps.model.LatLng
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.PlaceAutocomplete
import kotlinx.coroutines.flow.Flow

interface IGoogleServicesRepository {

    fun autocompleteFlow(query: String): Flow<List<PlaceAutocomplete>>

    fun cityAutocompleteFlow(query: String): Flow<List<PlaceAutocomplete>>

    fun getAddressByPlaceId(placeId: String): Flow<Address>

    fun requestLocationUpdates(): Flow<LatLng?>

    fun requestCurrentLocation(): Flow<LatLng?>

    fun getAddressByCoordinates(location: LatLng): Flow<Address?>
}
