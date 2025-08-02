package com.davidrevolt.core.data.di

import com.davidrevolt.core.data.repository.LocationRepository
import com.davidrevolt.core.data.repository.LocationRepositoryImpl
import com.davidrevolt.core.data.repository.POIRepository
import com.davidrevolt.core.data.repository.POIRepositoryImpl
import com.davidrevolt.core.data.repository.WeatherRepository
import com.davidrevolt.core.data.repository.WeatherRepositoryImpl
import com.davidrevolt.core.data.util.FusedLocation
import com.davidrevolt.core.data.util.FusedLocationImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    // Repos
    @Binds
    @Singleton
    abstract fun bindsLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindsWeatherRepository(weatherRepositoryImpl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindsPOIRepository(poiRepositoryImpl: POIRepositoryImpl): POIRepository

    @Binds
    @Singleton
    abstract fun bindsFusedLocation(fusedLocationImpl: FusedLocationImpl): FusedLocation

    companion object {
        // Dispatchers
        @Provides
        @Named("IO")
        fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO
    }

}