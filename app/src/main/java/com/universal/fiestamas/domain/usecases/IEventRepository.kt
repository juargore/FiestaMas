package com.universal.fiestamas.domain.usecases

import com.universal.fiestamas.domain.common.BaseResult
import com.universal.fiestamas.domain.common.ErrorResponse
import com.universal.fiestamas.domain.models.Event
import com.universal.fiestamas.domain.models.FirstQuestionsClient
import com.universal.fiestamas.domain.models.FirstQuestionsProvider
import com.universal.fiestamas.domain.models.MyPartyEvent
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.request.CreateEventResponseV2
import com.universal.fiestamas.domain.models.response.StatusResponseV2
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details.DetailsServiceViewModel.LogServiceType
import kotlinx.coroutines.flow.Flow

interface IEventRepository {

    fun getEventTypesList(): Flow<List<Event>>

    fun getEventTypeById(id: String): Flow<Event?>

    fun createEventByClient(clientId: String, eventId: String, questions: FirstQuestionsClient): Flow<BaseResult<CreateEventResponseV2, ErrorResponse>>

    fun createEventByProvider(providerId: String, eventId: String, questions: FirstQuestionsProvider): Flow<CreateEventResponseV2?>

    fun getClientEventsByClientId(clientId: String): Flow<List<MyPartyEvent>>

    fun getClientEventById(id: String): Flow<MyPartyEvent?>

    fun getServicesEventByClientEventId(id: String): Flow<List<MyPartyService>>

    fun getServicesEventByClientEventIdInThread(clientEventId: String): Flow<List<MyPartyService>>

    fun getServicesEventsByClient(clientId: String): Flow<List<MyPartyService>>

    fun getServicesEventByProviderId(id: String): Flow<List<MyPartyService>>

    fun getServicesEventByService(serviceId: String): Flow<List<MyPartyService>>

    fun getMyPartyService(serviceEventId: String): Flow<MyPartyService?>

    fun addServiceToEvent(eventId: String, serviceId: String): Flow<String>

    fun logService(idService: String, logType: LogServiceType): Flow<StatusResponseV2?>
}
