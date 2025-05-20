package com.universal.fiestamas.domain.usecases

import com.universal.fiestamas.domain.common.BaseResult
import com.universal.fiestamas.domain.common.ErrorResponse
import com.universal.fiestamas.domain.models.Event
import com.universal.fiestamas.domain.models.FirstQuestionsClient
import com.universal.fiestamas.domain.models.FirstQuestionsProvider
import com.universal.fiestamas.domain.models.MyPartyEvent
import com.universal.fiestamas.domain.models.MyPartyEventWithServices
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.request.CreateEventResponseV2
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details.DetailsServiceViewModel.LogServiceType
import com.universal.fiestamas.presentation.utils.extensions.daysUntilDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventUseCase @Inject constructor(
    private val eventRepository: IEventRepository
) {

    fun getEventTypesList(): Flow<List<Event>> = eventRepository.getEventTypesList()
        .map { it.filterNot { event -> event.name == "Fiesta" }
        .sortedBy { event -> event.index }
    }

    @Suppress("unused")
    fun getEventTypeById(id: String): Flow<Event?> = eventRepository.getEventTypeById(id)

    fun createEventByClient(
        clientId: String,
        eventId: String,
        questions: FirstQuestionsClient
    ): Flow<BaseResult<CreateEventResponseV2, ErrorResponse>> = eventRepository.createEventByClient(clientId, eventId, questions)

    fun createEventByProvider(
        providerId: String,
        eventId: String,
        questions: FirstQuestionsProvider
    ): Flow<CreateEventResponseV2?> = eventRepository.createEventByProvider(providerId, eventId, questions)

    fun addServiceToEvent(
        eventId: String,
        serviceId: String
    ): Flow<String> = eventRepository.addServiceToEvent(eventId, serviceId)

    fun getMyPartyEventsByClientId(clientId: String): Flow<List<MyPartyEvent>> {
        return eventRepository.getClientEventsByClientId(clientId).map { list: List<MyPartyEvent> ->
            list.forEach { event ->
                val pendingDays = daysUntilDate(event.date)
                event.pendingDays = pendingDays
            }
            return@map list
        }
    }

    fun getClientEventById(id: String) = eventRepository.getClientEventById(id)

    fun getMyPartyEventsWithServices(id: String): Flow<List<MyPartyEventWithServices>> {
        return eventRepository.getClientEventsByClientId(id).map { list: List<MyPartyEvent> ->
            val mList = mutableListOf<MyPartyEventWithServices>()
            list.forEachIndexed { index, event ->
                mList.add(MyPartyEventWithServices(event = event, null))
            }

            val allServiceEvents = eventRepository.getServicesEventsByClient(id).first()

            mList.forEach { myPartyEventWithServices ->
                myPartyEventWithServices.event?.let { event ->
                    val servicesList = allServiceEvents.filter { it.id_client_event == event.id }
                    myPartyEventWithServices.servicesEvents = servicesList

                    // calculate pending days until given date
                    val pendingDays = daysUntilDate(event.date)
                    event.pendingDays = pendingDays

                    // calculate sum of costs for every service to set in event
                    var sumOfCosts = 0
                    myPartyEventWithServices.servicesEvents?.forEach {
                        sumOfCosts += it.price
                    }
                    event.finalCost = sumOfCosts
                }
            }
            return@map mList
        }
    }

    fun getServicesEventByClientEventIdInThread(clientEventId: String) = eventRepository.getServicesEventByClientEventIdInThread(clientEventId)

    fun getMyPartyServicesByProvider(id: String) =
        eventRepository.getServicesEventByProviderId(id).map { list ->
            return@map list
        }

    fun getServicesEventByService(serviceId: String) = eventRepository.getServicesEventByService(serviceId)

    fun getMyPartyService(serviceEventId: String) = eventRepository.getMyPartyService(serviceEventId)

    fun logService(idService: String, logType: LogServiceType) = eventRepository.logService(idService, logType)
}
