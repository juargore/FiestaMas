package com.universal.fiestamas.presentation.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.universal.fiestamas.R
import java.io.File

class ComposeFileProvider : FileProvider(R.xml.filepaths) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory
            )

            val authority = context.packageName + ".fileprovider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }

        fun getImageFile(context: Context): File {
            val directory = File(context.cacheDir, "images")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory
            )
            return file
        }
    }
}
