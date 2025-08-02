package com.davidrevolt.core.workmanager.di

import com.davidrevolt.core.workmanager.SyncManager
import com.davidrevolt.core.workmanager.SyncManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    // Repos
    @Binds
    @Singleton
    abstract fun bindsSyncManager(syncManagerImpl: SyncManagerImpl): SyncManager

}