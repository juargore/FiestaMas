package com.universal.fiestamas.data.repositories

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.universal.fiestamas.data.module.Constants.FIELD_FIRST_TIME
import com.universal.fiestamas.data.module.Constants.FIELD_LAST_PUSH_TOKEN
import com.universal.fiestamas.data.module.Constants.FIELD_PASSWORD
import com.universal.fiestamas.data.module.Constants.SHARED_PREFS_NAME
import com.universal.fiestamas.domain.models.LoginAccount
import com.universal.fiestamas.domain.usecases.ISharedPrefsRepository
import com.google.gson.Gson
import com.universal.fiestamas.BuildConfig
import com.universal.fiestamas.data.module.Constants.FIELD_CAMERA_VIDEO
import com.universal.fiestamas.data.module.Constants.FIELD_LAST_KNOWN_SSID_N
import com.universal.fiestamas.data.module.Constants.FIELD_LAST_KNOWN_SSID_P
import com.universal.fiestamas.data.module.Constants.FIELD_PROVIDER_REDIRECTED_TO_SERVICES
import com.universal.fiestamas.data.module.Constants.FIELD_PROVIDER_REQUEST
import com.universal.fiestamas.data.module.Constants.FIELD_SERVICE_ID_NOTIFICATION
import com.universal.fiestamas.data.module.Constants.FIELD_SERVICE_ID_SHARED
import com.universal.fiestamas.data.module.Constants.FIELD_USER_ADDRESS
import com.universal.fiestamas.data.module.Constants.FIELD_USER_REQUEST
import com.universal.fiestamas.data.module.Constants.FIELD_WIFI_DIALOG_SHOWN
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.request.ProviderRequest
import com.universal.fiestamas.domain.models.request.UserRequest

