package com.example.pocflask.api

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatViewModel: ViewModel() {

    var showSiteTypeSelector by mutableStateOf(false)
    var showCustomerSelector by mutableStateOf(false)
    var selectedSiteType by mutableStateOf<String?>(null)
    var customerName by mutableStateOf<String?>(null)

    var currentTask by mutableStateOf(TaskData())
        private set
    var previousTask by mutableStateOf(TaskData())
        private set

    val messageList by lazy {
        mutableStateListOf<MessageModel>().apply {
            add(MessageModel("Hey, I am TrackOBot. Let's make a new task.","model"))
            add(MessageModel("Please provide your Task Description, Priority, Start time and end time", "model"))
        }
    }


    fun onSiteTypeSelected(type: String) {
        selectedSiteType = type
        currentTask.taskType=type
        showSiteTypeSelector = false

        if (type == "Customer") {
            showCustomerSelector = true
        } else {
            currentTask = currentTask.copy(allfilled = true)
        }
    }

    fun onCustomerSelected(name: String) {
        customerName=name;
        currentTask.customerName=name
        showCustomerSelector = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(question: String) {
        messageList.add(MessageModel(question,"user"))
        messageList.add(MessageModel("Typing...","model"))
        if(!currentTask.allfilled){
            sendPromptToServer(question)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendPromptToServer(promptText: String) {
        var promptWithHistory=promptText
        if(previousTask.startTime!=null){
            promptWithHistory="this is old  start time : ${previousTask.startTime} and this is new prompt :"+promptWithHistory
        }
        val prompt = PromptRequest(promptWithHistory)
        viewModelScope.launch {
            Log.i("original prior","inside" )
            val call= RetrofitClient.apiService.sendPrompt(prompt)
            Log.i("original", call.body().toString())
            if (call.isSuccessful && call.body() != null) {
                val taskData: TaskData = call.body()!!.answer
                Log.i("res1", taskData.toString())
                currentTask=taskData
//                messageList.add(MessageModel(taskData.message.toString(),"model"))
//                messageList.removeAt(messageList.size - 2)

                if(previousTask.taskDescription.toString().isNotEmpty() && currentTask.taskDescription.toString().isEmpty()){
                    currentTask.taskDescription=previousTask.taskDescription
                }
                if(previousTask.priority.toString().isNotEmpty() && currentTask.priority.toString().isEmpty()){
                    currentTask.priority=previousTask.priority
                }
                if(previousTask.startTime.toString().isNotEmpty() && currentTask.startTime.toString().isEmpty()){
                    currentTask.startTime=previousTask.startTime
                }
                if(previousTask.endTime.toString().isNotEmpty() && currentTask.endTime.toString().isEmpty()){
                    currentTask.endTime=previousTask.endTime
                }
                if(currentTask.taskDescription!=null &&currentTask.priority!=null && currentTask.startTime!=null &&currentTask.endTime!=null ){
                    currentTask.allfilled=true
                }
                var missingFields = mutableListOf<String>()
                var missingMessage="Please provide: "

                if(currentTask.taskDescription==null){
                    missingFields.add("Task Description")
                }
                if(currentTask.priority==null){
                    missingFields.add("Priority")
                }
                if(currentTask.startTime==null){
                    missingFields.add("Start Time")
                }
                if(currentTask.startTime!=null){
                    val formatter= DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")
                    val currentDateTime = LocalDateTime.now()
                    val start= LocalDateTime.parse(currentTask.startTime,formatter)
                    if(currentDateTime.isAfter(start)){
                        missingFields.add("Start time should be more than current time")
                    }
                }

                if(currentTask.endTime!=null &&currentTask.startTime!=null){
                    val formatter= DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")
                    val currentDateTime = LocalDateTime.now()
                    val start= LocalDateTime.parse(currentTask.startTime,formatter)
                    val end= LocalDateTime.parse(currentTask.endTime,formatter)
                    if(start.isAfter(end)){
                        missingFields.add("End time should be more than Start time : ${currentTask.startTime}")
                    }
                }

                if(currentTask.endTime==null){
                    missingFields.add("End Time")
                }

                for (field in missingFields) {
                    missingMessage=missingMessage+field.toString()+", "
                }

                if(missingFields.size>0){
                    currentTask.message=missingMessage.dropLast(2)
                }
                missingFields.clear()
                Log.i("Current task",currentTask.toString())
                if(!currentTask.allfilled){
                    messageList.add(MessageModel(currentTask.message.toString(),"model"))
                    messageList.removeAt(messageList.size - 2)
                }else{
//                    messageList.add(MessageModel("Please select site type", "model"))
                    messageList.removeAt(messageList.size - 1)
                    showSiteTypeSelector=true
                }
                previousTask=currentTask



            } else {
                Log.i("error", "response unsuccessful or empty")
            }
        }

    }



    fun getHomeData(){
        viewModelScope.launch {
            Log.i("check1","inside getHome")
            val call= RetrofitClient.apiService.getHome();
            Log.i("check2","after hit")
            if(call.isSuccessful){
                Log.i("Home", call.body().toString())
            }else{
                Log.i("error","no data")
            }
        }
    }

    fun updateTask(updatedTask: TaskData) {
        currentTask = updatedTask
    }

    fun clearTasks() {
        currentTask = TaskData()
        previousTask = TaskData()
    }

    fun resetMessageList() {
        messageList.clear()
        messageList.add(MessageModel("Hey, I am TrackOBot. Let's make a new task.","model"))
        messageList.add(MessageModel("Please provide your Task Description, Priority, Start time and end time", "model"))
        customerName=null
        selectedSiteType=null
    }

}