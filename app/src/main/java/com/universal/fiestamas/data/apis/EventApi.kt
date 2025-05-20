@file:Suppress("LocalVariableName")

package com.universal.fiestamas.data.apis

import com.universal.fiestamas.domain.models.ResponseEventTypeV2
import com.universal.fiestamas.domain.models.ResponseEventsTypesV2
import com.universal.fiestamas.domain.models.ResponseMyPartyServicesV2
import com.universal.fiestamas.domain.models.request.AddServiceToExistingEventResponseV2
import com.universal.fiestamas.domain.models.request.ContactResponse
import com.universal.fiestamas.domain.models.request.CreateEventProviderRequest
import com.universal.fiestamas.domain.models.request.CreateEventRequest
import com.universal.fiestamas.domain.models.request.CreateEventResponse
import com.universal.fiestamas.domain.models.request.CreateEventResponseV2
import com.universal.fiestamas.domain.models.request.EntityDataRequest
import com.universal.fiestamas.domain.models.request.FilterRequest
import com.universal.fiestamas.domain.models.response.StatusResponseV2
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EventApi {

    @POST("client_event")
    suspend fun createNewEvent(
        @Header("Authorization") authorizationHeader: String?,
        @Body eventRequest: CreateEventRequest
    ): Response<CreateEventResponse>

    @POST("client_event")
    suspend fun createNewProviderEvent(
        @Header("Authorization") authorizationHeader: String?,
        @Body eventRequest: CreateEventProviderRequest
    ): Response<CreateEventResponse>

    @POST("service_event/{id_event}/")
    suspend fun addServiceToExistingEvent(
        @Header("Authorization") authorizationHeader: String?,
        @Path("id_event") eventId: String,
        @Query("id_service") serviceId: String
    ): Response<ContactResponse>

    @POST("reports/log-view-service/{id_service}")
    fun logViewService(
        @Path("id_service") serviceId: String,
        @Query("uid") userId: String
    ): Call<Void>

    @POST("reports/log-contact-service/{id_service}")
    fun logContactService(
        @Path("id_service") serviceId: String,
        @Query("uid") userId: String
    ): Call<Void>

    @POST("reports/log-click-contact-service/{id_service}")
    fun logClickContactService(
        @Path("id_service") serviceId: String,
        @Query("uid") userId: String
    ): Call<Void>


    // ======= NEW METHODS FOR V2 API ========= //

    @GET("EventTypes/{event_id}")
    suspend fun getEventTypeByIdV2(@Path("event_id") event_id: String): Response<ResponseEventTypeV2>

    @GET("EventTypes")
    suspend fun getAllEventsTypesV2(): Response<ResponseEventsTypesV2>

    @POST("ClientEvents")
    suspend fun createNewEventV2(
        @Body eventRequest: EntityDataRequest
    ): Response<CreateEventResponseV2>

    @POST("ClientEvents")
    suspend fun createNewProviderEventV2(
        @Body eventRequest: EntityDataRequest
    ): Response<CreateEventResponseV2>

    @POST("ServiceEvents/{id_service}/{id_event}")
    suspend fun addServiceToExistingEventV2(
        @Path("id_event") eventId: String,
        @Path("id_service") serviceId: String
    ): Response<AddServiceToExistingEventResponseV2>

    @POST("Services/stat/{id_client}/{id_service}/{service_stat}")
    suspend fun logServiceV2(
        @Path("id_client") id_client: String,
        @Path("id_service") id_service: String,
        @Path("service_stat") service_stat: String
    ): Response<StatusResponseV2>

    @POST("serviceEvents/GetAllWithFilters")
    suspend fun getServicesEventByClientEventIdV2(
        @Body request: FilterRequest
    ): Response<ResponseMyPartyServicesV2>
}
