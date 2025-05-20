package com.universal.fiestamas.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.bumptech.glide.Glide
import com.universal.fiestamas.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.sqrt

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun uriToResizedBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null

        val width = (originalBitmap.width * 0.33).toInt()
        val height = (originalBitmap.height * 0.33).toInt()

        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)

        val compressedBitmap = BitmapFactory.decodeByteArray(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size())

        compressedBitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


fun bitmapToUri(context: Context, bitmap: Bitmap, originalUri: Uri): Uri? {
    var uri: Uri? = null
    try {
        val outputStream = context.contentResolver.openOutputStream(originalUri)
        uri = if (outputStream != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            originalUri
        } else {
            // If opening the output stream failed, create a new file and get a Uri for it.
            val file = File(context.cacheDir, "image.jpg")
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            Uri.fromFile(file)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return uri
}

fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

private fun getImageSizeFromBitmap(bitmap: Bitmap): Int {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray().size
}

fun getBitmapSize(bitmap: Bitmap): Int {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()

    return byteArray.size
}


fun scaleBitmapForMMS(context: Context, bitmap: Bitmap): Bitmap {
    val fileSize = getImageSizeFromBitmap(bitmap)
    if (fileSize > (1024 * 1024)) {
        val scale = sqrt((1024 * 1024).toDouble() / fileSize)
        return resizeBitmapWithGlide(context, bitmap, scale)
    }
    return bitmap
}

private fun resizeBitmapWithGlide(context: Context, bitmap: Bitmap, scale: Double): Bitmap {
    val targetWidth = (bitmap.width * scale).toInt()
    val targetHeight = (bitmap.height * scale).toInt()
    return Glide
        .with(context)
        .asBitmap()
        .load(bitmap)
        .override(targetWidth, targetHeight)
        .centerInside()
        .submit()
        .get()
}

suspend fun loadBitmapFromUrl(context: Context, url: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .build()
            val result = (loader.execute(request) as? SuccessResult)?.drawable
            val originalBitmap = (result as? BitmapDrawable)?.bitmap

            originalBitmap?.let {
                val scaledBitmap = Bitmap.createScaledBitmap(it, 80, 80, false)
                val pin = BitmapFactory.decodeResource(context.resources, R.drawable.ic_location_pin_png)
                val scaledPin = Bitmap.createScaledBitmap(pin, 250, 250, false)

                combineBitmapWithBackground(scaledPin, scaledBitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun combineBitmapWithBackground(pin: Bitmap, image: Bitmap): Bitmap {
    val pinBitmap = pin.copy(Bitmap.Config.ARGB_8888, true)
    val imageBitmap = image.copy(Bitmap.Config.ARGB_8888, true)

    val output = Bitmap.createBitmap(pinBitmap.width, pinBitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    canvas.drawBitmap(imageBitmap, 80f, 60f, null)
    canvas.drawBitmap(pinBitmap, 0f, 0f, null)

    return output
}
