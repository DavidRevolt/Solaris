package com.davidrevolt.core.network.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class NetworkLocation(
    val name: String,
    val place_id: String,
    val adm_area1: String,
    val adm_area2: String,
    val country: String,
    val lat: String,
    val lon: String,
    val timezone: String,
    val type: String
)

/**
 * Retrofit can assigning null to a non-nullable field!!
 * Under the hood Gson does not enforce non-null safety when deserializing into Kotlin data classes
 * It will assign null even to non-nullable fields, and you won’t get a crash in THIS module!
 * We can fix this by declare nullability explicitly using a ?
 * If not sure which field can be null, we can use a custom deserializer
 * */
internal class NetworkLocationDeserializer : JsonDeserializer<NetworkLocation> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): NetworkLocation {
        val jsonObject = json.asJsonObject

        // Helper function to get string or "Unknown" if null or not present
        fun getStringOrUnknown(fieldName: String): String {
            return if (jsonObject.has(fieldName) && !jsonObject.get(fieldName).isJsonNull) {
                jsonObject.get(fieldName).asString
            } else {
                "Unknown"
            }
        }

        return NetworkLocation(
            name = getStringOrUnknown("name"),
            place_id = getStringOrUnknown("place_id"),
            adm_area1 = getStringOrUnknown("adm_area1"),
            adm_area2 = getStringOrUnknown("adm_area2"),
            country = getStringOrUnknown("country"),
            lat = getStringOrUnknown("lat"),
            lon = getStringOrUnknown("lon"),
            timezone = getStringOrUnknown("timezone"),
            type = getStringOrUnknown("type")
        )
    }
}

