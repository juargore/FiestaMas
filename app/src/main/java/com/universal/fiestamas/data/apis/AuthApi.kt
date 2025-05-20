@file:Suppress("LocalVariableName")

package com.universal.fiestamas.data.apis

import com.universal.fiestamas.domain.models.request.EntityDataRequest
import com.universal.fiestamas.domain.models.request.GoogleProviderRequest
import com.universal.fiestamas.domain.models.request.GoogleUserRequest
import com.universal.fiestamas.domain.models.request.ProviderRequest
import com.universal.fiestamas.domain.models.request.ProviderRequestEdit
import com.universal.fiestamas.domain.models.request.SubscribeRequest
import com.universal.fiestamas.domain.models.request.UpdatePasswordRequest
import com.universal.fiestamas.domain.models.request.UpdateRequest
import com.universal.fiestamas.domain.models.request.UserRequest
import com.universal.fiestamas.domain.models.request.UserRequestEdit
import com.universal.fiestamas.domain.models.response.FirebaseProviderDb
import com.universal.fiestamas.domain.models.response.FirebaseUserDb
import com.universal.fiestamas.domain.models.response.StatusAndDataResponseV2
import com.universal.fiestamas.domain.models.response.StatusResponseV2
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApi {

    @POST("user")
    suspend fun createUserOnServer(@Body userRequest: UserRequest): Response<FirebaseUserDb>

    @POST("user/only-db/{uid}")
    fun createGoogleUserOnServer(
        @Path("uid") uid: String,
        @Body userRequest: GoogleUserRequest): Call<Void>

    @POST("user")
    suspend fun createProviderOnServer(@Body providerRequest: ProviderRequest): Response<FirebaseProviderDb>

    @POST("user/only-db/{uid}")
    fun createGoogleProviderOnServer(
        @Path("uid") uid: String,
        @Body googleProviderRequest: GoogleProviderRequest): Call<Void>

    @PUT("user/{id_user}")
    fun updateProviderOnServer(
        @Header("Authorization") authorizationHeader: String?,
        @Path("id_user") id_user: String,
        @Body providerRequestEdit: ProviderRequestEdit
    ): Call<Void>

    @PUT("user/{id_user}")
    fun updateClientOnServer(
        @Header("Authorization") authorizationHeader: String?,
        @Path("id_user") id_user: String,
        @Body userRequestEdit: UserRequestEdit
    ): Call<Void>

    @POST("push-notifications")
    fun subscribeTokenPushNotifications(
        @Header("Authorization") authorizationHeader: String?,
        @Body request: SubscribeRequest
    ): Call<Void>

    @HTTP(method = "DELETE", path = "push-notifications", hasBody = true)
    fun unsubscribeTokenPushNotifications(
        @Header("Authorization") authorizationHeader: String?,
        @Body request: SubscribeRequest
    ): Call<Void>

    @PUT("push-notifications")
    fun updateTokenPushNotifications(
        @Header("Authorization") authorizationHeader: String?,
        @Body request: UpdateRequest
    ): Call<Void>

    @PUT("user/password")
    fun updatePassword(
        @Header("Authorization") authorizationHeader: String?,
        @Body request: UpdatePasswordRequest
    ): Call<Void>


    // ======= NEW METHODS FOR V2 API ========= //


    @POST("Users")
    suspend fun createUserOnServerV2(
        @Body userRequest: EntityDataRequest
    ): Response<StatusResponseV2>

    @PUT("Users/{user_id}")
    suspend fun updateUserOnServerV2(
        @Path("user_id") user_id: String,
        @Body userRequest: EntityDataRequest
    ): Response<StatusResponseV2>

    @POST("Users")
    suspend fun createProviderOnServerV2(
        @Body providerRequest: EntityDataRequest
    ): Response<StatusAndDataResponseV2>

    @PUT("Users/{user_id}")
    suspend fun updateProviderOnServerV2(
        @Path("user_id") user_id: String,
        @Body userRequest: EntityDataRequest
    ): Response<StatusResponseV2>

    @POST("Users/subscribeToken")
    suspend fun subscribeTokenPushNotificationsV2(
        @Body request: EntityDataRequest
    ): Response<StatusResponseV2>

    @HTTP(method = "DELETE", path = "Users/unsubscribeToken", hasBody = true)
    suspend fun unsubscribeTokenPushNotificationsV2(
        @Body request: EntityDataRequest
    ): Response<StatusResponseV2>

    @PUT("Users/password")
    suspend fun updatePasswordV2(
        @Body request: EntityDataRequest
    ): Response<StatusResponseV2>
}
