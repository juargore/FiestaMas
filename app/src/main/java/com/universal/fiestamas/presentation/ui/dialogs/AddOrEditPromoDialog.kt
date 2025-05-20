package com.universal.fiestamas.presentation.ui.dialogs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.UriFile
import com.universal.fiestamas.domain.models.request.ItemEditPromo
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape10
import com.universal.fiestamas.presentation.ui.ButtonAddMediaFile
import com.universal.fiestamas.presentation.ui.ButtonPinkRoundedCornersV2
import com.universal.fiestamas.presentation.ui.CardPhotoOrVideo
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.ValidationText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.bottom_sheets.ImageSource
import com.universal.fiestamas.presentation.utils.ComposeFileProvider
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.convertTimestampToDateYYYYmmDD
import com.universal.fiestamas.presentation.utils.extensions.formatStringToDDMMYYYY
import com.universal.fiestamas.presentation.utils.extensions.isDateEndPriorDateStart
import com.universal.fiestamas.presentation.utils.getFileNameFromUri
import com.universal.fiestamas.presentation.utils.showToast

@Composable
fun AddPromoDialog(
    isVisible: Boolean,
    onAccept: (
        promoName: String,
        startDate: String,
        endDate: String,
        images: List<UriFile>
    ) -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        BaseDialog(
            title = stringResource(R.string.promo_promo),
            addCloseIcon = true,
            isCancelable = true,
            onDismiss = onDismiss,
            content = {
                NewOrEditPromoContent(
                    _promoName = "",
                    _endDate = "",
                    _startDate = "",
                    _images = listOf()
                ) { newName, newStart, newEnd, newImages ->
                    onAccept(newName, newStart, newEnd, newImages)
                }
            }
        )
    }
}

@Composable
fun EditPromoDialog(
    isVisible: Boolean,
    itemEditPromo: ItemEditPromo?,
    onAccept: (
        id: String?,
        newPromoName: String,
        newStartDate: String,
        newEndDate: String,
        newImages: List<UriFile>
    ) -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        BaseDialog(
            title = stringResource(R.string.promo_promo),
            addCloseIcon = true,
            isCancelable = true,
            onDismiss = onDismiss,
            content = {
                NewOrEditPromoContent(
                    _images = itemEditPromo?.images.orEmpty(),
                    _promoName = itemEditPromo?.name.orEmpty(),
                    _endDate = convertTimestampToDateYYYYmmDD(itemEditPromo?.endDate).first,
                    _startDate = convertTimestampToDateYYYYmmDD(itemEditPromo?.startDate).first
                ) { newName, newStart, newEnd, newImages ->
                    onAccept(itemEditPromo?.id, newName, newStart, newEnd, newImages)
                }
            }
        )
    }
}

