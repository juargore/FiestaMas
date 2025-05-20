package com.universal.fiestamas.data.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.universal.fiestamas.data.apis.EventApi
import com.universal.fiestamas.data.extensions.collectionListenerFlow
import com.universal.fiestamas.data.extensions.documentListenerFlow
import com.universal.fiestamas.data.extensions.getFilterByQuery
import com.universal.fiestamas.data.module.Constants
import com.universal.fiestamas.data.module.Constants.CLIENT_EVENTS
import com.universal.fiestamas.data.module.Constants.EVENT_TYPES
import com.universal.fiestamas.data.module.Constants.SERVICES_EVENT
import com.universal.fiestamas.data.utils.NetworkManager
import com.universal.fiestamas.domain.common.BaseResult
import com.universal.fiestamas.domain.common.ErrorResponse
import com.universal.fiestamas.domain.models.Event
import com.universal.fiestamas.domain.models.FirstQuestionsClient
import com.universal.fiestamas.domain.models.FirstQuestionsProvider
import com.universal.fiestamas.domain.models.MyPartyEvent
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.request.CreateEventProviderRequest
import com.universal.fiestamas.domain.models.request.CreateEventRequest
import com.universal.fiestamas.domain.models.request.EntityDataRequest
import com.universal.fiestamas.domain.usecases.IEventRepository
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details.DetailsServiceViewModel.LogServiceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class EventRepositoryImpl(
    private val authFirebase: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val eventApi: EventApi,
    private val networkManager: NetworkManager
) : IEventRepository {

    private val _firebaseUser = MutableStateFlow(authFirebase.currentUser)

    private suspend fun getAuthToken(): String {
        return suspendCancellableCoroutine { continuation ->
            _firebaseUser.value?.getIdToken(true)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result.token.orEmpty()
                    continuation.resume(token)
                } else {
                    Log.e("Token error", "Error getting auth token on AuthRepositoryImpl: ${task.exception}")
                    continuation.resume("")
                }
            }
        }
    }

    override fun getEventTypesList(): Flow<List<Event>>  {
        val eventListRef: CollectionReference = firestore.collection(EVENT_TYPES)
        return eventListRef.collectionListenerFlow(Event::class.java)
        /*
        val request = eventApi.getAllEventsTypesV2()
        emit(request.body()?.data?.map { it.toEvent() } ?: emptyList())*/
    }

    override fun getEventTypeById(id: String): Flow<Event?> {
        val eventRef: DocumentReference = firestore.collection(EVENT_TYPES).document(id)
        return eventRef.documentListenerFlow(Event::class.java)
        /*
        val request = eventApi.getEventTypeByIdV2(id)
        emit(request.body()?.data?.toEvent())*/
    }

    override fun createEventByClient(
        clientId: String,
        eventId: String,
        questions: FirstQuestionsClient
    ) = flow {
        val request = EntityDataRequest(
            CreateEventRequest(
                name = questions.festejadosNames,
                id_client = clientId,
                id_event_type = eventId,
                date = questions.date,
                location = questions.city,
                lat = questions.location?.lat.toString(),
                lng = questions.location?.lng.toString(),
                attendees = questions.numberOfGuests
            )
        )
        val response = eventApi.createNewEventV2(request)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(BaseResult.Success(response.body()!!))
        } else {
            emit(BaseResult.Error(ErrorResponse(
                message = response.errorBody().toString(),
                status = response.body()?.status ?: 400
            )))
        }
    }

    override fun createEventByProvider(
        providerId: String,
        eventId: String,
        questions: FirstQuestionsProvider
    ) = flow {
        val request = EntityDataRequest(
            CreateEventProviderRequest(
                name = questions.contactName,
                id_client = providerId,
                id_event_type = eventId,
                date = questions.date,
                location = questions.city,
                lat = questions.location?.lat.toString(),
                lng = questions.location?.lng.toString(),
                attendees = "100",
                contact_email = questions.email,
                contact_name = questions.contactName,
                contact_phone = questions.phone
            )
        )
        val response = eventApi.createNewProviderEventV2(request)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(response.body())
        } else {
            emit(null)
        }
    }

    // keep it as real time!
    override fun getClientEventsByClientId(clientId: String): Flow<List<MyPartyEvent>> {
        val clientEventsCollection = firestore.collection(CLIENT_EVENTS)
        val query = clientEventsCollection
            .whereEqualTo(Constants.ID_CLIENT, clientId)
        return clientEventsCollection.collectionListenerFlow(MyPartyEvent::class.java, query)
    }

    // keep it as real time!
    override fun getClientEventById(id: String): Flow<MyPartyEvent?> {
        val eventRef: DocumentReference = firestore.collection(CLIENT_EVENTS).document(id)
        return eventRef.documentListenerFlow(MyPartyEvent::class.java)
    }

    override fun getServicesEventByClientEventId(id: String): Flow<List<MyPartyService>> = flow {
        val query1 = getFilterByQuery(Constants.ID_CLIENT_EVENT, id)
        val request = eventApi.getServicesEventByClientEventIdV2(query1)
        emit(request.body()?.data?.map { it.toMyPartyService() } ?: emptyList())
        /*val serviceEventsCollection = firestore.collection(SERVICES_EVENT)
        val query = serviceEventsCollection.whereEqualTo(Constants.ID_CLIENT_EVENT, id)
        return serviceEventsCollection.collectionListenerFlow(MyPartyService::class.java, query)*/
    }

    // done in V2
    override fun getServicesEventByClientEventIdInThread(clientEventId: String): Flow<List<MyPartyService>> = flow {
        val query = getFilterByQuery(Constants.ID_CLIENT_EVENT, clientEventId)
        val request = eventApi.getServicesEventByClientEventIdV2(query)
        emit(request.body()?.data?.map { it.toMyPartyService() } ?: emptyList())
    }

    // keep it as real time!
    override fun getServicesEventByProviderId(id: String): Flow<List<MyPartyService>> {
        val serviceEventsCollection = firestore.collection(SERVICES_EVENT)
        val query = serviceEventsCollection
            .whereEqualTo(Constants.ID_PROVIDER, id)
        return serviceEventsCollection.collectionListenerFlow(MyPartyService::class.java, query)
    }

    override fun getServicesEventsByClient(clientId: String): Flow<List<MyPartyService>> = flow {
        val query = getFilterByQuery(Constants.ID_CLIENT, clientId)
        val request = eventApi.getServicesEventByClientEventIdV2(query)
        emit(request.body()?.data?.map { it.toMyPartyService() } ?: emptyList())
    }

    // keep it as real time!
    override fun getServicesEventByService(serviceId: String): Flow<List<MyPartyService>> {
        val serviceEventsCollection = firestore.collection(SERVICES_EVENT)
        val query = serviceEventsCollection
            .whereEqualTo(Constants.ID_SERVICE, serviceId)
        return serviceEventsCollection.collectionListenerFlow(MyPartyService::class.java, query)
    }

    // keep it as real time!
    override fun getMyPartyService(serviceEventId: String): Flow<MyPartyService?> {
        val eventRef: DocumentReference = firestore.collection(SERVICES_EVENT).document(serviceEventId)
        return eventRef.documentListenerFlow(MyPartyService::class.java)
    }

    override fun addServiceToEvent(eventId: String, serviceId: String) = flow {
        val response = eventApi.addServiceToExistingEventV2(eventId, serviceId)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(response.body()!!.data?.id.orEmpty())
        } else {
            emit(response.message())
        }
    }

    override fun logService(idService: String, logType: LogServiceType) = flow {
        val clientId = _firebaseUser.value?.uid ?: "unknown"
        val stat = when (logType) {
            LogServiceType.VIEW -> "views"
            LogServiceType.CONTACT -> "contacts"
            LogServiceType.CLICK_CONTACT -> "clicks"
        }
        val response = eventApi.logServiceV2(clientId, idService, stat)
        emit(response.body())
    }
}
