package com.universal.fiestamas.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attribute(
    override var id: String,
    val name: String,
    val description: String,
    val icon: String,
    var isSelected: Boolean = false
): FirebaseModel, Parcelable {
    constructor() : this(
        id = "",
        name = "",
        description = "",
        icon = "",
        isSelected = false
    )
}

@Parcelize
data class ResponseAttributesV2(
    val status: Int,
    val data: List<AttributeV2>
): Parcelable

@Parcelize
data class ResponseAttributeV2(
    val status: Int,
    val data: AttributeV2
): Parcelable


@Parcelize
data class AttributeV2(
    val id: String,
    val id_sub_service_type: String?,
    val name: String?,
    val icon: String?,
    val id_service_category: String?,
    val id_service_type: String?,
    val creation_date: String?
): Parcelable {
    fun toAttribute() = Attribute (
        id = this.id,
        description = "",
        name = this.name.orEmpty(),
        icon = this.icon.orEmpty()
    )
}


//        "name": "Requested attribute name",
//        "provider": "Fernando Arévalo",
//        "email": "example@email.com",
//        "name_service_category": "Test",
//        "name_service_type": "Test",
//        "name_sub_service_type": "Test"

data class SuggestedAttribute(
    val name: String,
    val provider: String,
    val email: String,
    val name_service_category: String,
    val name_service_type: String,
    val name_sub_service_type: String
)

// AttributeV2 response example
/*
"id": "01zfzPIBzVbz4RK9nOqL",
"id_sub_service_type": "zjGMapcENK2aOglo1ub2",
"name": "Mobiliario y mantelería",
"icon": "https://firebasestorage.googleapis.com/v0/b/fiestaki-1.appspot.com/o/test%2FHacienda.jpeg?alt=media&token=bea9279c-99e6-4384-86c1-632f88282bd8",
"id_service_category": null,
"id_service_type": null,
"creation_date": "2024-02-17T19:04:41.123Z"
*/
