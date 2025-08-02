package com.davidrevolt.feature.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.davidrevolt.core.model.DailyForecast
import kotlin.text.Typography.degree


@Composable
internal fun TempDisplay(
    modifier: Modifier = Modifier,
    dailyForecast: DailyForecast,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "${dailyForecast.temperature.toInt()}$degree",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Light,
                fontSize = 140.sp
            ),
            color = Color.White,
        )
        val minMaxTemp =
            "${dailyForecast.temperatureMin}$degree/${dailyForecast.temperatureMax}$degree "
        Text(
            text = minMaxTemp + dailyForecast.shortDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}