@file:Suppress("PropertyName")

package com.universal.fiestamas.domain.models.request

import com.google.firebase.Timestamp
import com.universal.fiestamas.domain.models.LocalTimestamp
import com.universal.fiestamas.domain.models.UriFile

data class PromoRequest(
    val name: String,
    val start_date: String,
    val end_date: String,
    var images: List<String?>,
    val id_provider: String
)

data class EditPromoRequest(
    val id: String,
    val name: String,
    val start_date: String,
    val end_date: String,
    var images: List<String>
)

data class ItemEditPromo(
    val id: String,
    val name: String,
    val startDate: LocalTimestamp?,
    val endDate: LocalTimestamp?,
    var images: List<UriFile>
)
