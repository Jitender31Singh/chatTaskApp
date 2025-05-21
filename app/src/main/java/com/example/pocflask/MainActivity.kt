package com.example.pocflask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pocflask.api.ChatViewModel
import com.example.pocflask.ui.theme.POCFlaskTheme

class MainActivity : ComponentActivity() {
    val voiceToTextParser by lazy {
        VoiceToTextParser(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

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

                    composable("task_page") {
                        TaskPage(chatViewModel=chatViewModel,navController)
                    }
                }
            }
        }
    }
}

