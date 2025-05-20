package com.universal.fiestamas.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GoogleUserData(
    val uid: String?,
    val userName: String?,
    val userLastName: String?,
): Parcelable
