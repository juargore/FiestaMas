package com.universal.fiestamas.domain.models

import android.os.Parcelable
import android.text.TextUtils
import com.universal.fiestamas.domain.models.request.CreateEventRequest
import kotlinx.parcelize.Parcelize

@Suppress("PropertyName")
@Parcelize
data class Service(
    override var id: String,
    val active: Boolean? = true,
    val address: String,
    val attributes: List<String>? = listOf(),
    val description: String,
    val distance: Int?,
    val icon: String,
    val id_provider: String,
    val id_service_category: String,
    val id_service_type: String? = "",
    val id_sub_service_type: String? = "",
    val image: String?,
    val images: List<String>? = listOf(),
    val lat: String?,
    var lng: String?,
    val max_attendees: Int,
    val min_attendees: Int,
    val name: String,
    val name_service_category: String? = "",
    val name_service_type: String? = "",
    val name_sub_service_type: String? = "",
    val price: Int,
    val provider_name: String,
    val rating: Int,
    val unit: String?,
    val videos: List<String>? = listOf(),
) : FirebaseModel, Parcelable {
    constructor() : this(
        id = "",
        active = true,
        address = "",
        attributes = listOf(),
        description = "",
        distance = null,
        icon = "",
        id_provider = "",
        id_service_category = "",
        id_service_type = "",
        id_sub_service_type = "",
        image = "",
        images = listOf(),
        lat = "0.0",
        lng = "0.0",
        max_attendees = 0,
        min_attendees = 0,
        name = "",
        price = 0,
        provider_name = "",
        rating = 0,
        unit = "",
        videos = listOf()
    )
    constructor(id: String, name: String) : this(
        id = id,
        active = true,
        address = "",
        attributes = listOf(),
        description = "",
        distance = null,
        icon = "",
        id_provider = "",
        id_service_category = "",
        id_service_type = "",
        id_sub_service_type = "",
        image = "",
        images = listOf(),
        lat = "0.0",
        lng = "0.0",
        max_attendees = 0,
        min_attendees = 0,
        name = name,
        price = 0,
        provider_name = "",
        rating = 0,
        unit = "",
        videos = listOf()
    )
}

fun Service?.isShared(): Boolean {
    return this?.name?.let { TextUtils.equals(it, "shared") } ?: false
}

@Parcelize
data class ResponseServiceV2(
    val status: Int,
    val data: ServiceV2
): Parcelable

@Parcelize
data class ResponseServicesV2(
    val status: Int,
    val data: List<ServiceV2>
): Parcelable

@Parcelize
data class ServiceV2(
    val id: String,
    val min_attendees: Int?,
    val icon: String?,
    val rating: Int?,
    val description: String?,
    val videos: List<String>?,
    val id_provider: String?,
    val price: Int?,
    val provider_name: String?,
    val lat: String?,
    val id_service_type: String?,
    val image: String?,
    val images: List<String>?,
    val name_service_category: String?,
    val address: String?,
    val lng: String?,
    val name_sub_service_type: String?,
    val unit: String?,
    val max_attendees: Int?,
    val id_sub_service_type: String?,
    val name: String?,
    val attributes: List<String>?,
    val id_service_category: String?,
    val name_service_type: String?,
    val is_deleted: Boolean?,
    val active: Boolean?,
    val creation_date: String?
): Parcelable {

    fun toService() = Service(
        id = this.id,
        active = this.active,
        address = this.address.orEmpty(),
        attributes = this.attributes,
        description = this.description.orEmpty(),
        distance = null,
        icon = this.icon.orEmpty(),
        id_provider = this.id_provider.orEmpty(),
        id_service_category = this.id_service_category.orEmpty(),
        id_service_type = this.id_service_type,
        id_sub_service_type = this.id_sub_service_type,
        image = this.image,
        images = this.images,
        lat = this.lat,
        lng = this.lng,
        max_attendees = this.max_attendees ?: 0,
        min_attendees = this.min_attendees ?: 0,
        name = this.name.orEmpty(),
        price = this.price ?: 0,
        provider_name = this.provider_name.orEmpty(),
        rating = this.rating ?: 0,
        unit = this.unit.orEmpty(),
        videos = this.videos
    )
}

@Parcelize
data class ActiveServiceV2(
    val active: Boolean
): Parcelable

@Parcelize
data class UpdateActiveServiceRequestV2(
    val entityData: ActiveServiceV2
): Parcelable
