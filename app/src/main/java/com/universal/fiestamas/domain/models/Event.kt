package com.universal.fiestamas.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    override var id: String,
    var clientEventId: String,
    val name: String,
    val image: String,
    val description: String?,
    val icon: String,
    var pendingDays: Int? = null,
    val index: Int,
    val text_position: String,
    val video: String
) : FirebaseModel, Parcelable {
    constructor() : this(
        id = "",
        clientEventId = "",
        name = "",
        image = "",
        description = "",
        icon = "",
        pendingDays = 0,
        index = 0,
        text_position = "",
        video = ""
    )
    constructor(id: String) : this(
        id = id,
        clientEventId = "",
        name = if (id.isBlank()) "Editar Servicio" else "Nuevo Servicio",
        image = "",
        description = "",
        icon = "",
        pendingDays = 0,
        index = 0,
        text_position = "",
        video = ""
    )
}

@Parcelize
data class ResponseEventTypeV2(
    val status: Int,
    val data: EventV2
): Parcelable

@Parcelize
data class ResponseEventsTypesV2(
    val status: Int,
    val data: List<EventV2>
): Parcelable

@Parcelize
data class EventV2(
    val id: String,
    val image: String?,
    val text_position: String?,
    val active_shadow: Boolean?,
    val background: String?,
    val translation_distance: String?,
    val name: String?,
    val icon: String?,
    val index: Int?,
    val description: String?,
    val video: String?,
    val creation_date: String?
): Parcelable {

    fun toEvent() = Event(
        id = this.id,
        clientEventId = "",
        name = this.name.orEmpty(),
        image = this.image.orEmpty(),
        description = this.description,
        icon = this.icon.orEmpty(),
        pendingDays = 0,
        index = this.index ?: 0,
        text_position = this.text_position.orEmpty(),
        video = this.video.orEmpty()
    )
}

/*
"id": "I8upnIRM4JSBEP8VEU1S",
"image": "https://firebasestorage.googleapis.com/v0/b/fiestaki-1.appspot.com/o/test%2F1700157003511_baby.png?alt=media&token=41b2a0a6-0be5-4c25-9b3e-9bee67579a8b",
"text_position": "top",
"active_shadow": false,
"background": "https://firebasestorage.googleapis.com/v0/b/fiestaki-1.appspot.com/o/event_types%2Fbackgrounds%2FBTN_BABYSHOWER.png?alt=media&token=6b1cdc2c-076f-4cf2-be4a-7308180b07f1",
"translation_distance": "5",
"name": "Baby Shower",
"icon": "https://firebasestorage.googleapis.com/v0/b/fiestaki-1.appspot.com/o/test%2F1701706182951_baby.png?alt=media&token=775ff202-f7fb-4526-8361-e1c4f454341c",
"index": 1,
"description": "Celebremos su pronta llegada",
"video": "https://firebasestorage.googleapis.com/v0/b/fiestaki-1.appspot.com/o/event_types%2Fvideos%2FBABYSHOWER.mp4?alt=media&token=424ee65a-304b-41b3-935d-fa8772912083&_gl=1*1bf0tj*_ga*OTA0MDgzNjM5LjE2OTgzMzY3MTU.*_ga_CW55HF8NVT*MTY5OTAzMTYzNi4xNDMuMS4xNjk5MDMzOTQ4LjMxLjAuMA..",
"creation_date": "2024-02-17T19:04:17.923Z"
*/