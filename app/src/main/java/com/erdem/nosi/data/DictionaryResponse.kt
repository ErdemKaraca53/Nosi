package com.erdem.nosi.data

import kotlinx.serialization.Serializable


@Serializable
data class DictionaryApiResponse(
    val word: String = "",
    /** Genel fonetik yazım (ör. "/rʌn/") — bazen boş gelir */
    val phonetic: String? = null,
    /** Telaffuz varyantları: her biri metin ve/veya ses dosyası içerir */
    val phonetics: List<Phonetic> = emptyList(),
    val meanings: List<Meaning>,
)

@Serializable
data class Phonetic(
    val text: String? = null,
    /** mp3 ses dosyası URL'si (çoğu girişte boş) */
    val audio: String = ""
)

@Serializable
data class Meaning(
    val partOfSpeech: String,
    val definitions: List<Definitions>,
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList()
)

@Serializable
data class Definitions(
    val definition: String,
    val synonyms: List<String>,
    val antonyms: List<String>,
    val example : String? = null
)