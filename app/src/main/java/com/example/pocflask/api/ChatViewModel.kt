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
    val messageList by lazy {
        mutableStateListOf<MessageModel>().apply {
            add(MessageModel("Hello,\n please provide taskDescription ,priority,start time ,duration and customer name", "model")) // 👈 Add default message
        }
    }
    var currentTask by mutableStateOf(TaskData())
        private set
    fun sendMessage(question: String) {
        messageList.add(MessageModel(question,"user"))
        getHomeData()
        sendPromptToServer(question)
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