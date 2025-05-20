package com.universal.fiestamas.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class PlaceAutocomplete(
    val placeId: String,
    val primaryText: String,
    val secondaryText: String,
    val fullAddress: String
)

@Parcelize
data class Address(
    var city: String? = null,
    var country: String? = null,
    var line1: String? = null,
    var location: Location? = null,
    var state: String? = null,
    var zipcode: String? = null
) : Parcelable

@Parcelize
data class Location(
    var lat: String,
    var lng: String
): Parcelable
