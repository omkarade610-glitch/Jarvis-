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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CharacterProfile
import com.example.data.repository.JarvisRepository
import com.example.service.JarvisAiService
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBorderCyan
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDeepNavy
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDesignerScreen(
    currentUserId: String,
    repository: JarvisRepository,
    aiService: JarvisAiService,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val savedCharacters by repository.observeCharacters(currentUserId).collectAsState(initial = emptyList())

    var name by remember { mutableStateOf("Kaelen Voss") }
    var ageCategory by remember { mutableStateOf("Young Adult (22)") }
    var personality by remember { mutableStateOf("Stoic, fiercely protective, tactical thinker with dry humor") }
    var powers by remember { mutableStateOf("Gravitational manipulation and plasma kinetic discharge") }
    var costume by remember { mutableStateOf("Matte obsidian composite armor with luminescent azure energy conduits") }
    var hairstyle by remember { mutableStateOf("Undercut with silver-frosted bangs") }
    var weapons by remember { mutableStateOf("Foldable quantum phase glaive") }
    var bio by remember { mutableStateOf("Former elite orbital vanguard commander who broke protocol to safeguard the Last Colony.") }

    var isGeneratingIdea by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI CHARACTER DESIGNER",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = JarvisCyan
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("character_back_button")) {
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
            // Auto Idea Generator
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0x33FFD600), RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1700)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("AI Auto-Character Synthesis", fontWeight = FontWeight.Bold, color = JarvisAmber, fontSize = 13.sp)
                            Text("Generate an original, complete anime/movie character concept", fontSize = 11.sp, color = Color(0xFFFDE68A))
                        }
                        Button(
                            onClick = {
                                isGeneratingIdea = true
                                coroutineScope.launch {
                                    val prompt = """
                                    Generate an original anime/movie character concept.
                                    Return concise text with lines labeled:
                                    NAME:
                                    AGE:
                                    PERSONALITY:
                                    POWERS:
                                    COSTUME:
                                    HAIR:
                                    WEAPONS:
                                    BIO:
                                    """.trimIndent()

                                    aiService.generateResponse(prompt, "You are a master character designer.").onSuccess { res ->
                                        res.lines().forEach { line ->
                                            when {
                                                line.startsWith("NAME:") -> name = line.substringAfter("NAME:").trim()
                                                line.startsWith("AGE:") -> ageCategory = line.substringAfter("AGE:").trim()
                                                line.startsWith("PERSONALITY:") -> personality = line.substringAfter("PERSONALITY:").trim()
                                                line.startsWith("POWERS:") -> powers = line.substringAfter("POWERS:").trim()
                                                line.startsWith("COSTUME:") -> costume = line.substringAfter("COSTUME:").trim()
                                                line.startsWith("HAIR:") -> hairstyle = line.substringAfter("HAIR:").trim()
                                                line.startsWith("WEAPONS:") -> weapons = line.substringAfter("WEAPONS:").trim()
                                                line.startsWith("BIO:") -> bio = line.substringAfter("BIO:").trim()
                                            }
                                        }
                                    }
                                    isGeneratingIdea = false
                                }
                            },
                            enabled = !isGeneratingIdea,
                            colors = ButtonDefaults.buttonColors(containerColor = JarvisAmber, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (isGeneratingIdea) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "Auto", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Input Fields
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Character Name", fontSize = 12.sp) },
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = ageCategory,
                        onValueChange = { ageCategory = it },
                        label = { Text("Age / Category", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JarvisCyan, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = hairstyle,
                        onValueChange = { hairstyle = it },
                        label = { Text("Hairstyle & Color", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JarvisCyan, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = personality,
                    onValueChange = { personality = it },
                    label = { Text("Personality & Traits", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JarvisCyan, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    maxLines = 2
                )
            }

            item {
                OutlinedTextField(
                    value = powers,
                    onValueChange = { powers = it },
                    label = { Text("Powers & Signature Abilities", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JarvisCyan, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    maxLines = 2
                )
            }

            item {
                OutlinedTextField(
                    value = costume,
                    onValueChange = { costume = it },
                    label = { Text("Costume / Armor Design", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JarvisCyan, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    maxLines = 2
                )
            }

            item {
                OutlinedTextField(
                    value = weapons,
                    onValueChange = { weapons = it },
                    label = { Text("Weapons & Accessories", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JarvisCyan, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    maxLines = 2
                )
            }

            item {
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Character Biography", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JarvisCyan, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    maxLines = 4
                )
            }

            item {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            coroutineScope.launch {
                                val charProfile = CharacterProfile(
                                    userId = currentUserId,
                                    name = name,
                                    ageCategory = ageCategory,
                                    personality = personality,
                                    powers = powers,
                                    costume = costume,
                                    hairstyle = hairstyle,
                                    weapons = weapons,
                                    bio = bio
                                )
                                repository.saveCharacter(currentUserId, charProfile).onSuccess {
                                    Toast.makeText(context, "Character saved to Cloud!", Toast.LENGTH_SHORT).show()
                                }.onFailure { e ->
                                    Toast.makeText(context, "Save error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_character_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan, contentColor = Color(0xFF00363A))
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Character to Firestore Database", fontWeight = FontWeight.Bold)
                }
            }

            // Saved Characters List
            if (savedCharacters.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "SAVED CHARACTER ROSTER (${savedCharacters.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = JarvisAmber,
                        letterSpacing = 1.sp
                    )
                }

                items(savedCharacters) { char ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1A36)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(Color(0xFF00363A), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Person, contentDescription = "Avatar", tint = JarvisCyan, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = char.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(text = char.ageCategory, color = JarvisAmber, fontSize = 11.sp)
                                    }
                                }

                                IconButton(onClick = {
                                    coroutineScope.launch {
                                        repository.deleteCharacter(currentUserId, char.id)
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Powers: ${char.powers}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Text(text = "Bio: ${char.bio}", color = Color(0xFFE2E8F0), fontSize = 12.sp, maxLines = 2)
                        }
                    }
                }
            }
        }
    }
}
