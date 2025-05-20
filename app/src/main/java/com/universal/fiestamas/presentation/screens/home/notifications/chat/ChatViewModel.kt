package com.universal.fiestamas.presentation.screens.home.notifications.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universal.fiestamas.domain.common.BaseResult
import com.universal.fiestamas.domain.common.ErrorResponse
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.Notification
import com.universal.fiestamas.domain.models.NotificationStatus
import com.universal.fiestamas.domain.models.UriFile
import com.universal.fiestamas.domain.models.request.AcceptOrDeclineEditQuoteRequestV2
import com.universal.fiestamas.domain.models.request.NewChatMessageRequest
import com.universal.fiestamas.domain.usecases.MessageUseCase
import com.universal.fiestamas.domain.usecases.ServiceUseCase
import com.universal.fiestamas.domain.usecases.SharedPrefsUseCase
import com.universal.fiestamas.presentation.ui.cards.MessageType
import com.universal.fiestamas.presentation.ui.dialogs.OptionsQuote
import com.universal.fiestamas.presentation.utils.convertListNotificationDbToListNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageUseCase: MessageUseCase,
    private val serviceUseCase: ServiceUseCase,
    private val sharedPrefsUseCase: SharedPrefsUseCase
) : ViewModel() {

    var alreadyUploadedMediaFiles = false
    var alreadyGotChatMessages = false

    private val _chatMessagesList = MutableStateFlow<List<Notification>>(emptyList())
    val chatMessagesList: StateFlow<List<Notification>>
        get() = _chatMessagesList

    fun sendMessage(
        message: String,
        senderId: String,
        receiverId: String,
        serviceEventId: String,
        clientEventId: String,
        serviceId: String,
        type: String,
        notificationTitle: String,
        onFinished: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            messageUseCase.sendChatMessage(
                notificationTitle = notificationTitle,
                request = NewChatMessageRequest(
                    content = message,
                    id_sender = senderId,
                    id_receiver = receiverId,
                    id_service_event = serviceEventId,
                    id_client_event = clientEventId,
                    id_service = serviceId,
                    type = type,
                )
            ).collectLatest { response ->
                when (response) {
                    is BaseResult.Success -> onFinished("")
                    is BaseResult.Error -> onFinished(response.rawResponse.message)
                }
            }
        }
    }

    fun getChatMessages(
        isProvider: Boolean,
        serviceEvent: MyPartyService?,
        senderId: String,
        serviceEventId: String
    ) {
        //if (!alreadyGotChatMessages) {
        //alreadyGotChatMessages = true
            viewModelScope.launch(Dispatchers.IO) {
                messageUseCase.getChatMessagesByServiceEvent(serviceEventId).collectLatest { listDb ->
                    _chatMessagesList.value = convertListNotificationDbToListNotification(isProvider, serviceEvent, listDb)

                    // mark unread messages as read in server (IDs as list string)
                    val unreadMessagesList = mutableListOf<String>()
                    for (notification in chatMessagesList.value) {
                        if (notification.status == NotificationStatus.Unread
                            && notification.idSender == senderId
                        ) {
                            unreadMessagesList.add(notification.id)
                        }
                    }
                    markMessagesAsRead(unreadMessagesList)
                }
            }
        //}
    }

    private fun markMessagesAsRead(messages: List<String>) {
        if (messages.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                messageUseCase.markMessagesAsRead(messages)
            }
        }
    }

    fun uploadMediaFilesAndSendMMS(
        images: List<UriFile?>,
        videos: List<UriFile?>,
        senderId: String,
        receiverId: String,
        serviceEventId: String,
        clientEventId: String,
        serviceId: String,
        notificationTitle: String,
        onFinished: (String) -> Unit
    ) {
        if (!alreadyUploadedMediaFiles) {
            alreadyUploadedMediaFiles = true
            viewModelScope.launch(Dispatchers.IO) {
                serviceUseCase.uploadMediaFiles(images, videos).collectLatest { pair ->
                    sendMessage(
                        message = pair.first.first(),
                        senderId = senderId,
                        receiverId = receiverId,
                        serviceEventId = serviceEventId,
                        clientEventId = clientEventId,
                        serviceId = serviceId,
                        type = MessageType.IMAGE.name,
                        notificationTitle = notificationTitle,
                        onFinished = onFinished
                    )
                }
            }
        }
    }

    fun acceptOrDeclineQuote(request: AcceptOrDeclineEditQuoteRequestV2, accepted: Boolean, serviceEventId: String, messageId: String, onFinished: (String) -> Unit) {
        if (accepted) {
            acceptEditQuote(request, serviceEventId, messageId, onFinished)
        } else {
            declineEditQuote(request, serviceEventId, messageId, onFinished)
        }
    }

    private fun acceptEditQuote(request: AcceptOrDeclineEditQuoteRequestV2, serviceEventId: String, messageId: String, onFinished: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = serviceUseCase.acceptEditQuoteFromClientToProvider(request, serviceEventId, messageId).first()
            if (response.status == 200) {
                onFinished("")
                delay(2000L)
                updateServiceStatus(serviceEventId, OptionsQuote.Pending) { }
            } else {
                onFinished("Error al aceptar la Cotización")
            }
        }
    }

    private fun declineEditQuote(request: AcceptOrDeclineEditQuoteRequestV2, serviceEventId: String,messageId: String, onFinished: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = serviceUseCase.declineEditQuoteFromClientToProvider(request, serviceEventId, messageId).first()
            if (response.status == 200) {
                onFinished("")
            } else {
                onFinished("Error al aceptar la Cotización")
            }
        }
    }

    fun resetServiceIdForNotification() {
        sharedPrefsUseCase.setServiceIdNotification("")
    }

    private fun updateServiceStatus(serviceEventId: String, status: OptionsQuote, onFinished: (BaseResult<Boolean, ErrorResponse>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            serviceUseCase.updateServiceStatus(serviceEventId, status).collectLatest { response ->
                onFinished(response)
            }
        }
    }
}
