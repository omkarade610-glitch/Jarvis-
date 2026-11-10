package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.repository.JarvisRepository
import com.example.service.JarvisAiService
import com.example.service.VoiceService
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBorderCyan
import com.example.ui.theme.JarvisCardDark
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDeepNavy
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentUserId: String,
    repository: JarvisRepository,
    aiService: JarvisAiService,
    voiceService: VoiceService,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var activeConversationId by remember { mutableStateOf("default_conv") }
    val messages by repository.observeMessages(currentUserId, activeConversationId).collectAsState(initial = emptyList())
    val userProfile by repository.observeUserProfile(currentUserId).collectAsState(initial = null)

    var inputPrompt by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    val assistantName = userProfile?.assistantName ?: "JARVIS"
    val ownerName = userProfile?.ownerName ?: "Sir"
    val language = userProfile?.language ?: "English"

    // Auto-scroll to latest message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "$assistantName AI CHAT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = JarvisCyan
                        )
                        Text(
                            text = "Model: Gemini 3.8 Flash // Lang: $language",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("chat_back_button")) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = JarvisCyan
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                repository.createConversation(currentUserId, "New Query Thread", language).onSuccess { newId ->
                                    activeConversationId = newId
                                    Toast.makeText(context, "New thread started", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.testTag("chat_new_thread_button")
                    ) {
                        Icon(
                            Icons.Default.DeleteSweep,
                            contentDescription = "New Thread",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = JarvisCardDark
                )
            )
        },
        containerColor = JarvisDeepNavy
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Chat Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x3300E5FF), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Connected to Gemini 3.8 Flash. Multilingual reasoning active in $language. Ask any complex engineering, coding, or creative problem.",
                            fontSize = 12.sp,
                            color = Color(0xFFE0F2FE),
                            lineHeight = 16.sp
                        )
                    }
                }

                items(messages) { msg ->
                    ChatBubble(
                        message = msg,
                        assistantName = assistantName,
                        onSpeak = {
                            voiceService.speak(
                                text = msg.text,
                                languageCode = language
                            )
                        },
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(msg.text))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                if (isGenerating) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = JarvisCyan,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "$assistantName is processing neural response...",
                                fontSize = 12.sp,
                                color = JarvisCyan,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                color = Color(0xFF0A1428),
                border = androidx.compose.foundation.BorderStroke(1.dp, JarvisBorderCyan),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputPrompt,
                        onValueChange = { inputPrompt = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        placeholder = {
                            Text("Ask $assistantName anything...", color = Color(0xFF64748B), fontSize = 14.sp)
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JarvisCyan,
                            unfocusedBorderColor = Color(0xFF1E293B),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = JarvisCyan
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            val promptToSend = inputPrompt.trim()
                            if (promptToSend.isNotBlank() && !isGenerating) {
                                inputPrompt = ""
                                isGenerating = true
                                coroutineScope.launch {
                                    // Save user message to Firestore
                                    val userMsg = ChatMessage(
                                        userId = currentUserId,
                                        sender = "user",
                                        text = promptToSend
                                    )
                                    repository.addMessage(currentUserId, activeConversationId, userMsg)

                                    // Build system context
                                    val systemPrompt = "You are $assistantName, a highly intelligent, sophisticated personal AI assistant inspired by futuristic sci-fi AI. You address the user respectfully as $ownerName. You communicate in $language whenever preferred. Give clear, brilliant, accurate explanations, code, or ideas. Never use simulated placeholders."
                                    val aiResult = aiService.generateResponse(promptToSend, systemPrompt)

                                    isGenerating = false
                                    aiResult.onSuccess { replyText ->
                                        val replyMsg = ChatMessage(
                                            userId = currentUserId,
                                            sender = "jarvis",
                                            text = replyText
                                        )
                                        repository.addMessage(currentUserId, activeConversationId, replyMsg)
                                    }.onFailure { err ->
                                        val errMsg = ChatMessage(
                                            userId = currentUserId,
                                            sender = "jarvis",
                                            text = "Neural calculation error: ${err.localizedMessage ?: "Unable to complete request."}"
                                        )
                                        repository.addMessage(currentUserId, activeConversationId, errMsg)
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .background(if (inputPrompt.isNotBlank()) JarvisCyan else Color(0xFF1E293B), CircleShape)
                            .testTag("chat_send_button"),
                        enabled = inputPrompt.isNotBlank() && !isGenerating
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Message",
                            tint = if (inputPrompt.isNotBlank()) Color(0xFF00363A) else Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    assistantName: String,
    onSpeak: () -> Unit,
    onCopy: () -> Unit
) {
    val isUser = message.sender == "user"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Text(
                text = if (isUser) "YOU" else assistantName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUser) Color(0xFF60A5FA) else JarvisCyan
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(if (message.text.length > 50) 0.92f else 0.75f)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) Color(0xFF1E3A8A) else Color(0xFF0F172A)
                )
                .border(
                    width = 1.dp,
                    color = if (isUser) Color(0x553B82F6) else JarvisBorderCyan,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = message.text,
                    color = Color(0xFFF1F5F9),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                if (!isUser) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = onSpeak, modifier = Modifier.size(28.dp)) {
                            Icon(
                                Icons.Default.VolumeUp,
                                contentDescription = "Read Aloud",
                                tint = JarvisCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy Text",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
