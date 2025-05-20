package com.universal.fiestamas.domain.models.request

data class SubscribeRequest(
    val device_token: String,
    val uid: String
)

data class UpdateRequest(
    val device_token: String,
    val prev_device_token: String,
    val uid: String
)
