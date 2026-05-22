package com.erdem.nosi.data

import kotlinx.serialization.Serializable

@Serializable
data class AutoCompleteResponse(
    val word: String,
    val score: Int
)