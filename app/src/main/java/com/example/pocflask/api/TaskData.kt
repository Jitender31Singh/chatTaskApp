package com.example.pocflask.api

//Removing location and customer name
data class TaskData(
    var taskDescription: String? = null,
    val location: String? = null,
    var priority: String? = null,
    var startTime: String? = null,
    var endTime: String? = null,
    var customerName: String? = null,
    val message: String? = null,
    var allfilled: Boolean = false
)

data class PromptRequest(
    val input: String
)