package com.example.pocflask.pages


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.pocflask.TaskViewModel
import com.example.pocflask.api.ChatViewModel

@Composable
fun NoneTask(chatViewModel: ChatViewModel, navController: NavHostController,taskViewModel: TaskViewModel) {
    var taskData by remember { mutableStateOf(chatViewModel.currentTask) }
    if (taskData.allfilled) {
        chatViewModel.messageList.clear()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Edit Task Details (None type)", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.size(16.dp))

        TaskField(label = "Task Description", value = taskData.taskDescription ?: "") {
            taskData = taskData.copy(taskDescription = it)
        }

        DateTimePickerField(
            label = "Start Time",
            initialValue = taskData.startTime ?: "",
            onValueChange = { taskData = taskData.copy(startTime = it) }
        )

        DateTimePickerField(
            label = "End Time",
            initialValue = taskData.endTime ?: "",
            onValueChange = { taskData = taskData.copy(endTime = it) }
        )

        PriorityDropdown(
            selected = taskData.priority ?: "",
            onSelect = { taskData = taskData.copy(priority = it) }
        )

        TaskField(label = "site type", value = chatViewModel.selectedSiteType.toString()) { }


        Spacer(modifier = Modifier.size(24.dp))

        Button(
            onClick = {
                navController.popBackStack()
                taskViewModel.addTask(taskData)
                navController.navigate("allTasks") {
                    popUpTo("chat_page") { inclusive = true } // Optional: clears backstack
                }

            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Make Task")
        }
    }
}
