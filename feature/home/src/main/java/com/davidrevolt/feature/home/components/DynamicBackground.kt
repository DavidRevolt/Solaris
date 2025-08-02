package com.davidrevolt.feature.home.components

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.RenderMode
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.davidrevolt.feature.home.R
import java.time.Instant
import java.time.ZoneId

/**
 * App Background that blurs the content when transitioning between loading state and content state
 */


@RequiresApi(Build.VERSION_CODES.S)
@Composable
internal fun DynamicSkyBackground(
    blurValue: Float,
    content: @Composable () -> Unit
) {
    val currentTime = Instant.now().atZone(ZoneId.systemDefault()).toLocalTime().hour
    val isDaytime = currentTime in 6..18


    val dayBackgroundAnimation by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.background_day_animation))
    val nightBackgroundAnimation by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.background_night_animation))

    /*
       // Load in viewmodel, classes, etc...
    val context = LocalContext.current
    val dayBackgroundAnimation =
        LottieCompositionFactory.fromRawRes(context, R.raw.background_day_animation)
    val nightBackgroundAnimation =
        LottieCompositionFactory.fromRawRes(context, R.raw.background_night_animation)
    */

    /*
    // Load from asset from main/assets
    val dayBackgroundAnimation by rememberLottieComposition(LottieCompositionSpec.Asset("background_night_animation.lottie"))
    val nightBackgroundAnimation by rememberLottieComposition(LottieCompositionSpec.Asset("background_night_animation.lottie"))
    */


    val backgroundAnimation = if (isDaytime) {
        dayBackgroundAnimation
    } else {
        nightBackgroundAnimation
    }

    // lottie takes time to load, meanwhile we show matching gradient
    val backgroundColors = if (isDaytime) {
        listOf(
            Color(0xFF4A7B8A), // Top - lighter blue-gray (combined effect)
            Color(0xFF4F8088), //
            Color(0xFF538489), //
            Color(0xFF4D7B83), // Middle section
            Color(0xFF41697A), //
            Color(0xFF3E6578), //
            Color(0xFF3A6176)  // Bottom - darkest blue-gray
        )
    } else {
        listOf(
            // matching gradient for lottie's background_night_animation
            Color(0xFF013A4C),
            Color(0xFF1B3A5D),
            Color(0xFF354B6F),
            Color(0xFF304E69),
            Color(0xFF335074),
            Color(0xFF37557E),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(backgroundColors))
            .graphicsLayer {
                if (blurValue > 0f) {
                    renderEffect = RenderEffect.createBlurEffect(
                        blurValue, blurValue, Shader.TileMode.CLAMP
                    ).asComposeRenderEffect()
                }
            }
    ) {
        val animationAlpha by animateFloatAsState(
            targetValue = if (backgroundAnimation == null) 0f else 1f,
            animationSpec = tween(durationMillis = 600), // fade duration
            label = "FadeIn"
        )
        LottieAnimation(
            composition = backgroundAnimation,
            modifier = Modifier.alpha(animationAlpha),
            speed = 0.3f,
            renderMode = RenderMode.HARDWARE,
            iterations = LottieConstants.IterateForever,
            contentScale = ContentScale.Crop
        )

        // Content (UI elements) on top of the background
        content()
    }
}
