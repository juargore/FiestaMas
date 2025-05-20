package com.universal.fiestamas.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserReview(
    override var id: String,
    val name: String,
    val photo: String,
    val message: String
) : FirebaseModel, Parcelable {
    constructor(): this(
        id = "",
        name = "",
        photo = "",
        message = "",
    )
}
