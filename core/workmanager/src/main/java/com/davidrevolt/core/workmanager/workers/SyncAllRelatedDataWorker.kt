package com.davidrevolt.core.workmanager.workers


import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.davidrevolt.core.data.repository.LocationRepository
import com.davidrevolt.core.domain.SyncRelatedDataUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import java.util.concurrent.TimeUnit

/**
 * Worker that syncs all related data
 * */
@HiltWorker
class SyncAllRelatedDataWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val locationRepository: LocationRepository,
    private val syncRelatedDataUseCase: SyncRelatedDataUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        locationRepository.syncCurrentLocation()
        locationRepository.getAllLocations().first().forEach { location ->
            // will move to next location iteration when the current flow/sync is completed
            syncRelatedDataUseCase(location)
                .onCompletion { cause ->
                    if (cause != null) {
                        Result.failure()
                    }
                }
                .collect { }
        }
        return Result.success()
    }

    /**
     * Lower API levels requires foreground information for expedited jobs
     * because workers are being with a foreground service and MUST HAVE NOTIFICATION!
     */
    override suspend fun getForegroundInfo(): ForegroundInfo =
        ForegroundInfo(SYNC_NOTIFICATION_ID, createSyncWorkerNotification(appContext))


    companion object {
        fun startUpExpeditedSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(SyncWorkerConstraints)
            .setInputData(SyncAllRelatedDataWorker::class.classNameData())
            .build()

        fun startUpPeriodicSyncWork() =
            PeriodicWorkRequestBuilder<DelegatingWorker>(2, TimeUnit.HOURS)
                .setConstraints(SyncWorkerConstraints)
                .setInputData(SyncAllRelatedDataWorker::class.classNameData())
                .build()
    }
}
