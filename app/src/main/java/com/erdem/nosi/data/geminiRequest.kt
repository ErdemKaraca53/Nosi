package com.erdem.nosi.data

/**
 * Gemini API'ye gönderilen request yapısı
 * Firebase AI SDK veya REST kullanımı için uygundur
 */

data class TranslationRequest(
    val sourceSentence: String,
    val sourceLanguage: Language = Language.TR,
    val targetLanguage: Language = Language.EN,
    val translationCount: Int = 3
)

enum class Language {
    TR,
    EN,
    DE,
    FR,
    ES
}

data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)
