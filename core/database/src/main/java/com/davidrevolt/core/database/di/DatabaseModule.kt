package com.davidrevolt.core.database.di

import android.content.Context
import androidx.room.Room
import com.davidrevolt.core.database.SolarisDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    // Room use its own Background Dispatcher so no need for Dispatcher.IO
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): SolarisDatabase =
        Room.databaseBuilder(
            context,
            SolarisDatabase::class.java,
            "solaris_database",
        ).fallbackToDestructiveMigration(false).build()
}