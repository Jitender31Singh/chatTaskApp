package com.example.pocflask.api

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocflask.MessageModel
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
            add(MessageModel("Please provide your Task Description, Customer name, Priority, Start time and Duration", "model"))

        }
    }
    init {
        // Add the third message

    }

    fun onSiteTypeSelected(type: String) {
        selectedSiteType = type
        showSiteTypeSelector = false

        if (type == "Customer") {
            showCustomerSelector = true
        } else {
            currentTask = currentTask.copy(allfilled = true)
        }
    }

    fun onCustomerSelected(name: String) {
        Log.i("name cust",  name)
        currentTask.customerName=name
        customerName=name;
        Log.i("customer",  currentTask.toString())
        showCustomerSelector = false
    }

    fun sendMessage(question: String) {
        messageList.add(MessageModel(question,"user"))
        if(currentTask.allfilled){
            messageList.add(MessageModel("Please select site type", "model"))
            showSiteTypeSelector=true
        }else{
            sendPromptToServer(question)
        }
    }

    fun sendPromptToServer(promptText: String) {
        val prompt = PromptRequest(promptText)
        viewModelScope.launch {
            val call= RetrofitClient.apiService.sendPrompt(prompt)
            if (call.isSuccessful && call.body() != null) {
                val taskData: TaskData = call.body()!!.answer
                Log.i("res1", taskData.toString())
                currentTask=taskData
                messageList.add(MessageModel(taskData.message.toString(),"model"))

                if(previousTask.taskDescription!=null && currentTask.taskDescription==null){
                    currentTask.taskDescription=previousTask.taskDescription
                }
                if(previousTask.priority!=null && currentTask.priority==null){
                    currentTask.priority=previousTask.priority
                }
                if(previousTask.startTime!=null && currentTask.startTime==null){
                    currentTask.startTime=previousTask.startTime
                }
                if(previousTask.endTime!=null && currentTask.endTime==null){
                    currentTask.endTime=previousTask.endTime
                }
                if(currentTask.taskDescription!=null &&currentTask.priority!=null && currentTask.startTime!=null &&currentTask.endTime!=null ){
                    currentTask.allfilled=true
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

}