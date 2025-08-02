package com.davidrevolt.core.model

data class Location(
    val id: String,
    val name: String,
    val administrativeArea: String,
    val country: String,
    val latitude: String,
    val longitude: String,
    val timezone: String,
    val type: String
)
