package com.example.pocflask

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.pocflask.api.ChatViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskPage(chatViewModel: ChatViewModel, navController: NavHostController) {
    var taskData by remember { mutableStateOf(chatViewModel.currentTask) }
    if (taskData.allfilled == true) {
        chatViewModel.messageList.clear()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Edit Task Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)

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

        TaskField(label = "Location", value = taskData.location ?: "") {
            taskData = taskData.copy(location = it)
        }

        PriorityDropdown(
            selected = taskData.priority ?: "",
            onSelect = { taskData = taskData.copy(priority = it) }
        )

        TaskField(label = "Customer Name", value = taskData.customerName ?: "") {
            taskData = taskData.copy(customerName = it)
        }

        Spacer(modifier = Modifier.size(24.dp))

        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}

@Composable
fun TaskField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}
@Composable
fun DateTimePickerField(label: String, initialValue: String, onValueChange: (String) -> Unit) {
    val context = LocalContext.current
    var displayValue by remember { mutableStateOf(initialValue) }
    val calendar = Calendar.getInstance()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                                val formatted = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault()).format(calendar.time)
                                displayValue = formatted
                                onValueChange(formatted)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                        ).show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
    ) {
        OutlinedTextField(
            value = displayValue,
            onValueChange = {},
            label = { Text(label) },
            enabled = false, // disable keyboard input
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun PriorityDropdown(selected: String, onSelect: (String) -> Unit) {
    val priorities = listOf("Low", "Normal", "High", "Critical")
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text("Priority") },
            enabled = false, // disables input but still shows
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            priorities.forEach { priority ->
                DropdownMenuItem(
                    text = { Text(priority) },
                    onClick = {
                        onSelect(priority)
                        expanded = false
                    }
                )
            }
        }
    }
}



//this is another comment