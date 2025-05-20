package com.universal.fiestamas.domain.models.request

data class UserRequest(
    val role: String,
    val name: String,
    val last_name: String,
    val phone_one: String,
    val phone_two: String,
    val email: String,
    val password: String
)

data class GoogleUserRequest(
    val role: String,
    val name: String,
    val last_name: String,
    val phone_one: String,
    val phone_two: String
)

data class UserRequestEdit(
    val name: String,
    val email: String,
    val phone_one: String,
    val phone_two: String,
    val last_name: String,
    var photo: String
)
