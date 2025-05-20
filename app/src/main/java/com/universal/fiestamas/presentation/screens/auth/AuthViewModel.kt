package com.universal.fiestamas.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.LoginAccount
import com.universal.fiestamas.domain.models.UriFile
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
import com.universal.fiestamas.domain.usecases.AuthUseCase
import com.universal.fiestamas.domain.usecases.GoogleServicesUseCase
import com.universal.fiestamas.domain.usecases.ServiceUseCase
import com.universal.fiestamas.domain.usecases.SharedPrefsUseCase
import com.universal.fiestamas.presentation.utils.Constants.HALF_SECOND
import com.universal.fiestamas.presentation.utils.Constants.ONE_SECOND
import com.universal.fiestamas.presentation.utils.LocationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val serviceUseCase: ServiceUseCase,
    private val sharedPrefsUseCase: SharedPrefsUseCase,
    private val locationService: LocationService,
    private val googleUseCase: GoogleServicesUseCase
) : ViewModel() {

    var alreadySignedInWithEmailAndPassword = false
    private var checkedIfUserIsSignedIn = false
    private var gotFirebaseUserDb = false
    private var gotFirebaseProviderDb = false
    var alreadyCreatedNewUserOnServer = false
    var alreadyCreatedNewProviderOnServer = false
    private var alreadyUploadedMediaFiles = false
    private var alreadyUploadedSingleMediaFile = false
    private var alreadyUpdatedPasswordOnServer = false
    var listenForLocationUpdates = true

    private val _firebaseUser = MutableStateFlow<FirebaseUser?>(null)
    val firebaseUser: StateFlow<FirebaseUser?>
        get() = _firebaseUser

    private val _firebaseUserDb = MutableStateFlow<FirebaseUserDb?>(null)
    val firebaseUserDb: StateFlow<FirebaseUserDb?>
        get() = _firebaseUserDb

    private val _firebaseProviderDb = MutableStateFlow<FirebaseUserDb?>(null)
    val firebaseProviderDb: StateFlow<FirebaseUserDb?>
        get() = _firebaseProviderDb

    /*
    private val _userCreatedResponse = MutableStateFlow<BaseResult<FirebaseUserDb, ErrorResponse>?>(null)
    val userCreatedResponse: StateFlow<BaseResult<FirebaseUserDb, ErrorResponse>?>
        get() = _userCreatedResponse

    private val _providerCreatedResponse = MutableStateFlow<BaseResult<FirebaseProviderDb, ErrorResponse>?>(null)
    val providerCreatedResponse: StateFlow<BaseResult<FirebaseProviderDb, ErrorResponse>?>
        get() = _providerCreatedResponse
    */

    val allAccounts = sharedPrefsUseCase.getStoredAccountsAsList()
    private val _accountsList = MutableStateFlow<List<LoginAccount>>(emptyList())
    val accountsList: StateFlow<List<LoginAccount>>
        get() = _accountsList

    fun userHasNewestVersion(currentVersion: Int, onResponse: (Boolean) -> Unit) {
        viewModelScope.launch {
            authUseCase.userHasNewestVersion(currentVersion).collectLatest {
                onResponse(it)
            }
        }
    }

    fun getStoredAccountsFromInternalDb(query: String) {
        if (query.length >= 3) {
            val filteredList = allAccounts.filter { account -> account.email.contains(query, ignoreCase = true) }
            _accountsList.value = filteredList
        } else {
            _accountsList.value = emptyList()
        }
    }

    fun saveAccountIntoInternalDb(account: LoginAccount) {
        sharedPrefsUseCase.saveLoginAccountIfNeeded(account)
    }

    fun setProviderShouldBeRedirectedToServices(redirected: Boolean) {
        sharedPrefsUseCase.setProviderShouldBeRedirectedToServices(redirected)
    }

    fun getProviderShouldBeRedirectedToServices(): Boolean {
        return sharedPrefsUseCase.getProviderShouldBeRedirectedToServices()
    }

    fun setFirebaseUser(firebaseUser: FirebaseUser) {
        viewModelScope.launch {
            authUseCase.setFirebaseUser(firebaseUser)
        }
    }

    fun checkIfUserIsSignedIn() {
        if (!checkedIfUserIsSignedIn) {
            checkedIfUserIsSignedIn = true
            viewModelScope.launch(Dispatchers.IO) {
                authUseCase.getFirebaseUser().collectLatest { user ->
                    _firebaseUser.value = user
                }
            }
        }
    }

    fun getFirebaseUserDb(id: String? = null) {
        if (!gotFirebaseUserDb) {
            gotFirebaseUserDb = true
            viewModelScope.launch(Dispatchers.IO) {
                authUseCase.getFirebaseUserDb(id).collectLatest { user ->
                    if (user?.id != null) {
                        _firebaseUserDb.value = user
                    }
                }
            }
        }
    }

    fun getFirebaseProviderDb(id: String) {
        if (!gotFirebaseProviderDb) {
            gotFirebaseProviderDb = true
            viewModelScope.launch(Dispatchers.IO) {
                authUseCase.getFirebaseUserDb(id).collectLatest { provider ->
                    _firebaseProviderDb.value = provider
                }
            }
        }
    }

    fun signOutFromAccount() {
        authUseCase.signOutFromFirebaseAuth()
    }

    @Suppress("unused")
    fun checkIfEmailExistsInFirebase(email: String, existsEmailInFirebase: (exists: Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            authUseCase.getSingedInMethodsFromEmailInFirebase(email).collectLatest {  signInMethods ->
                withContext(Dispatchers.Main) {
                    val exists = signInMethods?.isNotEmpty() ?: false
                    existsEmailInFirebase(exists)
                }
            }
        }
    }

    fun getSingedInMethodsFromEmailInFirebase(email: String, signInMethods: (List<String>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            authUseCase.getSingedInMethodsFromEmailInFirebase(email).collectLatest { signInMethods ->
                withContext(Dispatchers.Main) {
                    signInMethods(signInMethods.orEmpty())
                }
            }
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String, validCredentials: (Boolean) -> Unit) {
        if (!alreadySignedInWithEmailAndPassword) {
            alreadySignedInWithEmailAndPassword = true
            viewModelScope.launch(Dispatchers.IO) {
                authUseCase.signInWithEmailAndPassword(email, password).collectLatest { response ->
                    withContext(Dispatchers.Main) {
                        validCredentials(response.success)

                        if (response.success) {
                            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    println("Error Token Push: ${task.exception}")
                                    return@OnCompleteListener
                                }

                                // Get new FCM registration token
                                val token = task.result
                                println("Token: $token")
                                println("authFirebase currentUser uid: ${response.uid}")
                                registerTokenForPushNotification(token, response.uid)
                            })
                        }
                    }
                }
            }
        }
    }

    private fun registerTokenForPushNotification(token: String, id: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            authUseCase.registerTokenForPushNotification(token, id)
        }
    }

    fun unregisterTokenForPushNotification(uid: String?) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            viewModelScope.launch(Dispatchers.IO) {
                authUseCase.unregisterTokenForPushNotification(token, uid)
            }
        })
    }

    fun updateTokenForPushNotificationIfNeeded() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val newToken = task.result
            viewModelScope.launch(Dispatchers.IO) {
                authUseCase.updateTokenForPushNotificationIfNeeded(newToken, null)
            }
        })
    }

    fun createNewUserOnServer(
        uid: String?,
        googleUserRequest: GoogleUserRequest?,
        userRequest: UserRequest,
        onFinished: (StatusResponseV2) -> Unit
    ) {
        if (!alreadyCreatedNewUserOnServer) {
            alreadyCreatedNewUserOnServer = true
            viewModelScope.launch(Dispatchers.IO) {
                val response: StatusResponseV2 = authUseCase.createNewUserOnServer(uid, googleUserRequest, userRequest).first()
                onFinished(response)
            }
        }
    }

    fun createNewProviderOnServer(
        uid: String?,
        googleProviderRequest: GoogleProviderRequest?,
        providerRequest: ProviderRequest,
        onFinished: (StatusAndDataResponseV2) -> Unit
    ) {
        if (!alreadyCreatedNewProviderOnServer) {
            alreadyCreatedNewProviderOnServer = true
            viewModelScope.launch(Dispatchers.IO) {
                val response = authUseCase.createNewProviderOnServer(uid, googleProviderRequest, providerRequest).first()
                onFinished(response)
            }
        }
    }

    fun uploadMediaFileAndGetUrl(photo: UriFile?, onFinished: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!alreadyUploadedSingleMediaFile) {
                alreadyUploadedSingleMediaFile = true
                serviceUseCase.uploadMediaFiles(listOf(photo), emptyList()).collectLatest { pair ->
                    val photoUrlFromServer = pair.first.getOrNull(0) ?: ""
                    onFinished(photoUrlFromServer)
                }
            }
        }
    }

    fun updateProviderOnServer(
        photo: UriFile?,
        providerId: String,
        providerRequestEdit: ProviderRequestEdit,
        onFinished: (StatusResponseV2) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (photo?.url?.contains("firebasestorage.googleapis.com") == true) {
                // photo didn't change -> don't upload image to firestore and send the same photo
                providerRequestEdit.photo = photo.url
                val response = authUseCase.updateProviderOnServer(providerId, providerRequestEdit).first()
                onFinished(response)
            } else if (!alreadyUploadedMediaFiles) {
                // photo changed -> upload image to firestore first
                alreadyUploadedMediaFiles = true
                serviceUseCase.uploadMediaFiles(listOf(photo), emptyList()).collectLatest { pair ->
                    providerRequestEdit.photo = pair.first.getOrNull(0) ?: ""
                    val response = authUseCase.updateProviderOnServer(providerId, providerRequestEdit).first()
                    onFinished(response)
                }
                alreadyUploadedMediaFiles = false
            }
            delay(ONE_SECOND)
        }
    }

    fun updateClientOnServer(
        photo: UriFile?,
        clientId: String,
        userRequestEdit: UserRequestEdit,
        onFinished: (StatusResponseV2) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (photo?.url?.contains("firebasestorage.googleapis.com") == true) {
                // photo didn't change -> don't upload image to firestore and send the same photo
                userRequestEdit.photo = photo.url
                val response = authUseCase.updateClientOnServer(clientId, userRequestEdit).first()
                onFinished(response)
            } else if (!alreadyUploadedMediaFiles) {
                // photo changed -> upload image to firestore first
                alreadyUploadedMediaFiles = true
                serviceUseCase.uploadMediaFiles(listOf(photo), emptyList()).collectLatest { pair ->
                    userRequestEdit.photo = pair.first.getOrNull(0) ?: ""
                    val response = authUseCase.updateClientOnServer(clientId, userRequestEdit).first()
                    onFinished(response)
                }
                alreadyUploadedMediaFiles = false
            }
            delay(ONE_SECOND)
        }
    }

    fun updatePasswordOnServer(email: String, newPassword: String, onFinished: (Boolean) -> Unit) {
        if (!alreadyUpdatedPasswordOnServer) {
            alreadyUpdatedPasswordOnServer = true
            viewModelScope.launch(Dispatchers.IO) {
                val request = UpdatePasswordRequest(email, newPassword)
                val response: StatusResponseV2 = authUseCase.updatePasswordOnServer(request).first()
                onFinished(response.status == 200)
            }
        }
    }

    fun getUserLocation(onFinished: (Address?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            locationService.requestLocationUpdates().collectLatest { latLng ->
                latLng?.let {
                    googleUseCase.getAddressByCoordinates(it).collectLatest { address ->
                        if (listenForLocationUpdates) {
                            delay(HALF_SECOND)
                            onFinished(address)
                        }
                    }
                } ?: run {
                    delay(HALF_SECOND)
                    onFinished(null)
                }
            }
        }
    }

    fun sendEmailForPasswordRecovery(email: String, onFinished: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            authUseCase.sendEmailForPasswordRecovery(email).collectLatest {
                onFinished(it)
            }
        }
    }

    fun checkIfUserExistsInDb(email: String, onFinished: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            authUseCase.checkIfUserExistsInDb(email).collectLatest {
                onFinished(it.isNotEmpty())
            }
        }
    }

    fun storeUserDataInShPrefs(userRequest: UserRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPrefsUseCase.saveStoredDataForRegistrationUser(userRequest)
        }
    }

    fun getStoredUserDataInShPrefs(email: String): UserRequest? {
        return sharedPrefsUseCase.getStoredDataForRegistrationUser(email)
    }

    fun resetStoredUserDataInShPrefs() {
        sharedPrefsUseCase.resetStoredDataForRegistrationUser()
    }

    fun storeProviderDataInShPrefs(providerRequest: ProviderRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPrefsUseCase.saveStoredDataForRegistrationProvider(providerRequest)
        }
    }

    fun getStoredProviderDataInShPrefs(email: String): ProviderRequest? {
        return sharedPrefsUseCase.getStoredDataForRegistrationProvider(email)
    }

    fun resetStoredProviderDataInShPrefs() {
        sharedPrefsUseCase.resetStoredDataForRegistrationProvider()
    }
}
