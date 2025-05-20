package com.universal.fiestamas.domain.models.request

data class AddNewGuestRequest(
    val id_client_event: String,
    val name: String,
    val email: String,
    val phone: String
)

data class EditGuestRequest(
    val name: String,
    val num_table: Int?,
    val email: String,
    val phone: String,
    val status: String
)

data class ListOfGuestsRequest(
    val guests: List<String>
)

data class TagsOnGuestsRequest(
    val guestIds: List<String>,
    val tagName: String,
    val id_client_event: String
)
