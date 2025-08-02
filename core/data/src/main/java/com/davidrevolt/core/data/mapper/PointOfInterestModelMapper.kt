package com.davidrevolt.core.data.mapper

import com.davidrevolt.core.database.model.PointOfInterestEntity
import com.davidrevolt.core.model.PointOfInterest

internal fun PointOfInterestEntity.asExternalModel() =
    PointOfInterest(
        id = id,
        name = name,
        description = description,
        latitude = latitude,
        longitude = longitude
    )