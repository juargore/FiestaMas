package com.universal.fiestamas.presentation.screens.home.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universal.fiestamas.domain.models.MyPartyService
import com.universal.fiestamas.domain.models.Notification
import com.universal.fiestamas.domain.usecases.MessageUseCase
import com.universal.fiestamas.presentation.utils.convertNotificationDbToNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val messageUseCase: MessageUseCase
) : ViewModel() {

    private var alreadyGotNotifications = false

    private val _notificationServerList = MutableStateFlow<List<Notification>>(emptyList())
    val notificationServerList: StateFlow<List<Notification>>
        get() = _notificationServerList

    private val _counterClientUnreadNotifications = MutableStateFlow("0")
    val counterClientUnreadNotifications: StateFlow<String>
        get() = _counterClientUnreadNotifications

    private val _counterProviderUnreadNotifications = MutableStateFlow(0)
    val counterProviderUnreadNotifications: StateFlow<Int>
        get() = _counterProviderUnreadNotifications


    fun getMessagesNotificationsByUserId(isProvider: Boolean, userId: String, myPartyServiceList: List<MyPartyService?>) {
        if (!alreadyGotNotifications) {
            alreadyGotNotifications = true
            viewModelScope.launch(Dispatchers.IO) {
                messageUseCase.getChatMessagesBySenderId(userId).collectLatest { listA->
                    messageUseCase.getChatMessagesByReceiverId(userId).collectLatest { listB ->
                        val notificationList = mutableListOf<Notification>()
                        val listDb = (listA + listB).sortedByDescending { it.timestamp }
                        val distinctList = listDb.distinctBy { it.id_service_event }

                        distinctList.forEach { notificationDb ->
                            myPartyServiceList.forEach { serviceEvent ->
                                if (notificationDb.id_service_event == serviceEvent?.id) {
                                    val notification = convertNotificationDbToNotification(
                                        isProvider = isProvider,
                                        serviceEvent = serviceEvent,
                                        notification = notificationDb
                                    )
                                    notificationList.add(notification)
                                }
                            }
                        }

                        _notificationServerList.value = notificationList
                    }
                }
            }
        }
    }

    fun getCountUnreadNotificationsByClientId(
        clientId: String,
        serviceEventsList: List<MyPartyService?>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            messageUseCase.getChatMessagesBySenderId(clientId).collectLatest { messagesBySender ->
                messageUseCase.getChatMessagesByReceiverId(clientId).collectLatest { messagesByReceiver ->
                    val unreadBySenderAndBelongsToServiceList = messagesBySender.filter { message ->
                        serviceEventsList.any { service -> service?.id == message.id_service_event } && !message.read
                    }.size
                    val unreadByReceiverAndBelongsToServiceList = messagesByReceiver.filter { message ->
                        serviceEventsList.any { service -> service?.id == message.id_service_event } && !message.read
                    }.size
                    _counterClientUnreadNotifications.value = (unreadByReceiverAndBelongsToServiceList + unreadBySenderAndBelongsToServiceList).toString()
                }
            }
        }
    }

    fun getCountUnreadNotificationsByProviderId(providerId: String, myPartyServiceList: List<MyPartyService?>) {
        viewModelScope.launch(Dispatchers.IO) {
            messageUseCase.getChatMessagesBySenderId(providerId).collectLatest { messagesBySender ->
                messageUseCase.getChatMessagesByReceiverId(providerId).collectLatest { messagesByReceiver ->
                    val unreadBySenderAndBelongsToServiceList = messagesBySender.filter { message ->
                        myPartyServiceList.any { service -> service?.id == message.id_service_event } && !message.read
                    }.size
                    val unreadByReceiverAndBelongsToServiceList = messagesByReceiver.filter { message ->
                        myPartyServiceList.any { service -> service?.id == message.id_service_event } && !message.read
                    }.size
                    _counterProviderUnreadNotifications.value = unreadBySenderAndBelongsToServiceList + unreadByReceiverAndBelongsToServiceList
                }
            }
        }
    }
}
