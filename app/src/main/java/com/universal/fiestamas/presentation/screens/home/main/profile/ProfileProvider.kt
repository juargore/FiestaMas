@file:Suppress("UselessCallOnNotNull")
package com.universal.fiestamas.presentation.screens.home.main.profile

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.domain.models.UriFile
import com.universal.fiestamas.domain.models.request.ProviderRequestEdit
import com.universal.fiestamas.domain.models.response.FirebaseUserDb
import com.universal.fiestamas.presentation.screens.auth.AuthViewModel
import com.universal.fiestamas.presentation.screens.location.AddressAutoCompleteScreen
import com.universal.fiestamas.presentation.theme.topRoundedCornerShape15
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCorners
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.RoundedPhoneEdittext
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.ValidationText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.CardAuthBackground
import com.universal.fiestamas.presentation.ui.bottom_sheets.BottomSheetCameraOrGallery
import com.universal.fiestamas.presentation.ui.bottom_sheets.ImageSource
import com.universal.fiestamas.presentation.ui.dialogs.PhoneAlreadyExistsDialog
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.utils.ComposeFileProvider
import com.universal.fiestamas.presentation.utils.bitmapToUri
import com.universal.fiestamas.presentation.utils.cleanPhoneNumberNewFormat
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.shouldShowRequestPermissionRationaleForCamera
import com.universal.fiestamas.presentation.utils.extensions.showPermissionDeniedDialogForCamera
import com.universal.fiestamas.presentation.utils.getFileNameFromUri
import com.universal.fiestamas.presentation.utils.isValidPhoneNumberNewFormat
import com.universal.fiestamas.presentation.utils.isValidRFC
import com.universal.fiestamas.presentation.utils.rotateBitmap
import com.universal.fiestamas.presentation.utils.scaleBitmapForMMS
import com.universal.fiestamas.presentation.utils.showToast
import com.universal.fiestamas.presentation.utils.showToastOnUiThread
import com.universal.fiestamas.presentation.utils.toUri
import com.universal.fiestamas.presentation.utils.uriToResizedBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileProvider(
    vma: AuthViewModel = hiltViewModel(),
    firebaseUserDb: FirebaseUserDb,
    onNavigateUpdatePassword: (email: String, userId: String) -> Unit
) {
    val context = LocalContext.current
    val firebaseUser by vma.firebaseUser.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var currentImage: UriFile? by remember { mutableStateOf(firebaseUserDb.photo?.toUri()) }
    var tempPhoto: Uri? by remember { mutableStateOf(null) }
    var showProgressDialog by remember { mutableStateOf(false) }
    var showPhoneAlreadyExistsDialog by remember { mutableStateOf(false) }
    var showAutoComplete by remember { mutableStateOf(false) }
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    var businessName by rememberSaveable { mutableStateOf(firebaseUserDb.business_name.orEmpty()) }
    var name by rememberSaveable { mutableStateOf(firebaseUserDb.name.orEmpty()) }
    val email by rememberSaveable { mutableStateOf(firebaseUserDb.email.orEmpty()) }
    var lastName by rememberSaveable { mutableStateOf(firebaseUserDb.last_name.orEmpty()) }
    var mobilePhone by rememberSaveable { mutableStateOf(firebaseUserDb.phone_one.orEmpty()) }
    var homePhone by rememberSaveable { mutableStateOf(firebaseUserDb.phone_two.orEmpty()) }
    var rfc by rememberSaveable { mutableStateOf(firebaseUserDb.rfc.orEmpty()) }
    var address by rememberSaveable { mutableStateOf(firebaseUserDb.address.orEmpty()) }
    var city by rememberSaveable { mutableStateOf(firebaseUserDb.city.orEmpty()) }
    var state by rememberSaveable { mutableStateOf(firebaseUserDb.state.orEmpty()) }
    var postalCode by rememberSaveable { mutableStateOf(firebaseUserDb.cp.orEmpty()) }
    var country by rememberSaveable { mutableStateOf(firebaseUserDb.country.orEmpty()) }
    var latitude by rememberSaveable { mutableStateOf(firebaseUserDb.lat) }
    var longitude by rememberSaveable { mutableStateOf(firebaseUserDb.lng) }

    var showValidationBusinessName by remember { mutableStateOf(false) }
    var showValidationTextName by remember { mutableStateOf(false) }
    var showValidationTextLastName by remember { mutableStateOf(false) }
    var showValidationTextAddress by remember { mutableStateOf(false) }
    var showValidationTextPhone1 by remember { mutableStateOf(false) }
    var showValidationTextPhone2 by remember { mutableStateOf(false) }
    var showValidationTextHomePhone by remember { mutableStateOf(false) }
    var showValidationTextRFC by remember { mutableStateOf(false) }

    BackHandler(modalSheetState.isVisible) {
        coroutineScope.launch { modalSheetState.hide() }
    }

    PhoneAlreadyExistsDialog(
        isVisible = showPhoneAlreadyExistsDialog,
        icon = R.drawable.ic_close,
        title = "Error",
        body = "El número de teléfono ya está registrado en otro usuario, ingresa otro.",
        buttonString = "OK",
        onOk = { showPhoneAlreadyExistsDialog = false }
    )

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
            uri?.let {
                currentImage = UriFile(it, getFileNameFromUri(context, it))
            }
        }
    )

    ProgressDialog(showProgressDialog)

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = topRoundedCornerShape15,
        sheetContent = {
            if (showAutoComplete) {
                AddressAutoCompleteScreen(
                    showMapOption = true
                ) { mAddress: Address?, _ ->
                    if (mAddress != null) {
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
            } else {
                BottomSheetCameraOrGallery { imgSource ->
                    coroutineScope.launch { modalSheetState.hide() }
                    when (imgSource) {
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
            }
        }
    ) {
        CardAuthBackground(
            centerContent = false,
            addScroll = true,
            bottomPadding = 10.dp,
            backgroundColor = Color.White,
            content = {

                Row(modifier = Modifier.fillMaxWidth()) {
                    HorizontalSpacer(2.dp)
                    ProfilePhoto(firebaseUserDb, currentImage)
                    HorizontalSpacer(15.dp)

                    Column {
                        TextMedium(
                            text = "$name $lastName",
                            align = TextAlign.Start,
                            size = 18.sp.autoSize()
                        )
                        TextRegular(
                            text = email,
                            align = TextAlign.Start,
                            size = 14.sp.autoSize()
                        )
                        VerticalSpacer(10.dp)
                        OptionsProfileButton(stringResource(R.string.profile_change_photo)) {
                            coroutineScope.launch {
                                showAutoComplete = false
                                modalSheetState.show()
                            }
                        }
                        VerticalSpacer(height = 7.dp)
                        OptionsProfileButton(stringResource(R.string.profile_change_password)) {
                            onNavigateUpdatePassword(email, firebaseUserDb.uid)
                        }
                    }
                }

                VerticalSpacer(15.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    TextSemiBold(
                        modifier = Modifier.align(Alignment.CenterStart),
                        text = stringResource(R.string.profile_edit_profile),
                        size = 22.sp,
                        fillMaxWidth = false
                    )
                    ProfileLogOutButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        vm = vma,
                        uid = firebaseUser?.uid,
                        onStartedLogOutProcess = { showProgressDialog = true },
                        onFinishedLogOutProcess = { showProgressDialog = false }
                    )
                }

                RoundedEdittext(
                    placeholder = stringResource(R.string.business_name),
                    value = businessName
                ) {
                    businessName = it
                    showValidationBusinessName = it.isBlank()
                }

                ValidationText(show = showValidationBusinessName, text = context.getString(R.string.gral_error_empty, "El nombre de negocio"))

                VerticalSpacer(20.dp)

                Text(
                    text = stringResource(R.string.business_contact_info),
                    fontSize = 12.sp.autoSize()
                )

                RoundedEdittext(
                    placeholder = stringResource(R.string.business_contact_name),
                    value = name
                ) {
                    name = it
                    showValidationTextName = it.isBlank()
                }

                ValidationText(show = showValidationTextName, text = context.getString(R.string.gral_error_empty, "El nombre"))

                VerticalSpacer(8.dp)

                RoundedEdittext(
                    placeholder = stringResource(R.string.business_contact_last_name),
                    value = lastName
                ) {
                    lastName = it
                    showValidationTextLastName = it.isBlank()
                }

                ValidationText(show = showValidationTextLastName, text = context.getString(R.string.gral_error_empty, "El apellido"))

                VerticalSpacer(8.dp)

                RoundedEdittext(
                    placeholder = stringResource(R.string.business_contact_address),
                    value = address,
                    isEnabled = false,
                    onClicked = {
                        showValidationTextAddress = false
                        coroutineScope.launch {
                            showAutoComplete = true
                            modalSheetState.show()
                        }
                    },
                    onValueChange = {
                        address = it
                        showValidationTextAddress = it.isBlank()
                    }
                )

                ValidationText(show = showValidationTextAddress, text = context.getString(R.string.gral_error_empty, "El domicilio"))

                VerticalSpacer(8.dp)

                Row(
                    modifier = Modifier.wrapContentWidth()
                ) {
                    RoundedEdittext(
                        modifier = Modifier.width(90.dp.autoSize()),
                        placeholder = stringResource(R.string.business_contact_country),
                        value = stringResource(R.string.business_contact_country_code),
                        isEnabled = false
                    ) {  }
                    HorizontalSpacer(width = 5.dp)
                    RoundedPhoneEdittext(
                        placeholder = stringResource(R.string.business_contact_mobile_phone),
                        value = mobilePhone
                    ) {
                        mobilePhone = it
                        showValidationTextPhone1 = it.isBlank()
                        showValidationTextPhone2 = it.isNotEmpty() && !isValidPhoneNumberNewFormat(it)
                    }
                }

                if (showValidationTextPhone1 || showValidationTextPhone2) {
                    VerticalSpacer(height = 2.dp)
                    TextRegular(
                        text = if (showValidationTextPhone1) {
                            context.getString(R.string.gral_error_empty, "El teléfono móvil")
                        } else {
                            context.getString(R.string.gral_error_phone_formatted, "teléfono móvil")
                        },
                        size = 13.sp,
                        color = Color.Red
                    )
                }

                VerticalSpacer(8.dp)

                Row(
                    modifier = Modifier.wrapContentWidth()
                ) {
                    RoundedEdittext(
                        modifier = Modifier.width(90.dp.autoSize()),
                        placeholder = stringResource(R.string.business_contact_country),
                        value = stringResource(R.string.business_contact_country_code),
                        isEnabled = false
                    ) {  }
                    HorizontalSpacer(width = 5.dp)
                    RoundedPhoneEdittext(
                        placeholder = stringResource(R.string.business_contact_home_phone),
                        value = homePhone
                    ) {
                        homePhone = it
                        showValidationTextHomePhone = it.isNotEmpty() && !isValidPhoneNumberNewFormat(homePhone)
                    }
                }

                ValidationText(show = showValidationTextHomePhone, text = context.getString(R.string.gral_error_phone_formatted, "teléfono fijo"))

                VerticalSpacer(8.dp)

                RoundedEdittext(
                    placeholder = stringResource(R.string.business_contact_rfc),
                    value = rfc
                ) {
                    rfc = it
                    showValidationTextRFC = it.isNotEmpty() && !isValidRFC(it.trim())
                }

                ValidationText(show = showValidationTextRFC, text = context.getString(R.string.gral_error_invalid, "El RFC"))

                VerticalSpacer(30.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    ButtonPinkRoundedCorners(text = stringResource(R.string.profile_update_profile)) {
                        if (businessName.isBlank()) {
                            showToast(context, context.getString(R.string.gral_error_empty, "El nombre de negocio"))
                            return@ButtonPinkRoundedCorners
                        }
                        if (name.isBlank()) {
                            showToast(context, context.getString(R.string.gral_error_empty, "El nombre"))
                            return@ButtonPinkRoundedCorners
                        }
                        if (lastName.isBlank()) {
                            showToast(context, context.getString(R.string.gral_error_empty, "El apellido"))
                            return@ButtonPinkRoundedCorners
                        }
                        if (address.isBlank()) {
                            showToast(context, context.getString(R.string.gral_error_empty, "El domicilio"))
                            return@ButtonPinkRoundedCorners
                        }
                        if (mobilePhone.isBlank()) {
                            showToast(context, context.getString(R.string.gral_error_empty, "El teléfono móvil"))
                            return@ButtonPinkRoundedCorners
                        }
                        if (!isValidPhoneNumberNewFormat(mobilePhone)) {
                            showToast(context, context.getString(R.string.gral_error_phone_formatted, "teléfono móvil"))
                            return@ButtonPinkRoundedCorners
                        }
                        if (homePhone.isNotEmpty()) {
                            if (!isValidPhoneNumberNewFormat(homePhone)) {
                                showToast(context, context.getString(R.string.gral_error_phone_formatted, "teléfono fijo"))
                                return@ButtonPinkRoundedCorners
                            }
                        }
                        if (rfc.isNotEmpty()) {
                            if (!isValidRFC(rfc.trim())) {
                                showToast(context, context.getString(R.string.gral_error_invalid, "El RFC"))
                                return@ButtonPinkRoundedCorners
                            }
                        }

                        showProgressDialog = true
                        vma.updateProviderOnServer(
                            photo = currentImage,
                            providerId = firebaseUser?.uid ?: "",
                            providerRequestEdit = ProviderRequestEdit(
                                business_name = businessName,
                                email = email,
                                name = name,
                                last_name = lastName,
                                phone_one = mobilePhone.cleanPhoneNumberNewFormat(),
                                phone_two = homePhone.cleanPhoneNumberNewFormat(),
                                rfc = rfc.trim(),
                                photo = "",
                                address = address,
                                state = state,
                                city = city,
                                cp = postalCode,
                                country = country,
                                lat = latitude,
                                lng = longitude
                            ),
                            onFinished = { response ->
                                if (response.status == 200) {
                                    showProgressDialog = false
                                    showToastOnUiThread(context, context.getString(R.string.profile_updated_successfully))

                                    businessName = firebaseUserDb.business_name.trim()
                                    name = firebaseUserDb.name.trim()
                                    lastName = firebaseUserDb.last_name.trim()
                                    rfc = firebaseUserDb.rfc.orEmpty().trim()
                                } else {
                                    showProgressDialog = false
                                    showPhoneAlreadyExistsDialog = true
                                }
                            }
                        )
                    }
                }
            }
        )
    }
}
