package com.example.pocflask.api

import com.example.pocflask.ApiResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST(ApiConstants.END_POINT)
    suspend fun sendPrompt(@Body input: PromptRequest): Response<ApiResponse>

    @GET("home")
    suspend fun getHome(): Response<HomeData>
}