package com.universal.fiestamas.domain.models.request

data class AddServiceProviderRequest(
    val id_service_category: String,
    val id_service_type: String,
    val id_sub_service_type: String,
    val id_provider: String,
    val provider_name: String,
    val name: String,
    val description: String,
    val icon: String,
    val image: String,
    val rating: Int,
    val min_attendees: Int,
    val max_attendees: Int,
    val price: Int,
    val attributes: List<String>,
    val images: List<String>,
    val videos: List<String>,
    val unit: String,
    val distance: Int,
    val address: String,
    val lat: String,
    val lng: String,
    val requested_attribute: String? = null
)

data class UpdateServiceProviderRequest(
    val name: String,
    val description: String,
    val min_attendees: Int,
    val max_attendees: Int,
    val price: Int,
    val attributes: List<String>,
    val images: List<String>,
    val videos: List<String>,
    val unit: String,
    val distance: Int,
    val address: String,
    val lat: String,
    val lng: String,
    val requested_attribute: String
)