class SharedPrefsRepositoryImpl(
    context: Context
): ISharedPrefsRepository {

    private val gson = Gson()
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveLoginAccountIfNeeded(data: LoginAccount) {
        val currentData = sharedPreferences.getString(FIELD_PASSWORD, null)
        val newData: String = if (currentData != null) {
            val dataList = gson.fromJson(currentData, Array<LoginAccount>::class.java).toMutableList()
            if (!dataList.any { it.email == data.email }) {
                dataList.add(LoginAccount(data.email, data.password))
            }
            gson.toJson(dataList)
        } else {
            gson.toJson(arrayOf(LoginAccount(data.email, data.password)))
        }
        sharedPreferences.edit().putString(FIELD_PASSWORD, newData).apply()
    }

    override fun getStoredAccountsAsList(): List<LoginAccount> {
        val currentData = sharedPreferences.getString(FIELD_PASSWORD, null)
        return if (currentData != null) {
            gson.fromJson(currentData, Array<LoginAccount>::class.java).toList()
        } else {
            emptyList()
        }
    }

    override fun setFirstTimeAppRunning(value: Boolean) {
        sharedPreferences.edit().putBoolean(FIELD_FIRST_TIME, value).apply()
    }

    override fun getFirstTimeAppRunning(): Boolean =
        sharedPreferences.getBoolean(FIELD_FIRST_TIME, false)

    override fun setLastPushFirebaseToken(token: String) {
        sharedPreferences.edit().putString(FIELD_LAST_PUSH_TOKEN, token).apply()
    }

    override fun getLastPushFirebaseToken(): String =
        sharedPreferences.getString(FIELD_LAST_PUSH_TOKEN, "").orEmpty()

    override fun setServiceIdNotification(serviceId: String) {
        sharedPreferences.edit().putString(FIELD_SERVICE_ID_NOTIFICATION, serviceId).apply()
    }

    override fun getServiceIdNotification(): String =
        sharedPreferences.getString(FIELD_SERVICE_ID_NOTIFICATION, "").orEmpty()

    override fun saveUserAddress(address: Address) {
        val mAddress: String = gson.toJson(address, Address::class.java)
        sharedPreferences.edit().putString(FIELD_USER_ADDRESS, mAddress).apply()
    }

    override fun getUserAddress(): Address? {
        val currentData = sharedPreferences.getString(FIELD_USER_ADDRESS, null)
        return if (currentData != null) {
            gson.fromJson(currentData, Address::class.java)
        } else {
            null
        }
    }

    override fun resetUserAddress() {
        sharedPreferences.edit().putString(FIELD_USER_ADDRESS, null).apply()
    }

    override fun saveVideoFromCamera(uri: Uri) {
        sharedPreferences.edit().putString(FIELD_CAMERA_VIDEO, uri.toString()).apply()
    }

    override fun getVideoFromCamera(): Uri? {
        val videoString = sharedPreferences.getString(FIELD_CAMERA_VIDEO, null)
        return if (videoString != null) {
            Uri.parse(videoString)
        } else {
            null
        }
    }

    override fun resetVideoFromCamera() {
        sharedPreferences.edit().putString(FIELD_CAMERA_VIDEO, null).apply()
    }

    override fun setWifiDialogAlreadyShown(wasShown: Boolean) {
        sharedPreferences.edit().putBoolean(FIELD_WIFI_DIALOG_SHOWN, wasShown).apply()
    }

    override fun getWifiDialogAlreadyShown(): Boolean =
        sharedPreferences.getBoolean(FIELD_WIFI_DIALOG_SHOWN, false)

    override fun setLastKnownSsidAndPassword(credentials: Pair<String, String>) {
        sharedPreferences.edit().putString(FIELD_LAST_KNOWN_SSID_N, credentials.first).apply()
        sharedPreferences.edit().putString(FIELD_LAST_KNOWN_SSID_P, credentials.second).apply()
    }

    override fun getLastKnownSsidAndPassword(): Pair<String, String> {
        val defaultSsid = BuildConfig.SSID_NAME
        val defaultPass = BuildConfig.SSID_PASS
        val ssid = sharedPreferences.getString(FIELD_LAST_KNOWN_SSID_N, defaultSsid) ?: defaultSsid
        val pass = sharedPreferences.getString(FIELD_LAST_KNOWN_SSID_P, defaultPass) ?: defaultPass
        return Pair(ssid, pass)
    }

    override fun setServiceIdShared(serviceId: String) {
        sharedPreferences.edit().putString(FIELD_SERVICE_ID_SHARED, serviceId).apply()
    }

    override fun getServiceIdShared(): String =
        sharedPreferences.getString(FIELD_SERVICE_ID_SHARED, "").orEmpty()

    override fun saveStoredDataForRegistrationUser(userRequest: UserRequest) {
        val mUserRequest: String = gson.toJson(userRequest, UserRequest::class.java)
        sharedPreferences.edit().putString(FIELD_USER_REQUEST, mUserRequest).apply()
    }

    override fun getStoredDataForRegistrationUser(email: String): UserRequest? {
        val currentData = sharedPreferences.getString(FIELD_USER_REQUEST, null)
        return if (currentData != null) {
            val user = gson.fromJson(currentData, UserRequest::class.java)
            if (user.email == email) {
                return user
            } else {
                resetStoredDataForRegistrationUser()
                return null
            }
        } else null
    }

    override fun resetStoredDataForRegistrationUser() {
        sharedPreferences.edit().putString(FIELD_USER_REQUEST, null).apply()
    }

    override fun saveStoredDataForRegistrationProvider(providerRequest: ProviderRequest) {
        val mProviderRequest: String = gson.toJson(providerRequest, ProviderRequest::class.java)
        sharedPreferences.edit().putString(FIELD_PROVIDER_REQUEST, mProviderRequest).apply()
    }

    override fun getStoredDataForRegistrationProvider(email: String): ProviderRequest? {
        val currentData = sharedPreferences.getString(FIELD_PROVIDER_REQUEST, null)
        return if (currentData != null) {
            val provider = gson.fromJson(currentData, ProviderRequest::class.java)
            if (provider.email == email) {
                return provider
            } else {
                resetStoredDataForRegistrationProvider()
                return null
            }
        } else null
    }

    override fun resetStoredDataForRegistrationProvider() {
        sharedPreferences.edit().putString(FIELD_PROVIDER_REQUEST, null).apply()
    }

    override fun setProviderShouldBeRedirectedToServices(redirected: Boolean) {
        sharedPreferences.edit().putBoolean(FIELD_PROVIDER_REDIRECTED_TO_SERVICES, redirected).apply()
    }

    override fun getProviderShouldBeRedirectedToServices(): Boolean {
        return sharedPreferences.getBoolean(FIELD_PROVIDER_REDIRECTED_TO_SERVICES, false)
    }
}
