package com.davidrevolt.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidrevolt.core.data.repository.LocationRepository
import com.davidrevolt.core.domain.GetLocationsWithRelatedDataUseCase
import com.davidrevolt.core.domain.SyncRelatedDataUseCase
import com.davidrevolt.core.model.LocationWithRelatedData
import com.davidrevolt.core.workmanager.SyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = HomeViewModel.Factory::class)
class HomeViewModel @AssistedInject constructor(
    @Assisted private val locationIdToFocusOn: String?,
    private val locationRepository: LocationRepository,
    getLocationsWithRelatedData: GetLocationsWithRelatedDataUseCase,
    private val syncRelatedDataUseCase: SyncRelatedDataUseCase,
    syncManager: SyncManager
) : ViewModel() {

    @AssistedFactory
    interface Factory { // Assisted injection from navigation3 args
        fun create(locationIdToFocusOn: String?): HomeViewModel
    }

    private var syncJob: Job? = null
    private val _isSyncing = MutableStateFlow(false)

    val homeUiState =
        combine(getLocationsWithRelatedData(), _isSyncing) { locationsWithRelatedData, isSyncing ->
            HomeUiState.Data(locationsWithRelatedData, locationIdToFocusOn, isSyncing)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = HomeUiState.Loading
            )

    init {
        syncManager.syncAllRelatedDataPeriodic()
    }

    fun syncAllRelatedData() { // or use synManager to survive app termination
        syncJob?.cancel() // Cancel the previous sync job if it exists
        syncJob = viewModelScope.launch {
            _isSyncing.value = true
            locationRepository.syncCurrentLocation()
            locationRepository.getAllLocations().first().forEach { location ->
                // will move to next location iteration when the current flow/sync is completed
                syncRelatedDataUseCase(location)
                    .collect { }
            }
            _isSyncing.value = false
        }
    }
}

sealed interface HomeUiState {
    data class Data(
        val locationsWithRelatedData: List<LocationWithRelatedData> = emptyList(),
        val locationIdToFocusOn: String? = null,
        val isSyncing: Boolean = false
    ) : HomeUiState

    object Loading : HomeUiState
}

