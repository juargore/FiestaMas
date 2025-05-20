package com.universal.fiestamas.domain.models.request

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.universal.fiestamas.domain.models.QuoteProductsInformation
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemQuoteRequest(
    val qty: Int,
    val description: String,
    val price: Int,
    val subTotal: Int
): Parcelable {
    constructor(): this(
        qty = 0,
        description = "",
        price = 0,
        subTotal = 0
    )

    fun toQuoteProductsInformation() = QuoteProductsInformation(
        quantity = qty.toString(),
        description = description,
        price = price.toString(),
        subtotal = subTotal.toString()
    )
}

@Parcelize
data class ItemsQuoteRequest(
    val id_user: String,
    val elements: List<ItemQuoteRequest>
): Parcelable

@Parcelize
data class ItemsExpressQuoteRequest(
    val type: String
): Parcelable

@Parcelize
data class ItemBidOfferRequest(
    val bid: Int,
    val id_user: String
): Parcelable

@Parcelize
data class ItemBidAcceptedRequest(
    val status: String,
    val id_user: String
): Parcelable

@Parcelize
data class ItemBidAcceptOrRejectRequest(
    val title: String,
    val content: String,
    val status: String,
    val id_user: String,
    val bid: Int,
    val user_role: String = "client"
): Parcelable

@Parcelize
data class ItemUpdateStatusRequest(
    val status: String
): Parcelable

@Parcelize
data class ItemAddNotesToQuoteRequest(
    val notes: String,
    val deposit: Int,
    val id_user: String,
    val noteBook_client: String,
    val noteBook_provider: String,
    val elements: List<ItemQuoteRequest>
): Parcelable

@Parcelize
data class ItemEditQuoteRequest(
    val notes: String,
    val deposit: Int,
    val noteBook_client: String,
    val noteBook_provider: String,
    val id_user: String,
    val elements: List<ItemQuoteRequest>
): Parcelable

data class ClientNotesQuoteV2(
    val noteBook_client: String
)

data class ProviderNotesQuoteV2(
    val noteBook_provider: String
)

data class ImportantNotesQuoteV2(
    val notes: String
)

@Parcelize
data class QuoteV2(
    val type: String,
    val notes: String,
    val id_service_event: String,
    val title: String? = null,
    val elements: List<ItemQuoteRequest>
): Parcelable


data class RequestQuotation(
    val title: String, // push title
    val content: String,  // push body
    val id_client_event: String,
    val id_sender: String,
    val name_sender: String,
    val id_receiver: String,
    val name_receiver: String,
    val timestamp: Timestamp = Timestamp.now(),
    val id_service_event: String,
    val id_service: String,
    val type: String = "NOTIFICATION"
)

data class AcceptOrDeclineEditQuoteRequestV2(
    val id_event_type: String,
    //val name: String,
    val id_client: String,
    //val location: String,
    //val lat: String,
    //val lng: String,
    //val date: String
)