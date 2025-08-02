package com.davidrevolt.feature.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.sin


@Composable
internal fun NoSavedLocationsMessage(
    navigateToLocationsManagement: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    // Trigger animation on screen load
    LaunchedEffect(Unit) {
        delay(200) // Slight delay for smooth entry
        isVisible = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4A90E2), // Sky blue
                        Color(0xFF8CC7F0)  // Lighter blue
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(800)) + scaleIn(animationSpec = tween(800)),
            exit = fadeOut(animationSpec = tween(800)) + scaleOut(animationSpec = tween(800))
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No Locations Added",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 28.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.3f),
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Tap the button below to add a location and start exploring the weather!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                // Awesome new button with pulsating aurora and glowing border
                val infiniteTransition = rememberInfiniteTransition()
                val auroraOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 2 * Math.PI.toFloat(),
                    animationSpec = infiniteRepeatable(
                        animation = tween(4000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "aurora offset"
                )
                val glowAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 0.6f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "glow alpha"
                )
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.92f else 1f,
                    animationSpec = tween(
                        200,
                        easing = androidx.compose.animation.core.FastOutSlowInEasing
                    ),
                    label = "press scale"
                )
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .drawWithContent {
                            // Glowing border
                            drawCircle(
                                color = Color(0xFF00CED1).copy(alpha = glowAlpha),
                                radius = size.minDimension / 2 + 8.dp.toPx(),
                                blendMode = androidx.compose.ui.graphics.BlendMode.SrcOver
                            )
                            // Aurora-like gradient with wave effect
                            val colors = listOf(
                                Color(0xFF1E90FF), // Dodger Blue (vibrant ocean)
                                Color(0xFF87CEFA), // Light Sky Blue (breezy sky)
                                Color(0xFF4682B4), // Steel Blue (deep sea)
                                Color(0xFF1E90FF)  // Dodger Blue for smooth loop
                            )
                            val gradientPoints = List(8) { i ->
                                val x = size.width * (i / 7f)
                                val y =
                                    size.height / 2 + sin(auroraOffset + i * 0.6f) * size.height * 0.25f
                                Offset(x, y)
                            }
                            drawCircle(
                                brush = Brush.linearGradient(
                                    colors = colors,
                                    start = gradientPoints.first(),
                                    end = gradientPoints.last()
                                ),
                                radius = size.minDimension / 2
                            )
                            drawContent()
                        }
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.15f), // Subtle highlight
                                    Color.Transparent
                                ),
                                radius = 150f
                            ),
                            shape = CircleShape
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.6f),
                                    Color.White.copy(alpha = 0.3f)
                                )
                            ),
                            shape = CircleShape
                        )
                        .scale(scale)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { navigateToLocationsManagement() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add Location",
                        modifier = Modifier
                            .size(50.dp)
                            .alpha(0.9f),
                        tint = Color.White
                    )
                }
            }
        }
    }
}