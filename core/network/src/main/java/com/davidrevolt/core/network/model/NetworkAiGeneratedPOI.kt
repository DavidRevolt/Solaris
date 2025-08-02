package com.davidrevolt.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkAiGeneratedPOI(
    val success: Boolean,
    val errorMessage: String? = null,
    val pointsOfInterest: List<NetworkPointOfInterest>
)

@Serializable
data class NetworkPointOfInterest(
    val name: String,
    val description: String
)
