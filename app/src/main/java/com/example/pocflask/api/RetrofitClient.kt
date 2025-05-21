package com.example.pocflask.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = ApiConstants.BASE_URL // for Android emulator use 10.0.2.2 instead of 127.0.0.1

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // ⏱️ increase connect timeout
        .readTimeout(60, TimeUnit.SECONDS)    // ⏱️ increase read timeout
        .writeTimeout(60, TimeUnit.SECONDS)   // ⏱️ increase write timeout
        .build()
    val gson = GsonBuilder().setLenient().create()
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient) // ✅ set custom client
            .build()
            .create(ApiService::class.java)
    }


}