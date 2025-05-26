package com.example.pocflask.api

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

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

    fun sendMessage(question: String) {
        messageList.add(MessageModel(question,"user"))
        messageList.add(MessageModel("Typing...","model"))
        if(!currentTask.allfilled){
            sendPromptToServer(question)
        }
    }

    fun sendPromptToServer(promptText: String) {
        val prompt = PromptRequest(promptText)
        Log.i("1st", "inside you ")
        viewModelScope.launch {
            Log.i("original prior","inside" )
            val call= RetrofitClient.apiService.sendPrompt(prompt)
            Log.i("original", call.body().toString())
            if (call.isSuccessful && call.body() != null) {
                val taskData: TaskData = call.body()!!.answer
                Log.i("res1", taskData.toString())
                currentTask=taskData
                messageList.add(MessageModel(taskData.message.toString(),"model"))
                messageList.removeAt(messageList.size - 2)

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


                if(currentTask.taskDescription==null){
                    currentTask.message="please provide task description"
                }
                if(currentTask.endTime==null){
                    currentTask.message="please provide end time"
                }
                else if(currentTask.startTime==null){
                    currentTask.message="please provide start time"
                }
                if(currentTask.priority==null){
                    currentTask.message="please provide priority"
                }
                Log.i("Current task",currentTask.toString())
                if(!currentTask.allfilled){
                    messageList.add(MessageModel(currentTask.message.toString(),"model"))
                    messageList.removeAt(messageList.size - 2)
                }else{
                    messageList.add(MessageModel("Please select site type", "model"))
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