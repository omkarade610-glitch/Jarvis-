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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import com.example.data.model.MemoryItem
import com.example.data.repository.JarvisRepository
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBorderCyan
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDeepNavy
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryScreen(
    currentUserId: String,
    repository: JarvisRepository,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val memories by repository.observeMemories(currentUserId).collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("Preferences") }
    var newKey by remember { mutableStateOf("") }
    var newValue by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }

    val filteredMemories = memories.filter { item ->
        searchQuery.isBlank() ||
                item.key.contains(searchQuery, ignoreCase = true) ||
                item.value.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PERSONAL MEMORY SYSTEM",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = JarvisCyan
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("memory_back_button")) {
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
            // Search Input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search stored memories...", color = Color(0xFF64748B), fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = JarvisCyan) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JarvisCyan,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 1
                )
            }

            // Add Memory Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, JarvisBorderCyan, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1B38)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = "Memory", tint = JarvisCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("STORE NEW MEMORY OR PREFERENCE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = JarvisCyan)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = newCategory,
                                onValueChange = { newCategory = it },
                                label = { Text("Category", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JarvisCyan, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            OutlinedTextField(
                                value = newKey,
                                onValueChange = { newKey = it },
                                label = { Text("Topic / Key", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JarvisCyan, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newValue,
                            onValueChange = { newValue = it },
                            label = { Text("Information / Rule to Remember", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JarvisCyan, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isPrivate,
                                onCheckedChange = { isPrivate = it },
                                colors = CheckboxDefaults.colors(checkedColor = JarvisAmber)
                            )
                            Text("Private Memory (Encrypted in user vault)", fontSize = 12.sp, color = Color(0xFFCBD5E1))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (newKey.isNotBlank() && newValue.isNotBlank()) {
                                    coroutineScope.launch {
                                        val item = MemoryItem(
                                            userId = currentUserId,
                                            category = newCategory.ifBlank { "General" },
                                            key = newKey,
                                            value = newValue,
                                            isPrivate = isPrivate
                                        )
                                        repository.saveMemory(currentUserId, item).onSuccess {
                                            newKey = ""
                                            newValue = ""
                                            Toast.makeText(context, "Memory committed to Firestore", Toast.LENGTH_SHORT).show()
                                        }.onFailure { e ->
                                            Toast.makeText(context, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("memory_commit_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan, contentColor = Color(0xFF00363A))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Commit to JARVIS Memory", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Memories List
            item {
                Text(
                    text = "RECALLED KNOWLEDGE ITEMS (${filteredMemories.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = JarvisAmber,
                    letterSpacing = 1.sp
                )
            }

            items(filteredMemories) { memory ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1630)),
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
                                        .background(Color(0xFF00363A), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = memory.category.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = JarvisCyan
                                    )
                                }
                                if (memory.isPrivate) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.Lock, contentDescription = "Private", tint = JarvisAmber, modifier = Modifier.size(14.dp))
                                }
                            }

                            IconButton(onClick = {
                                coroutineScope.launch {
                                    repository.deleteMemory(currentUserId, memory.id)
                                    Toast.makeText(context, "Memory erased", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Erase", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = memory.key, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = memory.value, fontSize = 13.sp, color = Color(0xFFCBD5E1))
                    }
                }
            }
        }
    }
}
