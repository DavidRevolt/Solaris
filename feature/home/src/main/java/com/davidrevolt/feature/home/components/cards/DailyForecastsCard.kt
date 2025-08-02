package com.davidrevolt.feature.home.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.davidrevolt.core.model.DailyForecast
import com.davidrevolt.feature.home.components.MeteoSourceAsyncIcon
import com.davidrevolt.feature.home.components.cards.common.ScrimCard
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun DailyForecastsCard(
    modifier: Modifier = Modifier,
    dailyForecasts: List<DailyForecast>
) {
    ScrimCard(
        modifier = modifier.fillMaxWidth(),
        title = "${dailyForecasts.size}-DAY FORECAST",
        leadingIcon = Icons.Rounded.DateRange
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            dailyForecasts.forEachIndexed { index, forecast ->
                DailyForecastItem(
                    modifier = Modifier.padding(vertical = 8.dp),
                    forecast = forecast
                )
                if (index != dailyForecasts.lastIndex)
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = Color.White.copy(alpha = 0.25f)
                    )
            }
        }
    }
}


@Composable
private fun DailyForecastItem(modifier: Modifier, forecast: DailyForecast) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Day of the week
        Text(
            text = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
                .format(forecast.date.atZone(ZoneId.systemDefault())),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )

        MeteoSourceAsyncIcon(icon = forecast.icon, modifier = Modifier.size(32.dp))

        // Min/Max Temperature
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "${forecast.temperatureMin}°",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "${forecast.temperatureMax}°",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}