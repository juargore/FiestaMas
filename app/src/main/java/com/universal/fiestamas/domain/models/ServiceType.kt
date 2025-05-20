package com.universal.fiestamas.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceType(
    override var id: String,
    val name: String,
    val image: String,
    val description: String,
    val icon: String,
    val id_service_category: String,
    var hasSubServices: Boolean = false
) : FirebaseModel, Parcelable {
    constructor() : this(
        id = "",
        name = "",
        image = "",
        description = "",
        icon = "",
        id_service_category = ""
    )
    constructor(id: String) : this(
        id = id,
        name = "",
        image = "",
        description = "",
        icon = "",
        id_service_category = ""
    )
    constructor(id: String, name: String) : this(
        id = id,
        name = name,
        image = "",
        description = "",
        icon = "",
        id_service_category = ""
    )
}

@Parcelize
data class ResponseServicesTypesV2(
    val status: Int,
    val data: List<ServiceTypeV2>
): Parcelable


@Parcelize
data class ServiceTypeV2(
    val id: String,
    val image: String?,
    val text_position: String?,
    val translation_distance: String?,
    val name: String?,
    val icon: String?,
    val description: String?,
    val id_service_category: String?,
    val creation_date: String?
): Parcelable {

    fun toServiceType() = ServiceType(
        id = this.id,
        name = this.name.orEmpty(),
        image = this.image.orEmpty(),
        description = this.description.orEmpty(),
        icon = this.icon.orEmpty(),
        id_service_category = this.id_service_category.orEmpty()
    )
}

/*
"id": "1ByHv5WUgaihGo39uGWE",
"image": "https://firebasestorage.googleapis.com/v0/b/fiestaki-1.appspot.com/o/test%2F1701966040392_postres.png?alt=media&token=1c6150fd-617b-4ab4-b0d6-228103e964da",
"text_position": "bottom",
"translation_distance": "1",
"name": "Postres / pasteles",
"icon": "https://firebasestorage.googleapis.com/v0/b/fiestaki-1.appspot.com/o/test%2F1702923847536_postres.png?alt=media&token=a7d2440f-464c-4516-96ee-adde531c053e",
"description": "Una delicia que te acompaña",
"id_service_category": "4edPHSmYUxrr1xayIBj6",
"creation_date": "2024-02-17T19:04:43.742Z"
*/