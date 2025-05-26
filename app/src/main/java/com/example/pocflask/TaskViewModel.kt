package com.example.pocflask

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.pocflask.api.TaskData

class TaskViewModel: ViewModel() {
    // Backing state list
    private val _tasks = mutableStateListOf<TaskData>()
    val tasks: List<TaskData> get() = _tasks

    init {
        // Sample tasks
        _tasks.addAll(
            listOf(
                TaskData("Call John", "High", "10:00 AM", "11:00 AM", "Follow-up call", "John Doe", true),
                TaskData("Meeting with team", "Medium", "12:00 PM", "1:00 PM", "Project discussion", "Team Alpha", true),
                TaskData("Visit client", "High", "2:00 PM", "3:00 PM", "Product demo", "Jane Smith", true)
            )
        )
    }

    fun addTask(task: TaskData) {
        _tasks.add(task)
    }

    fun removeTask(task: TaskData) {
        _tasks.remove(task)
    }
}