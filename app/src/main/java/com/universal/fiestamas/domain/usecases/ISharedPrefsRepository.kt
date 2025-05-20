package com.universal.fiestamas.domain.usecases

import android.net.Uri
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.LoginAccount
import com.universal.fiestamas.domain.models.request.ProviderRequest
import com.universal.fiestamas.domain.models.request.UserRequest

interface ISharedPrefsRepository {

    fun saveLoginAccountIfNeeded(data: LoginAccount)

    fun getStoredAccountsAsList(): List<LoginAccount>

    fun setFirstTimeAppRunning(value: Boolean)

    fun getFirstTimeAppRunning(): Boolean

    fun setLastPushFirebaseToken(token: String)

    fun getLastPushFirebaseToken(): String

    fun setServiceIdNotification(serviceId: String)

    fun getServiceIdNotification(): String

    fun saveUserAddress(address: Address)

    fun getUserAddress(): Address?

    fun resetUserAddress()

    fun saveVideoFromCamera(uri: Uri)

    fun getVideoFromCamera(): Uri?

    fun resetVideoFromCamera()

    fun setWifiDialogAlreadyShown(wasShown: Boolean)

    fun getWifiDialogAlreadyShown(): Boolean

    fun setLastKnownSsidAndPassword(credentials: Pair<String, String>)

    fun getLastKnownSsidAndPassword(): Pair<String, String>

    fun setServiceIdShared(serviceId: String)

    fun getServiceIdShared(): String

    fun saveStoredDataForRegistrationUser(userRequest: UserRequest)

    fun getStoredDataForRegistrationUser(email: String) : UserRequest?

    fun resetStoredDataForRegistrationUser()

    fun saveStoredDataForRegistrationProvider(providerRequest: ProviderRequest)

    fun getStoredDataForRegistrationProvider(email: String) : ProviderRequest?

    fun resetStoredDataForRegistrationProvider()

    fun setProviderShouldBeRedirectedToServices(redirected: Boolean)

    fun getProviderShouldBeRedirectedToServices(): Boolean
}
