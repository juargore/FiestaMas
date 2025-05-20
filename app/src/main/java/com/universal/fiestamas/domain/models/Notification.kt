package com.universal.fiestamas.domain.models

import android.os.Parcelable
import com.universal.fiestamas.presentation.ui.cards.MessageType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    val id: String,
    var status: NotificationStatus?,
    var icon: String?,
    var eventName: String?,
    var eventType: String?,
    var festejadosName: String?,
    var serviceName: String?,
    val message: String,
    val clientName: String,
    val providerName: String,
    val idReceiver: String,
    val idSender: String,
    val date: String?,
    val clientEventId: String,
    val receiverId: String,
    val senderId: String,
    val receiverPhoto: String,
    val senderPhoto: String,
    val serviceId: String,
    val serviceEventId: String,
    val serviceEvent: MyPartyService?,
    val isApproved: Boolean? = null,
    val type: MessageType
): Parcelable

enum class NotificationStatus {
    All,
    Read,
    Unread
}

data class BottomNotificationStatus(
    val status: NotificationStatus,
    val name: String
)
