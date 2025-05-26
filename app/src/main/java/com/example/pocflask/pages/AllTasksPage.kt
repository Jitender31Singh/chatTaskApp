package com.example.pocflask.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.pocflask.TaskViewModel
import com.example.pocflask.api.ChatViewModel
import com.example.pocflask.api.TaskData


@Composable
fun AllTasksPage(taskViewModel: TaskViewModel = viewModel(), navController: NavHostController,chatViewModel: ChatViewModel) {
    val taskList = taskViewModel.tasks

    Column(modifier = Modifier.padding(16.dp)) {
        Text("All Tasks", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(taskList){task->
                TaskItem(task)
                Divider()
            }
        }
    }
    Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Button(onClick = {
            chatViewModel.resetMessageList()
            Log.i("history", chatViewModel.messageList.toString())
            chatViewModel.clearTasks()
            navController.navigate("none_page") // or any screen to create new task
        }) {
            Text("+")
        }

        Button(onClick = {
            chatViewModel.resetMessageList()
            chatViewModel.clearTasks()
            navController.navigate("chat_screen")
        }) {
            Text("Chat")
        }
    }
}

@Composable
fun TaskItem(task: TaskData) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text("Description: ${task.taskDescription}")
        Text("Site type: ${task.taskType}" )
        if(task.customerName!=null){
            Text("Customer: ${task.customerName}")
        }

        Text("Priority: ${task.priority}")
        Text("Time: ${task.startTime} - ${task.endTime}")
    }
}