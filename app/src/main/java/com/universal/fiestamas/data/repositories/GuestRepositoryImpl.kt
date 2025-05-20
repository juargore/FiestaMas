package com.universal.fiestamas.data.repositories

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.universal.fiestamas.data.apis.GuestApi
import com.universal.fiestamas.data.extensions.collectionListenerFlow
import com.universal.fiestamas.data.extensions.documentListenerFlow
import com.universal.fiestamas.data.module.Constants
import com.universal.fiestamas.data.module.Constants.CLIENT_EVENTS
import com.universal.fiestamas.data.module.Constants.GUESTS
import com.universal.fiestamas.domain.models.Guest
import com.universal.fiestamas.domain.models.Tag
import com.universal.fiestamas.domain.models.request.AddNewGuestRequest
import com.universal.fiestamas.domain.models.request.TagsOnGuestsRequest
import com.universal.fiestamas.domain.models.request.EditGuestRequest
import com.universal.fiestamas.domain.models.request.ListOfGuestsRequest
import com.universal.fiestamas.domain.usecases.IGuestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GuestRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val guestApi: GuestApi
) : IGuestRepository {

    override fun getGuestsList(idClientEvent: String): Flow<List<Guest>> {
        val guestsCollection = firestore.collection(GUESTS)
        val query = guestsCollection
            .whereEqualTo(Constants.ID_CLIENT_EVENT, idClientEvent)
        return guestsCollection.collectionListenerFlow(Guest::class.java, query)
    }

    override fun getGuest(guestId: String): Flow<Guest?> {
        val eventRef: DocumentReference = firestore.collection(GUESTS).document(guestId)
        return eventRef.documentListenerFlow(Guest::class.java)
    }

    override fun addNewGuest(body: AddNewGuestRequest) {
        guestApi.addNewGuest(body).execute()
    }

    override fun editGuest(guestId: String, body: EditGuestRequest) {
        guestApi.editGuest(guestId, body).execute()
    }

    override fun deleteGuest(guestId: String) {
        guestApi.deleteGuest(guestId).execute()
    }

    override fun sendInvitationToGuests(idClientEvent: String, body: ListOfGuestsRequest) {
        guestApi.sendInvitationToGuests(body).execute()
    }

    override fun getAllTagsByEvent(idClientEvent: String): Flow<List<Tag>> {
        val eventRef: DocumentReference = firestore.collection(CLIENT_EVENTS).document(idClientEvent)
        return eventRef.documentListenerFlow(Guest::class.java).map {
            it?.tags.orEmpty()
        }
    }

    override fun createTagForEvent(idClientEvent: String, tag: Tag) {
        guestApi.addNewTagOnServer(idClientEvent, tag).execute()
    }

    override fun deleteTagForEvent(idClientEvent: String, tag: Tag) {
        guestApi.deleteTagOnServer(idClientEvent, tag).execute()
    }

    override fun addTagToGuest(body: TagsOnGuestsRequest) {
        guestApi.addTagToGuest(body).execute()
    }

    override fun deleteTagToManyGuests(body: TagsOnGuestsRequest) {
        guestApi.deleteTagToManyGuests(body).execute()
    }

    override fun updateGuestStatus(body: ListOfGuestsRequest, status: String) {
        guestApi.updateGuestStatus(body, status).execute()
    }
}
