package com.davidrevolt.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.davidrevolt.core.datastore.PreferencesDataSource
import com.davidrevolt.core.datastore.PreferencesDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    // Repos
    @Binds
    @Singleton
    abstract fun bindsPreferencesDataSource(preferencesDataSourceImpl: PreferencesDataSourceImpl): PreferencesDataSource


    companion object {
        @Provides
        @Singleton
        fun providesPreferencesDataStore(
            @ApplicationContext context: Context,
            @Named("IO")
            ioDispatcher: CoroutineDispatcher,
        ): DataStore<Preferences> =
            PreferenceDataStoreFactory.create(
                corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
                scope = CoroutineScope(ioDispatcher + SupervisorJob()),
                produceFile = { context.preferencesDataStoreFile("current_location_prefs") }
            )
    }

}
