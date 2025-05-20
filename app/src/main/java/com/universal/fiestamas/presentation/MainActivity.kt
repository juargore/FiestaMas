package com.universal.fiestamas.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.os.bundleOf
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.universal.fiestamas.data.repositories.SharedPrefsRepositoryImpl
import com.universal.fiestamas.domain.usecases.SharedPrefsUseCase
import com.universal.fiestamas.presentation.navigation.RootNavigationGraph
import com.universal.fiestamas.presentation.theme.FiestakiTheme
import com.universal.fiestamas.presentation.utils.Constants.NOTIFICATION_CHAT
import com.universal.fiestamas.presentation.utils.Constants.NOTIFICATION_SCREEN
import com.universal.fiestamas.presentation.utils.Constants.NOTIFICATION_SERVICE_EVENT_ID
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //private lateinit var networkMonitor: NetworkMonitor
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @Inject
    lateinit var sharedPrefs: SharedPrefsUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        networkMonitor = NetworkMonitor(
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        )

        networkMonitor.startMonitoring()
        */

        handleNotificationClick(intent)
        FirebaseApp.initializeApp(this)

        sharedPrefs.setFirstTimeAppRunning(true)
        firebaseAnalytics = Firebase.analytics

        firebaseAnalytics.logEvent(
            FirebaseAnalytics.Event.APP_OPEN,
            bundleOf(FirebaseAnalytics.Param.DESTINATION to MainActivity::javaClass.name)
        )

        // check if link was shared via intent
        intent.data?.let { uri ->
            uri.lastPathSegment?.let { serviceId ->
                sharedPrefs.setServiceIdShared(serviceId)
            } ?: run {
                sharedPrefs.setServiceIdShared("")
            }
        } ?: run {
            sharedPrefs.setServiceIdShared("")
        }

        setContent {
            FiestakiTheme {
                RootNavigationGraph(navController = rememberNavController())
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleNotificationClick(intent)
    }

    private fun handleNotificationClick(intent: Intent?) {
        if (intent != null) {
            if (intent.hasExtra(NOTIFICATION_SCREEN) && intent.hasExtra(NOTIFICATION_SERVICE_EVENT_ID)) {
                val screen = intent.getStringExtra(NOTIFICATION_SCREEN)
                val serviceEventId = intent.getStringExtra(NOTIFICATION_SERVICE_EVENT_ID).orEmpty()
                if (screen == NOTIFICATION_CHAT && serviceEventId.isNotEmpty()) {
                    SharedPrefsRepositoryImpl(applicationContext).setServiceIdNotification(serviceEventId)
                }
            }
        }
    }

    /*override fun onStop() {
        super.onStop()
        networkMonitor.stopMonitoring()
    }*/
}
