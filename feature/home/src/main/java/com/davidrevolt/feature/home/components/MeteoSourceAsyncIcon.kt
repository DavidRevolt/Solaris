package com.davidrevolt.feature.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest

@Composable
internal fun MeteoSourceAsyncIcon(icon: Int, modifier: Modifier = Modifier) {
    val url = "https://www.meteosource.com/static/img/ico/weather/$icon.svg"
    Box(modifier) {
        AsyncImage(
            ImageRequest.Builder(LocalContext.current)
                .data(url)
                .build(),
            placeholder = null,
            contentDescription = "Weather Icon",
            modifier = Modifier
                .align(Alignment.Center)
                .clip(MaterialTheme.shapes.large)
                .fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}