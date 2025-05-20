package com.universal.fiestamas.domain.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Guest(
    override var id: String,
    val created_at: Timestamp?,
    var email: String?,
    val id_client_event: String?,
    var name: String?,
    val num_table: Int?,
    var phone: String?,
    val status: String?,
    val tags: List<Tag>? = listOf(),
    val viewed: Viewed?
) : FirebaseModel, Parcelable {
    constructor() : this(
        id = "",
        created_at = null,
        email = "",
        id_client_event = "",
        name = "",
        num_table = 0,
        phone = "",
        status = "",
        tags = listOf(),
        viewed = null
    )
    constructor(name: String) : this(
        id = "",
        created_at = null,
        email = "",
        id_client_event = "",
        name = name,
        num_table = 0,
        phone = "",
        status = "",
        tags = listOf(),
        viewed = null
    )
}

@Parcelize
data class Tag(
    val name: String
): Parcelable {
    constructor(): this("")
}

@Parcelize
data class Viewed(
    val date: Timestamp?,
    val viewed: Boolean
): Parcelable {
    constructor() : this(
        date = null,
        viewed = false
    )
}
