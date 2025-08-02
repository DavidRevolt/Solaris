package com.davidrevolt.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.davidrevolt.core.database.model.PointOfInterestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PointOfInterestDao {

    @Query("SELECT * FROM points_of_interest")
    fun getAllPOI(): Flow<List<PointOfInterestEntity>>

    @Query("SELECT * FROM points_of_interest WHERE latitude = :latitude AND longitude = :longitude")
    fun getPOI(latitude: String, longitude: String): Flow<List<PointOfInterestEntity>>

    @Insert
    suspend fun insertPOI(poi: List<PointOfInterestEntity>)

    @Query("DELETE FROM points_of_interest")
    suspend fun deleteAllPOI()

    @Query("DELETE FROM points_of_interest WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun deletePOI(latitude: String, longitude: String)


    @Transaction
    suspend fun replaceLocationPOI(
        latitude: String,
        longitude: String,
        newPOI: List<PointOfInterestEntity>
    ) {
        deletePOI(latitude, longitude)
        insertPOI(newPOI)
    }

}