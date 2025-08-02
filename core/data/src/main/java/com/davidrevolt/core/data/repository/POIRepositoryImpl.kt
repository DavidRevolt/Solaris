package com.davidrevolt.core.data.repository

import com.davidrevolt.core.data.mapper.asExternalModel
import com.davidrevolt.core.database.dao.PointOfInterestDao
import com.davidrevolt.core.database.model.PointOfInterestEntity
import com.davidrevolt.core.network.FirebaseAiDataSource
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class POIRepositoryImpl @Inject constructor(
    val poiDao: PointOfInterestDao,
    val firebaseAi: FirebaseAiDataSource
) :
    POIRepository {

    override fun getAllPOI() = poiDao.getAllPOI().map { it.map { it.asExternalModel() } }

    override fun getNearbyPOI(latitude: String, longitude: String) =
        poiDao.getPOI(latitude, longitude).map { it.map { it.asExternalModel() } }

    override suspend fun deleteNearbyPOI(latitude: String, longitude: String) =
        poiDao.deletePOI(latitude, longitude)

    override suspend fun deleteAllPOI() = poiDao.deleteAllPOI()

    override suspend fun sync(
        latitude: String,
        longitude: String,
        extraLocationDescription: String,
        limit: Int
    ): Result<Unit> {
        // prompt with lat and lon doesn't generate exact location response on FirebaseAi
        val prompt =
            "Generate $limit specific points of interest in $extraLocationDescription." +
                    "Each should be short, relevant, and interesting to visitors."

        return firebaseAi.executePOITextGeneration(prompt).fold(
            onSuccess = { generatedPOI ->
                val poiEntities = generatedPOI.map {
                    PointOfInterestEntity(
                        name = it.name,
                        description = it.description,
                        latitude = latitude,
                        longitude = longitude,
                    )
                }
                // delete the old POI and insert the new ones
                poiDao.replaceLocationPOI(latitude, longitude, poiEntities)
                Result.success(Unit)
            },
            onFailure = { e -> Result.failure(e) }
        )
    }

}