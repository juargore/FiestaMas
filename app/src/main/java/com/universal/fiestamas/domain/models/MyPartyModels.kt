@file:Suppress("unused", "PropertyName")

package com.universal.fiestamas.domain.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.universal.fiestamas.domain.models.request.QuoteV2
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyPartyEventWithServices(
    val event: MyPartyEvent?,
    var servicesEvents: List<MyPartyService>?
): Parcelable {
    constructor() : this(null, listOf())
}


@Parcelize
data class MyPartyEvent(
    override var id: String,
    val attendees: String,
    val color_hex: String,
    val date: Timestamp?,
    val id_client: String,
    val id_event_type: String,
    val location: String,
    val lat: String?,
    val lng: String?,
    val name: String,
    val name_event_type: String,
    val progress_event: Int? = null,
    var finalCost: Int? = null,
    var pendingDays: Int? = null,
    var image: String? = null
): FirebaseModel, Parcelable {

    constructor() : this(
        id = "",
        attendees = "0",
        color_hex = "",
        date = null,
        id_client = "",
        id_event_type = "",
        location = "",
        lat = "0.0",
        lng = "0.0",
        name = "",
        name_event_type = "",
        progress_event = 0,
        finalCost = 0,
        pendingDays = 0,
        image = ""
    )
    constructor(id: String) : this(
        id = id,
        attendees = "0",
        color_hex = "",
        date = null,
        id_client = "",
        id_event_type = "",
        location = "",
        lat = "0.0",
        lng = "0.0",
        name = "",
        name_event_type = "",
        progress_event = 0,
        finalCost = 0,
        pendingDays = 0,
        image = ""
    )
    fun toEvent() = Event(
        id = id_event_type,
        clientEventId = id,
        name = name_event_type,
        image = image.orEmpty(),
        description = "",
        icon = "",
        pendingDays = pendingDays,
        index = 0,
        text_position = "",
        video = ""
    )
}

@Parcelize
data class MyPartyService(
    override var id: String,
    val address: String,
    var date: Timestamp?,
    val description: String,
    var hex_color: String? = null,
    val id_client: String,
    val client_contact_name: String,
    val id_client_event: String,
    val id_provider: String,
    val id_service_category: String,
    val id_service: String? = "",
    val image: String,
    val name: String, // Hacienda La Joya
    val price: Int,
    val provider_contact_email: String,
    val provider_contact_name: String,
    val provider_contact_phone: String,
    val rating: Int,
    val service_category_name: String? = null,
    val status: String,
    var serviceStatus: ServiceStatus? = null, // get from status in use case
    val event_data: EventData? = null
): FirebaseModel, Parcelable {
    constructor(): this(
        id = "",
        address = "",
        date = null,
        description = "",
        id_client = "",
        client_contact_name = "",
        id_client_event = "",
        id_provider = "",
        id_service_category = "",
        id_service = "",
        image = "",
        name = "",
        price = 0,
        provider_contact_email = "",
        provider_contact_name = "",
        provider_contact_phone = "",
        rating = 0,
        service_category_name = "",
        status = "",
        serviceStatus = ServiceStatus.Hired,
        event_data = EventData(
            attendees = "0",
            color_hex = "",
            date = Timestamp.now(),
            id_client = "",
            id_event_type = "",
            image = "",
            lat = "0.0",
            lng = "0.0",
            location = "",
            name = "",
            name_event_type = ""
        )
    )
}

@Parcelize
data class EventData(
    val attendees: String,
    val color_hex: String,
    val date: Timestamp?,
    val id_client: String,
    val id_event_type: String,
    val image: String,
    val lat: String? = null,
    val lng: String? = null,
    val location: String,
    val name: String,
    val name_event_type: String
): Parcelable {
    constructor(): this (
        attendees = "0",
        color_hex = "",
        date = null,
        id_client = "",
        id_event_type = "",
        image = "",
        lat = "0.0",
        lng = "0.0",
        location = "",
        name = "",
        name_event_type = ""
    )
}

enum class ServiceStatus {
    All,
    Hired,
    Pending,
    Canceled,
    DateAsc,
    DateDsc
}


