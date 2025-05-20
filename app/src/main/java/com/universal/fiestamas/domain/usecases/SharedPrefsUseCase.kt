package com.universal.fiestamas.domain.usecases

import android.net.Uri
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.LoginAccount
import com.universal.fiestamas.domain.models.request.ProviderRequest
import com.universal.fiestamas.domain.models.request.UserRequest
import javax.inject.Inject

class SharedPrefsUseCase @Inject constructor(
    private val prefsRepository: ISharedPrefsRepository
) {
    fun saveLoginAccountIfNeeded(data: LoginAccount) = prefsRepository.saveLoginAccountIfNeeded(data)

    fun getStoredAccountsAsList() = prefsRepository.getStoredAccountsAsList()

    fun setFirstTimeAppRunning(value: Boolean) = prefsRepository.setFirstTimeAppRunning(value)

    fun getFirstTimeAppRunning(): Boolean = prefsRepository.getFirstTimeAppRunning()

    fun setServiceIdNotification(serviceId: String) = prefsRepository.setServiceIdNotification(serviceId)

    fun getServiceIdNotification(): String = prefsRepository.getServiceIdNotification()

    fun saveUserAddress(address: Address) = prefsRepository.saveUserAddress(address)

    fun getUserAddress(): Address? = prefsRepository.getUserAddress()

    fun resetUserAddress() = prefsRepository.resetUserAddress()

    fun saveVideoFromCamera(uri: Uri) = prefsRepository.saveVideoFromCamera(uri)

    fun getVideoFromCamera(): Uri? = prefsRepository.getVideoFromCamera()

    fun resetVideoFromCamera() = prefsRepository.resetVideoFromCamera()

    fun setWifiDialogAlreadyShown(wasShown: Boolean) = prefsRepository.setWifiDialogAlreadyShown(wasShown)

    fun getWifiDialogAlreadyShown() = prefsRepository.getWifiDialogAlreadyShown()

    fun setLastKnownSsidAndPassword(credentials: Pair<String, String>) = prefsRepository.setLastKnownSsidAndPassword(credentials)

    fun getLastKnownSsidAndPassword() = prefsRepository.getLastKnownSsidAndPassword()

    fun setServiceIdShared(serviceId: String) = prefsRepository.setServiceIdShared(serviceId)

    fun getServiceIdShared(): String = prefsRepository.getServiceIdShared()

    fun saveStoredDataForRegistrationUser(userRequest: UserRequest) = prefsRepository.saveStoredDataForRegistrationUser(userRequest)

    fun getStoredDataForRegistrationUser(email: String) : UserRequest? = prefsRepository.getStoredDataForRegistrationUser(email)

    fun resetStoredDataForRegistrationUser() = prefsRepository.resetStoredDataForRegistrationUser()

    fun saveStoredDataForRegistrationProvider(providerRequest: ProviderRequest) = prefsRepository.saveStoredDataForRegistrationProvider(providerRequest)

    fun getStoredDataForRegistrationProvider(email: String) : ProviderRequest? = prefsRepository.getStoredDataForRegistrationProvider(email)

    fun resetStoredDataForRegistrationProvider() = prefsRepository.resetStoredDataForRegistrationProvider()

    fun setProviderShouldBeRedirectedToServices(redirected: Boolean) = prefsRepository.setProviderShouldBeRedirectedToServices(redirected)

    fun getProviderShouldBeRedirectedToServices(): Boolean = prefsRepository.getProviderShouldBeRedirectedToServices()
}
