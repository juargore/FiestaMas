@file:Suppress("LocalVariableName")

package com.universal.fiestamas.data.apis

import com.universal.fiestamas.domain.models.ResponseAttributeV2
import com.universal.fiestamas.domain.models.ResponseAttributesV2
import com.universal.fiestamas.domain.models.ResponsePromotionsV2
import com.universal.fiestamas.domain.models.ResponseServiceCategoryV2
import com.universal.fiestamas.domain.models.ResponseServiceV2
import com.universal.fiestamas.domain.models.ResponseServicesCategoriesV2
import com.universal.fiestamas.domain.models.ResponseServicesTypesV2
import com.universal.fiestamas.domain.models.ResponseServicesV2
import com.universal.fiestamas.domain.models.ResponseSubServicesV2
import com.universal.fiestamas.domain.models.request.AddServiceProviderRequest
import com.universal.fiestamas.domain.models.request.EditPromoRequest
import com.universal.fiestamas.domain.models.request.EntityDataRequest
import com.universal.fiestamas.domain.models.request.FilterRequest
import com.universal.fiestamas.domain.models.request.ItemAddNotesToQuoteRequest
import com.universal.fiestamas.domain.models.request.ItemBidAcceptOrRejectRequest
import com.universal.fiestamas.domain.models.request.ItemBidAcceptedRequest
import com.universal.fiestamas.domain.models.request.ItemBidOfferRequest
import com.universal.fiestamas.domain.models.request.ItemEditQuoteRequest
import com.universal.fiestamas.domain.models.request.ItemUpdateStatusRequest
import com.universal.fiestamas.domain.models.request.ItemsExpressQuoteRequest
import com.universal.fiestamas.domain.models.request.ItemsQuoteRequest
import com.universal.fiestamas.domain.models.request.PromoRequest
import com.universal.fiestamas.domain.models.request.UpdateServiceProviderRequest
import com.universal.fiestamas.domain.models.response.CreateQuoteResponse
import com.universal.fiestamas.domain.models.response.StatusResponseV2
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ServiceApi {

    //@PUT("user/{userId}/like-service/{serviceId}")
    //suspend fun likeService(@Path("userId") userId: String, @Path("serviceId") serviceId: String): Response<Void>

    @Multipart
    @POST("upload")
    fun uploadMediaFile(@Part file: MultipartBody.Part): Call<ResponseBody>

    @POST("service")
    suspend fun createService(@Body request: AddServiceProviderRequest): Response<Void>

    @PUT("service/{id_service}")
    suspend fun updateService(@Path("id_service") id_service: String, @Body request: UpdateServiceProviderRequest): Response<Void>

    @DELETE("service/{serviceId}")
    suspend fun deleteService(@Path("serviceId") serviceId: String): Response<Void>

    @PUT("service/{id_service}/status")
    fun enableOrDisableService(@Path("id_service") id_service: String): Call<Void>

    @POST("service_event/{id_service_event}/quotation")
    suspend fun createNewClassicQuote(@Path("id_service_event") id_service_event: String): Response<CreateQuoteResponse>

    @POST("service_event/{id_service_event}/quotation")
    suspend fun createNewExpressQuote(
        @Path("id_service_event") id_service_event: String,
        @Body request: ItemsExpressQuoteRequest
    ): Response<CreateQuoteResponse>

    @POST("service_event/{id_service_event}/quotation/{id_quotation}/element")
    suspend fun addItemsToExistingQuote(
        @Path("id_service_event") id_service_event: String,
        @Path("id_quotation") id_quotation: String,
        @Body request: ItemsQuoteRequest
    ): Response<Void>

    @DELETE("service_event/{id_service_event}/quotation/{id_quotation}/element/{index}")
    suspend fun deleteItemsFromExistingQuote(
        @Path("id_service_event") id_service_event: String,
        @Path("id_quotation") id_quotation: String,
        @Path("index") index: Int
    ): Response<Void>

    @POST("service_event/{id_service_event}/quotation/{id_quotation}/bid")
    suspend fun createNewOffer(
        @Path("id_service_event") id_service_event: String,
        @Path("id_quotation") id_quotation: String,
        @Body request: ItemBidOfferRequest
    ): Response<Void>

    @POST("service_event/{id_service_event}/quotation/{id_quotation}/bid")
    fun acceptOrDeclineOfferV2(
        @Path("id_service_event") id_service_event: String,
        @Path("id_quotation") id_quotation: String,
        @Body request: ItemBidAcceptOrRejectRequest
    ): Call<Void>

    @POST("service_event/{id_service_event}/quotation/{id_quotation}/bid")
    suspend fun acceptOffer(
        @Path("id_service_event") id_service_event: String,
        @Path("id_quotation") id_quotation: String,
        @Body request: ItemBidAcceptedRequest
    ): Response<Void>

    @PUT("service_event/{id_service_event}")
    fun updateServiceStatus(
        @Path("id_service_event") id_service_event: String,
        @Body request: ItemUpdateStatusRequest
    ): Call<Void>

    @PUT("service_event/{id_service_event}/quotation/{id_quotation}")
    fun addNotesToExistingQuote(
        @Path("id_service_event") id_service_event: String,
        @Path("id_quotation") id_quotation: String,
        @Body request: ItemAddNotesToQuoteRequest
    ): Call<Void>

    @PUT("service_event/{id_service_event}/quotation/{id_quotation}")
    fun updateExpressQuote(
        @Path("id_service_event") id_service_event: String,
        @Path("id_quotation") id_quotation: String,
        @Body request: ItemEditQuoteRequest
    ): Call<Void>

    @GET("service_event/{id_service_event}/quotation")
    fun requestQuotation(@Path("id_service_event") id_service_event: String): Call<Void>

    @GET("service_event/{id_service_event}/quotation/{id_quotation}/approval")
    fun requestEditQuote(
        @Path("id_service_event") id_service_event: String,
        @Path("id_quotation") id_quotation: String
    ): Call<Void>

    @POST("service_event/{id_service_event}/quotation/{id_quotation}/approval/{id_message}/")
    fun approveRequestEditQuote(
        @Path("id_service_event") id_service_event: String,
        @Path("id_quotation") id_quotation: String,
        @Path("id_message") id_message: String,
        @Query("is_approved") is_approved: Boolean,
    ): Call<Void>

    @POST("service_event/{id_service_event}/quotation/{id_quotation}/decline/{id_message}")
    fun declineRequestEditQuote(
        @Path("id_service_event") id_service_event: String,
        @Path("id_quotation") id_quotation: String,
        @Path("id_message") id_message: String,
    ): Call<Void>

    @PUT("promotion/{promo_id}")
    fun editPromotion(
        @Path("promo_id") promo_id: String,
        @Body request: EditPromoRequest
    ): Call<Void>

    @POST("promotion")
    fun createNewPromotion(@Body request: PromoRequest): Call<Void>

    @DELETE("promotion/{id_promotion}")
    fun deletePromotion(@Path("id_promotion") id_promotion: String): Call<Void>


    // ======= NEW METHODS FOR V2 API ========= //

    @POST("Attributes/GetAllWithFilters")
    suspend fun getAttributesByQueryV2(
        @Body request: FilterRequest
    ): Response<ResponseAttributesV2>

    @GET("Attributes/{attribute_id}")
    suspend fun getAttributeByIdV2(
        @Path("attribute_id") attribute_id: String
    ): Response<ResponseAttributeV2>

    @GET("ServiceCategories")
    suspend fun getAllServicesCategoriesV2(): Response<ResponseServicesCategoriesV2>

    @POST("ServiceTypes/GetAllWithFilters")
    suspend fun getServicesTypesByCategoryIdV2(
        @Body request: FilterRequest
    ): Response<ResponseServicesTypesV2>

    @POST("SubserviceTypes/GetAllWithFilters")
    suspend fun getSubServicesByServiceTypeIdV2(
        @Body request: FilterRequest
    ): Response<ResponseSubServicesV2>

    @POST("Services/GetAllWithFilters")
    suspend fun getServicesOptionsByQueryV2(
        @Body request: FilterRequest
    ): Response<ResponseServicesV2>

    @GET("ServiceCategories/{service_category_id}")
    suspend fun getServicesCategoryByIdV2(
        @Path("service_category_id") service_category_id: String
    ): Response<ResponseServiceCategoryV2>

    @POST("ServiceCategories/GetAllWithFilters")
    suspend fun getServicesCategoriesByQueryV2(
        @Body request: FilterRequest
    ): Response<ResponseServicesCategoriesV2>

    @GET("Services/{service_id}")
    suspend fun getServiceByIdV2(
        @Path("service_id") service_id: String
    ): Response<ResponseServiceV2>

    @PUT("Users/{userId}/like-service/{serviceId}")
    suspend fun likeServiceV2(
        @Path("userId") userId: String,
        @Path("serviceId") serviceId: String
    ): Response<StatusResponseV2>

    @POST("Services")
    suspend fun createServiceV2(
        @Body request: EntityDataRequest
    ): Response<StatusResponseV2>

    @POST("RequestedAttributes")
    fun addSuggestedAttributes(
        @Body request: EntityDataRequest
    ): Response<StatusResponseV2>

    @DELETE("Services/{serviceId}")
    suspend fun deleteServiceV2(
        @Path("serviceId") serviceId: String
    ): Response<StatusResponseV2>

    @PUT("Services/{id_service}")
    suspend fun enableOrDisableServiceV2(
        @Path("id_service") id_service: String,
        @Body request: EntityDataRequest
    ): Response<StatusResponseV2>

    @PUT("Services/{id_service}")
    suspend fun updateServiceV2(
        @Path("id_service") id_service: String,
        @Body request: EntityDataRequest
    ): Response<StatusResponseV2>

    @POST("Quotations")
    suspend fun createNewQuoteV2(
        @Body request: EntityDataRequest
    ): Response<StatusResponseV2>

    @POST("Quotations/{id_quotation}/bid")
    fun acceptOrDeclineOfferV3(
        @Path("id_quotation") id_quotation: String,
        @Body request: EntityDataRequest
    ): Call<Void>

    @PUT("ServiceEvents/{id_service_event}")
    suspend fun updateServiceStatusV2(
        @Path("id_service_event") id_service_event: String,
        @Body request: EntityDataRequest
    ): Response<StatusResponseV2>

    @PUT("Quotations/{id_quote}")
    suspend fun editQuoteV2(
        @Path("id_quote") id_quote: String,
        @Body request: EntityDataRequest
    ): Response<StatusResponseV2>

    @PUT("Quotations/{id_quote}")
    suspend fun editQuoteNotesV2(
        @Path("id_quote") id_quote: String,
        @Body request: EntityDataRequest
    ): Response<StatusResponseV2>

    @POST("Promotions")
    suspend fun createNewPromotionV2(
        @Body request: EntityDataRequest
    ): Response<StatusResponseV2>

    @PUT("Promotions/{id_promo}")
    suspend fun editPromotionV2(
        @Path("id_promo") id_promo: String,
        @Body request: EntityDataRequest
    ): Response<StatusResponseV2>

    @DELETE("Promotions/{id_promotion}")
    suspend fun deletePromotionV2(
        @Path("id_promotion") id_promotion: String
    ): Response<StatusResponseV2>

    @POST("Promotions/GetAllWithFilters")
    suspend fun getPromotionsByQueryV2(
        @Body request: FilterRequest
    ): Response<ResponsePromotionsV2>

    @POST("Messages")
    suspend fun requestQuoteFromClientToProviderV2(
        @Body request: EntityDataRequest
    ): Response<StatusResponseV2>

    @POST("Messages")
    suspend fun requestEditQuoteFromProviderToClientV2(
        @Body request: EntityDataRequest
    ): Response<StatusResponseV2>

    @POST("Quotations/quotationEditionMessage/{quotationId}/{messageId}?action=approve")
    suspend fun acceptRequestEditQuoteV2(
        @Path("quotationId") quotationId: String,
        @Path("messageId") messageId: String,
        @Body request: EntityDataRequest
    ): Response<StatusResponseV2>

    @POST("Quotations/quotationEditionMessage/{quotationId}/{messageId}?action=reject")
    suspend fun declineRequestEditQuoteV2(
        @Path("quotationId") quotationId: String,
        @Path("messageId") messageId: String,
        @Body request: EntityDataRequest
    ): Response<StatusResponseV2>
}
