package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.ContentCopy
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
fun AnimeStudioScreen(
    currentUserId: String,
    repository: JarvisRepository,
    aiService: JarvisAiService,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var animeTitle by remember { mutableStateOf("Chronicles of Aethelgard: Project Chimera") }
    var animeGenre by remember { mutableStateOf("Cyber-Fantasy / Mecha Shonen") }
    var episodeCount by remember { mutableStateOf("12") }
    var premise by remember { mutableStateOf("In a neon-drenched metropolis built atop ancient ruins, a rookie mecha pilot uncovers an artificial consciousness that can alter temporal physics.") }

    var isWriting by remember { mutableStateOf(false) }
    var generatedProductionScript by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ANIME PRODUCTION STUDIO",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = JarvisCyan
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("anime_back_button")) {
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1030)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("PRODUCTION PIPELINE // SCENE-BY-SCENE", fontWeight = FontWeight.Bold, color = Color(0xFFEA80FC), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Generates complete season narrative arc (10-12 episodes), character dialogue, scene shot directions, storyboard beats, and sound FX cues.", color = Color(0xFFE2E8F0), fontSize = 11.sp)
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = animeTitle,
                    onValueChange = { animeTitle = it },
                    label = { Text("Anime Title", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JarvisCyan, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    maxLines = 1
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = animeGenre,
                        onValueChange = { animeGenre = it },
                        label = { Text("Genre", fontSize = 11.sp) },
                        modifier = Modifier.weight(1.5f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JarvisCyan, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = episodeCount,
                        onValueChange = { episodeCount = it },
                        label = { Text("Episodes", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JarvisCyan, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = premise,
                    onValueChange = { premise = it },
                    label = { Text("Core Premise & Story Arc", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("anime_premise_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JarvisCyan, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    maxLines = 5
                )
            }

            item {
                Button(
                    onClick = {
                        if (premise.isNotBlank() && !isWriting) {
                            isWriting = true
                            coroutineScope.launch {
                                val prompt = """
                                Produce a professional Anime Production Script & Storyboard plan for "$animeTitle".
                                Genre: $animeGenre
                                Total Episodes: $episodeCount
                                Premise: $premise
                                
                                Provide:
                                1. Full Season Arc breakdown (Episodes 1 through $episodeCount with title and major climax).
                                2. Pilot Episode 1 scene-by-scene script:
                                   - SCENE 1: Visual Shot (Wide/Close/Pacing), Environment, Lighting, Sound FX
                                   - Character Dialogue with vocal inflection notes
                                   - Key animation director notes
                                3. Storyboard description and background art prompt.
                                """.trimIndent()

                                val result = aiService.generateResponse(prompt, "You are JARVIS Anime Showrunner & Director.")
                                isWriting = false
                                result.onSuccess { script ->
                                    generatedProductionScript = script
                                }.onFailure { err ->
                                    generatedProductionScript = "Production script error: ${err.localizedMessage ?: "Unknown"}"
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("anime_generate_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA80FC), contentColor = Color.Black),
                    enabled = premise.isNotBlank() && !isWriting
                ) {
                    if (isWriting) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Drafting Series Arc & Script...", fontWeight = FontWeight.Bold)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Animation, contentDescription = "Write")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate Anime Series & Storyboard", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (generatedProductionScript != null) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRODUCTION SCRIPT & STORYBOARD",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFFEA80FC)
                        )

                        Row {
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(generatedProductionScript!!))
                                Toast.makeText(context, "Copied script to clipboard", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = JarvisCyan, modifier = Modifier.size(18.dp))
                            }

                            IconButton(onClick = {
                                coroutineScope.launch {
                                    val project = StudioProject(
                                        userId = currentUserId,
                                        title = animeTitle,
                                        projectType = "anime",
                                        language = "Script / Storyboard",
                                        content = generatedProductionScript!!,
                                        description = premise.take(200),
                                        status = "completed"
                                    )
                                    repository.saveProject(currentUserId, project).onSuccess {
                                        Toast.makeText(context, "Anime production saved to Cloud Projects!", Toast.LENGTH_SHORT).show()
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
                            .border(1.dp, Color(0x33EA80FC), RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0B1E)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = generatedProductionScript!!,
                            color = Color(0xFFF3E8FF),
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
