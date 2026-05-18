package com.erdem.nosi.data

import kotlinx.serialization.Serializable


@Serializable
data class ApiResponse(
    val meanings: List<Meaning>,
)

@Serializable
data class Meaning(
    val partOfSpeech: String,
    val definitions: List<Definitions>
)

@Serializable
data class Definitions(
    val definition: String,
    val synonyms: List<String>,
    val antonyms: List<String>,
    val example : String? = null
)