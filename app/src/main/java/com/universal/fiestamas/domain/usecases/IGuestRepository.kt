package com.universal.fiestamas.domain.usecases

import com.universal.fiestamas.domain.models.Guest
import com.universal.fiestamas.domain.models.Tag
import com.universal.fiestamas.domain.models.request.AddNewGuestRequest
import com.universal.fiestamas.domain.models.request.TagsOnGuestsRequest
import com.universal.fiestamas.domain.models.request.EditGuestRequest
import com.universal.fiestamas.domain.models.request.ListOfGuestsRequest
import kotlinx.coroutines.flow.Flow

interface IGuestRepository {

    fun getGuestsList(idClientEvent: String): Flow<List<Guest>>

    fun getGuest(guestId: String): Flow<Guest?>

    fun addNewGuest(body: AddNewGuestRequest)

    fun editGuest(guestId: String, body: EditGuestRequest)

    fun deleteGuest(guestId: String)

    fun sendInvitationToGuests(idClientEvent: String, body: ListOfGuestsRequest)

    fun getAllTagsByEvent(idClientEvent: String): Flow<List<Tag>>

    fun createTagForEvent(idClientEvent: String, tag: Tag)

    fun deleteTagForEvent(idClientEvent: String, tag: Tag)

    fun addTagToGuest(body: TagsOnGuestsRequest)

    fun deleteTagToManyGuests(body: TagsOnGuestsRequest)

    fun updateGuestStatus(body: ListOfGuestsRequest, status: String)
}
