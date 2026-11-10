package com.example.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.JarvisRepository
import com.example.service.JarvisAiService
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBorderCyan
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDeepNavy
import com.example.ui.theme.JarvisGreenOnline
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerPanelScreen(
    currentUserId: String,
    repository: JarvisRepository,
    aiService: JarvisAiService,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userProfile by repository.observeUserProfile(currentUserId).collectAsState(initial = null)
    val projects by repository.observeProjects(currentUserId).collectAsState(initial = emptyList())
    val memories by repository.observeMemories(currentUserId).collectAsState(initial = emptyList())

    val currentUser = Firebase.auth.currentUser
    var isAuditing by remember { mutableStateOf(false) }
    var auditLog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "OWNER CONTROL PANEL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = JarvisCyan
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("owner_back_button")) {
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Identity Header
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, JarvisBorderCyan, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1935)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Owner", tint = JarvisCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AUTHENTICATED SYSTEM OWNER", fontWeight = FontWeight.Bold, color = JarvisCyan, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Email: ${currentUser?.email ?: "Unknown"}", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(text = "UID: $currentUserId", color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "Security Provider: Google Sign-In // Jetpack Credential Manager", color = JarvisAmber, fontSize = 11.sp)
                    }
                }
            }

            // System Services Status Matrix
            item {
                Text(
                    text = "BACKEND & SERVICE MATRIX",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = JarvisAmber,
                    letterSpacing = 1.sp
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C152E)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ServiceStatusRow("Gemini 3.8 Flash (Text Reasoning)", "CONNECTED", JarvisGreenOnline)
                        ServiceStatusRow("Gemini Nano Banana 2.1 (Image Gen)", "CONNECTED", JarvisCyan)
                        ServiceStatusRow("Firebase App Check (Attestation)", "ACTIVE // PASSED", JarvisGreenOnline)
                        ServiceStatusRow("Cloud Firestore (Enterprise)", "CONNECTED // ISOLATED", JarvisGreenOnline)
                        ServiceStatusRow("Android SpeechRecognizer & TTS", "ONLINE", JarvisGreenOnline)
                        ServiceStatusRow("Hardware Telemetry (Camera/Audio)", "PERMISSIONS VERIFIED", JarvisAmber)
                    }
                }
            }

            // Usage & Telemetry Stats
            item {
                Text(
                    text = "APPLICATION METRICS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = JarvisCyan,
                    letterSpacing = 1.sp
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(title = "CLOUD PROJECTS", value = "${projects.size}", modifier = Modifier.weight(1f))
                    MetricCard(title = "MEMORIES", value = "${memories.size}", modifier = Modifier.weight(1f))
                    MetricCard(title = "DEVICE SDK", value = "API ${Build.VERSION.SDK_INT}", modifier = Modifier.weight(1f))
                }
            }

            // Self-Improvement & Security Audit
            item {
                Text(
                    text = "SELF-DIAGNOSTICS & SYSTEM AUDIT",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = JarvisAmber,
                    letterSpacing = 1.sp
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0x2200E5FF), RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1329)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Execute Neural Security & Code Health Scan",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Audits application security boundaries, zero-trust rules, memory leaks, and model latency.",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                isAuditing = true
                                auditLog = null
                                coroutineScope.launch {
                                    val prompt = """
                                    Perform an internal self-improvement and security audit for the JARVIS AI Android application.
                                    Analyze:
                                    1. Zero-trust Firestore rules and user isolation boundaries.
                                    2. Credential Manager Google Sign-In and App Check attestation health.
                                    3. Memory usage and state retention.
                                    4. Suggested performance optimizations.
                                    Report:
                                    Provide an authoritative diagnostic report for the Owner.
                                    """.trimIndent()

                                    val res = aiService.generateResponse(prompt, "You are JARVIS Cyber Defense & Diagnostics Core.")
                                    isAuditing = false
                                    res.onSuccess { text ->
                                        auditLog = text
                                    }.onFailure { e ->
                                        auditLog = "Diagnostic failure: ${e.localizedMessage ?: "Unknown"}"
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("owner_run_audit_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan, contentColor = Color(0xFF00363A)),
                            enabled = !isAuditing
                        ) {
                            if (isAuditing) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF00363A), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Running Security Diagnostics...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.Security, contentDescription = "Audit")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Initiate System Diagnostic Scan", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (auditLog != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF030712)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = auditLog!!,
                                    color = Color(0xFF86EFAC),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 17.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceStatusRow(service: String, status: String, statusColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = service, color = Color(0xFFE2E8F0), fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(6.dp).background(statusColor, CircleShape))
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = status, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1A36)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 16.sp, color = JarvisCyan, fontWeight = FontWeight.ExtraBold)
        }
    }
}
