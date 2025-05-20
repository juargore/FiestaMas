package com.universal.fiestamas.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubService(
    override var id: String,
    val id_service_type: String,
    val name: String,
    val image: String,
    val description: String,
    val icon: String
) : FirebaseModel, Parcelable {
    constructor(): this(
        id = "",
        id_service_type = "",
        name = "",
        image = "",
        description = "",
        icon = ""
    )
    constructor(id: String): this(
        id = id,
        id_service_type = "",
        name = "",
        image = "",
        description = "",
        icon = ""
    )
}

@Parcelize
data class ResponseSubServicesV2(
    val status: Int,
    val data: List<SubServiceV2>
): Parcelable

@Parcelize
data class SubServiceV2(
    val id: String,
    val image: String?,
    val name: String?,
    val icon: String?,
    val id_service_type: String?,
    val creation_date: String?
): Parcelable {

    fun toSubService() = SubService(
        id = this.id,
        id_service_type = this.id_service_type.orEmpty(),
        name = this.name.orEmpty(),
        image = this.image.orEmpty(),
        description = "",
        icon = this.icon.orEmpty()
    )
}

/*
"id": "1AU3dZEM3Twty7kIEjhE",
"image": "https://firebasestorage.googleapis.com/v0/b/fiestaki-1.appspot.com/o/test%2F1701966227360_hamburguesa_after.png?alt=media&token=f30eb90c-a029-4528-a76a-e43a9697dc54",
"name": "Hamburguesas y/o hot dogs",
"icon": "https://firebasestorage.googleapis.com/v0/b/fiestaki-1.appspot.com/o/test%2F1703171525377_hamb_botanas.png?alt=media&token=ec1fc7ae-0d11-444f-8cb4-a9f4dbbd75e6",
"id_service_type": "Ge4w3Eih3aDDZ74KBxtn",
"creation_date": "2024-02-17T19:04:29.449Z"
*/