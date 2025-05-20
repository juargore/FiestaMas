package com.universal.fiestamas.data.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.universal.fiestamas.data.apis.AuthApi
import com.universal.fiestamas.data.extensions.collectionListenerFlow
import com.universal.fiestamas.data.extensions.documentListenerFlow
import com.universal.fiestamas.data.module.Constants
import com.universal.fiestamas.data.utils.NetworkManager
import com.universal.fiestamas.domain.common.BaseResult
import com.universal.fiestamas.domain.models.request.AndroidBuildVersion
import com.universal.fiestamas.domain.models.request.EntityDataRequest
import com.universal.fiestamas.domain.models.request.GoogleProviderRequest
import com.universal.fiestamas.domain.models.request.GoogleUserRequest
import com.universal.fiestamas.domain.models.request.ProviderRequest
import com.universal.fiestamas.domain.models.request.ProviderRequestEdit
import com.universal.fiestamas.domain.models.request.SsidCredentials
import com.universal.fiestamas.domain.models.request.SubscribeRequest
import com.universal.fiestamas.domain.models.request.UpdatePasswordRequest
import com.universal.fiestamas.domain.models.request.UserRequest
import com.universal.fiestamas.domain.models.request.UserRequestEdit
import com.universal.fiestamas.domain.models.response.DataResponseV2
import com.universal.fiestamas.domain.models.response.FirebaseUserDb
import com.universal.fiestamas.domain.models.response.LoginResponse
import com.universal.fiestamas.domain.models.response.StatusAndDataResponseV2
import com.universal.fiestamas.domain.models.response.StatusResponseV2
import com.universal.fiestamas.domain.usecases.IAuthRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthRepositoryImpl(
    private val authFirebase: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val authApi: AuthApi,
    private val networkManager: NetworkManager
) : IAuthRepository {

    private val _firebaseUser = MutableStateFlow(authFirebase.currentUser)

    override fun getFirebaseUser() = _firebaseUser

    override fun setFirebaseUser(firebaseUser: FirebaseUser) {
        _firebaseUser.value = firebaseUser
    }

    private suspend fun getAuthToken(): String {
        return suspendCancellableCoroutine { continuation ->
            _firebaseUser.value?.getIdToken(true)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result.token.orEmpty()
                    continuation.resume(token)
                } else {
                    Log.e("Token error", "Error getting auth token on AuthRepositoryImpl: ${task.exception}")
                    continuation.resume("")
                }
            }
        }
    }

    override fun userHasNewestVersion(currentVersion: Int): Flow<Boolean> {
        val collection = firestore.collection(Constants.SETTINGS).document("KmBWThqxrew9A2BycqeF")
        return collection.documentListenerFlow(AndroidBuildVersion::class.java).map { serverVersion ->
            return@map currentVersion >= (serverVersion?.android_build_number ?: 0)
        }
    }

    override fun getSsidCredentials(): Flow<SsidCredentials?> {
        val collection = firestore.collection(Constants.SETTINGS).document("QvqZV0TFtY3zJ3lImHuf")
        return collection.documentListenerFlow(SsidCredentials::class.java)
    }

    override fun getFirebaseUserDb(id: String): Flow<FirebaseUserDb?> {
        val eventRef: DocumentReference = firestore.collection(Constants.USERS).document(id)
        return eventRef.documentListenerFlow(FirebaseUserDb::class.java)
    }

    override fun signOutFromFirebaseAuth() {
        authFirebase.signOut()
        _firebaseUser.value = null
    }

    override fun getSingedInMethodsFromEmailInFirebase(email: String): Flow<List<String>?> = callbackFlow {
        authFirebase.fetchSignInMethodsForEmail(email.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods: List<String>? = task.result?.signInMethods
                    trySend(signInMethods)
                    close()
                } else {
                    trySend(null)
                    close()
                }
            }

        awaitClose { }
    }

    override fun signInWithEmailAndPassword(email: String, password: String) = callbackFlow {
        authFirebase.signInWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                _firebaseUser.value = authFirebase.currentUser
                trySend(LoginResponse(task.isSuccessful, authFirebase.currentUser?.uid.orEmpty()))
                close()
            }
            .addOnFailureListener {
                println("Error Firebase: ${it.localizedMessage}")
                trySend(LoginResponse(false, ""))
                close()
            }

        awaitClose { }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun registerTokenForPushNotification(token: String, uid: String?) {
        GlobalScope.launch {
            val body = EntityDataRequest(entityData = SubscribeRequest(
                device_token = token,
                uid = uid ?: authFirebase.currentUser?.uid.orEmpty())
            )
            authApi.subscribeTokenPushNotificationsV2(body)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun unregisterTokenForPushNotification(token: String, uid: String?) {
        GlobalScope.launch {
            if (networkManager.isNetworkAvailable()) {
                val body = EntityDataRequest(entityData = SubscribeRequest(
                    device_token = token,
                    uid = uid ?: authFirebase.currentUser?.uid.orEmpty())
                )
                authApi.unsubscribeTokenPushNotificationsV2(body)
            }
        }
    }

    override fun updateTokenForPushNotification(prevToken: String, newToken: String, uid: String?) = flow {
        emit(BaseResult.Success(true))
    }

    override fun createNewUserOnServer(uid: String?, googleUserRequest: GoogleUserRequest?, userRequest: UserRequest) = flow {
        val body = EntityDataRequest(entityData = userRequest)
        val response = authApi.createUserOnServerV2(body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(response.body()!!)
        } else {
            emit(StatusResponseV2(status = 400))
        }
    }

    override fun createNewProviderOnServer(uid: String?, googleProviderRequest: GoogleProviderRequest?, providerRequest: ProviderRequest) = flow {
        val body = EntityDataRequest(entityData = providerRequest)
        val response = authApi.createProviderOnServerV2(body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(response.body()!!)
        } else {
            emit(StatusAndDataResponseV2(status = 400, data = DataResponseV2(id = "")))
        }
    }

    override fun updateProviderOnServer(providerId: String, providerRequestEdit: ProviderRequestEdit) = flow {
        val body = EntityDataRequest(entityData = providerRequestEdit)
        val response = authApi.updateProviderOnServerV2(providerId, body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(response.body()!!)
        } else {
            emit(StatusResponseV2(status = 400))
        }
    }

    override fun updateClientOnServer(clientId: String, clientRequestEdit: UserRequestEdit) = flow {
        val body = EntityDataRequest(entityData = clientRequestEdit)
        val response = authApi.updateUserOnServerV2(clientId, body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(response.body()!!)
        } else {
            emit(StatusResponseV2(status = 400))
        }
    }

    override fun updatePasswordOnServer(passwordRequest: UpdatePasswordRequest) = flow {
        val body = EntityDataRequest(entityData = passwordRequest)
        val response = authApi.updatePasswordV2(body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(response.body()!!)
        } else {
            emit(StatusResponseV2(status = 400))
        }
    }

    override fun sendEmailForPasswordRecovery(email: String): Flow<Boolean> = callbackFlow {
        val auth = FirebaseAuth.getInstance()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Success - Recovery email was sent to: $email")
                    trySend(true)
                    close()
                } else {
                    val exception = task.exception
                    println("Error - Error sending recovery email: ${exception?.message}")
                    trySend(false)
                    close()
                }
            }

        awaitClose { }
    }

    override fun checkIfUserExistsInDb(email: String): Flow<List<FirebaseUserDb>> {
        val clientEventsCollection = firestore.collection(Constants.USERS)
        val query = clientEventsCollection
            .whereEqualTo(Constants.EMAIL, email.trim())
        return clientEventsCollection.collectionListenerFlow(FirebaseUserDb::class.java, query)
    }
}
