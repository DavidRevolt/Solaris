package com.davidrevolt.feature.sync_ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.davidrevolt.core.model.SyncStatus


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncUiScreen(
    onBackClick: () -> Unit,
    viewModel: SyncUiViewModel,
) {
    val uiState by viewModel.syncingUiState.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Syncing data For ${uiState.location?.name}...",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                modifier = Modifier.clip(MaterialTheme.shapes.large),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        uiState.location?.let {
                            viewModel.syncRelatedData(
                                it
                            )
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Sync again",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            StatusRow(
                description = "Fetching weather data",
                syncStatus = uiState.weatherSyncSyncStatus
            )
            StatusRow(
                description = "Fetching nearby points of interest",
                syncStatus = uiState.nearbyPOISyncStatus
            )
        }
    }
}


@Composable
fun StatusRow(description: String, syncStatus: SyncStatus) {
    val successGreen = Color(0xFF4CAF50)
    val errorRed = Color(0xFFF44336)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.size(24.dp), // Size for the icon/indicator
            contentAlignment = Alignment.Center
        ) {
            when (syncStatus) {
                SyncStatus.InProgress -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp), // Slightly smaller than Box
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                SyncStatus.Success -> {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Success",
                        tint = successGreen
                    )
                }

                is SyncStatus.Failure -> {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Error",
                        tint = errorRed
                    )
                }
            }
        }

        // Description Text
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge
        )

        // Error message if applicable
        if (syncStatus is SyncStatus.Failure) {
            Spacer(Modifier.width(8.dp))
            Text(
                text = "(${syncStatus.throwable.message})",
                color = errorRed,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
