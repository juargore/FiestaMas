package com.universal.fiestamas.domain.usecases

import com.universal.fiestamas.domain.common.BaseResult
import com.universal.fiestamas.domain.common.ErrorResponse
import com.universal.fiestamas.domain.models.Attribute
import com.universal.fiestamas.domain.models.Promotion
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.domain.models.ServiceType
import com.universal.fiestamas.domain.models.SubService
import com.universal.fiestamas.domain.models.SuggestedAttribute
import com.universal.fiestamas.domain.models.UriFile
import com.universal.fiestamas.domain.models.request.AcceptOrDeclineEditQuoteRequestV2
import com.universal.fiestamas.domain.models.request.AddServiceProviderRequest
import com.universal.fiestamas.domain.models.request.PromoRequest
import com.universal.fiestamas.domain.models.request.EditPromoRequest
import com.universal.fiestamas.domain.models.request.QuoteV2
import com.universal.fiestamas.domain.models.request.RequestQuotation
import com.universal.fiestamas.domain.models.request.UpdateServiceProviderRequest
import com.universal.fiestamas.domain.models.response.GetQuoteResponse
import com.universal.fiestamas.domain.models.response.StatusResponseV2
import com.universal.fiestamas.presentation.ui.dialogs.OptionsQuote
import kotlinx.coroutines.flow.Flow

interface IServiceRepository {

    fun getAllServices(): Flow<List<Service>>

    fun getFavouriteServices(servicesIds: List<String>): Flow<List<Service>>

    fun getServicesCategoriesList(): Flow<List<ServiceCategory>>

    fun getServicesTypesByServiceCategoryId(id: String): Flow<List<ServiceType>>

    fun getServicesOptionsByServiceCategoryId(id: String): Flow<List<Service>>

    fun getServicesOptionsByServiceTypeId(id: String): Flow<List<Service>>

    fun getServicesOptionsBySubServiceId(id: String): Flow<List<Service>>

    fun addSuggestedAttributes(attributes: List<SuggestedAttribute>)

    fun getAttributesByServiceCategoryId(id: String): Flow<List<Attribute>>

    fun getAttributesByServiceTypeId(id: String): Flow<List<Attribute>>

    fun getAttributesBySubServiceId(id: String): Flow<List<Attribute>>

    fun getSubServicesByServiceTypeId(id: String): Flow<List<SubService>>

    fun getServiceDetails(id: String): Flow<Service?>

    fun getAttributeById(id: String): Flow<Attribute?>

    fun likeService(userId: String, serviceId: String): Flow<BaseResult<Boolean, ErrorResponse>>

    fun enableOrDisableService(serviceId: String, isActive: Boolean): Flow<BaseResult<Boolean, ErrorResponse>>

    fun uploadMediaFile(uriFile: UriFile?): Flow<String>

    fun createServiceForProvider(request: AddServiceProviderRequest): Flow<BaseResult<Boolean, ErrorResponse>>

    fun updateServiceForProvider(serviceId: String, request: UpdateServiceProviderRequest): Flow<BaseResult<Boolean, ErrorResponse>>

    fun getServicesByProviderId(id: String): Flow<List<Service>>

    fun deleteServiceById(id: String): Flow<BaseResult<Boolean, ErrorResponse>>

    fun createNewQuoteV2(quote: QuoteV2): Flow<BaseResult<Boolean, ErrorResponse>>

    fun editQuoteV2(quoteId: String, quote: QuoteV2): Flow<BaseResult<Boolean, ErrorResponse>>

    fun getQuoteByServiceEvent(serviceEventId: String): Flow<List<GetQuoteResponse>>

    fun acceptOrDeclineOfferV2(
        serviceEventId: String,
        quotationId: String,
        userId: String,
        status: String,
        bid: Int,
        title: String,
        content: String
    )

    fun updateServiceStatus(serviceEventId: String, status: OptionsQuote): Flow<BaseResult<Boolean, ErrorResponse>>

    fun addNotesToExistingQuote(
        quotationId: String,
        notesClient: String?,
        notesProvider: String?,
        importantNotes: String?
    ): Flow<BaseResult<Boolean, ErrorResponse>>

    fun requestQuoteFromClientToProvider(request: RequestQuotation): Flow<StatusResponseV2>

    fun requestEditQuoteFromProviderToClient(request: RequestQuotation): Flow<StatusResponseV2>

    fun acceptEditQuoteFromClientToProvider(request: AcceptOrDeclineEditQuoteRequestV2, quoteId: String, messageId: String): Flow<StatusResponseV2>

    fun declineEditQuoteFromClientToProvider(request: AcceptOrDeclineEditQuoteRequestV2, quoteId: String, messageId: String): Flow<StatusResponseV2>

    fun createPromo(request: PromoRequest): Flow<Boolean>

    fun editExistingPromo(promoId: String, request: EditPromoRequest): Flow<Boolean>

    fun getPromos(providerId: String): Flow<List<Promotion>>

    fun deletePromotion(promoId: String): Flow<Boolean>
}
