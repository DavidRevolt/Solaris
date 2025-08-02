package com.davidrevolt.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.davidrevolt.core.database.model.LocationEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface LocationDao {

    @Query("SELECT * FROM locations")
    fun getAllLocations(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE id=:id")
    fun getLocation(id: String): Flow<LocationEntity>

    @Upsert
    suspend fun insertLocation(location: LocationEntity)

    @Query("DELETE FROM locations")
    suspend fun deleteAllLocations()

    @Query("DELETE FROM locations WHERE id=:id")
    suspend fun deleteLocation(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM locations WHERE id = :id)")
    suspend fun containsLocation(id: String): Boolean
}