@Suppress("LocalVariableName")
@Composable
fun NewOrEditPromoContent(
    _promoName: String = "",
    _startDate: String = "",
    _endDate: String = "",
    _images: List<UriFile> = listOf(),
    onOkClicked: (
        promoName: String,
        startDate: String,
        endDate: String,
        images: List<UriFile>
    ) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var promoName by rememberSaveable { mutableStateOf(_promoName) }
    var startDate by rememberSaveable { mutableStateOf(_startDate) }
    var startDateFormatted by rememberSaveable { mutableStateOf(formatStringToDDMMYYYY(_startDate)) }
    var endDate by rememberSaveable { mutableStateOf(_endDate) }
    var endDateFormatted by rememberSaveable { mutableStateOf(formatStringToDDMMYYYY(_endDate)) }
    var imageList: List<UriFile> by remember { mutableStateOf(_images) }
    var tempPhoto: Uri? by remember { mutableStateOf(null) }
    var showCameraOrGalleryDialog by remember { mutableStateOf(false) }
    var datePickerDialogState by remember { mutableStateOf<DatePickerDialogState?>(null) }

    var isEndDate by remember { mutableStateOf(false) }
    var showValidationName by remember { mutableStateOf(false) }
    var showValidationDateStart by remember { mutableStateOf(false) }
    var showValidationDateEnd by remember { mutableStateOf(false) }
    var showValidationDateStartAndEnd by remember { mutableStateOf(false) }
    val showValidationMaxPhotos by remember { mutableStateOf(false) }

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

    CameraOrGalleryDialog(
        isVisible = showCameraOrGalleryDialog,
        onItemSelected = {
            showCameraOrGalleryDialog = false
            if (it == ImageSource.Gallery) {
                imagePicker.launch("image/*")
            } else {
                val uri = ComposeFileProvider.getImageUri(context)
                tempPhoto = uri
                cameraLauncher.launch(uri)
            }
        },
        onDismiss = {
            showCameraOrGalleryDialog = false
        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = allRoundedCornerShape10)
                .padding(12.dp)
        ) {
            RoundedEdittext(
                placeholder = "Nombre de la Promoción",
                value = promoName
            ) {
                promoName = it
                showValidationName = it.isBlank() || it.isBlank()
            }

            ValidationText(show = showValidationName, text = "Agregue un nombre a la Promoción")

            VerticalSpacer(height = 8.dp)

            Row {
                Column(
                    modifier = Modifier.weight(0.5f)
                ) {
                    RoundedEdittext(
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = "Fecha Inicio",
                        isEnabled = false,
                        value = startDateFormatted,
                        onClicked = {
                            isEndDate = false
                            datePickerDialogState = DatePickerDialogState(
                                onDismiss = { datePickerDialogState = null },
                                onSelectDate = { selectedDate ->
                                    startDateFormatted = formatStringToDDMMYYYY(selectedDate)
                                    startDate = selectedDate
                                    datePickerDialogState = null
                                    showValidationDateStart = startDate.isBlank()
                                    if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                                        showValidationDateStartAndEnd = isDateEndPriorDateStart(startDate, endDate)
                                    }
                                }
                            )
                        },
                    ) { startDate = it }

                    ValidationText(show = showValidationDateStart, text = "Agregue la Fecha Inicio")
                    ValidationText(show = showValidationDateStartAndEnd, text = "La Fecha Fin es antes que la Fecha Inicio")
                }

                HorizontalSpacer(width = 8.dp)

                Column(
                    modifier = Modifier.weight(0.5f)
                ) {
                    RoundedEdittext(
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = "Fecha Fin",
                        isEnabled = false,
                        value = endDateFormatted,
                        onClicked = {
                            isEndDate = true
                            datePickerDialogState = DatePickerDialogState(
                                onDismiss = { datePickerDialogState = null },
                                onSelectDate = { selectedDate ->
                                    endDate = selectedDate
                                    endDateFormatted = formatStringToDDMMYYYY(selectedDate)
                                    datePickerDialogState = null
                                    showValidationDateEnd = endDate.isBlank()
                                    if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                                        showValidationDateStartAndEnd = isDateEndPriorDateStart(startDate, endDate)
                                    }
                                }
                            )
                        },
                    ) { endDate = it }

                    ValidationText(show = showValidationDateEnd, text = "Agregue la Fecha Fin")
                    ValidationText(show = showValidationDateStartAndEnd, text = "La Fecha Fin es antes que la Fecha Inicio")
                }
            }

            VerticalSpacer(height = 12.dp)

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    contentDescription = null,
                    painter = painterResource(id = R.drawable.ic_camera_filled),
                    colorFilter = ColorFilter.tint(Color.DarkGray),
                    modifier = Modifier.height(28.dp)
                )
                HorizontalSpacer(width = 5.dp)
                TextSemiBold(
                    text = "Fotos",
                    color = PinkFiestamas,
                    size = 20.sp.autoSize(),
                    align = TextAlign.Start,
                    includeFontPadding = false
                )
            }

            VerticalSpacer(height = 10.dp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                ButtonAddMediaFile(text = "Agregar") {
                    showCameraOrGalleryDialog = true
                    focusManager.clearFocus()
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

            Column {
                if (imageList.isEmpty()) {
                    ValidationText(
                        show = showValidationMaxPhotos,
                        text = "Agregue al menos una imagen",
                        fillMaxWidth = false
                    )
                }
                if (imageList.size > 5) {
                    ValidationText(
                        show = showValidationMaxPhotos,
                        text = "Se permiten hasta 5 imágenes",
                        fillMaxWidth = false
                    )
                }
                VerticalSpacer(height = 12.dp)
                ButtonPinkRoundedCornersV2(
                    content = {
                        TextSemiBold(
                            text = "Guardar",
                            color = Color.White,
                            shadowColor = Color.Gray,
                            size = 16.sp.autoSize()
                        )
                    },
                    onClick = {
                        if (promoName.isBlank() || promoName.isBlank()) {
                            showToast(context, "Agregue un nombre a la Promoción")
                            return@ButtonPinkRoundedCornersV2
                        }
                        if (startDate.isBlank()) {
                            showToast(context, "Agregue la Fecha Inicio")
                            return@ButtonPinkRoundedCornersV2
                        }
                        if (endDate.isBlank()) {
                            showToast(context, "Agregue la Fecha Fin")
                            return@ButtonPinkRoundedCornersV2
                        }
                        if (imageList.isEmpty()) {
                            showToast(context, "Agregue al menos una imagen")
                            return@ButtonPinkRoundedCornersV2
                        }
                        if (imageList.size > 5) {
                            showToast(context, "Se permiten hasta 5 imágenes")
                            return@ButtonPinkRoundedCornersV2
                        }
                        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                            if (isDateEndPriorDateStart(startDate, endDate)) {
                                showToast(
                                    context,
                                    "La Fecha Fin es antes que la Fecha Inicio"
                                )
                                return@ButtonPinkRoundedCornersV2
                            }
                        }
                        onOkClicked(promoName, startDate, endDate, imageList)
                    }
                )
            }
        }
    }

    datePickerDialogState?.let { state ->
        DatePickerPopup(state, isEndDate, startDate)
    }
}
