package com.universal.fiestamas.domain.models.request

data class UpdatePasswordRequest(
    val email: String,
    val newPassword: String
)

