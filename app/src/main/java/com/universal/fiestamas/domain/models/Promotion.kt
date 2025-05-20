@file:Suppress("PropertyName")

package com.universal.fiestamas.domain.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class LocalTimestamp(
    val _seconds: Long,
    val _nanoseconds: Int
): Parcelable {
    fun toDate(): Date {
        return Date(_seconds * 1000 + _nanoseconds / 1_000_000)
    }
}


@Parcelize
data class Promotion(
    override var id: String,
    val business_name: String,
    val email_provider: String,
    val end_date: LocalTimestamp?,
    val id_provider: String,
    val images: List<String>,
    val is_active: Boolean,
    val name: String,
    val name_provider: String,
    val phone_provider: String,
    val start_date: LocalTimestamp?
) : FirebaseModel, Parcelable {
    constructor(): this(
        id = "",
        business_name = "",
        email_provider = "",
        end_date = null,
        id_provider = "",
        images = listOf(),
        is_active = false,
        name = "",
        name_provider = "",
        phone_provider = "",
        start_date = null
    )
}

@Parcelize
data class ResponsePromotionsV2(
    val status: Int,
    val data: List<Promotion>
): Parcelable