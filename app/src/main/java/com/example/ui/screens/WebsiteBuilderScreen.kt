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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Web
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
fun WebsiteBuilderScreen(
    currentUserId: String,
    repository: JarvisRepository,
    aiService: JarvisAiService,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var websiteName by remember { mutableStateOf("HoloMetrics AI Platform") }
    var websitePrompt by remember { mutableStateOf("Dark futuristic SaaS dashboard with interactive analytics cards, user authentication form, pricing table, and API webhook settings.") }
    var isBuilding by remember { mutableStateOf(false) }

    var frontendCode by remember { mutableStateOf<String?>(null) }
    var backendCode by remember { mutableStateOf<String?>(null) }
    var databaseSql by remember { mutableStateOf<String?>(null) }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Frontend (HTML/CSS/JS)", "Backend API", "Database SQL")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI WEBSITE BUILDER",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = JarvisCyan
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("website_back_button")) {
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
                    value = websiteName,
                    onValueChange = { websiteName = it },
                    label = { Text("Website / Product Name", fontSize = 12.sp) },
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
                    value = websitePrompt,
                    onValueChange = { websitePrompt = it },
                    label = { Text("Describe Full-Stack Website Specs", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(105.dp)
                        .testTag("website_spec_input"),
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
                        if (websitePrompt.isNotBlank() && !isBuilding) {
                            isBuilding = true
                            coroutineScope.launch {
                                val prompt = """
                                Build a complete full-stack website for "$websiteName".
                                Specs: $websitePrompt
                                
                                Provide 3 distinct sections clearly labeled with separators:
                                ===FRONTEND===
                                (Complete standalone HTML5 file with embedded responsive CSS3 and modern JavaScript)
                                ===BACKEND===
                                (Complete Node.js Express router endpoints for authentication and dashboard APIs)
                                ===DATABASE===
                                (Complete PostgreSQL schema with users, sessions, and data tables)
                                """.trimIndent()

                                val result = aiService.generateResponse(prompt, "You are JARVIS Full-Stack Architect. Deliver complete, working full-stack web artifacts.")
                                isBuilding = false
                                result.onSuccess { rawResponse ->
                                    val parts = rawResponse.split("===BACKEND===")
                                    val frontendPart = parts.getOrNull(0)?.replace("===FRONTEND===", "")?.trim() ?: rawResponse
                                    val rest = parts.getOrNull(1) ?: ""
                                    val backendParts = rest.split("===DATABASE===")
                                    val backendPart = backendParts.getOrNull(0)?.trim() ?: "// Backend routes"
                                    val dbPart = backendParts.getOrNull(1)?.trim() ?: "-- Database schema"

                                    frontendCode = frontendPart
                                    backendCode = backendPart
                                    databaseSql = dbPart
                                }.onFailure { err ->
                                    frontendCode = "<!-- Build error: ${err.localizedMessage ?: "Unknown"} -->"
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("website_build_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan, contentColor = Color(0xFF00363A)),
                    enabled = websitePrompt.isNotBlank() && !isBuilding
                ) {
                    if (isBuilding) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF00363A), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Architecting Full-Stack Application...", fontWeight = FontWeight.Bold)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Web, contentDescription = "Build")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate Full-Stack Website", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (frontendCode != null) {
                item {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color(0xFF0B1428),
                        contentColor = JarvisCyan,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = JarvisCyan
                            )
                        }
                    ) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                }

                item {
                    val currentContent = when (selectedTab) {
                        0 -> frontendCode.orEmpty()
                        1 -> backendCode.orEmpty()
                        else -> databaseSql.orEmpty()
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tabTitles[selectedTab].uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = JarvisAmber
                        )

                        Row {
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(currentContent))
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = JarvisCyan, modifier = Modifier.size(18.dp))
                            }

                            IconButton(onClick = {
                                coroutineScope.launch {
                                    val project = StudioProject(
                                        userId = currentUserId,
                                        title = websiteName,
                                        projectType = "website",
                                        language = "HTML/Node.js",
                                        content = currentContent,
                                        description = websitePrompt.take(200),
                                        status = "completed"
                                    )
                                    repository.saveProject(currentUserId, project).onSuccess {
                                        Toast.makeText(context, "Website project saved to Cloud!", Toast.LENGTH_SHORT).show()
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
                            .border(1.dp, JarvisBorderCyan, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF030712)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(14.dp)
                        ) {
                            Text(
                                text = currentContent,
                                color = Color(0xFF93C5FD),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
