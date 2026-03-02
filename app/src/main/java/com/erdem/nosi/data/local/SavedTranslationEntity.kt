package com.erdem.nosi.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Koleksiyonlar tablosu.
 */
@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Kayıtlı çeviriler tablosu.
 * collectionId ile CollectionEntity'ye bağlı (FK).
 */
@Entity(
    tableName = "saved_translations",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("collectionId")]
)
data class SavedTranslationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val collectionId: Long,
    val sourceSentence: String,
    val translatedSentence: String,
    val sourceLanguage: String = "tr",
    val targetLanguage: String = "en",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Çevirideki kelimelerin detayları tablosu.
 * translationId ile SavedTranslationEntity'ye bağlı (FK).
 */
@Entity(
    tableName = "saved_words",
    foreignKeys = [
        ForeignKey(
            entity = SavedTranslationEntity::class,
            parentColumns = ["id"],
            childColumns = ["translationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("translationId")]
)
data class SavedWordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val translationId: Long,
    val word: String,
    val lemma: String,
    val pos: String,
    val meaningTr: String
)

/**
 * Koleksiyon özet bilgisi (query sonucu).
 */
data class CollectionSummary(
    val id: Long,
    val name: String,
    val createdAt: Long,
    val sentenceCount: Int,
    val wordCount: Int
)
