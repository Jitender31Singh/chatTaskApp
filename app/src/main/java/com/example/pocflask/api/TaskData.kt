package com.example.pocflask.api

data class TaskData(
    val taskDescription: String? = null,
    val location: String? = null,
    val priority: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val customerName: String? = null,
    val message: String? = null,
    val allfilled: Boolean = false
)

data class PromptRequest(
    val input: String
)