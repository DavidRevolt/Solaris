package com.davidrevolt.feature.sync_ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidrevolt.core.data.repository.LocationRepository
import com.davidrevolt.core.domain.SyncRelatedDataUseCase
import com.davidrevolt.core.model.Location
import com.davidrevolt.core.model.SyncStatus
import com.davidrevolt.core.workmanager.SyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = SyncUiViewModel.Factory::class)
class SyncUiViewModel @AssistedInject constructor(
    @Assisted private val locationId: String,
    private val locationRepository: LocationRepository,
    private val syncRelatedDataUseCase: SyncRelatedDataUseCase,
    private val syncManager: SyncManager
) : ViewModel() {

    @AssistedFactory
    interface Factory { // Assisted injection from navigation3 args
        fun create(locationId: String): SyncUiViewModel
    }

    private var syncJob: Job? = null

    private var _syncingUiState = MutableStateFlow(SyncingUiState())
    val syncingUiState = _syncingUiState.asStateFlow()


    init {
        viewModelScope.launch {
            locationRepository.getLocation(locationId).collect { location ->
                _syncingUiState.update { it.copy(location = location) }
                location?.let { it -> syncRelatedData(it) }
            }
        }
    }

    fun syncRelatedData(location: Location) {
        syncJob?.cancel() // Cancel the sync job if already running before launching new one
        syncJob = viewModelScope.launch {
            Timber.d("1. Starting syncLocationRelatedData")
            _syncingUiState.update { it.copy(isSyncing = true) }
            // launch cold flow that syncing and emits the real time statuses.
            syncRelatedDataUseCase(location)
                .onCompletion { throwable -> // Completion can be success, cancellation of flow, etc...
                    Timber.d(
                        throwable,
                        "2. syncLocationRelatedDataUseCase Flow Completed ${if (throwable != null) "with error" else "successfully"}"
                    )
                }
                .collect { statuses ->
                    _syncingUiState.update { state ->
                        state.copy(
                            weatherSyncSyncStatus = statuses.weather,
                            nearbyPOISyncStatus = statuses.pointsOfInterest
                        )
                    }
                }
        }.also { job ->
            job.invokeOnCompletion { throwable ->
                _syncingUiState.update { it.copy(isSyncing = false) }
                Timber.d(
                    throwable,
                    "3. syncRelatedData job completed ${if (throwable != null) "with error" else "successfully"}"
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (syncJob?.isCancelled == true) // User exists the screen during the sync job
            syncManager.syncRelatedDataExpedited(locationId)
    }

}


data class SyncingUiState(
    val isSyncing: Boolean = false,
    val location: Location? = null,
    val weatherSyncSyncStatus: SyncStatus = SyncStatus.InProgress,
    val nearbyPOISyncStatus: SyncStatus = SyncStatus.InProgress,
)
