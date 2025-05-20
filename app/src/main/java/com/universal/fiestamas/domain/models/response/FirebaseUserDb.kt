@file:Suppress("PropertyName")
package com.universal.fiestamas.domain.models.response

import com.universal.fiestamas.domain.models.FirebaseModel

data class FirebaseUserDb(
    override var id: String = "",
    val address: String? = "",
    val business_name: String,
    val country: String,
    val role: String,
    val city: String,
    val facebook: String,
    val verified: Boolean,
    val last_name: String,
    val likes: List<String>? = listOf(),
    val lat: String? = "",
    val lng: String? = "",
    val instagram: String,
    val cp: String,
    val rfc: String,
    val tiktok: String,
    val name: String,
    val files: List<String>? = listOf(),
    val phone_one: String,
    val phone_two: String?,
    val photo: String?,
    val state: String,
    val events: List<String>? = listOf(),
    val email: String,
    val uid: String
) : FirebaseModel {
    constructor(): this(
        id = "",
        address = "",
        business_name = "",
        country = "",
        role = "Unauthenticated",
        city = "",
        facebook = "",
        verified = false,
        last_name = "",
        likes = listOf(),
        lat = "",
        lng = "",
        instagram = "",
        cp = "",
        rfc = "",
        tiktok = "",
        name = "",
        files = listOf(),
        phone_one = "",
        phone_two = "",
        photo = "",
        state = "",
        events = listOf(),
        email = "",
        uid = ""
    )
}
