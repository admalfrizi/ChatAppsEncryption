package com.aplikasi.chatappstrials.utils

import com.aplikasi.chatappstrials.models.PushNotifications
import com.aplikasi.chatappstrials.utils.Constants.CONTENT_TYPE
import com.aplikasi.chatappstrials.utils.Constants.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotifApi {

    @Headers("Authorization: key=$SERVER_KEY","Content-type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotif(
        @Body notifications: PushNotifications
    ): Response<ResponseBody>
}