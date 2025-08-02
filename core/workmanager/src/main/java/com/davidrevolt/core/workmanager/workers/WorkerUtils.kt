package com.davidrevolt.core.workmanager.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Constraints
import androidx.work.NetworkType
import com.davidrevolt.core.workmanager.R

/**
 * Constants and functions shared by Sync Workers
 * */

internal const val LOCATION_ID = "location_id" // Used for passing SyncRelatedDataWorker inputData
internal const val SYNC_NOTIFICATION_ID = 0 // Used for Worker ForegroundInfo

// Notification creation
private const val SYNC_NOTIFICATION_CHANNEL_ID = "SyncNotificationChannel"
private const val SYNC_NOTIFICATION_CHANNEL_NAME = "Sync"
private const val SYNC_NOTIFICATION_CHANNEL_DESCRIPTION = "Background tasks for Solaris"
private const val SYNC_NOTIFICATION_TITLE = "Solaris"

internal fun createSyncWorkerNotification(appContext: Context): Notification {
    // Versions Build.VERSION.SDK_INT >= Build.VERSION_CODES.O needs a notification channel
    val channel = NotificationChannel(
        SYNC_NOTIFICATION_CHANNEL_ID,
        SYNC_NOTIFICATION_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = SYNC_NOTIFICATION_CHANNEL_DESCRIPTION
    }
    // Register the channel with the system
    val notificationManager = getSystemService(appContext, NotificationManager::class.java)
    notificationManager?.createNotificationChannel(channel)


    val notification = NotificationCompat.Builder(appContext, SYNC_NOTIFICATION_CHANNEL_ID)
        .setContentTitle(SYNC_NOTIFICATION_TITLE)
        .setTicker(SYNC_NOTIFICATION_TITLE)
        //    .setContentText("Content")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setSmallIcon(R.drawable.notification_sync)
        .build()
    return notification
}

internal val SyncWorkerConstraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()