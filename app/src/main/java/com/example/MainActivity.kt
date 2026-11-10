package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.data.repository.JarvisRepository
import com.example.service.DeviceControlService
import com.example.service.JarvisAiService
import com.example.service.VoiceService
import com.example.ui.auth.AuthScreen
import com.example.ui.navigation.Screen
import com.example.ui.screens.AnimeStudioScreen
import com.example.ui.screens.AppBuilderScreen
import com.example.ui.screens.CharacterDesignerScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CodingStudioScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DrawingStudioScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.OwnerPanelScreen
import com.example.ui.screens.PhoneControlScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VoiceAssistantScreen
import com.example.ui.screens.WebSearchScreen
import com.example.ui.screens.WebsiteBuilderScreen
import com.example.ui.theme.JarvisDeepNavy
import com.example.ui.theme.JarvisTheme
import com.example.util.configureAppCheck
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private var voiceService: VoiceService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Authoritative App Check initialization with launch intent
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        val voice = VoiceService(this).also { voiceService = it }

        setContent {
            JarvisTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = JarvisDeepNavy
                ) {
                    AppRoot(voiceService = voice)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceService?.shutdown()
    }
}

@Composable
fun AppRoot(voiceService: VoiceService) {
    var currentUser by remember { mutableStateOf<FirebaseUser?>(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    val user = currentUser
    if (user == null) {
        AuthScreen(
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
            }
        )
    } else {
        key(user.uid) {
            JarvisMainContainer(
                currentUserId = user.uid,
                voiceService = voiceService,
                onSignOut = {
                    currentUser = null
                }
            )
        }
    }
}

@Composable
fun JarvisMainContainer(
    currentUserId: String,
    voiceService: VoiceService,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember(currentUserId) { JarvisRepository(context) }
    val aiService = remember { JarvisAiService() }
    val deviceControl = remember { DeviceControlService(context) }

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

    when (val screen = currentScreen) {
        is Screen.Dashboard -> {
            DashboardScreen(
                currentUserId = currentUserId,
                repository = repository,
                onNavigate = { currentScreen = it }
            )
        }
        is Screen.Chat -> {
            ChatScreen(
                currentUserId = currentUserId,
                repository = repository,
                aiService = aiService,
                voiceService = voiceService,
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
        is Screen.VoiceAssistant -> {
            VoiceAssistantScreen(
                currentUserId = currentUserId,
                repository = repository,
                aiService = aiService,
                voiceService = voiceService,
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
        is Screen.PhoneControl -> {
            PhoneControlScreen(
                deviceControl = deviceControl,
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
        is Screen.WebSearch -> {
            WebSearchScreen(
                aiService = aiService,
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
        is Screen.CodingStudio -> {
            CodingStudioScreen(
                currentUserId = currentUserId,
                repository = repository,
                aiService = aiService,
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
        is Screen.WebsiteBuilder -> {
            WebsiteBuilderScreen(
                currentUserId = currentUserId,
                repository = repository,
                aiService = aiService,
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
        is Screen.AppBuilder -> {
            AppBuilderScreen(
                currentUserId = currentUserId,
                repository = repository,
                aiService = aiService,
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
        is Screen.DrawingStudio -> {
            DrawingStudioScreen(
                currentUserId = currentUserId,
                repository = repository,
                aiService = aiService,
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
        is Screen.CharacterDesigner -> {
            CharacterDesignerScreen(
                currentUserId = currentUserId,
                repository = repository,
                aiService = aiService,
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
        is Screen.AnimeStudio -> {
            AnimeStudioScreen(
                currentUserId = currentUserId,
                repository = repository,
                aiService = aiService,
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
        is Screen.Memory -> {
            MemoryScreen(
                currentUserId = currentUserId,
                repository = repository,
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
        is Screen.OwnerPanel -> {
            OwnerPanelScreen(
                currentUserId = currentUserId,
                repository = repository,
                aiService = aiService,
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
        is Screen.Settings -> {
            SettingsScreen(
                currentUserId = currentUserId,
                repository = repository,
                onSignOut = onSignOut,
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
    }
}
