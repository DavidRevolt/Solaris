package com.davidrevolt.core.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "locations",
    // combination of latitude and longitude must be unique.
    indices = [Index(value = ["latitude", "longitude"], unique = true)]
)
data class LocationEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val administrativeArea: String,
    val country: String,
    val latitude: String,
    val longitude: String,
    val timezone: String,
    val type: String
)
