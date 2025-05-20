package com.universal.fiestamas.domain.models.request

data class CreateEventRequest(
    val id: String? = null,
    val name: String,
    val id_client: String,
    val id_event_type: String,
    val date: String,
    val location: String,
    val lat: String,
    val lng: String,
    val attendees: String
)

data class CreateEventResponse(
    val id: String?,
    val name: String?
)

data class ContactResponse(
    //val id_service_event: String
    val id: String
)

data class AddServiceToExistingEventResponseV2(
    val status: Int,
    val data: ContactResponse?
)

data class CreateEventProviderRequest(
    val id: String? = null,
    val name: String,
    val id_client: String,
    val id_event_type: String,
    val date: String,
    val location: String,
    val lat: String,
    val lng: String,
    val contact_email: String,
    val contact_phone: String,
    val contact_name: String,
    val attendees: String? = ""
)

data class CreateEventResponseV2(
    val status: Int,
    val data: CreateEventResponse?
)
