package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.example.data.model.UserProfile
import com.example.data.repository.JarvisRepository
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBorderCyan
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDeepNavy
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentUserId: String,
    repository: JarvisRepository,
    onSignOut: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userProfile by repository.observeUserProfile(currentUserId).collectAsState(initial = null)

    var assistantName by remember { mutableStateOf(userProfile?.assistantName ?: "JARVIS") }
    var ownerName by remember { mutableStateOf(userProfile?.ownerName ?: "Sir") }
    var selectedLanguage by remember { mutableStateOf(userProfile?.language ?: "English") }
    var voiceSpeed by remember { mutableFloatStateOf((userProfile?.voiceSpeed ?: 1.0).toFloat()) }
    var voicePitch by remember { mutableFloatStateOf((userProfile?.voicePitch ?: 1.0).toFloat()) }
    var wakeWordEnabled by remember { mutableStateOf(userProfile?.wakeWordEnabled ?: false) }
    var themeMode by remember { mutableStateOf(userProfile?.themeMode ?: "dark") }

    val languages = listOf("English", "Hindi", "Marathi", "Japanese", "Korean", "Spanish", "French", "German")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "JARVIS CONFIGURATION",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = JarvisCyan
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_button")) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Identity Customization
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, JarvisBorderCyan, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1B38)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ASSISTANT & OWNER IDENTIFIERS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JarvisAmber)
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = assistantName,
                            onValueChange = { assistantName = it },
                            label = { Text("AI Assistant Call-sign", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JarvisCyan, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = ownerName,
                            onValueChange = { ownerName = it },
                            label = { Text("Owner Form of Address", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JarvisCyan, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            maxLines = 1
                        )
                    }
                }
            }

            // Language Selection
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1630)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("NATURAL LANGUAGE MODELING", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JarvisCyan)
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(languages) { lang ->
                                FilterChip(
                                    selected = selectedLanguage == lang,
                                    onClick = { selectedLanguage = lang },
                                    label = { Text(lang, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = JarvisCyan,
                                        selectedLabelColor = Color(0xFF00363A),
                                        containerColor = Color(0xFF0E1A36),
                                        labelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Voice Engine Parameters
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1630)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("VOICE SYNTHESIS AUDIO TUNING", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JarvisCyan)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Speech Rate: ${String.format("%.1f", voiceSpeed)}x", color = Color(0xFFCBD5E1), fontSize = 13.sp)
                        Slider(
                            value = voiceSpeed,
                            onValueChange = { voiceSpeed = it },
                            valueRange = 0.5f..2.0f,
                            colors = SliderDefaults.colors(thumbColor = JarvisCyan, activeTrackColor = JarvisCyan)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Vocal Pitch: ${String.format("%.1f", voicePitch)}x", color = Color(0xFFCBD5E1), fontSize = 13.sp)
                        Slider(
                            value = voicePitch,
                            onValueChange = { voicePitch = it },
                            valueRange = 0.5f..2.0f,
                            colors = SliderDefaults.colors(thumbColor = JarvisAmber, activeTrackColor = JarvisAmber)
                        )
                    }
                }
            }

            // Save Settings
            item {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val updatedProfile = UserProfile(
                                userId = currentUserId,
                                assistantName = assistantName.ifBlank { "JARVIS" },
                                ownerName = ownerName.ifBlank { "Sir" },
                                language = selectedLanguage,
                                voiceSpeed = voiceSpeed.toDouble(),
                                voicePitch = voicePitch.toDouble(),
                                wakeWordEnabled = wakeWordEnabled,
                                themeMode = themeMode
                            )
                            repository.saveUserProfile(updatedProfile).onSuccess {
                                Toast.makeText(context, "System preferences synced to Cloud!", Toast.LENGTH_SHORT).show()
                            }.onFailure { e ->
                                Toast.makeText(context, "Save error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_settings_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan, contentColor = Color(0xFF00363A))
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save & Sync Preferences", fontWeight = FontWeight.Bold)
                }
            }

            // Account & Sign Out
            item {
                Button(
                    onClick = {
                        val credentialManager = CredentialManager.create(context)
                        coroutineScope.launch {
                            withContext(NonCancellable) {
                                try {
                                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                                } catch (e: Exception) {
                                    // Log and ignore
                                } finally {
                                    Firebase.auth.signOut()
                                    onSignOut()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("sign_out_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B1010), contentColor = Color(0xFFFF8A80)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF1744))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out of JARVIS", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
