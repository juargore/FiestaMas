package com.universal.fiestamas.domain.usecases

import com.universal.fiestamas.domain.models.request.NewChatMessageRequest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MessageUseCase @Inject constructor(
    private val messageRepository: IMessageRepository
) {

    fun sendChatMessage(notificationTitle: String, request: NewChatMessageRequest) = messageRepository.sendChatMessage(notificationTitle, request)

    fun getChatMessagesByServiceEvent(serviceEventId: String) =
        messageRepository.getChatMessagesByServiceEvent(serviceEventId)
            .map { list -> list.sortedByDescending { it.timestamp } }

    fun getChatMessagesByReceiverId(receiverId: String) = messageRepository.getChatMessagesByReceiverId(receiverId)

    fun getChatMessagesBySenderId(senderId: String) = messageRepository.getChatMessagesBySenderId(senderId)

    fun getUnreadCounterChatMessagesByServiceEvent(serviceEventId: String, senderId: String) =
        messageRepository.getUnreadCounterChatMessagesByServiceEvent(serviceEventId, senderId)

    fun markMessagesAsRead(messages: List<String>) = messageRepository.markMessagesAsRead(messages)
}
