package com.davidrevolt.feature.locations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidrevolt.core.model.Location
import com.davidrevolt.feature.locations.components.LocationCard
import com.davidrevolt.feature.locations.components.LocationSearchBar
import com.davidrevolt.feature.locations.components.LocationSwipeToDismissCard
import com.davidrevolt.feature.locations.components.UseCurrentLocationCard


@Composable
fun LocationsScreen(
    onBackClick: () -> Unit,
    onLocationClick: (String) -> Unit,
    onLocationAddedEffect: (String) -> Unit,
    viewModel: LocationsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.locationsUiState.collectAsStateWithLifecycle()
    val searchUiState by viewModel.searchUiState.collectAsStateWithLifecycle()
    val snackbarHostState by viewModel.snackbarHostState.collectAsStateWithLifecycle()

    val data = uiState
    LocationsScreenContent(
        onBackClick = onBackClick,
        onLocationClick = onLocationClick,
        locations = data.locations,
        currentLocation = data.currentLocation,
        deleteLocation = viewModel::deleteLocation,
        onSyncCurrentLocationClick = viewModel::syncCurrentLocation,
        isSyncingCurrentLocation = data.isSyncingCurrentLocation,
        searchResults = searchUiState.searchResults,
        isSearching = searchUiState.isSearching,
        searchLocations = viewModel::searchLocations,
        onSearchResultClick = viewModel::addLocation,
        onLocationAddedEffect = onLocationAddedEffect,
        lastAddedLocation = searchUiState.lastAddedLocation,
        setLastAddedLocationId = viewModel::setLastAddedLocation,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationsScreenContent(
    onBackClick: () -> Unit,
    onLocationClick: (String) -> Unit,
    locations: List<Location>,
    currentLocation: Location?,
    deleteLocation: (Location) -> Unit,
    onSyncCurrentLocationClick: () -> Unit,
    isSyncingCurrentLocation: Boolean,
    searchResults: List<Location>,
    isSearching: Boolean,
    searchLocations: (String) -> Unit,
    onSearchResultClick: (Location) -> Unit,
    onLocationAddedEffect: (String) -> Unit,
    lastAddedLocation: Location?,
    setLastAddedLocationId: (Location?) -> Unit,
    snackbarHostState: SnackbarHostState,
) {

    LaunchedEffect(lastAddedLocation) {
        // this is used to trigger onLocationAdded function [Navigation func] after a new location is added
        // The value is set to null to not trigger navigation over and over again.
        if (lastAddedLocation != null) {
            onLocationAddedEffect(lastAddedLocation.id)
            setLastAddedLocationId(null)
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            MediumTopAppBar(
                title = { Text("Locations Management") },
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    Snackbar(
                        snackbarData,
                        shape = SnackbarDefaults.shape,
                        modifier = Modifier.safeDrawingPadding()
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LocationSearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    searchLocations(it)
                },
                onSearchClick = searchLocations,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onClearSearchClick = {
                    searchQuery = ""
                    searchLocations("")
                },
                onCancelSearchClick = {
                    expanded = false
                },
                searchResults = searchResults,
                onSearchResultClick = { location ->
                    onSearchResultClick(location)
                    searchQuery = ""
                    expanded = false
                },
                isSearching = isSearching
            )
            LocationsListBody(
                locations = locations,
                currentLocation = currentLocation,
                isSyncingCurrentLocation = isSyncingCurrentLocation,
                onSyncCurrentLocationClick = onSyncCurrentLocationClick,
                onLocationClick = onLocationClick,
                deleteLocation = deleteLocation
            )
        }
    }

}

@Composable
private fun LocationsListBody(
    locations: List<Location>,
    currentLocation: Location?,
    isSyncingCurrentLocation: Boolean,
    onSyncCurrentLocationClick: () -> Unit,
    onLocationClick: (String) -> Unit,
    deleteLocation: (Location) -> Unit,
) {
    val locationCardsColors = listOf(
        Color(0xFF3D5F90),
        Color(0xFF555F71),
        Color(0xFF6E5676),
        Color(0xFF447ACE),
        Color(0xFF6650a4),
        Color(0xFF625b71),
        Color(0xFF1A237E)
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 5.dp, bottom = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Current location
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.my_location_32dp),
                    contentDescription = "Current location icon",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Black
                )
                Text(
                    text = " Current location",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        item {
            if (currentLocation != null) {
                LocationCard(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .animateItem(),
                    location = currentLocation,
                    onLocationClick = onLocationClick,
                    containerColor = Color(0xFF1A237E)
                )
            } else
                UseCurrentLocationCard(
                    modifier = Modifier.animateItem(),
                    onClick = onSyncCurrentLocationClick,
                    isSyncingCurrentLocation = isSyncingCurrentLocation
                )
        }

        // Saved locations Cards [Excluding the current location]
        if (locations.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Saved locations icon",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Black
                    )
                    Text(
                        text = " Saved locations",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            itemsIndexed(items = locations, key = { _, it -> it.id }) { index, location ->
                LocationSwipeToDismissCard(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .animateItem(),
                    location = location,
                    onLocationClick = onLocationClick,
                    onDismiss = {
                        deleteLocation(location)
                    },
                    containerColor = locationCardsColors[index % locationCardsColors.size]
                )
            }
        }
    }
}


