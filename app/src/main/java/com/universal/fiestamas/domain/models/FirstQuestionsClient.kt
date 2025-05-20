package com.universal.fiestamas.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirstQuestionsClient(
    val festejadosNames: String,
    val date: String,
    val numberOfGuests: String,
    val city: String,
    val location: Location?,
    val event: Event?
) : Parcelable


@Parcelize
data class FirstQuestionsClientStored(
    val event: Event?,
    val festejadosNames: String,
    val date: String,
    val time: String,
    val numberOfGuests: String,
    var city: String,
    var location: Location?
) : Parcelable


@Parcelize
data class FirstQuestionsProvider(
    val contactName: String,
    val phone: String,
    val email: String,
    val date: String,
    val city: String,
    val location: Location?
) : Parcelable

@Parcelize
data class FirstQuestionsProviderStored(
    val contactName: String,
    val phone: String,
    val email: String,
    val date: String,
    val time: String,
    var city: String,
    var location: Location?
) : Parcelable