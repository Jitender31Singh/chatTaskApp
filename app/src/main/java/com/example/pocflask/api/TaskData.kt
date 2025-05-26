package com.example.pocflask.api


data class TaskData(
    var taskDescription: String? = null,
    var priority: String? = null,
    var startTime: String? = null,
    var endTime: String? = null,
    var message: String? = null,
    var customerName:String?=null,
    var allfilled: Boolean = false,
    var taskType:String?=null
)

data class PromptRequest(
    val input: String
)