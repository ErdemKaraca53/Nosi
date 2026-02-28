package com.erdem.nosi.data

data class GeminiResponse(
    val candidates: List<Candidate> = emptyList()
)

data class Candidate(
    val content: ContentResponse  = ContentResponse()
)

data class ContentResponse(
    val parts: List<PartResponse> = emptyList()
)

data class PartResponse(
    val text: String = ""
)
