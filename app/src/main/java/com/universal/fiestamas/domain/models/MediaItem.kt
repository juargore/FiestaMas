package com.universal.fiestamas.domain.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaItemService(
    val url: String,
    val isVideo: Boolean,
    val thumbnail: Bitmap? = null
): Parcelable

@Parcelize
data class ListMediaItemService(
    val list: List<MediaItemService>
): Parcelable
