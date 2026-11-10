package com.example.ui.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Brush
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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
fun DrawingStudioScreen(
    currentUserId: String,
    repository: JarvisRepository,
    aiService: JarvisAiService,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val drawingStyles = listOf(
        "Realistic Pencil Sketch",
        "B&W Manga/Anime",
        "Anime Hero",
        "Anime Villain",
        "Full-Body Character",
        "Face & Expression Sheet",
        "Front/Side/Back Views",
        "Cybernetic Concept Art"
    )

    var selectedStyle by remember { mutableStateOf("B&W Manga/Anime") }
    var prompt by remember { mutableStateOf("Cybernetic superhero with glowing energy visor and carbon fiber mantle in dramatic lighting") }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI DRAWING & ANIME STUDIO",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = JarvisCyan
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("drawing_back_button")) {
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
            // Style Selector
            item {
                Text(
                    text = "ARTISTIC DRAWING STYLE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = JarvisAmber,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(drawingStyles) { style ->
                        FilterChip(
                            selected = selectedStyle == style,
                            onClick = { selectedStyle = style },
                            label = { Text(style, fontSize = 12.sp) },
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

            // Prompt Input
            item {
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    label = { Text("Character / Scene Description", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("drawing_prompt_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JarvisCyan,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 4
                )
            }

            // Generate Button
            item {
                Button(
                    onClick = {
                        if (prompt.isNotBlank() && !isGenerating) {
                            isGenerating = true
                            errorMessage = null
                            generatedBitmap = null
                            coroutineScope.launch {
                                val fullPrompt = "$selectedStyle style: $prompt. High definition masterpiece, intricate details, professional linework."
                                val result = aiService.generateImage(fullPrompt)
                                isGenerating = false
                                result.onSuccess { bitmap ->
                                    generatedBitmap = bitmap
                                }.onFailure { err ->
                                    errorMessage = "Image synthesis error: ${err.localizedMessage ?: "Failed"}. Note: Firebase AI Logic runtime image models require the Firebase Blaze plan."
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("drawing_generate_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisAmber, contentColor = Color(0xFF332000)),
                    enabled = prompt.isNotBlank() && !isGenerating
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF332000), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Synthesizing Artwork (30-60s)...", fontWeight = FontWeight.Bold)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Draw")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate $selectedStyle", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0x33FF1744)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF1744)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFFF8A80),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Image Output Display
            if (generatedBitmap != null) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GENERATED MASTERPIECE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = JarvisCyan
                        )

                        IconButton(onClick = {
                            coroutineScope.launch {
                                val project = StudioProject(
                                    userId = currentUserId,
                                    title = "Artwork: ${prompt.take(30)}",
                                    projectType = "drawing",
                                    language = selectedStyle,
                                    content = "Prompt: $prompt\nStyle: $selectedStyle\nGenerated at ${System.currentTimeMillis()}",
                                    status = "completed"
                                )
                                repository.saveProject(currentUserId, project).onSuccess {
                                    Toast.makeText(context, "Artwork record saved to Cloud Projects!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }) {
                            Icon(Icons.Default.BookmarkAdd, contentDescription = "Save Artwork", tint = JarvisAmber)
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, JarvisBorderCyan, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF030712)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Image(
                            bitmap = generatedBitmap!!.asImageBitmap(),
                            contentDescription = "Generated Artwork",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}
