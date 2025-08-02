package com.davidrevolt.core.network

import com.davidrevolt.core.network.model.NetworkAiGeneratedPOI
import com.davidrevolt.core.network.model.NetworkPointOfInterest
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.HarmBlockThreshold
import com.google.firebase.ai.type.HarmCategory
import com.google.firebase.ai.type.SafetySetting
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import timber.log.Timber
import javax.inject.Inject


interface FirebaseAiDataSource {
    suspend fun executePOITextGeneration(prompt: String): Result<List<NetworkPointOfInterest>>
}

class FirebaseAiDataSourceImpl @Inject constructor() : FirebaseAiDataSource {

    /**
     * Creates a generative text model with the specified JSON schema and temperature.
     *
     * @param jsonSchema The JSON schema the AI model should use for its generated response.
     * @param temperature The temperature to use for the model's generation. This controls how
     * deterministic or random the model's outputs are. A lower temperature will result in more
     * deterministic outputs, while a higher temperature will result in more random outputs.
     * @return A [GenerativeModel] instance.
     */
    private fun createGenerativeTextModel(
        jsonSchema: Schema,
        temperature: Float = 0.75f
    ): GenerativeModel =
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-2.5-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = jsonSchema
                this.temperature = temperature
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, HarmBlockThreshold.LOW_AND_ABOVE),
                SafetySetting(HarmCategory.HATE_SPEECH, HarmBlockThreshold.LOW_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, HarmBlockThreshold.LOW_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, HarmBlockThreshold.LOW_AND_ABOVE),
                SafetySetting(HarmCategory.CIVIC_INTEGRITY, HarmBlockThreshold.LOW_AND_ABOVE),
            ),
        )


    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun executePOITextGeneration(prompt: String): Result<List<NetworkPointOfInterest>> {
        Timber.i("FirebaseAi starts generating text")

        // Schema the AI model should respond with. [matched to NetworkPOIResponse.kt]
        val jsonSchema = Schema.obj(
            properties = mapOf(
                "success" to Schema.string(), // If location is recognizable. (but AI can still hallucinate)
                "error_message" to Schema.string(),
                "points_of_interest" to Schema.array(
                    Schema.obj(
                        properties = mapOf(
                            "name" to Schema.string(),
                            "description" to Schema.string()
                        )
                    )
                ),
            ),
            optionalProperties = listOf("error_message"),
        )

        val generativeModel = createGenerativeTextModel(jsonSchema)
        runCatching {
            generativeModel.generateContent(prompt)
        }.fold(
            onSuccess = { aiModelResponse ->
                Timber.i("FirebaseAi responded with: ${aiModelResponse.text}")

                // Convert the ai model response from json schema to Kotlin object [NetworkGeneratedPOI.kt]
                val format = Json {
                    ignoreUnknownKeys =
                        true // omit json fields that are not in NetworkPointOfInterest
                    // Match schema/json snake case fields to NetworkPOIResponse.kt camel case fields
                    namingStrategy = JsonNamingStrategy.SnakeCase
                }
                val generatedPOI =
                    format.decodeFromString<NetworkAiGeneratedPOI>(aiModelResponse.text!!)
                return if (generatedPOI.success)
                    Result.success(generatedPOI.pointsOfInterest)
                else
                    Result.failure(TextGenerationException(generatedPOI.errorMessage))
            },
            onFailure = { throwable -> // Communication error, etc with FirebaseAi
                Timber.e(throwable, "FirebaseAi error")
                return Result.failure(throwable)
            }
        )
    }
}


class TextGenerationException(errorMessage: String? = null) : Exception(errorMessage)