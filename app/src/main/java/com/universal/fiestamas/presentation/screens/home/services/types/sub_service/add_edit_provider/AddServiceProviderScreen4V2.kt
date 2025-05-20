package com.universal.fiestamas.presentation.screens.home.services.types.sub_service.add_edit_provider

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.AddressData
import com.universal.fiestamas.domain.models.Attribute
import com.universal.fiestamas.domain.models.ServiceCategory
import com.universal.fiestamas.domain.models.ServiceType
import com.universal.fiestamas.domain.models.SubService
import com.universal.fiestamas.domain.models.SuggestedAttribute
import com.universal.fiestamas.domain.models.UriFile
import com.universal.fiestamas.domain.models.request.AddServiceProviderRequest
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.home.main.MainParentClass
import com.universal.fiestamas.presentation.screens.home.services.types.sub_service.options_client.ServicesViewModel
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape24
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape30
import com.universal.fiestamas.presentation.theme.topRoundedCornerShape15
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.CardPhotoV2
import com.universal.fiestamas.presentation.ui.HorizontalProgressView
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.bottom_sheets.BottomSheetCameraOrGallery
import com.universal.fiestamas.presentation.ui.bottom_sheets.ImageSource
import com.universal.fiestamas.presentation.ui.bottom_sheets.MediaSource
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.utils.ComposeFileProvider
import com.universal.fiestamas.presentation.utils.Constants.BY_KG
import com.universal.fiestamas.presentation.utils.Constants.BY_PERSON
import com.universal.fiestamas.presentation.utils.Constants.BY_PIECE
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.shouldShowRequestPermissionRationaleForCamera
import com.universal.fiestamas.presentation.utils.extensions.showPermissionDeniedDialogForCamera
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.extensions.toColor
import com.universal.fiestamas.presentation.utils.getFileNameFromUri
import com.universal.fiestamas.presentation.utils.showToast
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.NoDragCancelledAnimation
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyGridState
import org.burnoutcrew.reorderable.reorderable

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddServiceProviderScreen4V2(
    vm: ServicesViewModel = hiltViewModel(),
    vma: AuthViewModel = hiltViewModel(),
    serviceCategory: ServiceCategory,
    serviceType: ServiceType?,
    subService: SubService?,
    selectedAttributes: List<Attribute>,
    suggestedAttributes: List<String>,
    serviceName: String,
    serviceDesc: String,
    serviceMin: String,
    serviceMax: String,
    servicePrice: String,
    serviceUnity: String,
    address: AddressData,
    distance: Int,
    onSuccessServiceCreated: () -> Unit,
    onBackClicked: () -> Unit
) {

    vma.getFirebaseUserDb(MainParentClass.userId)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userDb by vma.firebaseUserDb.collectAsState()
    val mediaLinks by vm.mediaLinksAfterSuccessMediaUpload.collectAsState()
    val onServiceProviderCreated by vm.onServiceProviderCreated.collectAsState()

    var mediaSource: MediaSource? by remember { mutableStateOf(null) }
    var showProgressDialog by remember { mutableStateOf(false) }
    var imageList: List<UriFile> by remember { mutableStateOf(emptyList()) }
    var tempPhoto: Uri? by remember { mutableStateOf(null) }
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    val state = rememberReorderableLazyGridState(
        dragCancelledAnimation = NoDragCancelledAnimation(),
        onMove = { from, to ->
            imageList = imageList.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
        }
    )

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

    mediaLinks?.let { pair ->
        val imageLinks = pair.first
        val videoLinks = pair.second

        val unity = when (serviceUnity) {
            BY_PERSON -> "person"
            BY_PIECE -> "pz"
            BY_KG -> "kg"
            else -> "event"
        }
        val requestToCreate = AddServiceProviderRequest(
            id_service_category = serviceCategory.id,
            id_service_type = serviceType?.id.orEmpty(),
            id_sub_service_type = subService?.id.orEmpty(),
            id_provider = userDb?.id.orEmpty(),
            provider_name = userDb?.business_name.orEmpty(),
            name = serviceName,
            description = serviceDesc,
            icon = imageLinks?.first().orEmpty(),
            image = imageLinks?.first().orEmpty(),
            rating = 5,
            min_attendees = serviceMin.toInt(),
            max_attendees = serviceMax.toInt(),
            price = servicePrice.toInt(),
            attributes = selectedAttributes.map { it.name },
            images = imageLinks ?: emptyList(),
            videos = videoLinks ?: emptyList(),
            unit = unity,
            distance = distance,
            address = address.address.trim(),
            lat = address.latitude,
            lng = address.longitude
        )

        val sAttributes = mutableListOf<SuggestedAttribute>()
        suggestedAttributes.forEach { att ->
            sAttributes.add(
                SuggestedAttribute(
                    name = att,
                    provider = userDb?.name.orEmpty(),
                    email = userDb?.email.orEmpty(),
                    name_service_category = serviceCategory.name,
                    name_service_type = serviceType?.name.orEmpty(),
                    name_sub_service_type = subService?.name.orEmpty()
                )
            )
        }

        vm.createServiceForProvider(requestToCreate)
    }

    if (onServiceProviderCreated == true) {
        showProgressDialog = false
        onSuccessServiceCreated()
    }

    ProgressDialog(showProgressDialog)

    GradientBackground(
        content = {
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
                            else -> Unit
                        }
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background("#f3f1f4".toColor())
                ) {
                    VerticalSpacer(height = 10.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        HorizontalProgressView(
                            totalBars = 8,
                            totalSelected = 7,
                            selectedColor = PinkFiestamas,
                            unselectedColor = Color.LightGray
                        )
                    }
                    if (imageList.isEmpty()) {
                        EmptyImageListView {
                            coroutineScope.launch {
                                mediaSource = MediaSource.Image
                                modalSheetState.show()
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .weight(0.8f)
                                .sidePadding(10.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TextBold(
                                modifier = Modifier.padding(top = 30.dp, bottom = 5.dp),
                                text = "Galería",
                                size = 20.sp,
                                color = PinkFiestamas
                            )
                            TextRegular(
                                text = "Organízalas a tu gusto, la primera imagen será la de portada",
                            )
                            VerticalSpacer(height = 20.dp)

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                state = state.gridState,
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                                modifier = Modifier
                                    .reorderable(state)
                                    .detectReorderAfterLongPress(state)
                                    .width(250.dp)
                            ) {
                                items(imageList, { it }) { item ->
                                    ReorderableItem(state, key = item) { //isDragging ->
                                        Box(
                                            modifier = Modifier.aspectRatio(1f)
                                        ) {
                                            CardPhotoV2(
                                                uriFile = item,
                                                onDelete = {
                                                    val mImageList = imageList.toMutableList()
                                                    mImageList.remove(item)
                                                    imageList = mImageList
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Column(
                            modifier = Modifier
                                .weight(0.2f)
                                .padding(bottom = 20.dp),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_add_fiestamas),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp.autoSize())
                                    .clickable {
                                        coroutineScope.launch {
                                            mediaSource = MediaSource.Image
                                            modalSheetState.show()
                                        }
                                    }
                            )
                            VerticalSpacer(height = 10.dp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                ButtonPinkRoundedCornersV2(
                                    verticalPadding = 14.dp,
                                    horizontalPadding = 45.dp,
                                    shape = allRoundedCornerShape24,
                                    content = {
                                        TextBold(
                                            text = "Crear Servicio".uppercase(),
                                            size = 17.sp,
                                            color = Color.White,
                                            fillMaxWidth = false
                                        )
                                    },
                                    onClick = {
                                        if (imageList.isEmpty()) {
                                            showToast(context, "Agrega al menos una imagen para continuar")
                                        } else {
                                            showProgressDialog = true
                                            vm.uploadMediaFilesIfNecessary(imageList, emptyList(), isEditing = false)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        titleScreen = "Nuevo Servicio",
        titleScreenColor = Color.White,
        isPinkBackground = true,
        showBackButton = true,
        backButtonColor = Color.White,
        showLogoFiestamas = false,
        showUserName = false,
        addBottomPadding = false,
        onBackButtonClicked = { onBackClicked() }
    )
}

@Composable
fun EmptyImageListView(onClick: () -> Unit) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box (
            modifier = Modifier
                .size(130.dp)
                .background(Color.White, allRoundedCornerShape30),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(60.dp),
                painter = painterResource(R.drawable.ic_image),
                colorFilter = ColorFilter.tint(PinkFiestamas),
                contentDescription = null
            )
        }
        TextBold(
            modifier = Modifier.padding(vertical = 10.dp),
            text = "Cargar galería",
            size = 20.sp,
            color = PinkFiestamas
        )
    }
}

@Preview
@Composable
fun EmptyImageListViewPreview() {
    EmptyImageListView {

    }
}
