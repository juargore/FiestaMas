package com.universal.fiestamas.domain.usecases

import com.universal.fiestamas.domain.common.BaseResult
import com.universal.fiestamas.domain.common.ErrorResponse
import com.universal.fiestamas.domain.models.Attribute
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.domain.models.SuggestedAttribute
import com.universal.fiestamas.domain.models.UriFile
import com.universal.fiestamas.domain.models.UserReview
import com.universal.fiestamas.domain.models.request.AcceptOrDeclineEditQuoteRequestV2
import com.universal.fiestamas.domain.models.request.AddServiceProviderRequest
import com.universal.fiestamas.domain.models.request.EditPromoRequest
import com.universal.fiestamas.domain.models.request.PromoRequest
import com.universal.fiestamas.domain.models.request.QuoteV2
import com.universal.fiestamas.domain.models.request.RequestQuotation
import com.universal.fiestamas.domain.models.request.UpdateServiceProviderRequest
import com.universal.fiestamas.domain.models.response.StatusResponseV2
import com.universal.fiestamas.presentation.ui.dialogs.OptionsQuote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ServiceUseCase @Inject constructor(
    private val serviceRepository: IServiceRepository,
    private val eventRepository: IEventRepository
) {

    fun getAllServices() = serviceRepository.getAllServices()

    fun getFavouriteServices(servicesIds: List<String>) = serviceRepository.getFavouriteServices(servicesIds)

    fun getServicesCategoriesList(): Flow<List<ServiceCategory>> = serviceRepository.getServicesCategoriesList()

    fun getServicesTypesByServiceCategoryId(id: String) = serviceRepository.getServicesTypesByServiceCategoryId(id)

    fun getSubServicesByServiceTypeId(id: String) = serviceRepository.getSubServicesByServiceTypeId(id)

    fun getServicesOptionsByServiceCategoryId(id: String) = serviceRepository.getServicesOptionsByServiceCategoryId(id)

    fun getServicesOptionsByServiceTypeId(id: String) = serviceRepository.getServicesOptionsByServiceTypeId(id)

    fun getServicesOptionsBySubServiceTypeId(id: String) = serviceRepository.getServicesOptionsBySubServiceId(id)

    fun addSuggestedAttributes(attributes: List<SuggestedAttribute>) = serviceRepository.addSuggestedAttributes(attributes)

    fun getAttributesByServiceCategoryId(id: String) = serviceRepository.getAttributesByServiceCategoryId(id)

    fun getAttributesByServiceTypeId(id: String)  = serviceRepository.getAttributesByServiceTypeId(id)

    fun getAttributesBySubServiceId(id: String)  = serviceRepository.getAttributesBySubServiceId(id)

    fun getServiceDetails(id: String) = serviceRepository.getServiceDetails(id)

    fun getAttributesByIds(ids: List<String>): Flow<List<Attribute>> = flow {
        val attributeList = mutableListOf<Attribute>()
        ids.forEach { id ->
            serviceRepository.getAttributeById(id).firstOrNull()?.let { attributeList.add(it) }
        }; emit(attributeList)
    }

    fun likeService(userId: String, serviceId: String) = serviceRepository.likeService(userId, serviceId)

    fun enableOrDisableService(serviceId: String, isActive: Boolean) = serviceRepository.enableOrDisableService(serviceId, isActive)

    fun uploadMediaFiles(
        images: List<UriFile?>,
        videos: List<UriFile?>
    ): Flow<Pair<List<String>, List<String>>> = flow {
        val imagesUrl = mutableListOf<String>()
        val videosUrl = mutableListOf<String>()
        for (uri in images) {

            val response = serviceRepository.uploadMediaFile(uri).first()
            if (response.isNotEmpty()) {
                imagesUrl.add(response)
            }
        }
        for (uri in videos) {
            val response = serviceRepository.uploadMediaFile(uri).first()
            if (response.isNotEmpty()) {
                videosUrl.add(response)
            }
        }
        emit(Pair(imagesUrl, videosUrl))
    }

    fun createServiceForProvider(request: AddServiceProviderRequest) = serviceRepository.createServiceForProvider(request)

    fun updateServiceForProvider(serviceId: String, request: UpdateServiceProviderRequest) = serviceRepository.updateServiceForProvider(serviceId, request)

    fun getServicesByProviderId(id: String) = serviceRepository.getServicesByProviderId(id)

    fun deleteServiceById(id: String): Flow<BaseResult<Boolean, ErrorResponse>> = serviceRepository.deleteServiceById(id)

    fun getReviewsByServiceId(@Suppress("UNUSED_PARAMETER") id: String): Flow<List<UserReview>> = flow {
        emit(
            listOf(
                UserReview(
                    id = "1",
                    name = "Bill Gates",
                    photo = "https://w7.pngwing.com/pngs/471/773/png-transparent-bill-gates-bill-gates-seattle-microsoft-berkshire-hathaway-chairman-bill-gates-company-people-recruiter-thumbnail.png",
                    message = "Buen servicio y atención"
                ),
                UserReview(
                    id = "2",
                    name = "John Doe",
                    photo = "https://www.freecodecamp.org/news/content/images/2022/06/hrishikesh.jpg",
                    message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
                ),
                UserReview(
                    id = "3",
                    name = "Laura Pérez",
                    photo = "https://media.revistavanityfair.es/photos/60e82d989bf55ca1055aabf0/master/w_1600%2Cc_limit/242118.jpg",
                    message = "Sin comentarios."
                )
            )
        )
    }

    fun createNewQuoteV2(quote: QuoteV2) = serviceRepository.createNewQuoteV2(quote)

    fun editQuoteV2(quoteId: String, quote: QuoteV2) = serviceRepository.editQuoteV2(quoteId, quote)


    fun getQuoteByServiceEvent(
        serviceEventId: String
    ) = serviceRepository.getQuoteByServiceEvent(serviceEventId)

    fun acceptOrDeclineOfferV2(
        serviceEventId: String,
        quotationId: String,
        userId: String,
        status: String,
        bid: Int,
        title: String,
        content: String
    ) = serviceRepository.acceptOrDeclineOfferV2(serviceEventId, quotationId, userId, status, bid, title, content)

    fun updateServiceStatus(
        serviceEventId: String,
        status: OptionsQuote
    ) = serviceRepository.updateServiceStatus(serviceEventId, status)

    fun addNotesToExistingQuote(
        quotationId: String,
        notesClient: String?,
        notesProvider: String?,
        importantNotes: String?
    ) = serviceRepository.addNotesToExistingQuote(quotationId, notesClient, notesProvider, importantNotes)

    fun requestQuoteFromClientToProvider(
        request: RequestQuotation,
    ) = serviceRepository.requestQuoteFromClientToProvider(request)

    fun requestEditQuoteFromProviderToClient(
        request: RequestQuotation
    ) = serviceRepository.requestEditQuoteFromProviderToClient(request)

    fun acceptEditQuoteFromClientToProvider(request: AcceptOrDeclineEditQuoteRequestV2, serviceEventId: String, messageId: String) = flow {
        val quote = getQuoteByServiceEvent(serviceEventId).firstOrNull()
        if (quote.isNullOrEmpty()) {
            emit(StatusResponseV2(status = 500))
        } else {
            emit(serviceRepository.acceptEditQuoteFromClientToProvider(request, quote.first().id, messageId).first())
        }
    }

    fun declineEditQuoteFromClientToProvider(request: AcceptOrDeclineEditQuoteRequestV2, serviceEventId: String, messageId: String) = flow {
        val quote = getQuoteByServiceEvent(serviceEventId).firstOrNull()
        if (quote.isNullOrEmpty()) {
            emit(StatusResponseV2(status = 500))
        } else {
            emit(serviceRepository.declineEditQuoteFromClientToProvider(request, quote.first().id, messageId).first())
        }
    }

    fun createPromo(request: PromoRequest) = serviceRepository.createPromo(request)

    fun editExistingPromo(promoId: String, request: EditPromoRequest) = serviceRepository.editExistingPromo(promoId, request)

    fun getPromos(providerId: String) = serviceRepository.getPromos(providerId)

    fun deletePromotion(promoId: String) = serviceRepository.deletePromotion(promoId)
}
