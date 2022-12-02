package com.aplikasi.chatappstrials

import com.aplikasi.chatappstrials.utils.Constants
import com.aplikasi.chatappstrials.utils.NotifApi
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitInstance {

    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Constants.FCM_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NotifApi::class.java)
        }
    }
}