package com.davidrevolt.core.workmanager

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.davidrevolt.core.workmanager.workers.SyncAllRelatedDataWorker
import com.davidrevolt.core.workmanager.workers.SyncRelatedDataWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * SyncManager is responsible for synchronization tasks in the BACKGROUND
 * using WorkManager instead of direct coroutines.
 *
 * This allows sync tasks to:
 * - Run reliably even if the app is killed or in the background.
 * - Periodically scheduled or
 * - Execute immediately (expedited) if quota allows; ideal for short tasks that finished within a few minutes.
 *
 * In contrast, coroutine-based sync in ViewModel or lifecycle scope runs only while the app is alive,
 * execute immediately, and will be cancelled if the process is killed.
 */

interface SyncManager {
    val isSyncing: Flow<Boolean>
    fun syncAllRelatedDataExpedited()
    fun syncRelatedDataExpedited(locationId: String)
    fun syncAllRelatedDataPeriodic()
}

class SyncManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SyncManager {

    private val workManager = WorkManager.getInstance(context)

    override val isSyncing: Flow<Boolean> =
        combine(
            workManager.getWorkInfosForUniqueWorkFlow(EXPEDITED_SYNC_ALL_WORK),
            workManager.getWorkInfosForUniqueWorkFlow(EXPEDITED_SYNC_SINGLE_LOCATION_WORK),
            workManager.getWorkInfosForUniqueWorkFlow(PERIODIC_SYNC_ALL_WORK),

            ) { expedited, expeditedSingleLocation, periodic ->
            expedited.anyRunning() || periodic.anyRunning() || expeditedSingleLocation.anyRunning()
        }


    override fun syncAllRelatedDataExpedited() {
        // For one Time Work use: workManager.enqueueUniqueWork(
        // For Periodic work use: workManager.enqueueUniquePeriodicWork
        workManager.enqueueUniqueWork(
            EXPEDITED_SYNC_ALL_WORK, // Identifier name for the job
            // For one Time Work use: ExistingWorkPolicy
            // for Periodic work use: ExistingPeriodicWorkPolicy
            ExistingWorkPolicy.REPLACE, //
            SyncAllRelatedDataWorker.startUpExpeditedSyncWork()
        )
    }


    override fun syncRelatedDataExpedited(locationId: String) {
        workManager.enqueueUniqueWork(
            EXPEDITED_SYNC_SINGLE_LOCATION_WORK,
            ExistingWorkPolicy.REPLACE, //
            SyncRelatedDataWorker.startUpExpeditedSyncWork(locationId)
        )
    }

    override fun syncAllRelatedDataPeriodic() {
        workManager.enqueueUniquePeriodicWork(
            PERIODIC_SYNC_ALL_WORK, // Identifier name for the job
            ExistingPeriodicWorkPolicy.KEEP, //
            SyncAllRelatedDataWorker.startUpPeriodicSyncWork()
        )
    }


    companion object {
        private const val EXPEDITED_SYNC_ALL_WORK = "Expedited Sync All Related Data"
        private const val EXPEDITED_SYNC_SINGLE_LOCATION_WORK =
            "Expedited Sync Related Data for single location"
        private const val PERIODIC_SYNC_ALL_WORK = "Periodic Sync All Related Data"
    }
}

private fun List<WorkInfo>.anyRunning() = any { it.state == WorkInfo.State.RUNNING }