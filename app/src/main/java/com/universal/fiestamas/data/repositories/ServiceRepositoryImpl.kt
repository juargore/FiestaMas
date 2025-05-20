@file:Suppress("LocalVariableName")

package com.universal.fiestamas.data.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.universal.fiestamas.data.apis.ServiceApi
import com.universal.fiestamas.data.extensions.collectionListenerFlow
import com.universal.fiestamas.data.extensions.documentListenerFlow
import com.universal.fiestamas.data.extensions.getFilterByQuery
import com.universal.fiestamas.data.extensions.getServicesTypesByServiceCategoryIdFlow
import com.universal.fiestamas.data.module.Constants
import com.universal.fiestamas.data.module.Constants.ACTIVE
import com.universal.fiestamas.data.module.Constants.ATTRIBUTES
import com.universal.fiestamas.data.module.Constants.IS_ACTIVE
import com.universal.fiestamas.data.module.Constants.IS_DELETED
import com.universal.fiestamas.data.module.Constants.PROMOTIONS
import com.universal.fiestamas.data.module.Constants.QUOTATIONS
import com.universal.fiestamas.data.module.Constants.SERVICES
import com.universal.fiestamas.data.module.Constants.SERVICE_CATEGORIES
import com.universal.fiestamas.domain.common.BaseResult
import com.universal.fiestamas.domain.common.ErrorResponse
import com.universal.fiestamas.domain.models.ActiveServiceV2
import com.universal.fiestamas.domain.models.Attribute
import com.universal.fiestamas.domain.models.MyPartyServiceStatusV2
import com.universal.fiestamas.domain.models.Promotion
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.domain.models.ServiceType
import com.universal.fiestamas.domain.models.SubService
import com.universal.fiestamas.domain.models.SuggestedAttribute
import com.universal.fiestamas.domain.models.UriFile
import com.universal.fiestamas.domain.models.request.AcceptOrDeclineEditQuoteRequestV2
import com.universal.fiestamas.domain.models.request.AddServiceProviderRequest
import com.universal.fiestamas.domain.models.request.ClientNotesQuoteV2
import com.universal.fiestamas.domain.models.request.EditPromoRequest
import com.universal.fiestamas.domain.models.request.EntityDataRequest
import com.universal.fiestamas.domain.models.request.FilterRequest
import com.universal.fiestamas.domain.models.request.ImportantNotesQuoteV2
import com.universal.fiestamas.domain.models.request.ItemBidAcceptOrRejectRequest
import com.universal.fiestamas.domain.models.request.PromoRequest
import com.universal.fiestamas.domain.models.request.ProviderNotesQuoteV2
import com.universal.fiestamas.domain.models.request.QuoteV2
import com.universal.fiestamas.domain.models.request.RequestQuotation
import com.universal.fiestamas.domain.models.request.UpdateServiceProviderRequest
import com.universal.fiestamas.domain.models.response.GetQuoteResponse
import com.universal.fiestamas.domain.models.response.StatusResponseV2
import com.universal.fiestamas.domain.usecases.IServiceRepository
import com.universal.fiestamas.presentation.ui.dialogs.OptionsQuote
import com.universal.fiestamas.presentation.ui.dialogs.toStringStatus
import com.universal.fiestamas.presentation.utils.awaitTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ServiceRepositoryImpl (
    authFirebase: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val serviceApi: ServiceApi
) : IServiceRepository {

    private val _firebaseUser = MutableStateFlow(authFirebase.currentUser)

    private suspend fun getAuthToken(): String? {
        return suspendCancellableCoroutine { continuation ->
            _firebaseUser.value?.getIdToken(true)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result.token
                    continuation.resume(token)
                } else {
                    Log.e("Token error", "Error getting auth token on ServiceRepositoryImpl: ${task.exception}")
                    continuation.resume(null)
                }
            }
        }
    }

    override fun getAllServices(): Flow<List<Service>> {
        val serviceCollection = firestore.collection(SERVICES)
        val query = serviceCollection
            .whereEqualTo(ACTIVE, true)
            .whereEqualTo(IS_DELETED, false)
        return serviceCollection.collectionListenerFlow(Service::class.java, query)
    }

    override fun getFavouriteServices(servicesIds: List<String>): Flow<List<Service>> {
        val serviceCollection = firestore.collection(SERVICES)
        val query = serviceCollection.whereIn(FieldPath.documentId(), servicesIds)
        return serviceCollection.collectionListenerFlow(Service::class.java, query)
    }

    override fun getServicesCategoriesList(): Flow<List<ServiceCategory>> {
        val eventListRef: CollectionReference = firestore.collection(SERVICE_CATEGORIES)
        return eventListRef.collectionListenerFlow(ServiceCategory::class.java)
        //val request = serviceApi.getAllServicesCategoriesV2()
        //emit(request.body()?.data?.map { it.toServiceCategory() } ?: emptyList() )
    }

    override fun getServicesTypesByServiceCategoryId(id: String): Flow<List<ServiceType>> {
        return firestore.getServicesTypesByServiceCategoryIdFlow(id)
        /*
        val query = getFilterByQuery(Constants.ID_SERVICE_CATEGORY, id)
        val request = serviceApi.getServicesTypesByCategoryId(query)
        emit(request.body()?.data?.map { it.toServiceType() } ?: emptyList() )*/
    }

    override fun getServicesOptionsByServiceCategoryId(id: String): Flow<List<Service>> {
        val serviceTypesCollection = firestore.collection(SERVICES)
        val query = serviceTypesCollection
            .whereEqualTo(Constants.ID_SERVICE_CATEGORY, id)
            .whereEqualTo(IS_DELETED, false)
        return serviceTypesCollection.collectionListenerFlow(Service::class.java, query)
        /*
        val query1 = getFilterByQuery(Constants.ID_SERVICE_CATEGORY, id)
        val query2 = getFilterByQuery(IS_DELETED, IS_FALSE)
        val finalQuery = FilterRequest(query1.filters + query2.filters)
        val request = serviceApi.getServicesOptionsByQueryV2(finalQuery)
        emit(request.body()?.data?.map { it.toService() } ?: emptyList() )*/
    }

    override fun getServicesOptionsByServiceTypeId(id: String): Flow<List<Service>> {
        val serviceTypesCollection = firestore.collection(SERVICES)
        val query = serviceTypesCollection
            .whereEqualTo(Constants.ID_SERVICE_TYPE, id)
            .whereEqualTo(IS_DELETED, false)
        return serviceTypesCollection.collectionListenerFlow(Service::class.java, query)
        /*
        val query1 = getFilterByQuery(Constants.ID_SERVICE_TYPE, id)
        val query2 = getFilterByQuery(IS_DELETED, IS_FALSE)
        val finalQuery = FilterRequest(query1.filters + query2.filters)
        val request = serviceApi.getServicesOptionsByQueryV2(finalQuery)
        emit(request.body()?.data?.map { it.toService() } ?: emptyList() )*/
    }

    override fun getServicesOptionsBySubServiceId(id: String): Flow<List<Service>> {
        val serviceTypesCollection = firestore.collection(SERVICES)
        val query = serviceTypesCollection
            .whereEqualTo(Constants.ID_SUB_SERVICE_TYPE, id)
            .whereEqualTo(IS_DELETED, false)
        return serviceTypesCollection.collectionListenerFlow(Service::class.java, query)
        /*
        val query1 = getFilterByQuery(Constants.ID_SUB_SERVICE_TYPE, id)
        val query2 = getFilterByQuery(IS_DELETED, IS_FALSE)
        val finalQuery = FilterRequest(query1.filters + query2.filters)
        val request = serviceApi.getServicesOptionsByQueryV2(finalQuery)
        emit(request.body()?.data?.map { it.toService() } ?: emptyList() )*/
    }

    override fun addSuggestedAttributes(attributes: List<SuggestedAttribute>) {
        attributes.forEach {
            val body = EntityDataRequest(entityData = it)
            serviceApi.addSuggestedAttributes(body)
        }
    }

    override fun getAttributesByServiceCategoryId(id: String): Flow<List<Attribute>> {
        val serviceTypesCollection = firestore.collection(ATTRIBUTES)
        val query = serviceTypesCollection.whereEqualTo(Constants.ID_SERVICE_CATEGORY, id)
        return serviceTypesCollection.collectionListenerFlow(Attribute::class.java, query)
        /*
        val query = getFilterByQuery(Constants.ID_SERVICE_CATEGORY, id)
        val request = serviceApi.getAttributesByQueryV2(query)
        emit(request.body()?.data?.map { it.toAttribute() } ?: emptyList() )*/
    }

    override fun getAttributesByServiceTypeId(id: String): Flow<List<Attribute>> {
        val serviceTypesCollection = firestore.collection(ATTRIBUTES)
        val query = serviceTypesCollection.whereEqualTo(Constants.ID_SERVICE_TYPE, id)
        return serviceTypesCollection.collectionListenerFlow(Attribute::class.java, query)
        /*
        val query = getFilterByQuery(Constants.ID_SERVICE_TYPE, id)
        val request = serviceApi.getAttributesByQueryV2(query)
        emit(request.body()?.data?.map { it.toAttribute() } ?: emptyList() )*/
    }

    override fun getAttributesBySubServiceId(id: String): Flow<List<Attribute>> {
        val serviceTypesCollection = firestore.collection(ATTRIBUTES)
        val query = serviceTypesCollection.whereEqualTo(Constants.ID_SUB_SERVICE_TYPE, id)
        return serviceTypesCollection.collectionListenerFlow(Attribute::class.java, query)
        /*
        val query = getFilterByQuery(Constants.ID_SUB_SERVICE_TYPE, id)
        val request = serviceApi.getAttributesByQueryV2(query)
        emit(request.body()?.data?.map { it.toAttribute() } ?: emptyList() )*/
    }

    override fun getAttributeById(id: String): Flow<Attribute?> {
        val eventRef: DocumentReference = firestore.collection(ATTRIBUTES).document(id)
        return eventRef.documentListenerFlow(Attribute::class.java)
        /*
        val request = serviceApi.getAttributeById(id)
        if (request.body()?.status == 200) {
            emit(request.body()?.data?.toAttribute())
        } else {
            emit(null)
        }*/
    }

    override fun getSubServicesByServiceTypeId(id: String): Flow<List<SubService>> = flow {
        val query = getFilterByQuery(Constants.ID_SERVICE_TYPE, id)
        val request = serviceApi.getSubServicesByServiceTypeIdV2(query)
        emit(request.body()?.data?.map { it.toSubService() } ?: emptyList() )
    }

    override fun getServiceDetails(id: String): Flow<Service?> {
        ///*
        val eventRef: DocumentReference = firestore.collection(SERVICES).document(id)
        return eventRef.documentListenerFlow(Service::class.java)
        //*/
        //val request = serviceApi.getServiceByIdV2(id)
        //emit(request.body()?.data?.toService())
    }

    override fun likeService(userId: String, serviceId: String) = flow {
        val request = serviceApi.likeServiceV2(userId, serviceId)
        if (request.isSuccessful && request.body()?.status == 200) {
            emit(BaseResult.Success(true))
        } else {
            emit(BaseResult.Error(ErrorResponse(
                message = request.errorBody().toString(),
                status = request.code()
            )))
        }
    }

    override fun enableOrDisableService(serviceId: String, isActive: Boolean) = flow {
        val body = EntityDataRequest(entityData = ActiveServiceV2(active = isActive))
        val response = serviceApi.enableOrDisableServiceV2(serviceId, body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(BaseResult.Success(true))
        } else {
            BaseResult.Error(ErrorResponse(
                message = "Error al cambiar status del servicio",
                status = response.code()
            ))
        }
    }

    override fun uploadMediaFile(uriFile: UriFile?) = flow {
        if (uriFile == null) {
            emit("")
        } else {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val testFolderRef = storageRef.child("test")
            val imageRef = testFolderRef.child(uriFile.fileName)
            try {
                val uploadTask = imageRef.putFile(uriFile.uri)
                uploadTask.awaitTask()
                val downloadUrl = imageRef.downloadUrl.awaitTask()
                emit(downloadUrl.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }.catch { e ->
        emit("Error: ${e.localizedMessage}")
    }

    override fun createServiceForProvider(request: AddServiceProviderRequest) = flow {
        val body = EntityDataRequest(entityData = request)
        val response = serviceApi.createServiceV2(body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(BaseResult.Success(true))
        } else {
            emit(BaseResult.Error(ErrorResponse(
                message = response.errorBody().toString(),
                status = response.code()
            )))
        }
    }

    override fun updateServiceForProvider(serviceId: String, request: UpdateServiceProviderRequest) = flow {
        val body = EntityDataRequest(entityData = request)
        val response = serviceApi.updateServiceV2(serviceId, body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(BaseResult.Success(true))
        } else {
            emit(BaseResult.Error(ErrorResponse(
                message = response.errorBody().toString(),
                status = response.code()
            )))
        }
    }

    override fun getServicesByProviderId(id: String): Flow<List<Service>> {
        val servicesCollection = firestore.collection(SERVICES)
        val query = servicesCollection
            .whereEqualTo(Constants.ID_PROVIDER, id)
            .whereEqualTo(IS_DELETED, false)
        return servicesCollection.collectionListenerFlow(Service::class.java, query)
    }

    override fun deleteServiceById(id: String) = flow {
        val response = serviceApi.deleteServiceV2(id)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(BaseResult.Success(true))
        } else {
            emit(BaseResult.Error(ErrorResponse(
                message = response.errorBody().toString(),
                status = response.code()
            )))
        }
    }

    override fun createNewQuoteV2(quote: QuoteV2): Flow<BaseResult<Boolean, ErrorResponse>> = flow {
        val body = EntityDataRequest(entityData = quote)
        val response = serviceApi.createNewQuoteV2(body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(BaseResult.Success(true))
        } else {
            emit(BaseResult.Error(ErrorResponse(
                message = response.errorBody().toString(),
                status = response.code()
            )))
        }
    }

    override fun editQuoteV2(
        quoteId: String,
        quote: QuoteV2
    ): Flow<BaseResult<Boolean, ErrorResponse>> = flow {
        val body = EntityDataRequest(entityData = quote)
        val response = serviceApi.editQuoteV2(quoteId, body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(BaseResult.Success(true))
        } else {
            emit(BaseResult.Error(ErrorResponse(
                message = response.errorBody().toString(),
                status = response.code()
            )))
        }
    }

    override fun getQuoteByServiceEvent(serviceEventId: String): Flow<List<GetQuoteResponse>> {
        val serviceTypesCollection = firestore.collection(QUOTATIONS)
        val query = serviceTypesCollection.whereEqualTo(Constants.ID_SERVICE_EVENT, serviceEventId)
        return serviceTypesCollection.collectionListenerFlow(GetQuoteResponse::class.java, query)
    }

    override fun acceptOrDeclineOfferV2(
        serviceEventId: String,
        quotationId: String,
        userId: String,
        status: String,
        bid: Int,
        title: String,
        content: String
    ) {
        val request = EntityDataRequest(entityData = ItemBidAcceptOrRejectRequest(title, content, status, userId, bid))
        serviceApi.acceptOrDeclineOfferV3(quotationId, request).execute()
    }

    override fun updateServiceStatus(
        serviceEventId: String,
        status: OptionsQuote
    ): Flow<BaseResult<Boolean, ErrorResponse>> = flow {
        val body = EntityDataRequest(
            entityData = MyPartyServiceStatusV2(status = status.toStringStatus())
        )
        val response = serviceApi.updateServiceStatusV2(serviceEventId, body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(BaseResult.Success(true))
        } else {
            emit(BaseResult.Error(ErrorResponse(
                message = response.errorBody().toString(),
                status = response.code()
            )))
        }
    }

    override fun addNotesToExistingQuote(
        quotationId: String,
        notesClient: String?,
        notesProvider: String?,
        importantNotes: String?
    ): Flow<BaseResult<Boolean, ErrorResponse>> = flow {
        val data: Any? = when {
            !notesClient.isNullOrEmpty() -> ClientNotesQuoteV2(noteBook_client = notesClient)
            !notesProvider.isNullOrEmpty() -> ProviderNotesQuoteV2(noteBook_provider = notesProvider)
            !importantNotes.isNullOrEmpty() -> ImportantNotesQuoteV2(notes = importantNotes)
            else -> null
        }

        if (data != null) {
            val body = EntityDataRequest(entityData = data)
            val response = serviceApi.editQuoteNotesV2(quotationId, body)
            if (response.isSuccessful && response.body()?.status == 200) {
                emit(BaseResult.Success(true))
            } else {
                emit(BaseResult.Error(ErrorResponse(
                    message = response.errorBody().toString(),
                    status = response.code()
                )))
            }
        }
    }

    override fun requestQuoteFromClientToProvider(request: RequestQuotation) = flow {
        val body = EntityDataRequest(entityData = request)
        val response = serviceApi.requestQuoteFromClientToProviderV2(body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(response.body()!!)
        } else {
            emit(StatusResponseV2(status = 400))
        }
    }

    override fun requestEditQuoteFromProviderToClient(
        request: RequestQuotation
    ) = flow {
        val body = EntityDataRequest(entityData = request)
        val response = serviceApi.requestEditQuoteFromProviderToClientV2(body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(response.body()!!)
        } else {
            emit(StatusResponseV2(status = 400))
        }
    }


    override fun acceptEditQuoteFromClientToProvider(
        request: AcceptOrDeclineEditQuoteRequestV2,
        quoteId: String,
        messageId: String
    ) = flow {
        val body = EntityDataRequest(entityData = request)
        val response = serviceApi.acceptRequestEditQuoteV2(quoteId, messageId, body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(response.body()!!)
        } else {
            emit(StatusResponseV2(status = 400))
        }
    }

    override fun declineEditQuoteFromClientToProvider(
        request: AcceptOrDeclineEditQuoteRequestV2,
        quoteId: String,
        messageId: String
    ) = flow {
        val body = EntityDataRequest(entityData = request)
        val response = serviceApi.declineRequestEditQuoteV2(quoteId, messageId, body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(response.body()!!)
        } else {
            emit(StatusResponseV2(status = 400))
        }
    }

    override fun createPromo(request: PromoRequest): Flow<Boolean> = flow {
        val body = EntityDataRequest(entityData = request)
        val response = serviceApi.createNewPromotionV2(body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(true)
        } else {
            emit(false)
        }
    }

    override fun editExistingPromo(promoId: String, request: EditPromoRequest): Flow<Boolean> = flow {
        val body = EntityDataRequest(entityData = request)
        val response = serviceApi.editPromotionV2(promoId, body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(true)
        } else {
            emit(false)
        }
    }

    override fun getPromos(providerId: String): Flow<List<Promotion>> = flow {
        val query1 = getFilterByQuery(Constants.ID_PROVIDER, providerId)
        val query2 = getFilterByQuery(IS_ACTIVE, true)
        val finalQuery = FilterRequest(query1.filters + query2.filters)
        val request = serviceApi.getPromotionsByQueryV2(finalQuery)
        emit(request.body()?.data ?: emptyList())
        /*val serviceTypesCollection = firestore.collection(PROMOTIONS)
        val query = serviceTypesCollection
            .whereEqualTo(Constants.ID_PROVIDER, providerId)
            .whereEqualTo(IS_ACTIVE, true)
        return serviceTypesCollection.collectionListenerFlow(Promotion::class.java, query)
        */
    }

    override fun deletePromotion(promoId: String): Flow<Boolean> = flow {
        val response = serviceApi.deletePromotionV2(promoId)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(true)
        } else {
            emit(false)
        }
    }
}
