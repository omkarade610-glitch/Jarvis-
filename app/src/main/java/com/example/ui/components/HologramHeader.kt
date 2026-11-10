package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisGreenOnline

@Composable
fun HologramHeader(
    assistantName: String = "JARVIS",
    ownerName: String = "Sir",
    isListening: Boolean = false,
    subtitle: String = "SYSTEM ONLINE // GEMINI 3.8 ACTIVE",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hologram")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF071228),
                        Color(0xFF0A1938)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated Holographic Core
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .scale(pulse)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(
                                if (isListening) JarvisAmber else JarvisCyan,
                                Color(0x2200E5FF),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Spinning outer ring
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .rotate(rotation)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    JarvisCyan,
                                    Color.Transparent,
                                    Color(0xFF80DEEA),
                                    Color.Transparent,
                                    JarvisCyan
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Inner core
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            color = Color(0xFF00363A),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.5.dp,
                            color = if (isListening) JarvisAmber else JarvisCyan,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isListening) "🎙" else "J",
                        color = if (isListening) JarvisAmber else JarvisCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = assistantName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = JarvisCyan,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(JarvisGreenOnline, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ONLINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = JarvisGreenOnline,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Welcome, $ownerName",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE2E8F0),
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isListening) JarvisAmber else Color(0xFF7DD3FC),
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
