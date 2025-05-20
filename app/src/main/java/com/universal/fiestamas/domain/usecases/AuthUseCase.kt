package com.universal.fiestamas.domain.usecases

import com.google.firebase.auth.FirebaseUser
import com.universal.fiestamas.domain.models.request.GoogleProviderRequest
import com.universal.fiestamas.domain.models.request.GoogleUserRequest
import com.universal.fiestamas.domain.models.request.ProviderRequest
import com.universal.fiestamas.domain.models.request.ProviderRequestEdit
import com.universal.fiestamas.domain.models.request.UpdatePasswordRequest
import com.universal.fiestamas.domain.models.request.UserRequest
import com.universal.fiestamas.domain.models.request.UserRequestEdit
import com.universal.fiestamas.domain.models.response.FirebaseUserDb
import com.universal.fiestamas.domain.models.response.StatusAndDataResponseV2
import com.universal.fiestamas.domain.models.response.StatusResponseV2
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
    private val sharedPrefsUseCase: ISharedPrefsRepository
) {

    // returns FirebaseUser directly from Firebase Auth
    fun getFirebaseUser(): Flow<FirebaseUser?> = authRepository.getFirebaseUser()

    fun setFirebaseUser(firebaseUser: FirebaseUser) = authRepository.setFirebaseUser(firebaseUser)

    // returns FirebaseUserDb a local data class that maps all data from firestore
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getFirebaseUserDb(id: String?): Flow<FirebaseUserDb?> {
        return if (id != null) {
            authRepository.getFirebaseUserDb(id)
        } else {
            authRepository.getFirebaseUser().flatMapConcat { firebaseUser ->
                if (firebaseUser != null) {
                    val mId = firebaseUser.uid
                    authRepository.getFirebaseUserDb(mId)
                } else {
                    flowOf(FirebaseUserDb())
                }
            }
        }
    }

    fun userHasNewestVersion(currentVersion: Int) = authRepository.userHasNewestVersion(currentVersion)

    fun getSsidCredentials() = authRepository.getSsidCredentials()

    fun signOutFromFirebaseAuth() = authRepository.signOutFromFirebaseAuth()

    fun getSingedInMethodsFromEmailInFirebase(email: String)
        = authRepository.getSingedInMethodsFromEmailInFirebase(email)

    fun signInWithEmailAndPassword(email: String, password: String)
        = authRepository.signInWithEmailAndPassword(email, password)

    fun registerTokenForPushNotification(token: String, uid: String?)
        = authRepository.registerTokenForPushNotification(token, uid)

    fun unregisterTokenForPushNotification(token: String, uid: String?) {
        authRepository.unregisterTokenForPushNotification(token, uid)
        sharedPrefsUseCase.setLastPushFirebaseToken("")
    }

    fun updateTokenForPushNotificationIfNeeded(newToken: String, uid: String?) {
        val prevToken = sharedPrefsUseCase.getLastPushFirebaseToken()
        if (newToken != prevToken) {
            if (prevToken.isNotEmpty()) {
                unregisterTokenForPushNotification(prevToken, uid)
            }
            if (newToken.isNotEmpty()) {
                registerTokenForPushNotification(newToken, uid)
            }
        }
    }

    fun createNewUserOnServer(uid: String?, googleUserRequest: GoogleUserRequest?, userRequest: UserRequest): Flow<StatusResponseV2>
        = authRepository.createNewUserOnServer(uid, googleUserRequest, userRequest)

    fun createNewProviderOnServer(uid: String?, googleProviderRequest: GoogleProviderRequest?, providerRequest: ProviderRequest): Flow<StatusAndDataResponseV2>
        = authRepository.createNewProviderOnServer(uid, googleProviderRequest, providerRequest)

    fun updateProviderOnServer(providerId: String, providerRequestEdit: ProviderRequestEdit): Flow<StatusResponseV2>
        = authRepository.updateProviderOnServer(providerId, providerRequestEdit)

    fun updateClientOnServer(clientId: String, userRequest: UserRequestEdit): Flow<StatusResponseV2>
        = authRepository.updateClientOnServer(clientId, userRequest)

    fun updatePasswordOnServer(passwordRequest: UpdatePasswordRequest): Flow<StatusResponseV2>
        = authRepository.updatePasswordOnServer(passwordRequest)

    fun sendEmailForPasswordRecovery(email: String) = authRepository.sendEmailForPasswordRecovery(email)

    fun checkIfUserExistsInDb(email: String) = authRepository.checkIfUserExistsInDb(email)
}
