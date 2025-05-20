package com.universal.fiestamas.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceCategory(
    override var id: String,
    var name: String,
    val image: String,
    val description: String,
    val icon: String,
    val events_types: List<String>,
): FirebaseModel, Parcelable {
    constructor(): this(
        id = "",
        name = "",
        image = "",
        description = "",
        icon = "",
        events_types = listOf()
    )
    constructor(id: String): this(
        id = id,
        name = "",
        image = "",
        description = "",
        icon = "",
        events_types = listOf()
    )
    constructor(id: String, name: String): this(
        id = id,
        name = name,
        image = "",
        description = "",
        icon = "",
        events_types = listOf()
    )
}

@Parcelize
data class ResponseServiceCategoryV2(
    val status: Int,
    val data: ServiceCategoryV2
): Parcelable

@Parcelize
data class ResponseServicesCategoriesV2(
    val status: Int,
    val data: List<ServiceCategoryV2>
): Parcelable

@Parcelize
data class ServiceCategoryV2(
    val id: String,
    val image: String?,
    val name: String?,
    val icon: String?,
    val index: Int?,
    val description: String?,
    val events_types: List<String>?,
    val creation_date: String?
): Parcelable {

    fun toServiceCategory() = ServiceCategory(
        id = this.id,
        name = this.name.orEmpty(),
        image = this.image.orEmpty(),
        description = this.description.orEmpty(),
        icon = this.icon.orEmpty(),
        events_types = this.events_types ?: listOf()
    )
}

/*
    "id": "4edPHSmYUxrr1xayIBj6",
    "image": "https://firebasestorage.googleapis.com/v0/b/fiestaki-1.appspot.com/o/test%2F1701966006269_portada_alimentos.png?alt=media&token=fb4e0919-9928-4e34-bbf1-37b048ab62ec",
    "name": "Alimentos",
    "icon": "https://firebasestorage.googleapis.com/v0/b/fiestaki-1.appspot.com/o/test%2F1701796908105_alimentos.png?alt=media&token=6fb7a8bf-c1ec-4776-8e0b-13c1b5163696",
    "index": 1,
    "description": "El Platillo ideal para tu Fiesta",
    "events_types": [
        "bUznELSNF9kOmsherguu",
        "q5t650zMRBK7vrolU1tw",
        "ySAPTXpwf6qKRMMf2qHY",
        "UoGnCJOOQMHCpItCGrIv"
    ],
    "creation_date": "2024-02-17T19:04:43.742Z"
*/