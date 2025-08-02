package com.davidrevolt.feature.locations


import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidrevolt.core.data.repository.LocationRepository
import com.davidrevolt.core.domain.DeleteLocationWithRelatedDataUseCase
import com.davidrevolt.core.model.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val deleteLocationWithRelatedDataUseCase: DeleteLocationWithRelatedDataUseCase
) : ViewModel() {

    // used to optimize the search process and reduce unnecessary network requests
    private var searchJob: Job? = null
    private val _searchUiState = MutableStateFlow(SearchUiState())
    val searchUiState = _searchUiState.asStateFlow()

    private var syncCurrentLocationJob: Job? = null
    private val _isSyncingCurrentLocation = MutableStateFlow(false)

    private var _snackbarHostState = MutableStateFlow(SnackbarHostState())
    val snackbarHostState = _snackbarHostState.asStateFlow()

    val locationsUiState = combine(
        locationRepository.getCurrentLocationId(),
        locationRepository.getAllLocations(),
        _isSyncingCurrentLocation
    ) { currentLocationId, locations, isSyncingCurrentLocation ->
        LocationsUiState.Data(
            locations = locations.filter { it.id != currentLocationId },
            currentLocation = locations.find { it.id == currentLocationId },
            isSyncingCurrentLocation = isSyncingCurrentLocation
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LocationsUiState.Data()
    )

    fun searchLocations(searchQuery: String) {
        searchJob?.cancel() // Cancel the previous search job if it exists
        searchJob = viewModelScope.launch {
            if (searchQuery.isNotEmpty()) {
                delay(300) // Wait 300 to see if new searchLocations is called to avoid unnecessary network requests
                _searchUiState.update { it.copy(isSearching = true) }
                locationRepository.searchLocations(searchQuery)
                    .onSuccess { result ->
                        _searchUiState.update {
                            it.copy(
                                isSearching = false,
                                searchResults = result
                            )
                        }
                    }
                    .onFailure { error ->
                        _searchUiState.update {
                            it.copy(
                                isSearching = false,
                                searchResults = emptyList(),
                            )
                        }
                        _snackbarHostState.value.showSnackbar("Error fetching location ${error.message}")
                    }
            } else { // Empty searchQuery
                _searchUiState.update { it.copy(isSearching = false, searchResults = emptyList()) }
            }
        }
    }

    fun syncCurrentLocation() {
        syncCurrentLocationJob?.cancel()
        syncCurrentLocationJob = viewModelScope.launch {
            try {
                _isSyncingCurrentLocation.update { true }
                locationRepository.syncCurrentLocation()
                    .onSuccess { location ->
                        setLastAddedLocation(location)
                    }
                    .onFailure { error -> // Network errors or gps return null coordinates
                        _snackbarHostState.value.showSnackbar("Can't fetch the current location ${error.message}")
                    }
            } catch (e: Exception) { // Missing Permissions
                _snackbarHostState.value.showSnackbar("Can't fetch the current location ${e.message}")
            } finally {
                _isSyncingCurrentLocation.update { false }
            }
        }
    }

    fun addLocation(location: Location) =
        viewModelScope.launch {
            locationRepository.addLocation(location)
            setLastAddedLocation(location)
        }


    fun deleteLocation(location: Location) {
        viewModelScope.launch {
            deleteLocationWithRelatedDataUseCase(location)
        }
    }

    fun setLastAddedLocation(location: Location?) {
        _searchUiState.update { it.copy(lastAddedLocation = location) }
    }
}

sealed interface LocationsUiState {
    data class Data(
        val locations: List<Location> = emptyList(),
        val currentLocation: Location? = null,
        val isSyncingCurrentLocation: Boolean = false,
    ) : LocationsUiState
}

/**
 * Represents the UI state of the Search bar composable.
 *
 * @property @lastAddedLocation The last location ID added from the search results.
 */
data class SearchUiState(
    val isSearching: Boolean = false,
    val lastAddedLocation: Location? = null,
    val searchResults: List<Location> = emptyList(),
)