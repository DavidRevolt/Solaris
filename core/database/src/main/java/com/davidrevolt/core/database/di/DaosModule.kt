package com.davidrevolt.core.database.di

import com.davidrevolt.core.database.SolarisDatabase
import com.davidrevolt.core.database.dao.LocationDao
import com.davidrevolt.core.database.dao.PointOfInterestDao
import com.davidrevolt.core.database.dao.WeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {
    @Provides
    fun provideWeatherDao(database: SolarisDatabase): WeatherDao =
        database.weatherDao()

    @Provides
    fun provideLocationDao(database: SolarisDatabase): LocationDao =
        database.locationDao()

    @Provides
    fun providePointOfInterestDao(database: SolarisDatabase): PointOfInterestDao =
        database.pointsOfInterestDao()
}