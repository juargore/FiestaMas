package com.universal.fiestamas.domain.usecases

import com.universal.fiestamas.domain.models.Guest
import com.universal.fiestamas.domain.models.Tag
import com.universal.fiestamas.domain.models.request.AddNewGuestRequest
import com.universal.fiestamas.domain.models.request.TagsOnGuestsRequest
import com.universal.fiestamas.domain.models.request.EditGuestRequest
import com.universal.fiestamas.domain.models.request.ListOfGuestsRequest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GuestUseCase @Inject constructor(
    private val guestRepository: IGuestRepository
) {

    @Suppress("unused")
    private val guests = listOf(
        Guest(
            id = "01",
            created_at = null,
            email = "test1@gmail.com",
            id_client_event = "",
            name = "Luis Pablo Rosales",
            num_table = 1,
            phone = "12345",
            status = "SENT",
            tags = listOf(Tag("MyTag1"), Tag("MyTag2"), Tag("MyTag3"), Tag("MyTag4")),
            viewed = null
        ),
        Guest(
            id = "02",
            created_at = null,
            email = "test2@gmail.com",
            id_client_event = "",
            name = "Ana María Cuevas",
            num_table = 12,
            phone = "67890",
            status = "ACCEPTED",
            tags = listOf(),
            viewed = null
        ),
        Guest(
            id = "03",
            created_at = null,
            email = "test3@gmail.com",
            id_client_event = "",
            name = "Luisa Herández Díaz",
            num_table = 0,
            phone = "246801",
            status = "CHECKED_IN",
            tags = listOf(Tag("MyTagAA con nombre largo a ver que show aqui")),
            viewed = null
        ),
        Guest(
            id = "04",
            created_at = null,
            email = "test3@gmail.com",
            id_client_event = "",
            name = "José Rojas Luna",
            num_table = null,
            phone = "246801",
            status = "PENDING",
            tags = listOf(),
            viewed = null
        ),
        Guest(
            id = "04",
            created_at = null,
            email = "test4@gmail.com",
            id_client_event = "",
            name = "Erika Cisneros Salazar",
            num_table = null,
            phone = "246801",
            status = "SENT",
            tags = listOf(Tag("Tag test - 01")),
            viewed = null
        )
    )

    fun getGuestsList(idClientEvent: String) =
        //flow { emit(guests) }
        //flow { emit(emptyList<Guest>()) }
        //flow { emit(guests+guests+guests) }
        guestRepository.getGuestsList(idClientEvent)

    fun getGuest(guestId: String) = guestRepository.getGuest(guestId)

    fun addNewGuest(body: AddNewGuestRequest) = guestRepository.addNewGuest(body)

    fun editGuest(guestId: String, body: EditGuestRequest) = guestRepository.editGuest(guestId, body)

    fun deleteGuest(guestId: String) = guestRepository.deleteGuest(guestId)

    fun sendInvitationToGuests(
        idClientEvent: String,
        body: ListOfGuestsRequest
    ) = guestRepository.sendInvitationToGuests(idClientEvent, body)

    fun getAllTagsByEvent(idClientEvent: String) = guestRepository.getAllTagsByEvent(idClientEvent)

    fun createTagForEvent(idClientEvent: String, tag: Tag) = guestRepository.createTagForEvent(idClientEvent, tag)

    fun deleteTagForEvent(idClientEvent: String, tag: Tag) = guestRepository.deleteTagForEvent(idClientEvent, tag)

    fun addTagToGuest(body: TagsOnGuestsRequest) = guestRepository.addTagToGuest(body)

    fun deleteTagToManyGuests(body: TagsOnGuestsRequest) = guestRepository.deleteTagToManyGuests(body)

    fun updateGuestStatus(body: ListOfGuestsRequest, status: String) = guestRepository.updateGuestStatus(body, status)
}
