package com.davidrevolt.core.model

sealed interface SyncStatus {
    object Success : SyncStatus
    object InProgress : SyncStatus
    data class Failure(val throwable: Throwable) : SyncStatus
}