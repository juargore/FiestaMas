package com.universal.fiestamas.presentation.screens.home.services.types.sub_service.add_edit_provider

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.ScreenInfo
import com.universal.fiestamas.domain.models.ServiceProviderData
import com.universal.fiestamas.domain.models.UriFile
import com.universal.fiestamas.domain.models.request.AddServiceProviderRequest
import com.universal.fiestamas.domain.models.request.UpdateServiceProviderRequest
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.home.main.MainParentClass
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.ServicesViewModel
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.details.DetailsServiceViewModel
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.topRoundedCornerShape15
import com.universal.fiestamas.presentation.ui.ButtonAddMediaFile
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCorners
import com.universal.fiestamas.presentation.ui.CardPhotoOrVideo
import com.universal.fiestamas.presentation.ui.LinkedStrings
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.TitleWithTopLine
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.CardServicesBackground
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.bottom_sheets.BottomSheetCameraOrGallery
import com.universal.fiestamas.presentation.ui.bottom_sheets.ImageSource
import com.universal.fiestamas.presentation.ui.bottom_sheets.MediaSource
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.utils.ComposeFileProvider
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.resetApplication
import com.universal.fiestamas.presentation.utils.extensions.shouldShowRequestPermissionRationaleForCamera
import com.universal.fiestamas.presentation.utils.extensions.showPermissionDeniedDialogForCamera
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.extensions.toUnit
import com.universal.fiestamas.presentation.utils.getFileNameFromUri
import com.universal.fiestamas.presentation.utils.showToast
import com.universal.fiestamas.presentation.utils.toUri
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddEditProviderSecondStepScreen(
    vm: ServicesViewModel = hiltViewModel(),
    vmd: DetailsServiceViewModel = hiltViewModel(),
    vma: AuthViewModel = hiltViewModel(),
    isEditing: Boolean,
    screenInfo: ScreenInfo,
    images: List<String>,
    videos: List<String>,
    serviceProviderData: ServiceProviderData,
    onTitleClickedOrServiceAdded: () -> Unit,
    onOpenRecordingScreen: () -> Unit,
    onBackClicked: () -> Unit
) {

    screenInfo.service?.id?.let {
        vmd.getServiceDetails(it, false)
    }

    if (!isEditing) {
        vm.getAllServicesByProvider(MainParentClass.userId.orEmpty())
    }

    val context = LocalContext.current
    val service by vmd.service.collectAsState()
    val userDb by vma.firebaseUserDb.collectAsState()
    val allServices by vm.allServicesProvider.collectAsState()
    val mediaLinks by vm.mediaLinksAfterSuccessMediaUpload.collectAsState()
    val onServiceProviderCreated by vm.onServiceProviderCreated.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    var mediaSource: MediaSource? by remember { mutableStateOf(null) }
    var showProgressDialog by remember { mutableStateOf(false) }
    var imageList by remember { mutableStateOf(images.map { it.toUri() }) }
    var videoList by remember { mutableStateOf(
        vm.convertListOfStringsIntoUriFiles(context, videos)
    ) }
    var tempPhoto: Uri? by remember { mutableStateOf(null) }
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    BackHandler(modalSheetState.isVisible) {
        coroutineScope.launch { modalSheetState.hide() }
    }

    vma.getFirebaseUserDb(MainParentClass.userId)

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                val mImageList = imageList.toMutableList()
                tempPhoto?.let {
                    val uriFile = UriFile(it, getFileNameFromUri(context, it))
                    mImageList.add(uriFile)
                }
                imageList = mImageList
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = ComposeFileProvider.getImageUri(context)
            tempPhoto = uri
            cameraLauncher.launch(uri)
        } else {
            showPermissionDeniedDialogForCamera(context)
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            val mImageList = imageList.toMutableList()
            uri?.let {
                val uriFile = UriFile(it, getFileNameFromUri(context, it))
                mImageList.add(uriFile)
            }
            imageList = mImageList
        }
    )

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            val mVideoList = videoList.toMutableList()
            uri?.let {
                val uriFile = UriFile(it, getFileNameFromUri(context, uri))
                mVideoList.add(uriFile)
            }
            videoList = mVideoList
        }
    )

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = topRoundedCornerShape15,
        sheetContent = {
            BottomSheetCameraOrGallery(mediaSource = mediaSource) { imgSource ->
                coroutineScope.launch { modalSheetState.hide() }
                when (mediaSource) {
                    MediaSource.Image -> {
                        when(imgSource) {
                            ImageSource.Camera -> {
                                when {
                                    ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED -> {
                                        val uri = ComposeFileProvider.getImageUri(context)
                                        tempPhoto = uri
                                        cameraLauncher.launch(uri)
                                    }
                                    shouldShowRequestPermissionRationaleForCamera(context) -> {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                    else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                            ImageSource.Gallery -> imagePicker.launch("image/*")
                        }
                    }
                    MediaSource.Video -> {
                        when(imgSource) {
                            ImageSource.Camera -> {
                                onOpenRecordingScreen()
                            }
                            ImageSource.Gallery -> videoPicker.launch("video/*")
                        }
                    }
                    else -> Unit
                }
            }
        }
    ) {
        val titlesList = listOf(
            screenInfo.serviceCategory?.name.orEmpty(),
            screenInfo.serviceType?.name.orEmpty(),
            screenInfo.subService?.name.orEmpty()
        )

        if (onServiceProviderCreated == true) {
            showProgressDialog = false
            if (isEditing) {
                showToast(context, context.getString(R.string.edit_service_success))
            } else {
                showToast(context, context.getString(R.string.add_service_success))
            }
            context.resetApplication(400L)
        }

        if (onServiceProviderCreated == false) {
            showProgressDialog = false
            showToast(context, context.getString(R.string.add_service_error))
        }

        mediaLinks?.let { pair ->
            val imageLinks = pair.first
            val videoLinks = pair.second

            with(serviceProviderData) {
                if (isEditing) {
                    val requestToUpdate = UpdateServiceProviderRequest(
                        name = name,
                        price = cost.toInt(),
                        unit = unit.toUnit(),
                        distance = distance,
                        description = description,
                        min_attendees = minCapacity.toInt(),
                        max_attendees = maxCapacity.toInt(),
                        images = imageLinks ?: emptyList(),
                        videos = videoLinks ?: emptyList(),
                        address = addressData?.address?.trim().orEmpty(),
                        lat = addressData?.latitude.orEmpty(),
                        lng = addressData?.longitude.orEmpty(),
                        attributes = attributes,
                        requested_attribute = suggestedAttribute
                    )
                    if (service?.id != null) {
                        vm.updateServiceForProvider(service?.id!!, requestToUpdate)
                    } else {
                        showProgressDialog = false
                        showToast(context, "El ID del servicio es null :(")
                    }
                } else {
                    val serviceCategoryId = screenInfo.serviceCategory?.id.orEmpty()
                    val subServiceTypeId = screenInfo.subService?.id.orEmpty()
                    val serviceTypeId = screenInfo.serviceType?.id.orEmpty()

                    val requestToCreate = AddServiceProviderRequest(
                        id_service_category = serviceCategoryId,
                        id_service_type = serviceTypeId,
                        id_sub_service_type = subServiceTypeId,
                        id_provider = userDb?.id.orEmpty(),
                        provider_name = userDb?.business_name.orEmpty(),
                        name = name,
                        description = description,
                        icon = imageLinks?.first().orEmpty(),
                        image = imageLinks?.first().orEmpty(),
                        rating = 5,
                        min_attendees = minCapacity.toInt(),
                        max_attendees = maxCapacity.toInt(),
                        price = cost.toInt(),
                        attributes = attributes,
                        images = imageLinks ?: emptyList(),
                        videos = videoLinks ?: emptyList(),
                        unit = unit.toUnit(),
                        distance = distance,
                        address = addressData?.address?.trim().orEmpty(),
                        lat = addressData?.latitude.orEmpty(),
                        lng = addressData?.longitude.orEmpty(),
                        requested_attribute = suggestedAttribute
                    )
                    vm.createServiceForProvider(requestToCreate)
                }
            }
        }

        ProgressDialog(
            isVisible = showProgressDialog,
            message = stringResource(id = R.string.add_service_sending_info)
        )

        GradientBackground(
            content = {
                Column(modifier = Modifier
                    .padding(bottom = 15.dp)
                    .sidePadding()
                ) {
                    if (isEditing) {
                        listOf(service?.name)
                    } else {
                        LinkedStrings(titlesList, Modifier.padding(start = 10.dp), small = true)
                    }

                    VerticalSpacer(10.dp)

                    CardServicesBackground {
                        Column(modifier = Modifier.padding(15.dp)) {
                            VerticalSpacer(height = 10.dp)

                            val title = if (isEditing) {
                                stringResource(id = R.string.edit_service_title)
                            } else {
                                stringResource(id = R.string.add_service_title)
                            }

                            TextSemiBold(
                                text = title,
                                color = PinkFiestamas,
                                size = 19.sp.autoSize()
                            )

                            TextMedium(text = serviceProviderData.name)

                            VerticalSpacer(height = 25.dp)

                            TitleWithTopLine(
                                text = stringResource(id = R.string.add_service_photos),
                                icon = R.drawable.ic_camera
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ButtonAddMediaFile {
                                    coroutineScope.launch {
                                        mediaSource = MediaSource.Image
                                        modalSheetState.show()
                                    }
                                }
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(imageList) {
                                        CardPhotoOrVideo(uriFile = it) { uriFile: UriFile ->
                                            val mImageList = imageList.toMutableList()
                                            mImageList.remove(uriFile)
                                            imageList = mImageList
                                        }
                                    }
                                }
                            }

                            VerticalSpacer(height = 20.dp)
                            TitleWithTopLine(
                                text = stringResource(id = R.string.add_service_videos),
                                icon = R.drawable.ic_play_one,
                                iconSize = 22.dp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ButtonAddMediaFile {
                                    coroutineScope.launch {
                                        mediaSource = MediaSource.Video
                                        modalSheetState.show()
                                    }
                                }
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(videoList) {
                                        CardPhotoOrVideo(uriFile = it, addShadow = true) { uriFile: UriFile ->
                                            val mVideoList = videoList.toMutableList()
                                            mVideoList.remove(uriFile)
                                            videoList = mVideoList
                                        }
                                    }
                                }
                            }

                            VerticalSpacer(height = 50.dp)
                            Box(modifier = Modifier.fillMaxWidth()) {
                                ButtonPinkRoundedCorners(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = stringResource(id = R.string.add_service_finish)
                                ) {
                                    if (!isEditing) {
                                        var allowSaving: Boolean
                                        allServices.forEach { storedService ->
                                            val storedServiceName = storedService.name.trim().lowercase(Locale.getDefault())
                                            val currentServiceName = serviceProviderData.name.trim().lowercase(Locale.getDefault())
                                            allowSaving = if (storedServiceName == currentServiceName) {
                                                // same name -> check if both belong to same sub-service
                                                if (storedService.id_sub_service_type == (screenInfo.subService?.id ?: "")) {
                                                    // same sub-service, but it could be null, so finally evaluate serviceType
                                                    // name + sub-service + serviceTye are the same -> not possible to add
                                                    storedService.id_service_type != (screenInfo.serviceType?.id ?: "")
                                                } else {
                                                    true
                                                }
                                            } else {
                                                true
                                            }
                                            if (!allowSaving) {
                                                showToast(context, context.getString(R.string.add_service_choose_other_service_name))
                                                return@ButtonPinkRoundedCorners
                                            }
                                        }
                                    }
                                    if (imageList.isEmpty()) {
                                        showToast(context, context.getString(R.string.add_service_add_photo_before_continue))
                                    } else {
                                        showProgressDialog = true
                                        vm.uploadMediaFilesIfNecessary(imageList, videoList, isEditing)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            titleScreen = screenInfo.event.name,
            showLogoFiestamas = false,
            addBottomPadding = false,
            onBackButtonClicked = {
                vm.resetVideoFromCameraInSharedPreferences()
                onBackClicked()
            },
            onTitleScreenClicked = {
                if (screenInfo.event.name.isNotEmpty()) {
                    onTitleClickedOrServiceAdded()
                }
            }
        )
    }
}
