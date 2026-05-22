package com.erdem.nosi.model

import kotlinx.serialization.Serializable

@Serializable
data class CollectionSummary(
    val id: Long,
    val name: String,
    val createdAt: Long,
    val sentenceCount: Int,
    val wordCount: Int
)

@Serializable
data class TranslationData(
    val translations: List<TranslatedSentenceData> = emptyList()
)

@Serializable
data class TranslatedSentenceData(
    val translatedSentence: String,
    val words: List<WordData> = emptyList()
)

@Serializable
data class WordData(
    val word: String,
    val lemma: String,
    val pos: String,
    val meaningTr: String
)

@Serializable
data class SavedTranslationEntity(
    val id: Long = 0,
    val collectionId: Long,
    val sourceSentence: String,
    val translatedSentence: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class SavedWordEntity(
    val id: Long = 0,
    val translationId: Long,
    val word: String,
    val lemma: String,
    val pos: String,
    val meaningTr: String
)

@Serializable
data class DictionaryResult(
    val word: String,
    val type: String,
    val definitions: List<String>,
    val synonyms: List<String>,
    val exampleSentences: List<String>
)

// Mock Data Source for UI viewing without a database
object MockData {
    val sampleCollections = listOf(
        CollectionSummary(1L, "Favorites", System.currentTimeMillis() - 100000, 5, 20),
        CollectionSummary(2L, "Travel Phrases", System.currentTimeMillis() - 200000, 10, 45),
        CollectionSummary(3L, "Work", System.currentTimeMillis() - 300000, 3, 12)
    )

    val sampleTranslations = listOf(
        SavedTranslationEntity(
            id = 101L,
            collectionId = 1L,
            sourceSentence = "Aklıma harika bir fikir geldi.",
            translatedSentence = "A great idea came to my mind."
        ),
        SavedTranslationEntity(
            id = 102L,
            collectionId = 1L,
            sourceSentence = "Bu projeyi yarına kadar bitirmeliyiz.",
            translatedSentence = "We have to finish this project by tomorrow."
        )
    )

    val sampleWordsFor101 = listOf(
        SavedWordEntity(1L, 101L, "idea", "idea", "NOUN", "fikir, düşünce"),
        SavedWordEntity(2L, 101L, "came", "come", "VERB", "gelmek"),
        SavedWordEntity(3L, 101L, "mind", "mind", "NOUN", "akıl, zihin")
    )

    val sampleWordDataForTranslation = listOf(
        WordData("idea", "idea", "NOUN", "fikir, düşünce"),
        WordData("came", "come", "VERB", "gelmek"),
        WordData("mind", "mind", "NOUN", "akıl, zihin")
    )

    val sampleTranslationData = TranslationData(
        translations = listOf(
            TranslatedSentenceData(
                translatedSentence = "A great idea came to my mind.",
                words = sampleWordDataForTranslation
            ),
            TranslatedSentenceData(
                translatedSentence = "I just got a brilliant idea.",
                words = sampleWordDataForTranslation
            )
        )
    )

    val sampleDictionaryResult = DictionaryResult(
        word = "Procrastinate",
        type = "Verb",
        definitions = listOf(
            "To delay or postpone action; put off doing something.",
            "To delay putting into practice."
        ),
        synonyms = listOf("Delay", "Postpone", "Stall", "Dilly-dally"),
        exampleSentences = listOf(
            "I should start working on my project, but I keep procrastinating.",
            "If you procrastinate too much, you will miss the deadline."
        )
    )
}
