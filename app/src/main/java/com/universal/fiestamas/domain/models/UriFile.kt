package com.universal.fiestamas.domain.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UriFile(
    val uri: Uri,
    val fileName: String,
    val url: String? = null
) : Parcelable
