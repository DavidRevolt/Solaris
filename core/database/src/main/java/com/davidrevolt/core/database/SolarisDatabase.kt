package com.davidrevolt.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.davidrevolt.core.database.dao.LocationDao
import com.davidrevolt.core.database.dao.PointOfInterestDao
import com.davidrevolt.core.database.dao.WeatherDao
import com.davidrevolt.core.database.model.LocationEntity
import com.davidrevolt.core.database.model.PointOfInterestEntity
import com.davidrevolt.core.database.model.weather.DailyForecastEntity
import com.davidrevolt.core.database.model.weather.WeatherEntity
import com.davidrevolt.core.database.util.InstantConverter

@Database(
    entities = [LocationEntity::class,
        WeatherEntity::class,
        DailyForecastEntity::class,
        PointOfInterestEntity::class],
    version = 1
)
// Converters In Use For RoomDB complex objects
@TypeConverters(
    InstantConverter::class
)
abstract class SolarisDatabase : RoomDatabase() {
    //List Of Dao's
    abstract fun weatherDao(): WeatherDao
    abstract fun locationDao(): LocationDao
    abstract fun pointsOfInterestDao(): PointOfInterestDao
}