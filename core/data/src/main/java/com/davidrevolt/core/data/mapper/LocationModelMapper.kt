package com.davidrevolt.core.data.mapper

import com.davidrevolt.core.database.model.LocationEntity
import com.davidrevolt.core.model.Location
import com.davidrevolt.core.network.model.NetworkLocation


internal fun NetworkLocation.asExternalModel() =
    Location(
        id = place_id,
        name = name,
        administrativeArea = adm_area1,
        country = country,
        latitude = lat,
        longitude = lon,
        timezone = timezone,
        type = type
    )

internal fun Location.asEntity() =
    LocationEntity(
        id = id,
        name = name,
        administrativeArea = administrativeArea,
        country = country,
        latitude = latitude,
        longitude = longitude,
        timezone = timezone,
        type = type
    )

internal fun LocationEntity.asExternalModel() =
    Location(
        id = id,
        name = name,
        administrativeArea = administrativeArea,
        country = country,
        latitude = latitude,
        longitude = longitude,
        timezone = timezone,
        type = type
    )