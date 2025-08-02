package com.davidrevolt.feature.home.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.davidrevolt.core.model.PointOfInterest
import com.davidrevolt.feature.home.components.cards.common.ScrimCard

@Composable
internal fun POICard(
    modifier: Modifier = Modifier,
    pointsOfInterest: List<PointOfInterest>
) {
    ScrimCard(
        modifier = modifier.fillMaxWidth(),
        title = "POINTS OF INTEREST"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
        ) {
            if (pointsOfInterest.isEmpty())
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Nothing to show at the moment",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            else {
                pointsOfInterest.forEachIndexed { index, poi ->
                    PointOfInterestItem(
                        Modifier.padding(vertical = 8.dp),
                        name = poi.name,
                        description = poi.description
                    )
                    if (index != pointsOfInterest.lastIndex)
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = Color.White.copy(alpha = 0.25f)
                        )
                }
            }
        }
    }
}


@Composable
private fun PointOfInterestItem(
    modifier: Modifier = Modifier,
    name: String,
    description: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = Icons.Rounded.Place,
            contentDescription = "Points of interest",
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier
                .size(32.dp)
                .padding(end = 4.dp)
        )
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }

    }
}