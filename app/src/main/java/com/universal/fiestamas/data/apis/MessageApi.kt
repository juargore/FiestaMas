package com.universal.fiestamas.data.apis

import com.universal.fiestamas.domain.models.request.MarkChatMessagesAsReadRequest
import com.universal.fiestamas.domain.models.request.MarkChatMessagesAsReadRequestV2
import com.universal.fiestamas.domain.models.request.NewChatMessageRequest
import com.universal.fiestamas.domain.models.request.SendMessageRequestV2
import com.universal.fiestamas.domain.models.response.StatusResponseV2
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface MessageApi {

    @POST("message")
    fun sendChatMessage(@Body request: NewChatMessageRequest): Call<Void>

    @PUT("message/read")
    fun markMessagesAsRead(@Body request: MarkChatMessagesAsReadRequest): Call<Void>


    // ======= NEW METHODS FOR V2 API ========= //

    @POST("Messages")
    suspend fun sendChatMessageV2(
        @Body request: SendMessageRequestV2
    ): Response<StatusResponseV2>

    @PUT("Messages/read")
    suspend fun markMessagesAsReadV2(
        @Body request: MarkChatMessagesAsReadRequestV2
    ): Response<StatusResponseV2>
}
