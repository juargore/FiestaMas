package com.universal.fiestamas.presentation.screens.home.main.mifiesta.promos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.request.PromoRequest
import com.universal.fiestamas.domain.models.request.EditPromoRequest
import com.universal.fiestamas.domain.models.request.ItemEditPromo
import com.universal.fiestamas.presentation.screens.home.main.mifiesta.MainPartyViewModel
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape14
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape16
import com.universal.fiestamas.presentation.ui.ButtonAlphaBackgroundV2
import com.universal.fiestamas.presentation.ui.CircleButtonPinkV2
import com.universal.fiestamas.presentation.ui.FloatingButtonV2
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextSemiBold
import com.universal.fiestamas.presentation.ui.TitleTopDescriptionBottomTextV2
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.dialogs.AddPromoDialog
import com.universal.fiestamas.presentation.ui.dialogs.EditPromoDialog
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.ui.dialogs.YesNoDialogV2
import com.universal.fiestamas.presentation.ui.empty_screen.BaseEmptyScreen
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.convertTimestampToDateYYYYmmDD
import com.universal.fiestamas.presentation.utils.extensions.formatStringAsDateV2ForPromos
import com.universal.fiestamas.presentation.utils.extensions.formatStringAsV2ForPromos
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.showToastOnUiThread
import com.universal.fiestamas.presentation.utils.toUri

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PromosScreen(
    vm: MainPartyViewModel = hiltViewModel(),
    providerId: String,
    reloadScreen: () -> Unit,
    onBackClicked: () -> Unit
) {
    vm.getPromosForProvider(providerId)

    val context = LocalContext.current
    val promosList by vm.promosList.collectAsState()
    val mediaLinks by vm.mediaLinksAfterSuccessMediaUpload.collectAsState()

    var newPromo: PromoRequest? by remember { mutableStateOf(null) }
    var editPromo: EditPromoRequest? by remember { mutableStateOf(null) }
    var tempEditPromo: ItemEditPromo? by remember { mutableStateOf(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showProgressDialog by remember { mutableStateOf(false) }
    var showAddPromoDialog by remember { mutableStateOf(false) }
    var showEditPromoDialog by remember { mutableStateOf(false) }
    var promoIdToDelete by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(launcher) {
        val permissionGranted = PackageManager.PERMISSION_GRANTED
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != permissionGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    mediaLinks?.let { pair ->
        if (editPromo != null) {
            // Editing promotion
            editPromo?.images = pair.first.orEmpty()
            vm.editExistingPromo(editPromo?.id!!, editPromo!!) { success ->
                vm.alreadyUploadedMediaFiles = false
                showProgressDialog = false
                showEditPromoDialog = false
                if (success) {
                    Handler(Looper.getMainLooper()).post { reloadScreen() }
                } else {
                    showToastOnUiThread(context, context.getString(R.string.promo_error_editing))
                }
            }
        } else {
            // Adding promotion
            newPromo?.images = pair.first.orEmpty()
            vm.createNewPromo(newPromo) { success ->
                showProgressDialog = false
                if (success) {
                    Handler(Looper.getMainLooper()).post { reloadScreen() }
                } else {
                    showToastOnUiThread(context, context.getString(R.string.promo_error_creating))
                }
            }
        }
    }

    YesNoDialogV2(
        isVisible = showDeleteDialog,
        title = stringResource(R.string.promo_delete_title),
        message = stringResource(R.string.promo_delete_body),
        textPrimaryButton = stringResource(R.string.gral_delete),
        onDismiss = { showDeleteDialog = false },
        onPrimaryButtonClicked = {
            showDeleteDialog = false
            showToastOnUiThread(context, context.getString(R.string.promo_delete_in_progress))
            vm.deletePromo(promoIdToDelete) {
                if (!it) {
                    showToastOnUiThread(context, context.getString(R.string.promo_error_deleting))
                }
                Handler(Looper.getMainLooper()).post { reloadScreen() }
            }
        }
    )

    AddPromoDialog(
        isVisible = showAddPromoDialog,
        onAccept = { name, start, end, images ->
            showProgressDialog = true
            val startTime = start + "T12:00:00.000Z"
            val endTime = end + "T12:00:00.000Z"

            newPromo = PromoRequest(
                name = name,
                start_date = startTime,
                end_date = endTime,
                images = listOf(),
                id_provider = providerId
            )
            vm.uploadMediaFiles(images, emptyList(), false)
        },
        onDismiss = { showAddPromoDialog = false }
    )

    EditPromoDialog(
        isVisible = showEditPromoDialog,
        itemEditPromo = tempEditPromo,
        onAccept = { id, newName, newStart, newEnd, newImages ->
            id?.let {
                showProgressDialog = true
                val startTime = newStart + "T12:00:00.000Z"
                val endTime = newEnd + "T12:00:00.000Z"

                editPromo = EditPromoRequest(
                    id = id,
                    name = newName,
                    start_date = startTime,
                    end_date = endTime,
                    images = listOf()
                )
                vm.uploadMediaFiles(newImages, emptyList(), true)
            }
        },
        onDismiss = { showEditPromoDialog = false }
    )

    ProgressDialog(showProgressDialog)

    GradientBackground(
        content = {
            if (promosList.isEmpty()) {
                BaseEmptyScreen(
                    grayText = stringResource(R.string.promo_no_promos),
                    buttonText = stringResource(R.string.promo_add_promo),
                    imageTop = {
                        Image(
                            modifier = Modifier.size(150.dp),
                            painter = painterResource(R.drawable.ic_offer),
                            colorFilter = ColorFilter.tint(color = PinkFiestamas),
                            contentDescription = null
                        )
                    },
                    onAddClicked = {
                        tempEditPromo = null
                        showAddPromoDialog = true
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        VerticalSpacer(height = 20.dp)
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .sidePadding()
                        ) {
                            FlowRow(
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                promosList.forEach { promo ->
                                    CardItemPromos(
                                        id = promo.id,
                                        name = promo.name,
                                        start = convertTimestampToDateYYYYmmDD(promo.start_date).first,
                                        end = convertTimestampToDateYYYYmmDD(promo.end_date).first,
                                        images = promo.images,
                                        onDelete = { id ->
                                            promoIdToDelete = id
                                            showDeleteDialog = true
                                        },
                                        onEdit = {
                                            tempEditPromo = ItemEditPromo(
                                                id = promo.id,
                                                name = promo.name,
                                                startDate = promo.start_date,
                                                endDate = promo.end_date,
                                                images = promo.images.map { it.toUri() }
                                            )
                                            showEditPromoDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                    FloatingButtonV2(
                        iconVector = Icons.Filled.Add,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(24.dp.autoSize())
                    ) {
                        tempEditPromo = null
                        showAddPromoDialog = true
                    }
                }
            }
        },
        addBottomPadding = false,
        showLogoFiestamas = false,
        titleScreen = stringResource(R.string.promo_promos),
        onBackButtonClicked = { onBackClicked() }
    )
}

@Composable
fun CardItemPromos(
    id: String,
    name: String,
    start: String,
    end: String,
    images: List<String>,
    onDelete: (String) -> Unit,
    onEdit: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp.autoSize())
            .clip(allRoundedCornerShape16)
            .background(Color.White)
            .padding(10.dp.autoSize())
    ) {
        Image(
            painter = rememberAsyncImagePainter(images.firstOrNull()),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .clip(allRoundedCornerShape16)
                .fillMaxHeight()
                .width(100.dp.autoSize())
        )
        HorizontalSpacer(width = 10.dp)
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Space for Promotion text name
                Row(
                    modifier = Modifier.weight(0.6f)
                ) {
                    TextSemiBold(
                        fillMaxWidth = false,
                        text = name,
                        size = 21.sp.autoSize(),
                        addThreeDots = true,
                    )
                }

                // Space for edit and delete buttons
                Row(
                    modifier = Modifier.weight(0.4f),
                    horizontalArrangement = Arrangement.End
                ) {
                    CircleButtonPinkV2(
                        icon = R.drawable.ic_pencil_filled
                    ) {
                        onEdit()
                    }
                    HorizontalSpacer(width = 6.dp)
                    CircleButtonPinkV2(
                        icon = R.drawable.ic_trash_can_filled
                    ) {
                        onDelete(id)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                verticalAlignment = Alignment.Bottom
            ) {
                // Space for start and end dates
                ButtonAlphaBackgroundV2(
                    icon = R.drawable.ic_calendar,
                    size = 40.dp.autoSize()
                )
                HorizontalSpacer(width = 8.dp)

                val nStartDate = formatStringAsDateV2ForPromos(start)
                TitleTopDescriptionBottomTextV2(
                    contentTop = {
                        TextMedium(
                            text = "Inicio",
                            fillMaxWidth = false,
                            size = 12.sp.autoSize(),
                            color = Color.Gray
                        )
                    },
                    contentBottom = {
                        TextMedium(
                            text = nStartDate,
                            fillMaxWidth = false,
                            size = 14.sp.autoSize(),
                            addThreeDots = true
                        )
                    }
                )

                HorizontalSpacer(width = 14.dp)

                val nEndDate = formatStringAsDateV2ForPromos(end)
                TitleTopDescriptionBottomTextV2(
                    contentTop = {
                        TextMedium(
                            text = "Fin",
                            fillMaxWidth = false,
                            size = 12.sp.autoSize(),
                            color = Color.Gray
                        )
                    },
                    contentBottom = {
                        TextMedium(
                            text = nEndDate,
                            fillMaxWidth = false,
                            size = 14.sp.autoSize(),
                            addThreeDots = true
                        )
                    }
                )
            }
        }
    }
}
