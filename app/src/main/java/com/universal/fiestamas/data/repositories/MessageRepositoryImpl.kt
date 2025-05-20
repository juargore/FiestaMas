package com.universal.fiestamas.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.universal.fiestamas.data.apis.MessageApi
import com.universal.fiestamas.data.extensions.collectionListenerFlow
import com.universal.fiestamas.data.module.Constants
import com.universal.fiestamas.data.module.Constants.ID_RECEIVER
import com.universal.fiestamas.data.module.Constants.ID_SENDER
import com.universal.fiestamas.data.module.Constants.ID_SERVICE_EVENT
import com.universal.fiestamas.data.module.Constants.IS_READ
import com.universal.fiestamas.domain.common.BaseResult
import com.universal.fiestamas.domain.common.ErrorResponse
import com.universal.fiestamas.domain.models.request.MarkChatMessagesAsReadRequestV2
import com.universal.fiestamas.domain.models.request.MessageV2
import com.universal.fiestamas.domain.models.request.MessagesAsReadV2
import com.universal.fiestamas.domain.models.request.NewChatMessageRequest
import com.universal.fiestamas.domain.models.request.SendMessageRequestV2
import com.universal.fiestamas.domain.models.response.NotificationDb
import com.universal.fiestamas.domain.usecases.IMessageRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MessageRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val messageApi: MessageApi
) : IMessageRepository {

    override fun sendChatMessage(notificationTitle: String, request: NewChatMessageRequest) = flow {
        val body = SendMessageRequestV2(
            entityData = MessageV2(
                content = request.content,
                id_receiver = request.id_receiver,
                id_sender = request.id_sender,
                id_service_event = request.id_service_event,
                title = notificationTitle,
                type = request.type
            )
        )
        val response = messageApi.sendChatMessageV2(body)
        if (response.isSuccessful && response.body()?.status == 200) {
            emit(BaseResult.Success(true))
        } else {
            emit(BaseResult.Error(ErrorResponse(
                message = response.errorBody().toString(),
                status = response.code()
            )))
        }
    }

    override fun getChatMessagesByServiceEvent(serviceEventId: String): Flow<List<NotificationDb>> {
        val serviceCategoriesCollection = firestore.collection(Constants.MESSAGES)
        val query = serviceCategoriesCollection.whereEqualTo(ID_SERVICE_EVENT, serviceEventId)
        return serviceCategoriesCollection.collectionListenerFlow(NotificationDb::class.java, query)
    }

    override fun getUnreadCounterChatMessagesByServiceEvent(serviceEventId: String, senderId: String): Flow<Int> {
        val serviceCategoriesCollection = firestore.collection(Constants.MESSAGES)
        val query = serviceCategoriesCollection
            .whereEqualTo(ID_SERVICE_EVENT, serviceEventId)
            .whereEqualTo(IS_READ, false)
        return serviceCategoriesCollection
            .collectionListenerFlow(NotificationDb::class.java, query)
            .map { notifications ->
                val unreadMessages = mutableListOf<NotificationDb>()
                for (notification in notifications) {
                    if (notification.id_sender == senderId) {
                        unreadMessages.add(notification)
                    }
                }
                unreadMessages.size
            }
    }

    override fun getChatMessagesByReceiverId(receiverId: String): Flow<List<NotificationDb>> {
        val serviceCategoriesCollection = firestore.collection(Constants.MESSAGES)
        val query = serviceCategoriesCollection.whereEqualTo(ID_RECEIVER, receiverId)
        return serviceCategoriesCollection.collectionListenerFlow(NotificationDb::class.java, query)
    }

    override fun getChatMessagesBySenderId(senderId: String): Flow<List<NotificationDb>> {
        val serviceCategoriesCollection = firestore.collection(Constants.MESSAGES)
        val query = serviceCategoriesCollection.whereEqualTo(ID_SENDER, senderId)
        return serviceCategoriesCollection.collectionListenerFlow(NotificationDb::class.java, query)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun markMessagesAsRead(messages: List<String>) {
        GlobalScope.launch {
            val body = MarkChatMessagesAsReadRequestV2(
                entityData = MessagesAsReadV2(messageIds = messages)
            )
            messageApi.markMessagesAsReadV2(body)
        }
    }
}
