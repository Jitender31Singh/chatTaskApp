package com.example.pocflask

import com.example.pocflask.api.TaskData
import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("answer")
    val answer: TaskData
)