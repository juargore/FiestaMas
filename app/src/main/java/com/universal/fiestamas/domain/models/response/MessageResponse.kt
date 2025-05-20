package com.universal.fiestamas.domain.models.response

import android.os.Parcelable
import com.universal.fiestamas.domain.models.FirebaseModel
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationDb(
    override var id: String,
    val content: String, // message
    val id_client_event: String,
    val id_receiver: String,
    val id_sender: String,
    val id_service: String,
    val id_service_event: String,
    val name_receiver: String,
    val name_sender: String,
    val photo: String,
    val photo_receiver: String?,
    val photo_sender: String?,
    val read: Boolean,
    val received: Boolean,
    val sent: Boolean,
    val timestamp: Timestamp?,
    @field:JvmField val is_approved: Boolean? = null,
    val type: String
) : FirebaseModel, Parcelable {
    constructor(): this(
        id = "",
        content = "",
        id_client_event = "",
        id_receiver = "",
        id_sender = "",
        id_service = "",
        id_service_event = "",
        name_receiver = "",
        name_sender = "",
        photo = "",
        photo_receiver = "",
        photo_sender = "",
        read = false,
        received = false,
        sent = false,
        timestamp = null,
        is_approved = null,
        type = ""
    )
}