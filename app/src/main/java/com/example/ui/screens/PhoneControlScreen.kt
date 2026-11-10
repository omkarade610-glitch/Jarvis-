package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.DeviceControlService
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBorderCyan
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisDeepNavy
import com.example.ui.theme.JarvisGreenOnline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneControlScreen(
    deviceControl: DeviceControlService,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    var flashlightOn by remember { mutableStateOf(false) }
    var alarmMessage by remember { mutableStateOf("Wake up for JARVIS briefing") }
    var alarmHour by remember { mutableStateOf("7") }
    var alarmMinute by remember { mutableStateOf("00") }
    var youtubeSearch by remember { mutableStateOf("") }
    var chromeSearch by remember { mutableStateOf("") }

    val installedApps = remember { deviceControl.getInstalledLaunchableApps() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ANDROID DEVICE CONTROLS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = JarvisCyan
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("device_back_button")) {
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, JarvisBorderCyan, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1B38)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SYSTEM PROTOCOLS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = JarvisCyan,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Execute direct hardware telemetry and official Android system intents without bypassing device sandbox.",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            // Quick App Launchers Grid
            item {
                Text(
                    text = "ESSENTIAL SHORTCUTS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = JarvisAmber,
                    letterSpacing = 1.sp
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DeviceActionTile(
                        title = "Chrome",
                        icon = Icons.Default.Language,
                        color = Color(0xFF38BDF8),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            deviceControl.openChrome()
                            Toast.makeText(context, "Opening Chrome...", Toast.LENGTH_SHORT).show()
                        }
                    )
                    DeviceActionTile(
                        title = "YouTube",
                        icon = Icons.Default.PlayArrow,
                        color = Color(0xFFFF0000),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            deviceControl.openYouTube()
                            Toast.makeText(context, "Opening YouTube...", Toast.LENGTH_SHORT).show()
                        }
                    )
                    DeviceActionTile(
                        title = "Settings",
                        icon = Icons.Default.Settings,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            deviceControl.openSettings()
                            Toast.makeText(context, "Opening System Settings...", Toast.LENGTH_SHORT).show()
                        }
                    )
                    DeviceActionTile(
                        title = "Gallery",
                        icon = Icons.Default.Image,
                        color = Color(0xFF34D399),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            deviceControl.openGallery()
                            Toast.makeText(context, "Opening Gallery...", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // Flashlight & Volume Hardware Controls
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0x2200E5FF), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1733)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "HARDWARE TELEMETRY",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = JarvisCyan
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Flashlight
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FlashlightOn, contentDescription = "Flashlight", tint = if (flashlightOn) JarvisAmber else Color.Gray)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Flashlight Torch", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                    Text("CameraManager LED hardware", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                }
                            }
                            Switch(
                                checked = flashlightOn,
                                onCheckedChange = { newState ->
                                    val res = deviceControl.toggleFlashlight(newState)
                                    res.onSuccess {
                                        flashlightOn = newState
                                        Toast.makeText(context, "Flashlight: ${if (newState) "ON" else "OFF"}", Toast.LENGTH_SHORT).show()
                                    }.onFailure { err ->
                                        Toast.makeText(context, "Flashlight error: ${err.localizedMessage}", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = JarvisAmber,
                                    checkedTrackColor = Color(0xFF5A3E00)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Audio Volume Control
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Volume", tint = JarvisCyan)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Media Volume", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                    Text("AudioManager stream adjust", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                }
                            }

                            Row {
                                Button(
                                    onClick = {
                                        deviceControl.adjustVolume(false)
                                        Toast.makeText(context, "Volume lowered", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                                ) {
                                    Icon(Icons.Default.VolumeDown, contentDescription = "Down", modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        deviceControl.adjustVolume(true)
                                        Toast.makeText(context, "Volume raised", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan, contentColor = Color(0xFF00363A))
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Up", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Set Alarm
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0x2200E5FF), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1733)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Alarm, contentDescription = "Alarm", tint = JarvisAmber)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SCHEDULE DEVICE ALARM", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = JarvisAmber)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = alarmHour,
                                onValueChange = { alarmHour = it },
                                label = { Text("Hour (0-23)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = JarvisCyan,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            OutlinedTextField(
                                value = alarmMinute,
                                onValueChange = { alarmMinute = it },
                                label = { Text("Minute (0-59)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = JarvisCyan,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = alarmMessage,
                            onValueChange = { alarmMessage = it },
                            label = { Text("Alarm Label", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = JarvisCyan,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val h = alarmHour.toIntOrNull() ?: 7
                                val m = alarmMinute.toIntOrNull() ?: 0
                                deviceControl.setAlarm(h, m, alarmMessage)
                                Toast.makeText(context, "Dispatched Alarm to Android Clock", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = JarvisAmber, contentColor = Color(0xFF332000))
                        ) {
                            Text("Set System Alarm", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Installed Apps Launcher list
            if (installedApps.isNotEmpty()) {
                item {
                    Text(
                        text = "INSTALLED APPLICATIONS (${installedApps.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = JarvisCyan,
                        letterSpacing = 1.sp
                    )
                }

                items(installedApps.take(12)) { app ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val launched = deviceControl.launchAppByPackage(app.packageName)
                                if (launched) {
                                    Toast.makeText(context, "Launching ${app.name}...", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Unable to launch ${app.name}", Toast.LENGTH_SHORT).show()
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1B38)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = app.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(text = "LAUNCH", color = JarvisCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceActionTile(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(84.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1733)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFFFFF)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = title, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
