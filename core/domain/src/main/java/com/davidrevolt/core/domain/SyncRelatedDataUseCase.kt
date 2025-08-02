package com.davidrevolt.core.domain

import com.davidrevolt.core.data.repository.POIRepository
import com.davidrevolt.core.data.repository.WeatherRepository
import com.davidrevolt.core.model.Location
import com.davidrevolt.core.model.SyncStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class SyncRelatedDataUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val poiRepository: POIRepository,
    @Named("IO")
    private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * Starts a synchronization process for location related data [Like weather and points of interest]
     * And return real time statuses of each related data sync process.
     * This function returns a cold [Flow], emitting the current sync status of each related data type.
     * It performs the sync operations in parallel.
     * channelFlow will complete (emit a Completed signal and close the channel) when done syncing.
     *
     * ## Concurrency:
     * - Uses `channelFlow` to allow running "launch".
     * - Uses `launch` to start new coroutines for each sync operation in parallel.
     * - Uses Dispatcher.IO for sync operations to launch on IO thread.
     * - `channelFlow` completes when all of its child coroutines have completed and the channelFlow block itself finishes execution.
     *
     * @param location the location for which to sync related data.
     * @return a cold [Flow] that emits [SyncStatuses] updates representing the progress and results of the sync operations.
     */

    operator fun invoke(location: Location): Flow<SyncStatuses> =
        channelFlow {
            Timber.i("Syncing related data for ${location.name}...")

            val mutex = Mutex() // dealing race conditioning when copying currentResult
            var syncStatuses = SyncStatuses()
            send(syncStatuses) // Initial state: Loading

            // Generic function to launch a sync job
            fun launchSyncJob(
                jobName: String,
                syncFunction: suspend () -> Result<Unit>,
                updateStatus: (SyncStatuses, SyncStatus) -> SyncStatuses
            ) =
                launch {
                    val syncStatus = syncFunction().fold(
                        onSuccess = {
                            Timber.i("$jobName for ${location.name} synced successfully")
                            SyncStatus.Success
                        },
                        onFailure = { throwable ->
                            Timber.e(throwable, "$jobName for ${location.name} sync has failed")
                            SyncStatus.Failure(throwable)
                        }
                    )
                    mutex.withLock {
                        syncStatuses = updateStatus(syncStatuses, syncStatus)
                        send(syncStatuses)
                    }
                }


            val weatherSyncJob = launchSyncJob(
                jobName = "Weather",
                syncFunction = {
                    weatherRepository.sync(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                },
                updateStatus = { syncStatuses, syncStatus ->
                    syncStatuses.copy(weather = syncStatus)
                }
            )

            val pointsOfInterestSyncJob = launchSyncJob(
                jobName = "POI",
                syncFunction = {
                    poiRepository
                        .sync(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            extraPromptInfo = location.name + ", " + location.country,
                        )
                },
                updateStatus = { syncStatuses, syncStatus ->
                    syncStatuses.copy(pointsOfInterest = syncStatus)
                }
            )

            joinAll(
                weatherSyncJob,
                pointsOfInterestSyncJob
            ) // Used just so the next Log will printed only when jobs done
            Timber.i("Syncing all related data for ${location.name} done")

        }.flowOn(ioDispatcher)

}


data class SyncStatuses(
    val weather: SyncStatus = SyncStatus.InProgress,
    val pointsOfInterest: SyncStatus = SyncStatus.InProgress
)
