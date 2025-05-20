package com.universal.fiestamas.domain.models.request

import com.universal.fiestamas.domain.models.FirebaseModel

data class AndroidBuildVersion(
    override var id: String,
    val android_build_number: Int
) : FirebaseModel {
    constructor(): this(
        id = "",
        android_build_number = 0
    )
}

data class SsidCredentials(
    override var id: String,
    val ssid: String,
    val password: String,
) : FirebaseModel {
    constructor(): this(
        id = "",
        ssid = "",
        password = ""
    )
}
