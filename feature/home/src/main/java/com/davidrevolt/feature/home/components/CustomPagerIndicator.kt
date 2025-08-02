package com.davidrevolt.feature.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
internal fun CustomPagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.White.copy(alpha = 0.4f),
    indicatorSize: Dp = 8.dp,
    spacing: Dp = 4.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val pageOffsetFraction = pagerState.currentPageOffsetFraction.coerceIn(-1f, 1f)

        repeat(pagerState.pageCount) { index ->
            val selected = pagerState.currentPage == index
            val isNext = pagerState.currentPage + 1 == index
            val isPrev = pagerState.currentPage - 1 == index

            val targetScale = when {
                selected -> 1.5f - 0.5f * abs(pageOffsetFraction)
                isNext && pageOffsetFraction > 0 -> 1f + 0.5f * pageOffsetFraction
                isPrev && pageOffsetFraction < 0 -> 1f - 0.5f * pageOffsetFraction
                else -> 1f
            }

            val animatedScale by animateFloatAsState(
                targetValue = targetScale,
                animationSpec = tween(durationMillis = 80, easing = FastOutSlowInEasing),
                label = "animated scale"
            )

            val targetColor = when {
                selected -> lerp(activeColor, inactiveColor, abs(pageOffsetFraction))
                isNext && pageOffsetFraction > 0 -> lerp(
                    inactiveColor,
                    activeColor,
                    pageOffsetFraction
                )

                isPrev && pageOffsetFraction < 0 -> lerp(
                    inactiveColor,
                    activeColor,
                    -pageOffsetFraction
                )

                else -> inactiveColor
            }


            val animatedColor by animateColorAsState(
                targetValue = targetColor,
                animationSpec = tween(durationMillis = 80, easing = FastOutSlowInEasing),
                label = "animated color"
            )

            Box(
                modifier = Modifier
                    .size(indicatorSize)
                    .scale(animatedScale)
                    .background(animatedColor, CircleShape)
            )
        }
    }
}