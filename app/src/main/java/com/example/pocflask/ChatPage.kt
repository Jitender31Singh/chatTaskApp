package com.example.pocflask

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.pocflask.api.ChatViewModel
import com.example.pocflask.ui.theme.ColorModelMessage
import com.example.pocflask.ui.theme.ColorUserMessage
import com.example.pocflask.ui.theme.Purple80

@Composable
fun ChatPage(
    modifier: Modifier = Modifier,
    voiceToTextParser: VoiceToTextParser,
    chatViewModel: ChatViewModel,
    navController: NavHostController
){
    val currentTask = chatViewModel.currentTask

    // Observe allfilled and navigate when true
    LaunchedEffect(currentTask.allfilled) {
        if (currentTask.allfilled) {
            navController.navigate("task_page") {
                popUpTo("chat_page") { inclusive = true } // Optional: clears backstack
            }
        }
    }
    Column(
        modifier = modifier
    ) {
        AppHeader()
        MessageList(modifier= Modifier.weight(1f),chatViewModel.messageList)
        MessageInput(voiceToTextParser, onMessageSend = {
            chatViewModel.sendMessage(it)
        })
    }

}

@Composable
fun MessageInput(voiceToTextParser: VoiceToTextParser,onMessageSend:(String)->Unit){
    val state by voiceToTextParser.state.collectAsState()
    var message by remember { mutableStateOf("") }
    var canRecord by remember { mutableStateOf(false) }

    val recordAudioLauncher= rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {isGranted->
            canRecord=isGranted
        }
    )

//    LaunchedEffect(key1=recordAudioLauncher){
//                recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
//            }

    LaunchedEffect(Unit) {
        recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    // Send spoken message when it changes and is not blank
    LaunchedEffect(state.spokenText) {
        if (state.spokenText.isNotBlank()) {
            Log.i("spoken message",state.spokenText )
            onMessageSend(state.spokenText)
        }
    }


    Row (modifier=Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically)
    {
        OutlinedTextField(value=message,
            modifier = Modifier.weight(1f),
            onValueChange ={
                message=it
            },
            placeholder = {
                Text(text = if (state.isSpeaking) "Listening..." else "Type or speak a message")
            }
        )
        IconButton(onClick = {
            if(message.isNotEmpty()){
                onMessageSend(message)
                message=""
            }

        }) {
            Icon(imageVector = Icons.Default.Send,contentDescription = "Send")
        }

        IconButton(onClick = {
            if (state.isSpeaking) {
                voiceToTextParser.stopListening()
            } else {
                if (canRecord) {
                    voiceToTextParser.startListening("en")
                } else {
                    recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        }) {
            AnimatedContent(targetState = state.isSpeaking) { isSpeaking ->
                if (isSpeaking) {
                    Icon(imageVector = Icons.Rounded.Stop, contentDescription = "Stop Recording")
                } else {
                    Icon(imageVector = Icons.Rounded.Mic, contentDescription = "Start Recording")
                }
            }
        }

    }

}


@Composable
fun MessageList(modifier: Modifier=Modifier,messageList: List<MessageModel>){
    if(messageList.isEmpty()){
        Column(
            modifier=modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(modifier= Modifier.size(60.dp),
                painter = painterResource(id = R.drawable.baseline_question_answer_24),
                contentDescription = "Q&A",
                tint = Purple80
            )
            Text(text="Make Task", fontSize = 22.sp)
        }
    }else{
        LazyColumn(
            modifier=modifier,
            reverseLayout = true
        ){
            items(messageList.reversed()){
                MessageRow(messageModel = it)
            }
        }
    }

}

@Composable
fun MessageRow(messageModel: MessageModel){
    val isModel=messageModel.role=="model"
    Row(
        verticalAlignment = Alignment.CenterVertically
    ){
        Box(
            modifier = Modifier.fillMaxWidth()
        ){
            Box(modifier = Modifier.align(if (isModel) Alignment.BottomStart else Alignment.BottomEnd)
                .padding(
                    start = if(isModel) 8.dp else 70.dp,
                    end = if(isModel)70.dp else 8.dp,
                    top = 8.dp,
                    bottom = 8.dp
                ).clip(RoundedCornerShape(40f))
                .background(if(isModel) ColorModelMessage else ColorUserMessage)
                .padding(16.dp)){
                SelectionContainer {
                    Text(text=messageModel.message, fontWeight = FontWeight.W500, color = Color.White)
                }
            }
        }
    }

}

@Composable
fun AppHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            color = Color.White,
            fontSize = 22.sp,
            text = "Chat Bot For Task Creation"
        )


    }

}
