package com.universal.fiestamas.data.apis

import com.universal.fiestamas.domain.models.Tag
import com.universal.fiestamas.domain.models.request.AddNewGuestRequest
import com.universal.fiestamas.domain.models.request.TagsOnGuestsRequest
import com.universal.fiestamas.domain.models.request.EditGuestRequest
import com.universal.fiestamas.domain.models.request.ListOfGuestsRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GuestApi {

    @POST("guest")
    fun addNewGuest(@Body request: AddNewGuestRequest): Call<Void>

    @PUT("guest/{guest_id}")
    fun editGuest(
        @Path("guest_id") guestId: String,
        @Body request: EditGuestRequest
    ): Call<Void>

    @DELETE("guest/{guest_id}")
    fun deleteGuest(@Path("guest_id") guestId: String): Call<Void>

    @POST("guest/invitation")
    fun sendInvitationToGuests(
        @Body request: ListOfGuestsRequest
    ): Call<Void>

    @POST("client_event/tag/{id_client_event}")
    fun addNewTagOnServer(
        @Path("id_client_event") idClientEvent: String,
        @Body request: Tag
    ): Call<Void>

    @HTTP(method = "DELETE", path = "client_event/tag/{id_client_event}", hasBody = true)
    fun deleteTagOnServer(
        @Path("id_client_event") idClientEvent: String,
        @Body request: Tag
    ): Call<Void>

    @POST("guest/tag")
    fun addTagToGuest(@Body request: TagsOnGuestsRequest): Call<Void>

    @HTTP(method = "DELETE", path = "guest/tag", hasBody = true)
    fun deleteTagToManyGuests(@Body request: TagsOnGuestsRequest): Call<Void>

    @PUT("guest/status/")
    fun updateGuestStatus(
        @Body request: ListOfGuestsRequest,
        @Query("status") status: String
    ): Call<Void>
}
