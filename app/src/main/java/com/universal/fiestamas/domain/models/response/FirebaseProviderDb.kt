package com.universal.fiestamas.domain.models.response

data class FirebaseProviderDb(
    val business_name: String,
    val country: String,
    val role: String,
    val city: String,
    val verified: Boolean,
    val last_name: String,
    val services: List<String>,
    val cp: String,
    val rfc: String,
    val street: String,
    val name: String,
    val files: List<String>,
    val attributes: List<String>,
    val phone_one: String,
    val state: String,
    val email: String,
    val uid: String
) {
    constructor(): this(
        business_name = "",
        country = "",
        role = "",
        city = "",
        verified = false,
        last_name = "",
        services = listOf(),
        cp = "",
        rfc = "",
        street = "",
        name = "",
        files = listOf(),
        attributes = listOf(),
        phone_one = "",
        state = "",
        email = "",
        uid = ""
    )
}
