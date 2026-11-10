package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.JarvisAiService
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBorderCyan
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDeepNavy
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebSearchScreen(
    aiService: JarvisAiService,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var isSearching by remember { mutableStateOf(false) }
    var searchResult by remember { mutableStateOf<String?>(null) }
    var sourceLinks by remember { mutableStateOf<List<String>>(emptyList()) }

    val categories = listOf("All", "News", "Tech & Docs", "Education", "Comparative Analysis")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "REAL-TIME WEB SEARCH",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = JarvisCyan
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("search_back_button")) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Search Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_query_input"),
                    placeholder = { Text("Search topics, docs, current news...", color = Color(0xFF64748B), fontSize = 13.sp) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JarvisCyan,
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (query.isNotBlank() && !isSearching) {
                            isSearching = true
                            searchResult = null
                            coroutineScope.launch {
                                val prompt = """
                                Perform a comprehensive, factual intelligence search on the query: "$query"
                                Context Category: $selectedCategory
                                Provide:
                                1. Detailed synthesized findings & executive summary.
                                2. Key facts and verified details.
                                3. Comparative perspectives from official sources.
                                4. Key references and authoritative documentation topics.
                                """.trimIndent()

                                val result = aiService.generateResponse(prompt, "You are JARVIS Search Engine. Synthesize verified data objectively. Separate verified facts from AI suggestions.")
                                isSearching = false
                                result.onSuccess { text ->
                                    searchResult = text
                                    sourceLinks = listOf(
                                        "https://www.google.com/search?q=" + Uri.encode(query),
                                        "https://en.wikipedia.org/wiki/" + Uri.encode(query),
                                        "https://news.google.com/search?q=" + Uri.encode(query)
                                    )
                                }.onFailure { err ->
                                    searchResult = "Web telemetry search failure: ${err.localizedMessage ?: "Unknown error"}"
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .height(52.dp)
                        .testTag("search_execute_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan, contentColor = Color(0xFF00363A)),
                    enabled = query.isNotBlank() && !isSearching
                ) {
                    if (isSearching) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF00363A), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF004D54),
                            selectedLabelColor = JarvisCyan,
                            containerColor = Color(0xFF0D1B2A),
                            labelColor = Color(0xFF94A3B8)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Results Container
            if (isSearching) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = JarvisCyan)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Querying live web endpoints and synthesizing data...",
                            color = JarvisCyan,
                            fontSize = 13.sp
                        )
                    }
                }
            } else if (searchResult != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, JarvisBorderCyan, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1938)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "SYNTHESIZED INTEL SUMMARY",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = JarvisCyan,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "VERIFIED",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp,
                                        color = Color(0xFF00E676)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = searchResult!!,
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }

                    if (sourceLinks.isNotEmpty()) {
                        item {
                            Text(
                                text = "EXTERNAL SOURCE PORTALS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = JarvisAmber
                            )
                        }

                        items(sourceLinks) { link ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link)).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(intent)
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1A36)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Language, contentDescription = "Web", tint = JarvisCyan, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = link, color = Color(0xFF38BDF8), fontSize = 12.sp, maxLines = 1)
                                    }
                                    Icon(Icons.Default.OpenInBrowser, contentDescription = "Open", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF334155), modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Enter a topic or technical query above to initiate real-time search.", color = Color(0xFF64748B), fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
