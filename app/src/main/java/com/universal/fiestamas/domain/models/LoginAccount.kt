package com.universal.fiestamas.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginAccount(
    val email: String,
    val password: String
): Parcelable
