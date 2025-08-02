package com.davidrevolt.feature.locations.components

import android.Manifest
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidrevolt.feature.locations.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun UseCurrentLocationCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isSyncingCurrentLocation: Boolean = false,
    containerColor: Color = Color.Black
) {

    val deviceLocationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )
    val allPermissionsRevoked =
        deviceLocationPermissionsState.permissions.size ==
                deviceLocationPermissionsState.revokedPermissions.size


    UseCurrentLocationCardBody(
        modifier = modifier,
        onClick = if (!allPermissionsRevoked) onClick else deviceLocationPermissionsState::launchMultiplePermissionRequest,
        isSyncingCurrentLocation = isSyncingCurrentLocation,
        containerColor = containerColor
    )
}

@Composable
private fun UseCurrentLocationCardBody(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    containerColor: Color,
    isSyncingCurrentLocation: Boolean
) {
    // Animation for rotating icon
    val infiniteTransition = rememberInfiniteTransition()
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "icon_animation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = !isSyncingCurrentLocation) { onClick() },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .let {
                    if (isSyncingCurrentLocation) {
                        it.background(
                            brush = shimmerBrush(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else it
                })
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.my_location_32dp),
                    contentDescription = "Current location",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(32.dp)
                        .rotate(if (isSyncingCurrentLocation) rotationAngle else 0f) // Rotation effect
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isSyncingCurrentLocation) "Syncing location..." else "Use current location",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// Shimmer effect brush
@Composable
private fun shimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.7f),
        Color.White.copy(alpha = 0.3f)
    )
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(0f, 0f),
        end = Offset(translateAnim, translateAnim)
    )
}