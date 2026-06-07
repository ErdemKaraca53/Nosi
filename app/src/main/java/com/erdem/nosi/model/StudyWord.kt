package com.erdem.nosi.model

/**
 * Bir listeye kaydedilmiş kelimenin, ekranda gösterilmeye hazır (JSON'dan çözülmüş) hâli.
 *
 * Room'daki SavedWordEntity tanım/eşanlamlı/zıt anlamlı alanlarını JSON string olarak tutar.
 * Repository bu entity'yi parse edip bu modele dönüştürür; böylece ekranlar JSON ile uğraşmaz.
 */
data class StudyWord(
    val id: Long,
    val word: String,
    val partOfSpeech: String,
    val meaningTr: String,
    val definitions: List<String>,
    val synonyms: List<String>,
    val antonyms: List<String>,
    val masteryLevel: Int = 0
) {
    /** Flashcard'ın arka yüzünde gösterilecek birincil anlam (ilk tanım). */
    val primaryDefinition: String
        get() = definitions.firstOrNull() ?: ""

    /** Bu seviye ve üstü "öğrenildi" sayılır. */
    val isKnown: Boolean
        get() = masteryLevel >= MASTERY_KNOWN

    companion object {
        /** "Biliniyor" eşiği ve üst sınır (basit SRS) */
        const val MASTERY_KNOWN = 2
        const val MASTERY_MAX = 3
    }
}
