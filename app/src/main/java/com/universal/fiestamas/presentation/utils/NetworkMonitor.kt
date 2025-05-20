package com.universal.fiestamas.presentation.utils

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class NetworkMonitor(private val connectivityManager: ConnectivityManager) :
    ConnectivityManager.NetworkCallback() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val isConnected = mutableStateOf(false)

    fun startMonitoring() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    fun stopMonitoring() {
        connectivityManager.unregisterNetworkCallback(this)
    }

    override fun onAvailable(network: Network) {
        mainHandler.post {
            isConnected.value = true
        }
    }

    override fun onUnavailable() {
        isConnected.value = false
    }

    override fun onLost(network: Network) {
        mainHandler.post {
            isConnected.value = false
        }
    }

    fun isConnected(): MutableState<Boolean> {
        return isConnected
    }
}
