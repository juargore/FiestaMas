package com.universal.fiestamas.domain.models

import android.os.Parcelable
import com.universal.fiestamas.domain.models.request.ItemQuoteRequest
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParentQuote(
    val title: String,
    val products: List<QuoteProductsInformation>,
    val bids: List<BidForQuote>,
    var noteBook: String,
    var eventCost: String,
    var advancePayment: String,
    var status: String
): Parcelable {
    constructor() : this(
        title = "",
        products = listOf(),
        bids = listOf(),
        noteBook = "",
        eventCost = "",
        advancePayment = "",
        status = ""
    )
}

@Parcelize
data class QuoteProductsInformation(
    val quantity: String,
    val description: String,
    val price: String,
    val subtotal: String
): Parcelable {
    constructor() : this(
        quantity = "",
        description = "",
        price = "",
        subtotal = ""
    )
    fun toItemQuoteRequest() = ItemQuoteRequest(
        qty = quantity.toInt(),
        description = description,
        price = price.toInt(),
        subTotal = (quantity.toInt() * price.toInt())
    )
}

@Parcelize
data class BidForQuote(
    var bid: Int,
    val id_user: String,
    val status: String,
    val user_role: String,
    val isTemp: Boolean = false
): Parcelable {
    constructor() : this(
        bid = 0,
        id_user = "",
        status = "",
        user_role = ""
    )
}
