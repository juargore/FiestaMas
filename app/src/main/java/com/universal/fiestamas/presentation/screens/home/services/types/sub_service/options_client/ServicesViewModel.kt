package com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.universal.fiestamas.domain.common.BaseResult
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.Attribute
import com.universal.fiestamas.domain.models.FirstQuestionsProvider
import com.universal.fiestamas.domain.models.ScreenInfo
import com.universal.fiestamas.domain.models.Service
import com.universal.fiestamas.domain.models.UriFile
import com.universal.fiestamas.domain.models.request.AddServiceProviderRequest
import com.universal.fiestamas.domain.models.request.CreateEventResponseV2
import com.universal.fiestamas.domain.models.request.UpdateServiceProviderRequest
import com.universal.fiestamas.domain.models.response.FirebaseUserDb
import com.universal.fiestamas.domain.usecases.EventUseCase
import com.universal.fiestamas.domain.usecases.ServiceUseCase
import com.universal.fiestamas.domain.usecases.SharedPrefsUseCase
import com.universal.fiestamas.presentation.utils.extensions.hasBeenAlreadySentToServer
import com.universal.fiestamas.presentation.utils.extensions.or
import com.universal.fiestamas.presentation.utils.getFileNameFromUri
import com.universal.fiestamas.presentation.utils.showToast
import com.universal.fiestamas.presentation.utils.toUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val serviceUseCase: ServiceUseCase,
    private val eventUseCase: EventUseCase,
    private val sharedPrefsUseCase: SharedPrefsUseCase
) : ViewModel() {

    var alreadyLikedService = false
    private var alreadyCreatedServiceForProvider = false
    private var alreadyUpdatedServiceForProvider = false
    private var gotServicesByServiceTypeId = false
    private var gotServicesBySubServiceTypeId = false
    private var gotAttributesByServiceCategoryId = false
    private var gotAttributesByServiceTypeId = false
    private var gotAttributesBySubServiceId = false
    private var alreadyUploadedMediaFiles = false
    private var gotServicesByServiceCategoryId = false
    private var alreadyCreatedEventByProvider = false

    private val _servicesByType = MutableStateFlow<List<Service?>?>(null)
    val servicesByType: StateFlow<List<Service?>?>
        get() = _servicesByType

    private val _likedServices = MutableStateFlow<List<Service>>(listOf())
    val likedServices: StateFlow<List<Service>>
        get() = _likedServices

    private val _onServiceProviderCreated = MutableStateFlow<Boolean?>(null)
    val onServiceProviderCreated: StateFlow<Boolean?>
        get() = _onServiceProviderCreated

    private val _attributes = MutableStateFlow<List<Attribute>?>(null)
    val attributes: StateFlow<List<Attribute>?>
        get() = _attributes

    private val _mediaLinksAfterSuccessMediaUpload = MutableStateFlow<Pair<List<String>?, List<String>?>?>(null)
    val mediaLinksAfterSuccessMediaUpload: StateFlow<Pair<List<String>?, List<String>?>?>
        get() = _mediaLinksAfterSuccessMediaUpload

    private val _allServicesProvider = MutableStateFlow<List<Service>>(emptyList())
    val allServicesProvider: StateFlow<List<Service>>
        get() = _allServicesProvider

    private val _eventCreated = MutableStateFlow<CreateEventResponseV2?>(null)
    val eventCreated: StateFlow<CreateEventResponseV2?>
        get() = _eventCreated

    private val _showProgressDialogForProvider = MutableStateFlow(false)
    val showProgressDialogForProvider: StateFlow<Boolean>
        get() = _showProgressDialogForProvider

    private val _serviceHasActiveEvents = MutableStateFlow<Boolean?>(null)
    val serviceHasActiveEvents: StateFlow<Boolean?>
        get() = _serviceHasActiveEvents

    val attributesSelected = mutableSetOf<String>()

    fun getServicesAccordingData(context: Context, screenInfo: ScreenInfo) {
        if (screenInfo.subService != null) {
            getServicesBySubServiceTypeId(screenInfo.subService.id)
        } else if (screenInfo.serviceType != null) {
            getServicesByServiceTypeId(screenInfo.serviceType.id)
        } else {
            screenInfo.serviceCategory?.id?.let { idServiceCategory ->
                getServicesByServiceCategoryId(idServiceCategory)
            }.or {
                showToast(context, "Error: Id es null!")
            }
        }
    }

    private fun getServicesByServiceCategoryId(serviceCategoryId: String) {
        if (!gotServicesByServiceCategoryId) {
            gotServicesByServiceCategoryId = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.getServicesOptionsByServiceCategoryId(serviceCategoryId).collect { services ->
                    _servicesByType.value = services.filter { it.active == true }
                }
            }
        }
    }

    private fun getServicesByServiceTypeId(serviceTypeId: String) {
        if (!gotServicesByServiceTypeId) {
            gotServicesByServiceTypeId = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.getServicesOptionsByServiceTypeId(serviceTypeId).collect { services ->
                    _servicesByType.value = services.filter { it.active == true }
                }
            }
        }
    }

    private fun getServicesBySubServiceTypeId(subServiceTypeId: String) {
        if (!gotServicesBySubServiceTypeId) {
            gotServicesBySubServiceTypeId = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.getServicesOptionsBySubServiceTypeId(subServiceTypeId).collect { services ->
                    _servicesByType.value = services.filter { it.active == true }
                }
            }
        }
    }

    fun getAttributesByServiceCategoryId(serviceCategoryId: String) {
        if (!gotAttributesByServiceCategoryId) {
            gotAttributesByServiceCategoryId = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.getAttributesByServiceCategoryId(serviceCategoryId).collectLatest { attributes ->
                    _attributes.value = attributes
                }
            }
        }
    }

    fun getAttributesByServiceTypeId(serviceTypeId: String) {
        if (!gotAttributesByServiceTypeId) {
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.getAttributesByServiceTypeId(serviceTypeId).collectLatest { attributes ->
                    _attributes.value = attributes
                }
            }
        }
    }

    fun getAttributesBySubServiceId(subServiceId: String) {
        if (!gotAttributesBySubServiceId) {
            gotAttributesBySubServiceId = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.getAttributesBySubServiceId(subServiceId).collectLatest { attributes ->
                    _attributes.value = attributes
                }
            }
        }
    }

    fun markAttributeAsSelected(attribute: Attribute) {
        _attributes.value = _attributes.value?.map {
            if (it.id == attribute.id) {
                it.copy(isSelected = !it.isSelected)
            } else {
                it
            }
        }
    }

    fun likeService(userId: String, serviceId: String, onFinished: () -> Unit) {
        if (!alreadyLikedService) {
            alreadyLikedService = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.likeService(userId, serviceId).collectLatest { response ->
                    when (response) {
                        is BaseResult.Success -> println("Success: ${response.data}")
                        is BaseResult.Error -> println("Error: ${response.rawResponse.message}")
                    }; onFinished()
                }
            }
        }
    }

    fun uploadMediaFilesIfNecessary(images: List<UriFile?>, videos: List<UriFile?>, isEditing: Boolean) {
        if (!alreadyUploadedMediaFiles) {
            alreadyUploadedMediaFiles = true
            if (!isEditing) {
                viewModelScope.launch(Dispatchers.IO) {
                    serviceUseCase.uploadMediaFiles(images, videos).collectLatest { pair ->
                        _mediaLinksAfterSuccessMediaUpload.value = pair
                    }
                }
            } else {
                val alreadySentImages = mutableListOf<UriFile?>()
                val notSentImages = mutableListOf<UriFile?>()
                images.forEach {
                    if (it?.uri?.path.hasBeenAlreadySentToServer()) {
                        alreadySentImages.add(it)
                    } else {
                        notSentImages.add(it)
                    }
                }

                val alreadySentVideos = mutableListOf<UriFile?>()
                val notSentVideos = mutableListOf<UriFile?>()
                videos.forEach {
                    if (it?.uri?.path.hasBeenAlreadySentToServer()) {
                        alreadySentVideos.add(it)
                    } else {
                        notSentVideos.add(it)
                    }
                }

                viewModelScope.launch(Dispatchers.IO) {
                    if (notSentImages.isEmpty() && notSentVideos.isEmpty()) {
                        val imagesUrl = alreadySentImages.map { it?.url ?: "" }
                        val videosUrl = alreadySentVideos.map { it?.url ?: "" }
                        _mediaLinksAfterSuccessMediaUpload.value = Pair(imagesUrl, videosUrl)
                    } else {
                        serviceUseCase.uploadMediaFiles(notSentImages, notSentVideos).collectLatest { pair ->
                            val imagesUrl = pair.first + alreadySentImages.map { it?.url ?: "" }
                            val videosUrl = pair.second + alreadySentVideos.map { it?.url ?: "" }
                            _mediaLinksAfterSuccessMediaUpload.value = Pair(imagesUrl, videosUrl)
                        }
                    }
                }
            }
        }
    }

    fun createServiceForProvider(request: AddServiceProviderRequest) {
        if (!alreadyCreatedServiceForProvider) {
            alreadyCreatedServiceForProvider =  true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.createServiceForProvider(request).collectLatest { response ->
                    when (response) {
                        is BaseResult.Success -> _onServiceProviderCreated.value = true
                        is BaseResult.Error -> _onServiceProviderCreated.value = false
                    }
                }
            }
        }
    }

    fun updateServiceForProvider(serviceId: String, request: UpdateServiceProviderRequest) {
        if (!alreadyUpdatedServiceForProvider) {
            alreadyUpdatedServiceForProvider = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.updateServiceForProvider(serviceId, request).collectLatest { response ->
                    when (response) {
                        is BaseResult.Success -> _onServiceProviderCreated.value = true
                        is BaseResult.Error -> _onServiceProviderCreated.value = false
                    }
                }
            }
        }
    }

    fun getLinkedStrings(screenInfo: ScreenInfo): List<String> {
        return listOf(
            screenInfo.serviceCategory?.name.orEmpty(),
            screenInfo.serviceType?.name.orEmpty(),
            screenInfo.subService?.name.orEmpty()
        )
    }

    fun getAllServicesByProvider(providerId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            serviceUseCase.getServicesByProviderId(providerId).collectLatest {
                _allServicesProvider.value = it
            }
        }
    }

    fun createEventByProvider(
        clientId: String,
        eventId: String,
        questions: FirstQuestionsProvider
    ) {
        if (!alreadyCreatedEventByProvider) {
            alreadyCreatedEventByProvider =  true
            _showProgressDialogForProvider.value = true
            viewModelScope.launch(Dispatchers.IO) {
                eventUseCase.createEventByProvider(clientId, eventId, questions).collectLatest { response ->
                    _showProgressDialogForProvider.value = false
                    _eventCreated.value = response
                }
            }
        }
    }

    fun checkIfServiceHasActiveEvents(serviceId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            eventUseCase.getServicesEventByService(serviceId).collectLatest { list ->
                val filteredList = list.filter { (it.date ?: Timestamp.now()) >= Timestamp.now() }
                _serviceHasActiveEvents.value = filteredList.isNotEmpty()
            }
        }
    }

    fun getFavouriteServices(firebaseUserDb: FirebaseUserDb) {
        if (firebaseUserDb.likes?.isNotEmpty() == true) {
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.getFavouriteServices(firebaseUserDb.likes).collect {
                    _likedServices.value = it
                }
            }
        }
    }

    private fun getVideoFromCameraIfExists(): Uri? {
        return sharedPrefsUseCase.getVideoFromCamera()
    }

    fun convertListOfStringsIntoUriFiles(context: Context, videos: List<String>): List<UriFile> {
        val i: MutableList<UriFile> = videos.map { it.toUri() }.toMutableList()
        val videoUri = getVideoFromCameraIfExists()
        if (videoUri != null) {
            val uriFile = UriFile(videoUri, getFileNameFromUri(context, videoUri, true))
            i.add(uriFile)
        }
        return i.toList()
    }

    fun resetVideoFromCameraInSharedPreferences() {
        sharedPrefsUseCase.resetVideoFromCamera()
    }

    fun getUserAddressIfExists(): Address? = sharedPrefsUseCase.getUserAddress()
}
