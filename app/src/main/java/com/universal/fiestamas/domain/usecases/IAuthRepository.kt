package com.universal.fiestamas.domain.usecases

import com.universal.fiestamas.domain.common.BaseResult
import com.universal.fiestamas.domain.common.ErrorResponse
import com.universal.fiestamas.domain.models.request.ProviderRequest
import com.universal.fiestamas.domain.models.request.ProviderRequestEdit
import com.universal.fiestamas.domain.models.request.UserRequest
import com.universal.fiestamas.domain.models.request.UserRequestEdit
import com.universal.fiestamas.domain.models.response.FirebaseUserDb
import com.universal.fiestamas.domain.models.response.LoginResponse
import com.google.firebase.auth.FirebaseUser
import com.universal.fiestamas.domain.models.request.GoogleProviderRequest
import com.universal.fiestamas.domain.models.request.GoogleUserRequest
import com.universal.fiestamas.domain.models.request.SsidCredentials
import com.universal.fiestamas.domain.models.request.UpdatePasswordRequest
import com.universal.fiestamas.domain.models.response.StatusAndDataResponseV2
import com.universal.fiestamas.domain.models.response.StatusResponseV2
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {

    fun userHasNewestVersion(currentVersion: Int): Flow<Boolean>

    fun getSsidCredentials(): Flow<SsidCredentials?>
    
    fun getFirebaseUser(): Flow<FirebaseUser?>

    fun setFirebaseUser(firebaseUser: FirebaseUser)

    fun getFirebaseUserDb(id: String): Flow<FirebaseUserDb?>

    fun signOutFromFirebaseAuth()

    fun getSingedInMethodsFromEmailInFirebase(email: String): Flow<List<String>?>

    fun signInWithEmailAndPassword(email: String, password: String): Flow<LoginResponse>

    fun registerTokenForPushNotification(token: String, uid: String?)

    fun unregisterTokenForPushNotification(token: String, uid: String?)

    fun updateTokenForPushNotification(prevToken: String, newToken: String, uid: String?): Flow<BaseResult<Boolean, ErrorResponse>>

    fun createNewUserOnServer(uid: String?, googleUserRequest: GoogleUserRequest?,  userRequest: UserRequest): Flow<StatusResponseV2>

    fun createNewProviderOnServer(uid: String?, googleProviderRequest: GoogleProviderRequest?, providerRequest: ProviderRequest): Flow<StatusAndDataResponseV2>

    fun updateProviderOnServer(providerId: String, providerRequestEdit: ProviderRequestEdit): Flow<StatusResponseV2>

    fun updateClientOnServer(clientId: String, clientRequestEdit: UserRequestEdit): Flow<StatusResponseV2>

    fun updatePasswordOnServer(passwordRequest: UpdatePasswordRequest): Flow<StatusResponseV2>

    fun sendEmailForPasswordRecovery(email: String): Flow<Boolean>

    fun checkIfUserExistsInDb(email: String): Flow<List<FirebaseUserDb>>
}
