@file:Suppress("DEPRECATION")

package com.universal.fiestamas.data.repositories

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.location.Geocoder
import android.os.Looper
import android.text.style.StyleSpan
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.Location
import com.universal.fiestamas.domain.models.PlaceAutocomplete
import com.universal.fiestamas.domain.usecases.IGoogleServicesRepository
import com.universal.fiestamas.presentation.utils.awaitTask
import com.universal.fiestamas.presentation.utils.extensions.hasLocationPermission
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.util.Locale


class GoogleServicesRepositoryImpl(
    private val placesClient: PlacesClient,
    private val context: Context,
    private val locationClient: FusedLocationProviderClient
): IGoogleServicesRepository {

    private val styleNormal = StyleSpan(Typeface.NORMAL)
    private val placeFields = listOf(
        Place.Field.ADDRESS,
        Place.Field.ADDRESS_COMPONENTS,
        Place.Field.LAT_LNG
    )

    override fun autocompleteFlow(query: String): Flow<List<PlaceAutocomplete>> = flow {
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest
            .builder()
            .setTypeFilter(TypeFilter.ADDRESS)
            .setCountry("MX")
            .setSessionToken(token)
            .setQuery(query)
            .build()

        try {
            val findAutocompletePredictionsResponse = placesClient.findAutocompletePredictions(request).awaitTask()
            val resultList = ArrayList<PlaceAutocomplete>()
            for (prediction in findAutocompletePredictionsResponse.autocompletePredictions) {
                val types = prediction.placeTypes
                if (types.contains(Place.Type.STREET_ADDRESS) || types.contains(Place.Type.ROUTE)) {
                    resultList.add(
                        PlaceAutocomplete(
                            placeId = prediction.placeId,
                            primaryText = prediction.getPrimaryText(styleNormal).toString(),
                            secondaryText = prediction.getSecondaryText(styleNormal).toString(),
                            fullAddress = prediction.getFullText(styleNormal).toString()
                        )
                    )
                }
            }
            emit(resultList)
        } catch (e: Exception) {
            emit(emptyList())
            println("Error: ${e.message}")
        }
    }

    override fun cityAutocompleteFlow(query: String): Flow<List<PlaceAutocomplete>> = flow {
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest
            .builder()
            .setTypeFilter(TypeFilter.CITIES)
            .setCountry("MX")
            .setSessionToken(token)
            .setQuery(query)
            .build()

        try {
            val findAutocompletePredictionsResponse = placesClient.findAutocompletePredictions(request).awaitTask()
            val resultList = ArrayList<PlaceAutocomplete>()
            val limit = 3
            for (i in 0 until minOf(limit, findAutocompletePredictionsResponse.autocompletePredictions.size)) {
                val prediction = findAutocompletePredictionsResponse.autocompletePredictions[i]
                resultList.add(
                    PlaceAutocomplete(
                        placeId = prediction.placeId,
                        primaryText = prediction.getPrimaryText(styleNormal).toString(),
                        secondaryText = prediction.getSecondaryText(styleNormal).toString(),
                        fullAddress = prediction.getFullText(styleNormal).toString()
                    )
                )
            }
            emit(resultList)
        } catch (e: Exception) {
            emit(emptyList())
            println("Error: ${e.message}")
        }
    }

    override fun getAddressByPlaceId(placeId: String): Flow<Address> = flow {
        val request = FetchPlaceRequest
            .builder(placeId, placeFields)
            .build()

        try {
            val fetchPlaceResponse = placesClient.fetchPlace(request).awaitTask()
            val place = fetchPlaceResponse.place
            val address = convertPlaceToAddress(place)
            val res = place.latLng
            val location = Location(lat = res?.latitude.toString(), lng = res?.longitude.toString())
            address.location = location
            emit(address)
        } catch (e: Exception) {
            emit(Address())
            println("Error: ${e.message}")
        }
    }

    private fun convertPlaceToAddress(place: Place): Address {
        var streetNumber = ""
        var streetAddress = ""
        var zipCode = ""
        var city = ""
        var country = ""
        var state = ""
        var neighborhood = ""
        for (component in place.addressComponents!!.asList()) {
            when {
                component.types.contains("country") -> country = component.name
                component.types.contains("street_number") -> streetNumber = component.name
                component.types.contains("route") -> streetAddress = component.name
                component.types.contains("locality") -> city = component.name
                component.types.contains("administrative_area_level_1") -> state = component.name
                component.types.contains("postal_code") -> zipCode = component.name
                component.types.contains("sublocality") -> neighborhood = component.name
            }
        }
        if (neighborhood.isBlank()) {
            for (component in place.addressComponents!!.asList()) {
                if (component.types.contains("neighborhood")) {
                    neighborhood = component.name
                    break
                }
            }
        }

        return Address(
            line1 = "$streetAddress $streetNumber, $neighborhood, $zipCode",
            zipcode = zipCode,
            city = city,
            state = state,
            country = country
        )
    }

    @SuppressLint("MissingPermission")
    override fun requestLocationUpdates(): Flow<LatLng?> = callbackFlow {
        if (!context.hasLocationPermission()) {
            trySend(null)
            return@callbackFlow
        }

        val request = LocationRequest.Builder(10000L)
            .setIntervalMillis(10000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.lastOrNull()?.let {
                    trySend(LatLng(it.latitude, it.longitude))
                }
            }
        }

        locationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )

        awaitClose {
            locationClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun requestCurrentLocation(): Flow<LatLng?> = flow {

    }

    override fun getAddressByCoordinates(location: LatLng): Flow<Address?> = flow {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

            if (addresses!!.size > 0) {
                val address = addresses[0]
                var city = address.locality
                if (city.isNullOrEmpty()) { city = "n/a" }
                var country = address.countryName
                if (country.isNullOrEmpty()) { country = "n/a" }
                var state = address.adminArea
                if (state.isNullOrEmpty()) { state = "n/a" }
                var zipcode = address.postalCode
                if (zipcode.isNullOrEmpty()) { zipcode = "n/a" }
                var street = address.thoroughfare
                if (street.isNullOrEmpty()) { street = "n/a" }
                var number = address.featureName
                if (number.isNullOrEmpty()) { number = "n/a" }
                val line1 = "$number $street"
                val nLocation = Location(
                    location.latitude.toString(),
                    location.longitude.toString()
                )
                //val suburb = address.subLocality // colonia
                val response = Address(
                    city = city,
                    country = country,
                    line1 = line1,
                    location = nLocation,
                    state = state,
                    zipcode = zipcode
                )
                emit(response)
            } else {
                emit(null)
            }
        } catch (e: IOException) {
            emit(null)
            println("Error: ${e.message}")
        }
    }
}
