package com.universal.fiestamas.domain.models.request

data class NewChatMessageRequest(
    val content: String,
    val id_sender: String,
    val id_receiver: String,
    val id_service_event: String,
    val id_service: String,
    val id_client_event: String,
    val type: String
)

data class MarkChatMessagesAsReadRequest(
    val id_messages: List<String>
)

data class SendMessageRequestV2(
    val entityData: MessageV2
)

data class MarkChatMessagesAsReadRequestV2(
    val entityData: MessagesAsReadV2
)

data class MessagesAsReadV2(
    val messageIds: List<String>
)

data class MessageV2(
    val content: String,
    val id_sender: String,
    val id_receiver: String,
    val id_service_event: String,
    val title: String,
    val type: String
)
