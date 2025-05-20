package com.universal.fiestamas.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceProviderData (
    val name: String,
    val addressData: AddressData?,
    val description: String,
    val minCapacity: String,
    val maxCapacity: String,
    val cost: String,
    val unit: String,
    val distance: Int,
    val attributes: List<String>,
    val suggestedAttribute: String
): Parcelable

@Parcelize
data class AddressData(
    var address: String,
    var city: String,
    var state: String,
    var postalCode: String,
    var country: String,
    var latitude: String,
    var longitude: String
): Parcelable {
    constructor(): this(
        address = "",
        city = "",
        state = "",
        postalCode = "",
        country = "",
        latitude = "",
        longitude = ""
    )
}
