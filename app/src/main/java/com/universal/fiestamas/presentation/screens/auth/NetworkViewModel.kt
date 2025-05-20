@file:Suppress("DEPRECATION")

package com.universal.fiestamas.presentation.screens.auth

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_NOT_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.text.format.Formatter
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.universal.fiestamas.domain.usecases.SharedPrefsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.random.Random

@HiltViewModel
class NetworkViewModel @Inject constructor(
    private val application: Application,
    private val sharedPrefsUseCase: SharedPrefsUseCase
) : ViewModel() {

    enum class FiestamasConnectionState {
        CONNECTED,
        DETECTED_BUT_DISCONNECTED,
        UNDETECTED
    }

    private val wifiManager: WifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val _fiestamasConnectionState = MutableStateFlow(FiestamasConnectionState.UNDETECTED)
    val fiestamasConnectionState: StateFlow<FiestamasConnectionState>
        get() = _fiestamasConnectionState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (_fiestamasConnectionState.value != FiestamasConnectionState.CONNECTED) {
                try {
                    /*withTimeout(60_000) {
                        while (true) {
                            scanWifi()
                            // Scan every 4 seconds
                            delay(4000)
                        }
                    }*/
                } catch (e: TimeoutCancellationException) {
                    println("Se alcanzó el límite de tiempo -> Deja de escanear")
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanWifi() {
        if (wifiManager.isWifiEnabled) {
            val wifiList = wifiManager.scanResults
            val (lastKnownSsid, _) = sharedPrefsUseCase.getLastKnownSsidAndPassword()
            val isDesiredNetworkAvailable = wifiList.any { it.SSID == lastKnownSsid }
            val isConnectedToDesiredNetwork = isConnectedToNetwork(lastKnownSsid)
            _fiestamasConnectionState.value = when {
                isConnectedToDesiredNetwork -> FiestamasConnectionState.CONNECTED
                isDesiredNetworkAvailable && !isConnectedToDesiredNetwork -> FiestamasConnectionState.DETECTED_BUT_DISCONNECTED
                else -> FiestamasConnectionState.UNDETECTED
            }
        } else {
            _fiestamasConnectionState.value = FiestamasConnectionState.UNDETECTED
        }
    }

    private fun isConnectedToNetwork(ssid: String): Boolean {
        val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
        return networkInfo.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) && wifiManager.connectionInfo.ssid == "\"$ssid\""
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun connectToWifi(context: Context) {
        val (lastKnownSsid, lastKnownPass) = sharedPrefsUseCase.getLastKnownSsidAndPassword()
        val suggestion1 = WifiNetworkSuggestion.Builder()
            .setSsid(lastKnownSsid)
            .setWpa2Passphrase(lastKnownPass)
            .build()

        val suggestionsList = listOf(suggestion1)

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val status = wifiManager.addNetworkSuggestions(suggestionsList)
        if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            println("Error connecting to the network!!")
        }

        val intentFilter = IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (!intent.action.equals(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
                    return
                }
            }
        }

        context.registerReceiver(broadcastReceiver, intentFilter, RECEIVER_NOT_EXPORTED)
        informThatWifiDialogWasAlreadyShownToUser()
    }

    fun informThatWifiDialogWasAlreadyShownToUser() {
        sharedPrefsUseCase.setWifiDialogAlreadyShown(true)
    }

    fun wasWifiDialogAlreadyShownToUser(): Boolean =
        sharedPrefsUseCase.getWifiDialogAlreadyShown()



    // --------------- Functions to get the MAC address --------------- //

    // Ignore SSL warnings (not recommended for production environments)
    @SuppressLint("CustomX509TrustManager", "TrustAllX509TrustManager")
    object TrustAllCerts : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    }

    @SuppressLint("BadHostnameVerifier")
    object TrustAllHosts : HostnameVerifier {
        override fun verify(hostname: String?, session: SSLSession?): Boolean = true
    }

    private fun trustAllCertificates() {
        try {
            val trustAllCerts = arrayOf<TrustManager>(TrustAllCerts)
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier(TrustAllHosts)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("LocalVariableName")
    fun addMACToRouterWhitelist(context: Context): StringBuilder {
        val text = StringBuilder()
        val OMADA_URL = "https://192.168.2.3:443"
        val CLIENT_ID = "48335dd73c444a578a653779bf9bb689" // from Omada controller
        val CLIENT_SECRET = "afe19c3b1d254755a576a4d7cdb48214" // from Omada controller
        val OMADA_ID = "215d0209d809fd64f838191dce3a3ff6" // from Omada controller
        val SITE_ID = "66743ada04eccf2c24334087" // from Omada controller

        // Ignore SSL warnings
        trustAllCertificates()

        // STEP 1 - Obtain access token
        try {
            val tokenUrl = "$OMADA_URL/openapi/authorize/token?grant_type=client_credentials"
            val tokenBody = JsonObject().apply {
                addProperty("omadacId", OMADA_ID)
                addProperty("client_id", CLIENT_ID)
                addProperty("client_secret", CLIENT_SECRET)
            }

            val tokenResponse = postRequest(tokenUrl, tokenBody.toString())
            val jsonResponse = Gson().fromJson(tokenResponse, JsonObject::class.java)
            val accessToken = jsonResponse.getAsJsonObject("result").get("accessToken").asString
            val refreshToken = jsonResponse.getAsJsonObject("result").get("refreshToken").asString

            text.append("Access Token: $accessToken")
            text.append("Refresh Token: $refreshToken")

            val generalHeaders = mapOf(
                "Content-Type" to "application/json",
                "Authorization" to "AccessToken=$accessToken"
            )

            // STEP 2 - Obtain mobile IP
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val ipInt = wifiManager.connectionInfo.ipAddress
            val ipAddressString = Formatter.formatIpAddress(ipInt)

            // STEP 3 - Get MAC address from IP
            val clientsUrl = "$OMADA_URL/openapi/v1/$OMADA_ID/sites/$SITE_ID/clients?page=1&pageSize=1000"
            val clientsResponse = getRequest(clientsUrl, generalHeaders)
            val jsonObject = JSONObject(clientsResponse)
            val result = jsonObject.getJSONObject("result")
            val dataArray = result.getJSONArray("data")
            var macAddress = ""

            for (i in 0 until dataArray.length()) {
                val client = dataArray.getJSONObject(i)
                val clientIp = client.getString("ip")

                if (clientIp == ipAddressString) {
                    macAddress = client.getString("mac")
                }
            }

            // STEP 4 - Add MAC address to allowed devices list
            val addMacUrl = "$OMADA_URL/openapi/v1/$OMADA_ID/sites/$SITE_ID/mac-filters"
            val randomId = Random.nextInt(1000, 9999).toString()
            val addMacBody = JsonObject().apply {
                addProperty("id", randomId)
                addProperty("name", macAddress)
                addProperty("filterMode", 0)
                addProperty("type", 0)
                add("macAddresses", JsonArray().apply {
                    add(JsonObject().apply {
                        addProperty("macAddress", macAddress)
                        addProperty("name", macAddress)
                    })
                })
            }
            val addMacResponse = postRequest(addMacUrl, addMacBody.toString(), generalHeaders)
            text.append("Add MAC Response: $addMacResponse")
        } catch (e: Exception) {
            text.append("Exception: $e")
            e.printStackTrace()
            return text
        }
        return text
    }

    @Suppress("LocalVariableName")
    fun blockMACInRestrictedRouter(context: Context): StringBuilder {
        val text = StringBuilder()
        val OMADA_URL = "https://192.168.2.101:443"
        val CLIENT_ID = "48335dd73c444a578a653779bf9bb689" // from Omada controller
        val CLIENT_SECRET = "afe19c3b1d254755a576a4d7cdb48214" // from Omada controller
        val OMADA_ID = "215d0209d809fd64f838191dce3a3ff6" // from Omada controller
        val SITE_ID = "66743ada04eccf2c24334087" // from Omada controller

        // Ignore SSL warnings
        trustAllCertificates()

        // STEP 1 - Obtain access token
        try {
            val tokenUrl = "$OMADA_URL/openapi/authorize/token?grant_type=client_credentials"
            val tokenBody = JsonObject().apply {
                addProperty("omadacId", OMADA_ID)
                addProperty("client_id", CLIENT_ID)
                addProperty("client_secret", CLIENT_SECRET)
            }

            val tokenResponse = postRequest(tokenUrl, tokenBody.toString())
            val jsonResponse = Gson().fromJson(tokenResponse, JsonObject::class.java)
            val accessToken = jsonResponse.getAsJsonObject("result").get("accessToken").asString
            val refreshToken = jsonResponse.getAsJsonObject("result").get("refreshToken").asString

            text.append("Access Token: $accessToken")
            text.append("Refresh Token: $refreshToken")

            val generalHeaders = mapOf(
                "Content-Type" to "application/json",
                "Authorization" to "AccessToken=$accessToken"
            )

            // STEP 2 - Obtain mobile IP
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val ipInt = wifiManager.connectionInfo.ipAddress
            val ipAddressString = Formatter.formatIpAddress(ipInt)

            // STEP 3 - Get MAC address from IP
            val clientsUrl = "$OMADA_URL/openapi/v1/$OMADA_ID/sites/$SITE_ID/clients?page=1&pageSize=1000"
            val clientsResponse = getRequest(clientsUrl, generalHeaders)
            val jsonObject = JSONObject(clientsResponse)
            val result = jsonObject.getJSONObject("result")
            val dataArray = result.getJSONArray("data")
            var macAddress = ""

            if (dataArray.length() > 0) {
                for (i in 0 until dataArray.length()) {
                    val client = dataArray.getJSONObject(i)
                    val clientIp = client.getString("ip")

                    if (clientIp == ipAddressString) {
                        macAddress = client.getString("mac")
                    }
                }
            }

            // STEP 4 - Block MAC address to force disconnection in restricted router
            if (macAddress.isNotEmpty()) {
                val blockMacUrl = "$OMADA_URL/openapi/v1/$OMADA_ID/sites/$SITE_ID/mac-filters"
                val randomId = Random.nextInt(1000, 9999).toString()
                val blockMacBody = JsonObject().apply {
                    addProperty("id", randomId)
                    addProperty("name", macAddress)
                    addProperty(
                        "filterMode",
                        1
                    ) // filter mode should be a value as follows: 0: allow; 1: deny.
                    addProperty("type", 0)
                    add("macAddresses", JsonArray().apply {
                        add(JsonObject().apply {
                            addProperty("macAddress", macAddress)
                            addProperty("name", macAddress)
                        })
                    })
                }
                val blockMacResponse =
                    postRequest(blockMacUrl, blockMacBody.toString(), generalHeaders)
                text.append("Block MAC Response: $blockMacResponse")

                // STEP 5 - Block client too to force disconnection faster in restricted router
                val blockClientUrl = "$OMADA_URL/openapi/v1/$OMADA_ID/sites/$SITE_ID/clients/$macAddress/reconnect"
                val body = ""
                val blockClientResponse = postRequest(blockClientUrl, body, generalHeaders)
                text.append("Block Client Response: $blockClientResponse")
            } else {
                text.append("Can't block MAC because it is blank or null")
            }
        } catch (e: Exception) {
            text.append("Exception: $e")
            e.printStackTrace()
            return text
        }
        return text
    }

    private fun postRequest(url: String, jsonBody: String, headers: Map<String, String> = emptyMap()): String {
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        urlConnection.requestMethod = "POST"
        urlConnection.doOutput = true

        // Set the default Content-Type header
        urlConnection.setRequestProperty("Content-Type", "application/json")

        // Set the additional headers
        headers.forEach { (key, value) ->
            urlConnection.setRequestProperty(key, value)
        }

        // Write the JSON body to the output stream
        if (jsonBody.isNotEmpty()) {
            urlConnection.outputStream.use { os ->
                val input = jsonBody.toByteArray()
                os.write(input, 0, input.size)
            }
        }

        // Read and return the response
        return urlConnection.inputStream.bufferedReader().use { it.readText() }
    }

    private fun getRequest(url: String, headers: Map<String, String> = emptyMap()): String {
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        urlConnection.requestMethod = "GET"
        headers.forEach { (key, value) ->
            urlConnection.setRequestProperty(key, value)
        }

        return urlConnection.inputStream.bufferedReader().use { it.readText() }
    }
}
