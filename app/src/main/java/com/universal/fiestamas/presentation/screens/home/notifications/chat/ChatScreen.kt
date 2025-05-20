package com.universal.fiestamas.presentation.screens.home.notifications.chat

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.ViewTreeObserver
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.UriFile
import com.universal.fiestamas.domain.models.request.AcceptOrDeclineEditQuoteRequestV2
import com.universal.fiestamas.presentation.theme.LighterGray
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape16
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape20
import com.universal.fiestamas.presentation.theme.topRoundedCornerShape15
import com.universal.fiestamas.presentation.ui.HorizontalLine
import com.universal.fiestamas.presentation.ui.HorizontalSpacer
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.backgrounds.CardAuthBackground
import com.universal.fiestamas.presentation.ui.backgrounds.GradientBackground
import com.universal.fiestamas.presentation.ui.bottom_sheets.BottomSheetCameraOrGallery
import com.universal.fiestamas.presentation.ui.bottom_sheets.ImageSource
import com.universal.fiestamas.presentation.ui.cards.CardChatApproval
import com.universal.fiestamas.presentation.ui.cards.ChatCard
import com.universal.fiestamas.presentation.ui.cards.MessageType
import com.universal.fiestamas.presentation.ui.dialogs.ProgressDialog
import com.universal.fiestamas.presentation.utils.ComposeFileProvider
import com.universal.fiestamas.presentation.utils.bitmapToUri
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.sidePadding
import com.universal.fiestamas.presentation.utils.getBitmapSize
import com.universal.fiestamas.presentation.utils.getFileNameFromUri
import com.universal.fiestamas.presentation.utils.rotateBitmap
import com.universal.fiestamas.presentation.utils.scaleBitmapForMMS
import com.universal.fiestamas.presentation.utils.showToast
import com.universal.fiestamas.presentation.utils.showToastOnUiThread
import com.universal.fiestamas.presentation.utils.uriToResizedBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(
    vm: ChatViewModel = hiltViewModel(),
    myPartyService: MyPartyService?,
    clientId: String,
    providerId: String,
    serviceEventId: String,
    serviceId: String,
    isProvider: Boolean,
    clientEventId: String,
    eventName: String,
    onBackClicked: () -> Unit,
    onServiceNegotiationClicked: (
        myPartyService: MyPartyService
    ) -> Unit
) {

    val view = LocalView.current
    val context = LocalContext.current
    val viewTreeObserver = view.viewTreeObserver
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(true) {
        vm.resetServiceIdForNotification()

        vm.getChatMessages(
            isProvider = isProvider,
            serviceEvent = myPartyService,
            senderId = if (!isProvider) providerId else clientId,
            serviceEventId = serviceEventId
        )
    }

    val chatList by vm.chatMessagesList.collectAsState()

    val providerName by remember { mutableStateOf(myPartyService?.provider_contact_name.orEmpty()) }
    val clientName by remember { mutableStateOf(myPartyService?.client_contact_name.orEmpty()) }
    //var currentImage: UriFile? by remember { mutableStateOf(null) }
    var currentImage: UriFile? = null
    //var tempPhoto: Uri? by remember { mutableStateOf(null) }
    var tempPhoto: Uri? = null
    var showProgressDialog by remember { mutableStateOf(false) }
    var topViewWeight by remember { mutableFloatStateOf(0.92f) }
    var bottomViewWeight by remember { mutableFloatStateOf(0.08f) }
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    DisposableEffect(viewTreeObserver) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val isKeyboardOpen = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
            topViewWeight = if (isKeyboardOpen) 0.85f else 0.92f
            bottomViewWeight = if (isKeyboardOpen) 0.15f else 0.08f
        }
        viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
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
                                showProgressDialog = true
                                vm.uploadMediaFilesAndSendMMS(
                                    images = listOf(currentImage),
                                    videos = emptyList(),
                                    senderId = if (isProvider) providerId else clientId,
                                    receiverId = if (isProvider) clientId else providerId,
                                    serviceEventId = serviceEventId,
                                    clientEventId = clientEventId,
                                    serviceId = serviceId,
                                    notificationTitle = if (isProvider) providerName else clientName,
                                    onFinished = { message ->
                                        vm.alreadyUploadedMediaFiles = false
                                        showToastOnUiThread(context, message)
                                        showProgressDialog = false
                                    }
                                )
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
                showProgressDialog = true
                vm.uploadMediaFilesAndSendMMS(
                    images = listOf(currentImage),
                    videos = emptyList(),
                    senderId = if (isProvider) providerId else clientId,
                    receiverId = if (isProvider) clientId else providerId,
                    serviceEventId = serviceEventId,
                    clientEventId = clientEventId,
                    serviceId = serviceId,
                    notificationTitle = if (isProvider) providerName else clientName,
                    onFinished = { message ->
                        vm.alreadyUploadedMediaFiles = false
                        showToastOnUiThread(context, message)
                        showProgressDialog = false
                    }
                )
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

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = topRoundedCornerShape15,
        sheetContent = {
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
    ) {
        GradientBackground(
            content = {
                ProgressDialog(
                    isVisible = showProgressDialog,
                    message = stringResource(id = R.string.notification_sending_message)
                )

                CardAuthBackground(
                    centerContent = false,
                    addScroll = false,
                    bottomPadding = 10.dp,
                    backgroundColor = LighterGray,
                    content = {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Column(modifier = Modifier.weight(topViewWeight)) {
                                ChatHeader(
                                    serviceImage = myPartyService?.image,
                                    eventName = eventName,
                                    serviceName = myPartyService?.name,
                                    onOpenServiceNegotiationScreenClicked = {
                                        if (myPartyService != null) {
                                            onServiceNegotiationClicked(myPartyService)
                                        } else {
                                            showToast(context, context.getString(R.string.notification_unable_to_open_quote))
                                        }
                                    }
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            color = Color.White,
                                            shape = allRoundedCornerShape12
                                        )
                                ) {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
                                        reverseLayout = true
                                    ) {
                                        items(chatList) {
                                            if (it.type == MessageType.APPROVAL && !isProvider && it.isApproved == null) {
                                                CardChatApproval(
                                                    text = it.message,
                                                    onResponse = { accepted ->
                                                        showProgressDialog = true
                                                        val request = AcceptOrDeclineEditQuoteRequestV2(
                                                            id_event_type = myPartyService?.id.orEmpty(),
                                                            id_client = myPartyService?.id_client.orEmpty()
                                                        )
                                                        vm.acceptOrDeclineQuote(
                                                            request = request,
                                                            accepted = accepted,
                                                            serviceEventId = myPartyService?.id.orEmpty(),
                                                            messageId = it.id,
                                                            onFinished = { message ->
                                                                showProgressDialog = false
                                                                showToastOnUiThread(context, message)
                                                            }
                                                        )
                                                    }
                                                )
                                            } else {
                                                val isReceived = if (isProvider) {
                                                    providerId == it.idReceiver
                                                } else {
                                                    clientId != it.idReceiver
                                                }

                                                ChatCard(
                                                    date = it.date.orEmpty(),
                                                    photo = if (isReceived) it.receiverPhoto else it.senderPhoto,
                                                    name = if (isReceived) it.clientName else it.providerName,
                                                    message = it.message,
                                                    isReceived = isReceived,
                                                    type = it.type
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            FooterHeaderToSendMessage(
                                modifier = Modifier.weight(bottomViewWeight),
                                onAttachmentClicked = {
                                    coroutineScope.launch {
                                        modalSheetState.show()
                                    }
                                },
                                onSendMessageClicked = { message ->
                                    vm.sendMessage(
                                        message = message,
                                        senderId = if (isProvider) providerId else clientId,
                                        receiverId = if (isProvider) clientId else providerId,
                                        serviceEventId = serviceEventId,
                                        clientEventId = clientEventId,
                                        serviceId = serviceId,
                                        type = MessageType.MESSAGE.name,
                                        notificationTitle = if (isProvider) providerName else clientName,
                                        onFinished = {
                                            keyboardController?.show()
                                        }
                                    )
                                }
                            )
                        }
                    }
                )
            },
            addBottomPadding = false,
            validateOfflineMode = true,
            onBackButtonClicked = { onBackClicked() }
        )
    }
}


@Composable
fun ChatHeader(
    serviceImage: String?,
    eventName: String?,
    serviceName: String?,
    onOpenServiceNegotiationScreenClicked: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        HorizontalSpacer(2.dp)
        Image(
            painter = rememberAsyncImagePainter(serviceImage),
            contentDescription = null,
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .size(50.dp.autoSize())
                .clip(CircleShape)
                .border(1.dp.autoSize(), Color.Gray, CircleShape)
        )
        HorizontalSpacer(15.dp)
        Column {
            TextMedium(
                text = eventName.orEmpty(),
                align = TextAlign.Start,
                size = 18.sp.autoSize()
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.weight(0.6f)
                ) {
                    TextRegular(
                        text = serviceName.orEmpty(),
                        align = TextAlign.Start,
                        size = 14.sp.autoSize(),
                        maxLines = 2,
                        addThreeDots = true,
                        fillMaxWidth = false
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(0.4f)
                        .background(PinkFiestamas, shape = allRoundedCornerShape16)
                        .clip(allRoundedCornerShape20)
                        .clickable {
                            onOpenServiceNegotiationScreenClicked()
                        }
                ) {
                    TextRegular(
                        modifier = Modifier.padding(
                            vertical = 3.dp.autoSize(),
                            horizontal = 8.dp.autoSize()
                        ),
                        text = stringResource(R.string.mifiesta_quotation),
                        color = Color.White,
                        fillMaxWidth = true,
                        size = 12.sp.autoSize(),
                        align = TextAlign.Center
                    )
                }
            }
        }
    }
    VerticalSpacer(height = 13.dp)
    HorizontalLine(color = Color.Gray, thick = 0.5.dp.autoSize())
    VerticalSpacer(height = 13.dp)
}

@Composable
fun FooterHeaderToSendMessage(
    modifier: Modifier,
    onAttachmentClicked: () -> Unit,
    onSendMessageClicked: (String) -> Unit
) {
    var message by rememberSaveable { mutableStateOf("") }

    VerticalSpacer(height = 10.dp)
    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // clip for attachments section
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(0.12f)
                .clickable { onAttachmentClicked() }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp.autoSize())
                    .background(
                        color = Color.LightGray.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
            ) {
                Image(
                    modifier = Modifier.size(20.dp.autoSize()),
                    painter = painterResource(id = R.drawable.ic_clip),
                    contentDescription = null
                )
            }
        }
        HorizontalSpacer(7.dp)

        // input text for message as string section
        Row(
            modifier = Modifier
                .height(50.dp.autoSize())
                .weight(0.73f)
                .background(Color.White, allRoundedCornerShape12)
                .border(0.5.dp.autoSize(), Color.Gray, allRoundedCornerShape12)
                .sidePadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                value = message,
                onValueChange = { message = it },
                singleLine = false,
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp.autoSize()
                )
            )
        }

        // button send section
        HorizontalSpacer(7.dp)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(PinkFiestamas, shape = allRoundedCornerShape12)
                .height(50.dp.autoSize())
                .weight(0.15f)
                .clickable {
                    if (message.isNotEmpty()) {
                        onSendMessageClicked(message)
                        message = ""
                    }
                }
        ) {
            Image(
                modifier = Modifier.padding(12.dp.autoSize()),
                painter = painterResource(id = R.drawable.ic_send_chat),
                colorFilter = ColorFilter.tint(color = Color.White),
                contentDescription = null
            )
        }
    }
}
