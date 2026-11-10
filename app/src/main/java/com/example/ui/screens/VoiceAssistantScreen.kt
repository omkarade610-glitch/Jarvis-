package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.JarvisRepository
import com.example.service.JarvisAiService
import com.example.service.VoiceService
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBorderCyan
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDeepNavy
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceAssistantScreen(
    currentUserId: String,
    repository: JarvisRepository,
    aiService: JarvisAiService,
    voiceService: VoiceService,
    onBack: () -> Unit
) {
    BackHandler {
        voiceService.stop()
        onBack()
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userProfile by repository.observeUserProfile(currentUserId).collectAsState(initial = null)
    val isSpeaking by voiceService.isSpeaking.collectAsState()

    var isListening by remember { mutableStateOf(false) }
    var isThinking by remember { mutableStateOf(false) }
    var privateMode by remember { mutableStateOf(false) }
    var wakeWordEnabled by remember { mutableStateOf(userProfile?.wakeWordEnabled ?: false) }

    var lastSpokenPrompt by remember { mutableStateOf("Tap the microphone or say 'Hello'") }
    var lastAiReply by remember { mutableStateOf("JARVIS voice synthesizer online. Awaiting your instruction.") }

    val assistantName = userProfile?.assistantName ?: "JARVIS"
    val ownerName = userProfile?.ownerName ?: "Sir"
    val language = userProfile?.language ?: "English"

    // Launcher for official Android Speech Recognition
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListening = false
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val spokenMatches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedText = spokenMatches?.firstOrNull()
            if (!recognizedText.isNullOrBlank()) {
                lastSpokenPrompt = recognizedText
                isThinking = true

                coroutineScope.launch {
                    val prompt = "Spoken command from $ownerName: $recognizedText"
                    val systemContext = "You are $assistantName, a vocal AI assistant. Answer concisely and conversationally in $language. Address user as $ownerName."
                    val aiResult = aiService.generateResponse(prompt, systemContext)
                    isThinking = false
                    aiResult.onSuccess { reply ->
                        lastAiReply = reply
                        voiceService.speak(
                            text = reply,
                            speechRate = (userProfile?.voiceSpeed ?: 1.0).toFloat(),
                            speechPitch = (userProfile?.voicePitch ?: 1.0).toFloat(),
                            languageCode = language
                        )
                    }.onFailure { err ->
                        lastAiReply = "Audio processing failure: ${err.localizedMessage ?: "Unknown"}"
                    }
                }
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isSpeaking || isListening) 1.25f else 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSpeaking || isListening) 800 else 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "$assistantName VOICE ENGINE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = JarvisCyan
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            voiceService.stop()
                            onBack()
                        },
                        modifier = Modifier.testTag("voice_back_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = JarvisCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF071126))
            )
        },
        containerColor = JarvisDeepNavy
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Mode Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F1B38), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (privateMode) Icons.Default.VisibilityOff else Icons.Default.Mic,
                        contentDescription = "Privacy",
                        tint = if (privateMode) JarvisAmber else JarvisCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (privateMode) "PRIVATE MODE (MIC OFF)" else "ACTIVE LISTENING",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (privateMode) JarvisAmber else JarvisCyan
                    )
                }

                Switch(
                    checked = privateMode,
                    onCheckedChange = {
                        privateMode = it
                        if (it) {
                            voiceService.stop()
                            isListening = false
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = JarvisAmber,
                        checkedTrackColor = Color(0xFF451A03),
                        uncheckedThumbColor = JarvisCyan,
                        uncheckedTrackColor = Color(0xFF00363A)
                    )
                )
            }

            // Central Glowing Arc Reactor / Soundwave Visualization
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .scale(waveScale)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    if (isSpeaking) JarvisAmber else if (isListening) Color(0xFF00E676) else JarvisCyan,
                                    Color(0x2200E5FF),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(
                            2.dp,
                            if (isSpeaking) JarvisAmber else if (isListening) Color(0xFF00E676) else JarvisCyan,
                            CircleShape
                        )
                        .clickable(enabled = !privateMode) {
                            if (!isListening) {
                                voiceService.stop()
                                isListening = true
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to $assistantName...")
                                }
                                try {
                                    speechRecognizerLauncher.launch(intent)
                                } catch (e: Exception) {
                                    isListening = false
                                    Toast.makeText(context, "Speech recognizer not supported on this device.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .testTag("voice_reactor_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color(0xFF001B2E), CircleShape)
                            .border(1.5.dp, Color(0xFF80DEEA), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (privateMode) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = if (isSpeaking) JarvisAmber else if (isListening) Color(0xFF00E676) else JarvisCyan,
                            modifier = Modifier.size(46.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = when {
                        privateMode -> "PRIVATE MODE ENGAGED"
                        isListening -> "LISTENING TO $ownerName.toUpperCase()..."
                        isThinking -> "COMPUTING NEURAL RESPONSE..."
                        isSpeaking -> "$assistantName IS SPEAKING..."
                        else -> "TAP CORE TO SPEAK"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSpeaking) JarvisAmber else JarvisCyan,
                    letterSpacing = 2.sp
                )
            }

            // Dialogue Displays
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xDD0D1F3C)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "YOU SAID:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF60A5FA)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = lastSpokenPrompt,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, JarvisBorderCyan, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1938)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$assistantName RESPONSE:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = JarvisCyan
                            )
                            if (isSpeaking) {
                                IconButton(
                                    onClick = { voiceService.stop() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Stop,
                                        contentDescription = "Stop",
                                        tint = JarvisAmber,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = lastAiReply,
                            fontSize = 14.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Controls Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Speed: ${userProfile?.voiceSpeed ?: 1.0}x // Pitch: ${userProfile?.voicePitch ?: 1.0}x",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                if (isSpeaking) {
                    IconButton(
                        onClick = { voiceService.stop() },
                        modifier = Modifier
                            .background(Color(0xFF451A03), CircleShape)
                            .testTag("voice_interrupt_button")
                    ) {
                        Icon(
                            Icons.Default.Stop,
                            contentDescription = "Interrupt Voice",
                            tint = JarvisAmber
                        )
                    }
                }
            }
        }
    }
}
