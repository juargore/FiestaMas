package com.universal.fiestamas.domain.models.response

import android.os.Parcelable
import com.universal.fiestamas.domain.models.BidForQuote
import com.universal.fiestamas.domain.models.FirebaseModel
import com.universal.fiestamas.domain.models.request.ItemQuoteRequest
import kotlinx.parcelize.Parcelize

data class CreateQuoteResponse(
    val id: String,
    val notes: String,
    val id_service_event: String,
    val noteBook: String
)

@Parcelize
data class GetQuoteResponse(
    override var id: String,
    val deposit: Int,
    val elements: List<ItemQuoteRequest>,
    val bids: MutableList<BidForQuote>,
    val id_service_event: String,
    val noteBook_client: String?,
    val noteBook_provider: String?,
    val notes: String,
    val type: String = "",
    val allow_edit: Boolean = true,
): FirebaseModel, Parcelable {
    constructor(): this(
        id = "",
        deposit = 0,
        elements = listOf(),
        bids = mutableListOf(),
        id_service_event = "",
        noteBook_client ="",
        noteBook_provider = "",
        notes = "",
        type = "CLASSIC", //EXPRESS,
        allow_edit = true
    )
}
