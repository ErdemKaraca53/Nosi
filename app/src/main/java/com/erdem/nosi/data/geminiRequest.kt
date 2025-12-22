package com.erdem.nosi.data

//Gemini APİ'ye prompt gönderirken kullanıyoruz
data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)