@Parcelize
data class ResponseMyPartyServiceV2(
    val status: Int,
    val data: MyPartyServiceV2
): Parcelable

@Parcelize
data class ResponseMyPartyServicesV2(
    val status: Int,
    val data: List<MyPartyServiceV2>
): Parcelable

data class MyPartyServiceStatusV2(
    val status: String
)


@Parcelize
data class MyPartyServiceV2(
    val id: String,
    val date: Timestamp?,
    val provider_contact_name: String?,
    val client_contact_name: String?,
    val rating: Int?,
    val description: String?,
    val event_data: EventDataV2?,
    val id_client_event: String?,
    val id_provider: String?,
    val photo_provider: String?,
    val price: Int?,
    val client_contact_phone: String?,
    val photo_client: String?,
    val provider_contact_phone: String?,
    val lat: String?,
    val image: String?,
    val client_contact_email: String?,
    val address: String?,
    val lng: String?,
    val id_client: String?,
    val provider_contact_email: String?,
    val service_category_name: String?,
    val name: String?, // Hot-Dogs Patty2
    val id_service_category: String?,
    val status: String,
    val id_service: String?,
    val creation_date: String?
): Parcelable {
    fun toMyPartyService() = MyPartyService(
        id = this.id,
        address = this.address.orEmpty(),
        date = this.date,
        description = this.description.orEmpty(),
        id_client = this.id_client.orEmpty(),
        client_contact_name = this.client_contact_name.orEmpty(),
        id_client_event = this.id_client_event.orEmpty(),
        id_provider = this.id_provider.orEmpty(),
        id_service_category = this.id_service_category.orEmpty(),
        id_service = this.id_service,
        image = this.image.orEmpty(),
        name = this.name.orEmpty(),
        price = this.price ?: 0,
        provider_contact_email = this.provider_contact_email.orEmpty(),
        provider_contact_name = this.provider_contact_name.orEmpty(),
        provider_contact_phone = this.provider_contact_phone.orEmpty(),
        rating = this.rating ?: 0,
        service_category_name = this.service_category_name,
        status = this.status,
        serviceStatus = null,
        event_data = this.event_data?.toEventData()
    )
}

@Parcelize
data class EventDataV2(
    val id: String?,
    val date: Timestamp?,
    val image: String?,
    val contact_name: String?,
    val lng: String?,
    val contact_phone: String?,
    val event_thumbnail: String?,
    val id_client: String?,
    val color_hex: String?,
    val name_event_type: String?,
    val creation_date: Timestamp?,
    val tags: List<String>?,
    val id_event_type: String?,
    val contact_email: String?,
    val background: String?,
    val name: String?,
    val location: String?,
    val lat: String?,
    val progress_event: Int?,
    val attendees: String?
): Parcelable {
    fun toEventData() = EventData(
        attendees = this.attendees ?: "0",
        color_hex = this.color_hex.orEmpty(),
        date = this.date,
        id_client = this.id_client.orEmpty(),
        id_event_type = this.id_event_type.orEmpty(),
        image = this.image.orEmpty(),
        lat = this.lat ?: "0.0",
        lng = this.lng ?: "0.0",
        location = this.location.orEmpty(),
        name = this.name.orEmpty(),
        name_event_type = this.name_event_type.orEmpty()
    )
}

//              "event_data": {
//                "tags": [],
//                "id_event_type": "nXMbs5YqTTrMDlNpMsVI",
//                "contact_email": "prepa.2001@gmail.com",
//                "gift_registry": null,
//                "invitation_styles": {
//                    "event_location": {
//                        "font_color": "#000000",
//                        "font_size": "16px",
//                        "format": "shortFormat"
//                    },
//                    "event_date": {
//                        "font_color": "#000000",
//                        "font_size": "16px",
//                        "format": "shortFormat"
//                    },
//                    "event_description": {
//                        "font_color": "#000000",
//                        "font_size": "16px",
//                        "format": "shortFormat"
//                    },
//                    "event_name": {
//                        "font_color": "#000000",
//                        "font_size": "16px",
//                        "format": "shortFormat"
//                    }
//                },
//                "background": "https://fireb...",
//            },