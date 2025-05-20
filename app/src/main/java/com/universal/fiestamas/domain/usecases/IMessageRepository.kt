package com.universal.fiestamas.domain.usecases

import com.universal.fiestamas.domain.common.BaseResult
import com.universal.fiestamas.domain.common.ErrorResponse
import com.universal.fiestamas.domain.models.request.NewChatMessageRequest
import com.universal.fiestamas.domain.models.response.NotificationDb
import kotlinx.coroutines.flow.Flow

interface IMessageRepository {

    fun sendChatMessage(notificationTitle: String, request: NewChatMessageRequest): Flow<BaseResult<Boolean, ErrorResponse>>

    fun getChatMessagesByServiceEvent(serviceEventId: String): Flow<List<NotificationDb>>

    fun getUnreadCounterChatMessagesByServiceEvent(serviceEventId: String, senderId: String): Flow<Int>

    fun getChatMessagesByReceiverId(receiverId: String): Flow<List<NotificationDb>>

    fun getChatMessagesBySenderId(senderId: String): Flow<List<NotificationDb>>

    fun markMessagesAsRead(messages: List<String>)
}
