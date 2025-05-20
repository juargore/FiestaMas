@file:Suppress("PropertyName")
package com.universal.fiestamas.domain.models.request


data class ProviderRequest(
    val role: String,
    val name: String,
    val email: String,
    val phone_one: String,
    val phone_two: String,
    val business_name: String,
    val state: String,
    val city: String,
    val cp: String,
    val rfc: String,
    val country: String,
    val password: String,
    val last_name: String,
    val lat: String,
    val lng: String,
    val address: String,
    val photo: String? = null
)

data class GoogleProviderRequest(
    val role: String,
    val name: String,
    val phone_one: String,
    val phone_two: String,
    val business_name: String,
    val state: String,
    val city: String,
    val cp: String,
    val rfc: String,
    val country: String,
    val last_name: String,
    val lat: String,
    val lng: String,
    val address: String
)

data class ProviderRequestEdit(
    val business_name: String,
    val name: String,
    val last_name: String,
    val email: String,
    val phone_one: String,
    val phone_two: String,
    var photo: String,
    val rfc: String,
    val address: String?,
    val state: String?,
    val city: String?,
    val cp: String?,
    val country: String?,
    val lat: String?,
    val lng: String?
)
