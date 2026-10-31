package com.israadev.fluxlauncher.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.israadev.fluxlauncher.core.social.FluxSocialManager
import com.israadev.fluxlauncher.core.social.FluxVoiceManager
import com.israadev.fluxlauncher.ui.theme.FluxColors
import kotlin.math.roundToInt

/**
 * Floating Dark Obsidian Cyber-Glass Voice Bar for Android Landscape
 * Mengambang di atas layar ketika user terhubung dalam Voice Room.
 * Mendukung drag bebas, toggle mute, buka room, dan disconnect.
 */
@Composable
fun FloatingVoiceBar(
    onOpenVoiceRoom: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected by FluxVoiceManager.isConnected.collectAsState()
    val isConnecting by FluxVoiceManager.isConnecting.collectAsState()
    val isMuted by FluxVoiceManager.isMuted.collectAsState()
    val activeRoom by FluxSocialManager.activeVoiceRoom.collectAsState()
    val activeSpeakers by FluxVoiceManager.activeSpeakers.collectAsState()

    if (!isConnected && !isConnecting && activeRoom == null) return

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Pulsing green wave animation when speaking
    val infiniteTransition = rememberInfiniteTransition(label = "voice_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val roomName = activeRoom?.name?.ifBlank { "Voice Room" } ?: "Voice Room"
    val participantCount = activeRoom?.participantCount ?: 1
    val isSomeoneSpeaking = activeSpeakers.isNotEmpty()
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        // Main Bar Content
        Row(
            modifier = Modifier
                .clip(shape)
                .background(FluxColors.SurfaceElevated, shape)
                .border(1.dp, FluxColors.CardBorder, shape)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Audio Pulsing Dot
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(24.dp)
            ) {
                if (isSomeoneSpeaking) {
                    Box(
                        modifier = Modifier
                            .size((16 * pulseScale).dp)
                            .clip(CircleShape)
                            .background(FluxColors.ForestGreen.copy(alpha = 0.35f))
                    )
                }
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (isMuted) Color(0xFFF43F5E) else FluxColors.ForestGreen)
                )
            }

            // 2. Room Title & Info
            Column(
                modifier = Modifier
                    .widthIn(max = 180.dp)
                    .clickable { onOpenVoiceRoom() }
            ) {
                Text(
                    text = roomName,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = FluxColors.ForestGreen,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isConnecting) "Connecting..." else "$participantCount online",
                        color = Color(0xFFA1A1AA),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 3. Mute / Unmute Button
            val btnShape = RoundedCornerShape(10.dp)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(btnShape)
                    .background(if (isMuted) Color(0xFFF43F5E) else FluxColors.ForestGreen, btnShape)
                    .border(1.dp, Color(0x33FFFFFF), btnShape)
                    .clickable { FluxVoiceManager.toggleMute() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = if (isMuted) "Unmute" else "Mute",
                    tint = if (isMuted) Color.White else Color(0xFF09090B),
                    modifier = Modifier.size(16.dp)
                )
            }

            // 4. Expand / Open Room Button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(btnShape)
                    .background(Color(0xFF222733), btnShape)
                    .border(1.dp, Color(0x26FFFFFF), btnShape)
                    .clickable { onOpenVoiceRoom() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInFull,
                    contentDescription = "Open Room",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }

            // 5. Leave Voice Call Button
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(btnShape)
                    .background(Color(0xFF3F1923), btnShape)
                    .border(1.dp, Color(0xFFF43F5E).copy(alpha = 0.4f), btnShape)
                    .clickable { FluxSocialManager.leaveVoiceRoom() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CallEnd,
                    contentDescription = "Leave Room",
                    tint = Color(0xFFF43F5E),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
