package com.example.pocflask.api

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("answer")
    val answer: TaskData
)