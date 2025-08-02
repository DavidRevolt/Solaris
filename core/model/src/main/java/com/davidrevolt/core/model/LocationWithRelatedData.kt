package com.davidrevolt.core.model

/**
 * A [Location] with the additional related data such as [Weather] and [PointOfInterest].
 */
data class LocationWithRelatedData(
    val location: Location,
    val weather: Weather?,
    val pointsOfInterest: List<PointOfInterest>
)