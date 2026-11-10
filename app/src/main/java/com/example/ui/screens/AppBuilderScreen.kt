package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudioProject
import com.example.data.repository.JarvisRepository
import com.example.service.JarvisAiService
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBorderCyan
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDeepNavy
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBuilderScreen(
    currentUserId: String,
    repository: JarvisRepository,
    aiService: JarvisAiService,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var appName by remember { mutableStateOf("CyberGuard Mobile") }
    var appDescription by remember { mutableStateOf("A modern real-time cybersecurity telemetry and device integrity scanner with Material 3 UI and background coroutine workers.") }
    var isArchitecting by remember { mutableStateOf(false) }
    var generatedAppPlan by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI ANDROID APP BUILDER",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = JarvisCyan
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("app_builder_back_button")) {
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
            item {
                OutlinedTextField(
                    value = appName,
                    onValueChange = { appName = it },
                    label = { Text("Application Name", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JarvisCyan,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 1
                )
            }

            item {
                OutlinedTextField(
                    value = appDescription,
                    onValueChange = { appDescription = it },
                    label = { Text("App Specifications & Features", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("app_builder_spec_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JarvisCyan,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 5
                )
            }

            item {
                Button(
                    onClick = {
                        if (appDescription.isNotBlank() && !isArchitecting) {
                            isArchitecting = true
                            coroutineScope.launch {
                                val prompt = """
                                Act as an elite Android System Architect.
                                Design and code the full application architecture for: "$appName"
                                Specifications: $appDescription
                                
                                Provide:
                                1. Architecture Plan & Window Insets strategy.
                                2. Complete Kotlin Jetpack Compose Composable UI screens with StateFlow.
                                3. ViewModel with Kotlin Coroutines.
                                4. AndroidManifest.xml permissions and service entries.
                                5. Gradle dependency specifications.
                                """.trimIndent()

                                val result = aiService.generateResponse(prompt, "You are JARVIS Principal Android Engineer. Deliver clean, compiling Kotlin Compose architecture.")
                                isArchitecting = false
                                result.onSuccess { code ->
                                    generatedAppPlan = code
                                }.onFailure { err ->
                                    generatedAppPlan = "// Error architecting app: ${err.localizedMessage ?: "Unknown"}"
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("app_builder_generate_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676), contentColor = Color(0xFF003314)),
                    enabled = appDescription.isNotBlank() && !isArchitecting
                ) {
                    if (isArchitecting) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF003314), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Compiling Android App Architecture...", fontWeight = FontWeight.Bold)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PhoneAndroid, contentDescription = "Build")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate Android Application", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (generatedAppPlan != null) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ARCHITECTURE & COMPOSE CODE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF00E676)
                        )

                        Row {
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(generatedAppPlan!!))
                                Toast.makeText(context, "Copied Android plan to clipboard", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = JarvisCyan, modifier = Modifier.size(18.dp))
                            }

                            IconButton(onClick = {
                                coroutineScope.launch {
                                    val project = StudioProject(
                                        userId = currentUserId,
                                        title = appName,
                                        projectType = "app",
                                        language = "Kotlin / Compose",
                                        content = generatedAppPlan!!,
                                        description = appDescription.take(200),
                                        status = "completed"
                                    )
                                    repository.saveProject(currentUserId, project).onSuccess {
                                        Toast.makeText(context, "App saved to Cloud Projects!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }) {
                                Icon(Icons.Default.BookmarkAdd, contentDescription = "Save", tint = JarvisAmber, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0x3300E676), RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF030A05)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(14.dp)
                        ) {
                            Text(
                                text = generatedAppPlan!!,
                                color = Color(0xFFBBF7D0),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
