import kotlinx.serialization.Serializable

// 1. Gemini'nin Standart Yanıt Yapısı
@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>
)

@Serializable
data class Candidate(
    val content: Content
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String // Senin asıl JSON verin bu String'in içinde
)

// 2. Senin Tanımladığın Özel Sözlük Yapısı (Nested JSON)
@Serializable
data class MyTranslationResponse(
    val translations: List<TranslationItem>
)

@Serializable
data class TranslationItem(
    val translated_sentence: String,
    val native_equivalent: String,
    val words: List<WordDetail>
)

@Serializable
data class WordDetail(
    val word: String,
    val meaning: String,
    val part_of_speech: String,
    val pronunciation: String
)

