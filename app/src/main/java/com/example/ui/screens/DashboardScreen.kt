package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudioProject
import com.example.data.model.UserProfile
import com.example.data.repository.JarvisRepository
import com.example.ui.components.HologramHeader
import com.example.ui.navigation.Screen
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBorderCyan
import com.example.ui.theme.JarvisCardDark
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDeepNavy
import com.example.ui.theme.JarvisGreenOnline

data class QuickActionItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val screen: Screen,
    val accentColor: Color = JarvisCyan
)

@Composable
fun DashboardScreen(
    currentUserId: String,
    repository: JarvisRepository,
    onNavigate: (Screen) -> Unit
) {
    val userProfile by repository.observeUserProfile(currentUserId).collectAsState(initial = null)
    val recentProjects by repository.observeProjects(currentUserId).collectAsState(initial = emptyList())

    val assistantName = userProfile?.assistantName ?: "JARVIS"
    val ownerName = userProfile?.ownerName ?: "Sir"

    val actions = listOf(
        QuickActionItem("AI Chat", "ChatGPT-style reasoning", Icons.Default.Chat, Screen.Chat, JarvisCyan),
        QuickActionItem("Voice Engine", "Speech dialogue & TTS", Icons.Default.Mic, Screen.VoiceAssistant, JarvisAmber),
        QuickActionItem("Device Controls", "Apps, Torch, Alarms", Icons.Default.PhoneAndroid, Screen.PhoneControl, Color(0xFF00E676)),
        QuickActionItem("Web Search", "Real-time internet intelligence", Icons.Default.Search, Screen.WebSearch, Color(0xFF40C4FF)),
        QuickActionItem("Coding Studio", "17 languages & debugger", Icons.Default.Code, Screen.CodingStudio, Color(0xFFB388FF)),
        QuickActionItem("Website Builder", "Full-stack HTML/CSS/JS", Icons.Default.Web, Screen.WebsiteBuilder, Color(0xFFFF80AB)),
        QuickActionItem("App Builder", "Kotlin & Jetpack Compose", Icons.Default.PhoneAndroid, Screen.AppBuilder, Color(0xFF69F0AE)),
        QuickActionItem("Drawing Studio", "Gemini Image synthesis", Icons.Default.Brush, Screen.DrawingStudio, Color(0xFFFFD740)),
        QuickActionItem("Character Studio", "Original character design", Icons.Default.Person, Screen.CharacterDesigner, Color(0xFFFF6E40)),
        QuickActionItem("Anime Production", "Multi-episode arc planner", Icons.Default.Animation, Screen.AnimeStudio, Color(0xFFEA80FC)),
        QuickActionItem("Memory System", "Personalized knowledge", Icons.Default.Psychology, Screen.Memory, Color(0xFF18FFFF)),
        QuickActionItem("Owner Panel", "System & API Diagnostics", Icons.Default.AdminPanelSettings, Screen.OwnerPanel, Color(0xFFFF5252)),
        QuickActionItem("Settings", "Voice, Language, Theme", Icons.Default.Settings, Screen.Settings, Color(0xFFB0BEC5))
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(JarvisDeepNavy)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HologramHeader(
                assistantName = assistantName,
                ownerName = ownerName,
                subtitle = "NEURAL LINK ESTABLISHED // CLOUD READY",
                modifier = Modifier.testTag("dashboard_header")
            )
        }

        // Voice & Chat Quick Action Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onNavigate(Screen.VoiceAssistant) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("action_voice_assistant"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00363A),
                        contentColor = JarvisCyan
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JarvisCyan)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Voice Assistant", modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Voice Mode", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Button(
                    onClick = { onNavigate(Screen.Chat) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("action_chat_assistant"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = JarvisCyan,
                        contentColor = Color(0xFF00363A)
                    )
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "AI Chat", modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Chat", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

        // Live Telemetry Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, JarvisBorderCyan, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = JarvisCardDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TelemetryBadge(label = "GEMINI 3.8", status = "ACTIVE", color = JarvisGreenOnline)
                    TelemetryBadge(label = "IMAGE CORE", status = "ONLINE", color = JarvisCyan)
                    TelemetryBadge(label = "STORAGE", status = "FIRESTORE", color = JarvisAmber)
                    TelemetryBadge(label = "VOICE ENGINE", status = "READY", color = Color(0xFF00E676))
                }
            }
        }

        item {
            Text(
                text = "COMMAND MODULES",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = JarvisCyan,
                letterSpacing = 1.5.sp
            )
        }

        // Grid of command modules
        items(actions.chunked(2)) { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (item in pair) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp)
                            .border(1.dp, Color(0x2200E5FF), RoundedCornerShape(16.dp))
                            .clickable { onNavigate(item.screen) }
                            .testTag("module_${item.title.lowercase().replace(" ", "_")}"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xEE0B1630)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(item.accentColor.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = item.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = item.description,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // Recent Projects Section
        if (recentProjects.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT PROJECTS & CODE",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = JarvisAmber,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "${recentProjects.size} saved",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            items(recentProjects.take(4)) { project ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                        .clickable { onNavigate(Screen.CodingStudio) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1B38)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFF00363A), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = project.projectType.take(1).uppercase(),
                                color = JarvisCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = project.title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${project.projectType.uppercase()} // ${project.language}",
                                fontSize = 11.sp,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryBadge(label: String, status: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = status, fontSize = 10.sp, color = color, fontWeight = FontWeight.ExtraBold)
        }
    }
}
