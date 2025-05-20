@file:Suppress("unused", "DEPRECATION")

package com.universal.fiestamas.presentation.utils.extensions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.provider.Settings
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.universal.fiestamas.presentation.MainActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

fun Context.resetApplication(delay: Long = 0L) {
    Handler(Looper.getMainLooper()).postDelayed({
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TASK
                or Intent.FLAG_ACTIVITY_NO_ANIMATION
        )

        startActivity(intent)
        (this as? MainActivity)?.finishAffinity()

        if (this is Activity) {
            overridePendingTransition(0, 0)
        }
    }, delay)
}

fun recordException(
    throwable: Throwable? = null,
    message: String? = null
) {
    val t = throwable ?: Throwable(message = message)
    FirebaseCrashlytics
        .getInstance()
        .recordException(Throwable(t))
}

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}

fun getIntentShareSheet(text: String): Intent? {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    return Intent.createChooser(sendIntent, null)
}

@SuppressLint("QueryPermissionsNeeded")
fun Context.openGoogleMaps(latitude: Double, longitude: Double) {
    val uri = Uri.parse("geo:$latitude,$longitude")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setPackage("com.google.android.apps.maps")

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        // Si Google Maps no está instalado, puedes mostrar un mensaje de error o abrir la versión web en su lugar.
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"))
        startActivity(webIntent)
    }
}

@Composable
fun SetBlackStatusBar() {
    AndroidView(factory = { context ->
        object : View(context) {
            init {
                val window = (context as? Activity)?.window
                if (window != null) {
                    WindowCompat.setDecorFitsSystemWindows(window, true)
                    window.decorView.systemUiVisibility =
                        SYSTEM_UI_FLAG_FULLSCREEN or
                                SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                                SYSTEM_UI_FLAG_LAYOUT_STABLE
                }
            }

            override fun onDetachedFromWindow() {
                super.onDetachedFromWindow()
                val window = (context as? Activity)?.window
                if (window != null) {
                    window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_VISIBLE
                    WindowCompat.setDecorFitsSystemWindows(window, true)
                }
            }
        }
    })
    DisposableEffect(Unit) { onDispose { } }
}

@Composable
fun AllowLandscapeOrientation() {
    AndroidView(factory = { context ->
        object : View(context) {
            init {
                (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
            }
            @SuppressLint("SourceLockedOrientationActivity")
            override fun onDetachedFromWindow() {
                super.onDetachedFromWindow()
                (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    })
    DisposableEffect(Unit) { onDispose { } }
}

fun <T> T?.or(default: T): T = this ?: default
fun <T> T?.or(compute: () -> T): T = this ?: compute()

inline fun <T> List<T>?.notEmptyLet(block: (List<T>) -> Unit) {
    if (!isNullOrEmpty()) {
        block(this)
    }
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun isRunningOnTablet(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp > 720 // Puedes ajustar este valor según tus necesidades
}

fun isRunningOnTablet(context: Context): Boolean {
    val screenLayout = context.resources.configuration.screenLayout
    val screenSize = screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
    return screenSize >= Configuration.SCREENLAYOUT_SIZE_LARGE
}

@SuppressLint("ObsoleteSdkInt")
fun isInternetAvailable(context: Context): Boolean {
    var result = false
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
    }
    return result
}


fun shouldShowRequestPermissionRationaleForCamera(context: Context): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(
        (context as Activity), Manifest.permission.CAMERA
    )
}

fun showPermissionDeniedDialogForCamera(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("Permiso de cámara requerido")
        .setMessage("La aplicación necesita acceso a la cámara. Por favor, habilita los permisos en la configuración.")
        .setPositiveButton("Ir a configuración") { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
        .setNegativeButton("Cancelar", null)
        .show()
}
