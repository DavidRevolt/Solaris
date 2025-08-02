package com.davidrevolt.core.database.util

import androidx.room.TypeConverter
import java.time.Instant

internal class InstantConverter {
    @TypeConverter
    fun longToInstant(value: Long): Instant =
        Instant.ofEpochMilli(value)

    @TypeConverter
    fun instantToLong(instant: Instant): Long =
        instant.toEpochMilli()
}