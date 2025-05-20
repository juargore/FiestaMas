package com.universal.fiestamas.presentation.screens.auth

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.UriFile
import com.universal.fiestamas.presentation.screens.location.AddressAutoCompleteScreen
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape30
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.CircleAvatarWithInitials
import com.universal.fiestamas.presentation.ui.HorizontalProgressBar
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.TextBold
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.ValidationText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.bottom_sheets.BottomSheetCameraOrGallery
import com.universal.fiestamas.presentation.ui.bottom_sheets.ImageSource
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.utils.ComposeFileProvider
import com.universal.fiestamas.presentation.utils.bitmapToUri
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.getFileNameFromUri
import com.universal.fiestamas.presentation.utils.rotateBitmap
import com.universal.fiestamas.presentation.utils.scaleBitmapForMMS
import com.universal.fiestamas.presentation.utils.showToast
import com.universal.fiestamas.presentation.utils.uriToResizedBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateBusinessScreenV2(
    vm: AuthViewModel = hiltViewModel(),
    onRedirectToCreateContact: (
        businessName: String,
        businessAddress: Address,
        businessPhotoUrl: String?
    ) -> Unit,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    var tempPhoto: Uri? = null
    var currentImage: UriFile? by remember { mutableStateOf(null) }
    var businessName by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var showValidationName by remember { mutableStateOf(false) }
    var showValidationAddress by remember { mutableStateOf(false) }
    var showProgressDialog by remember { mutableStateOf(false) }
    var bottomSheetContent by remember { mutableStateOf(BottomSheetBusinessContent.AutoComplete) }

    var addressObj: Address? by rememberSaveable { mutableStateOf(null) }
    var city by rememberSaveable { mutableStateOf("") }
    var state by rememberSaveable { mutableStateOf("") }
    var postalCode by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf("") }
    var latitude by rememberSaveable { mutableStateOf("") }
    var longitude by rememberSaveable { mutableStateOf("") }

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.Expanded },
        skipHalfExpanded = true
    )

    LaunchedEffect(businessName, address) {
        showValidationName = businessName.isBlank()
        showValidationAddress = address.isBlank()
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempPhoto?.let { uri ->
                    uriToResizedBitmap(context, uri)?.let { bmp ->
                        coroutineScope.launch(Dispatchers.IO) {
                            val bitmap = rotateBitmap(scaleBitmapForMMS(context, bmp), 90f)
                            bitmapToUri(context, bitmap, uri)?.let { newUri ->
                                currentImage = UriFile(newUri, getFileNameFromUri(context, newUri))
                            }
                        }
                    }
                }
            }
        }
    )

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                currentImage = UriFile(it, getFileNameFromUri(context, it))
            }
        }
    )

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = ComposeFileProvider.getImageUri(context)
            tempPhoto = uri
            cameraLauncher.launch(uri)
        } else {
            showToast(context, "El permiso para la cámara es necesario")
        }
    }

    ProgressDialog(showProgressDialog)

    GradientBackground(
        content = {
            ModalBottomSheetLayout(
                sheetState = modalSheetState,
                sheetContent = {
                    when (bottomSheetContent) {
                        BottomSheetBusinessContent.AutoComplete -> {
                            AddressAutoCompleteScreen(
                                showMapOption = true
                            ) { mAddress: Address?, _ ->
                                if (mAddress != null) {
                                    addressObj = mAddress
                                    address = mAddress.line1.orEmpty()
                                    city = mAddress.city.orEmpty()
                                    state = mAddress.state.orEmpty()
                                    postalCode = mAddress.zipcode.orEmpty()
                                    country = mAddress.country.orEmpty()
                                    latitude = mAddress.location?.lat.toString()
                                    longitude = mAddress.location?.lng.toString()
                                }
                                coroutineScope.launch { modalSheetState.hide() }
                            }
                        }
                        BottomSheetBusinessContent.ImagePicker -> {
                            BottomSheetCameraOrGallery { imgSource ->
                                coroutineScope.launch { modalSheetState.hide() }
                                when (imgSource) {
                                    ImageSource.Camera -> {
                                        val permissionGranted = PackageManager.PERMISSION_GRANTED
                                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != permissionGranted) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                launcher.launch(Manifest.permission.CAMERA)
                                            }
                                        } else {
                                            val uri = ComposeFileProvider.getImageUri(context)
                                            tempPhoto = uri
                                            cameraLauncher.launch(uri)
                                        }
                                    }
                                    ImageSource.Gallery -> imagePicker.launch("image/*")
                                }
                            }
                        }
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                            })
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        HorizontalProgressBar(Modifier.weight(0.3f), isSelected = true)
                        HorizontalProgressBar(Modifier.weight(0.3f), isSelected = true)
                        HorizontalProgressBar(Modifier.weight(0.3f))
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .sidePadding(40.dp)
                    ) {
                        VerticalSpacer(height = 50.dp)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .height(160.dp)
                                .fillMaxWidth()
                        ) {
                            CircleAvatarWithInitials(
                                name = businessName,
                                textColor = PinkFiestamas,
                                circleSize = 160.dp,
                                textSize = 60.sp,
                                isBold = true,
                                backgroundColor = Color.LightGray
                            )
                            if (businessName.isBlank()) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_briefcase),
                                    modifier = Modifier.size(100.dp.autoSize()),
                                    contentDescription = null
                                )
                            }
                            if (currentImage != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(currentImage!!.uri),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(155.dp.autoSize())
                                        .clip(CircleShape)
                                        .align(Alignment.Center)
                                )
                            }
                        }

                        TextMedium(
                            text = "Agregar / Editar foto",
                            color = Color.LightGray,
                            modifier = Modifier
                                .padding(vertical = 6.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        bottomSheetContent = BottomSheetBusinessContent.ImagePicker
                                        modalSheetState.show()
                                    }
                                }
                        )

                        VerticalSpacer(height = 40.dp)

                        RoundedEdittext(
                            isForV2 = true,
                            value = businessName,
                            placeholder = stringResource(R.string.business_name),
                            onValueChange = { businessName = it }
                        )

                        ValidationText(
                            show = showValidationName,
                            isForV2 = true,
                            color = Color.White,
                            fillMaxWidth = false,
                            text = "Ingrese un nombre de negocio",
                        )

                        VerticalSpacer(height = 30.dp)

                        RoundedEdittext(
                            placeholder = stringResource(R.string.business_address),
                            value = address,
                            isForV2 = true,
                            isEnabled = false,
                            singleLine = true,
                            onClicked = {
                                coroutineScope.launch {
                                    bottomSheetContent = BottomSheetBusinessContent.AutoComplete
                                    modalSheetState.show()
                                }
                            },
                            onValueChange = { address = it }
                        )

                        ValidationText(
                            show = showValidationAddress,
                            isForV2 = true,
                            color = Color.White,
                            fillMaxWidth = false,
                            text = "Debes ingresar una dirección válida",
                        )

                        VerticalSpacer(height = 20.dp)

                        Box(
                            contentAlignment = Alignment.BottomCenter,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 30.dp),
                        ) {
                            ButtonPinkRoundedCornersV2(
                                verticalPadding = 10.dp,
                                horizontalPadding = 35.dp,
                                shape = allRoundedCornerShape30,
                                addBorder = true,
                                borderColor = Color.White,
                                content = {
                                    TextBold(
                                        text = stringResource(R.string.gral_continue).uppercase(),
                                        size = 18.sp,
                                        color = Color.White,
                                        fillMaxWidth = false
                                    )
                                },
                                onClick = {
                                    if (businessName.isBlank()) {
                                        showToast(context, "Ingrese un nombre de negocio")
                                        return@ButtonPinkRoundedCornersV2
                                    }
                                    if (address.isBlank() || addressObj == null) {
                                        showToast(context, "Debes ingresar una dirección válida")
                                        return@ButtonPinkRoundedCornersV2
                                    }
                                    showProgressDialog = true
                                    vm.uploadMediaFileAndGetUrl(currentImage) {
                                        showProgressDialog = false
                                        Handler(Looper.getMainLooper()).post {
                                            onRedirectToCreateContact(businessName, addressObj!!, it)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        titleScreen = "Registro",
        titleScreenColor = Color.White,
        isPinkBackground = true,
        showBackButton = true,
        showLogoFiestamas = false,
        backButtonColor = Color.White,
        showUserName = false,
        addBottomPadding = false,
        onBackButtonClicked = { onBackClicked() }
    )
}

enum class BottomSheetBusinessContent {
    AutoComplete,
    ImagePicker
}
