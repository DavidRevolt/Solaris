package com.davidrevolt.core.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "points_of_interest",
    indices = [Index(value = ["latitude", "longitude"])]
)
data class PointOfInterestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val latitude: String,
    val longitude: String,
    val timestamp: Instant = Instant.now()
)