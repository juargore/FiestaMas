package com.universal.fiestamas

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FiestamasApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // places initialization for Google Autocomplete
        Places.initialize(applicationContext, BuildConfig.TOKEN_GOOGLE_PLACE)
    }
}
