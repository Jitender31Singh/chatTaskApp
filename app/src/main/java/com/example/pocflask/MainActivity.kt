package com.example.pocflask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pocflask.api.ChatViewModel
import com.example.pocflask.pages.AllTasksPage
import com.example.pocflask.pages.ChatPage
import com.example.pocflask.pages.CustomerTask
import com.example.pocflask.pages.NoneTask
import com.example.pocflask.pages.VoiceToTextParser

class MainActivity : ComponentActivity() {
    val voiceToTextParser by lazy {
        VoiceToTextParser(application)
    }
//    this is testing comment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        val taskViewModel= ViewModelProvider(this)[TaskViewModel::class.java]

        setContent {
            val navController = rememberNavController()

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                // Define NavHost here
                NavHost(
                    navController = navController,
                    startDestination = "chat_screen",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("chat_screen") {
                        ChatPage(
                            voiceToTextParser = voiceToTextParser,
                            chatViewModel = chatViewModel,
                            navController = navController
                        )
                    }

                    composable("customer_page") {
                        CustomerTask(chatViewModel = chatViewModel, navController,taskViewModel)
                    }

                    composable("location_page") {
                        CustomerTask(chatViewModel = chatViewModel, navController,taskViewModel)
                    }

                    composable("none_page") {
                        NoneTask(chatViewModel = chatViewModel, navController,taskViewModel)
                    }

                    composable("allTasks") {
                        AllTasksPage(taskViewModel,navController,chatViewModel)
                    }


                }
            }
        }
    }
}

