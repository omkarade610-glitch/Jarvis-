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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
fun CodingStudioScreen(
    currentUserId: String,
    repository: JarvisRepository,
    aiService: JarvisAiService,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    val supportedLanguages = listOf(
        "Kotlin", "Python", "TypeScript", "JavaScript", "Rust", "Go",
        "Java", "C++", "C#", "Swift", "Dart", "HTML/CSS", "SQL", "PHP", "C", "Ruby"
    )

    val studioModes = listOf("Generate", "Explain", "Fix Bugs", "Refactor", "Full Architecture")

    var selectedLang by remember { mutableStateOf("Kotlin") }
    var selectedMode by remember { mutableStateOf("Generate") }
    var promptInput by remember { mutableStateOf("") }
    var projectTitle by remember { mutableStateOf("Quantum Algorithm Engine") }

    var isProcessing by remember { mutableStateOf(false) }
    var generatedCode by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ADVANCED CODING STUDIO",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = JarvisCyan
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("coding_back_button")) {
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
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Mode Selectors
            item {
                Text(
                    text = "STUDIO ACTION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = JarvisAmber,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(studioModes) { mode ->
                        FilterChip(
                            selected = selectedMode == mode,
                            onClick = { selectedMode = mode },
                            label = { Text(mode, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF004D54),
                                selectedLabelColor = JarvisCyan,
                                containerColor = Color(0xFF0D1B2A),
                                labelColor = Color(0xFF94A3B8)
                            )
                        )
                    }
                }
            }

            // Language Selector
            item {
                Text(
                    text = "TARGET LANGUAGE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = JarvisCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(supportedLanguages) { lang ->
                        FilterChip(
                            selected = selectedLang == lang,
                            onClick = { selectedLang = lang },
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

            // Inputs
            item {
                OutlinedTextField(
                    value = projectTitle,
                    onValueChange = { projectTitle = it },
                    label = { Text("Project / Module Title", fontSize = 12.sp) },
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
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    label = { Text("Code Description, Algorithm Specs, or Error Log", fontSize = 12.sp) },
                    placeholder = { Text("e.g. Build an asynchronous coroutine rate limiter in $selectedLang with backoff", color = Color(0xFF64748B), fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("coding_spec_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JarvisCyan,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 6
                )
            }

            // Action Button
            item {
                Button(
                    onClick = {
                        if (promptInput.isNotBlank() && !isProcessing) {
                            isProcessing = true
                            generatedCode = null
                            coroutineScope.launch {
                                val prompt = """
                                Action: $selectedMode
                                Target Language: $selectedLang
                                Title: $projectTitle
                                Specifications: $promptInput
                                
                                Instructions:
                                - Provide production-grade, cleanly structured, performant $selectedLang code.
                                - Include comprehensive error handling, unit test coverage examples, and inline doc comments.
                                - Avoid placeholder ellipsis or pseudo-code.
                                """.trimIndent()

                                val result = aiService.generateResponse(prompt, "You are JARVIS Senior Principal Software Engineer. Provide complete, tested, clean production code.")
                                isProcessing = false
                                result.onSuccess { code ->
                                    generatedCode = code
                                }.onFailure { err ->
                                    generatedCode = "// Compilation error: ${err.localizedMessage ?: "Unknown"}"
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("coding_generate_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan, contentColor = Color(0xFF00363A)),
                    enabled = promptInput.isNotBlank() && !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF00363A), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Synthesizing Code Architecture...", fontWeight = FontWeight.Bold)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Run")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("$selectedMode $selectedLang Code", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Generated Code Output
            if (generatedCode != null) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SOURCE CODE OUTPUT ($selectedLang)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = JarvisCyan
                        )

                        Row {
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(generatedCode!!))
                                Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = JarvisCyan, modifier = Modifier.size(18.dp))
                            }

                            IconButton(onClick = {
                                coroutineScope.launch {
                                    val project = StudioProject(
                                        userId = currentUserId,
                                        title = projectTitle,
                                        projectType = "coding",
                                        language = selectedLang,
                                        content = generatedCode!!,
                                        description = promptInput.take(200),
                                        status = "completed"
                                    )
                                    repository.saveProject(currentUserId, project).onSuccess {
                                        Toast.makeText(context, "Saved to Cloud Projects!", Toast.LENGTH_SHORT).show()
                                    }.onFailure { e ->
                                        Toast.makeText(context, "Save failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }) {
                                Icon(Icons.Default.BookmarkAdd, contentDescription = "Save Project", tint = JarvisAmber, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                item {
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
                                .padding(16.dp)
                        ) {
                            Text(
                                text = generatedCode!!,
                                color = Color(0xFF86EFAC),
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
