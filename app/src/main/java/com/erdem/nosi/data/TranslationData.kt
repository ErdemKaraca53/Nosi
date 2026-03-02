package com.erdem.nosi.data

/**
 * Gemini API'den dönen çeviri JSON'ının parse edilmiş hali.
 */
data class TranslationData(
    val sourceLanguage: String = "",
    val targetLanguage: String = "",
    val translations: List<TranslationItem> = emptyList()
)

data class TranslationItem(
    val translatedSentence: String = "",
    val words: List<WordItem> = emptyList()
)

data class WordItem(
    val word: String = "",
    val lemma: String = "",
    val pos: String = "",
    val meaningTr: String = ""
)
