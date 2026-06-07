package com.erdem.nosi.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Çeviri ekranından bir listeye kaydedilen cümleyi temsil eder.
 * Kaynak (Türkçe) ve çeviri (İngilizce) birlikte saklanır.
 *
 * Liste silinince (CASCADE) cümleler de silinir.
 */
@Entity(
    tableName = "saved_sentences",
    foreignKeys = [
        ForeignKey(
            entity = WordListEntity::class,
            parentColumns = ["id"],
            childColumns = ["list_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["list_id"])]
)
data class SavedSentenceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "list_id")
    val listId: Long,

    /** Kaynak cümle (Türkçe) */
    @ColumnInfo(name = "source_text")
    val sourceText: String,

    /** Çeviri (İngilizce) */
    @ColumnInfo(name = "translated_text")
    val translatedText: String,

    @ColumnInfo(name = "saved_at")
    val savedAt: Long = System.currentTimeMillis()
)
