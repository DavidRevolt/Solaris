package com.davidrevolt.core.workmanager.workers

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlin.reflect.KClass

/**
 * An entry point to retrieve the [HiltWorkerFactory] at runtime
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface HiltWorkerFactoryEntryPoint {
    fun hiltWorkerFactory(): HiltWorkerFactory
}

private const val WORKER_CLASS_NAME = "RouterWorkerDelegateClassName"

/**
 * Utility method to pass the fully qualified class name of the worker to the DelegatingWorker via the WorkerParameters
 */
internal fun KClass<out CoroutineWorker>.classNameData() =
    Data.Builder()
        .putString(WORKER_CLASS_NAME, qualifiedName)
        .build()

/**
 * A worker that dynamically delegates work to another [CoroutineWorker] constructed with a [HiltWorkerFactory].
 *
 * This allows for creating [CoroutineWorker] instances with extended arguments
 * without having to provide a custom WorkManager configuration/Factory in the App Module.
 *
 * How it works:
 * Normally, we create WorkManager Factory and instantiates workers using Hardcoded class name [e.g ::SyncWorker].
 * DelegatingWorker auto create factory and inject the workers class names using work parameters input data.
 */
class DelegatingWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    private val workerClassName =
        workerParams.inputData.getString(WORKER_CLASS_NAME) ?: ""

    private val delegateWorker =
        EntryPointAccessors.fromApplication<HiltWorkerFactoryEntryPoint>(appContext)
            .hiltWorkerFactory()
            .createWorker(
                appContext,
                workerClassName,
                workerParams
            ) // Here's where the magic happens
                as? CoroutineWorker
            ?: throw IllegalArgumentException("Unable to find appropriate worker")

    override suspend fun getForegroundInfo(): ForegroundInfo =
        delegateWorker.getForegroundInfo()

    override suspend fun doWork(): Result =
        delegateWorker.doWork()
}