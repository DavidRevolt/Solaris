package com.davidrevolt.core.workmanager.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.davidrevolt.core.data.repository.LocationRepository
import com.davidrevolt.core.domain.SyncRelatedDataUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion

/**
 * Worker that syncs related data for a given location id
 * */
@HiltWorker
class SyncRelatedDataWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val locationRepository: LocationRepository,
    private val syncRelatedDataUseCase: SyncRelatedDataUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val locationId = inputData.getString(LOCATION_ID) ?: return Result.failure()
        val location = locationRepository.getLocation(locationId).first()
        if (location == null) return Result.failure()
        syncRelatedDataUseCase(location)
            .onCompletion { cause ->
                if (cause != null) {
                    Result.failure()
                }
            }
            .collect { }
        return Result.success()
    }

    /**
     * Lower API levels requires foreground information for expedited jobs
     * because workers are being with a foreground service and MUST HAVE NOTIFICATION!
     */
    override suspend fun getForegroundInfo(): ForegroundInfo =
        ForegroundInfo(SYNC_NOTIFICATION_ID, createSyncWorkerNotification(appContext))


    companion object {
        fun startUpExpeditedSyncWork(locationId: String) =
            OneTimeWorkRequestBuilder<DelegatingWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(SyncWorkerConstraints)
                .setInputData(
                    Data.Builder().putString(LOCATION_ID, locationId).putAll(
                        SyncRelatedDataWorker::class.classNameData()
                    ).build()
                )
                .build()
    }
}
