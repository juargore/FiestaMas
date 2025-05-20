package com.universal.fiestamas.domain.models.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StatusResponseV2(
    val status: Int
): Parcelable

@Parcelize
data class StatusAndDataResponseV2(
    val status: Int,
    val data: DataResponseV2? = null
): Parcelable

@Parcelize
data class DataResponseV2(
    val id: String
    //"device_tokens": [],
    //        "services": [],
    //        "files": [],
    //        "likes": [],
    //        "events": [],
    //        "email": "email123@example.com",
    //        "name": "Fernando",
    //        "address": "Salamanca 130",
    //        "last_name": "Arévalo Proveedor",
    //        "role": "provider",
    //        "business_name": "Empresa Fernando",
    //        "lat": "10.8",
    //        "lng": "1.8",
    //        "country": "Aguascalientes",
    //        "state": "Aguascalientes",
    //        "city": "México",
    //        "cp": "20210",
    //        "phone_one": "1231231231",
    //        "phone_two": null,
    //        "rfc": null,
    //        "verified": false,
    //        "facebook": null,
    //        "instagram": null,
    //        "tiktok": null,
    //        "tourProgress": {
    //            "serviceTourCompleted": false,
    //            "dashboardClientCompleted": false,
    //            "dashboardProviderCompleted": false
    //        },
    //        "photo": "https://firebasestorage.googleapis.com/v0/b/fiestaki-1.appspot.com/o/default%2Fimages%2Fuser.jpg?alt=media&token=37e51a2d-6ce9-40f0-b255-92a0607f1002",
    //        "uid": "44SHfrokRqeGL4CyYbtddRhqbT43"
): Parcelable