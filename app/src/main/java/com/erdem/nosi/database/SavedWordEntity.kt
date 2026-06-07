package com.erdem.nosi.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Kullanıcının bir listeye kaydettiği sözlük kelimesini temsil eder.
 *
 * definitions / synonyms / antonyms alanları JSON string olarak saklanır.
 * Bu sayede Room şeması değiştirilmeden içerik zenginleştirilebilir.
 *
 * İleride eklenebilecekler:
 * - masteryLevel: Int     (SRS öğrenme seviyesi 0-5)
 * - nextReviewAt: Long    (bir sonraki tekrar zamanı)
 * - reviewCount: Int      (kaç kez tekrar edildi)
 * - isFavorite: Boolean
 * - userNote: String      (kullanıcının kendi notu)
 */
@Entity(
    tableName = "saved_words",
    foreignKeys = [
        ForeignKey(
            entity = WordListEntity::class,
            parentColumns = ["id"],
            childColumns = ["list_id"],
            onDelete = ForeignKey.CASCADE  // Liste silinince kelimeler de silinir
        )
    ],
    indices = [
        Index(value = ["list_id"]),
        Index(value = ["list_id", "word", "part_of_speech"], unique = true)  // Aynı kelime aynı listeye bir kez
    ]
)
data class SavedWordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "list_id")
    val listId: Long,

    /** Aranan ham kelime (ör. "running") */
    @ColumnInfo(name = "word")
    val word: String,

    /** Kaydedilen POS türü (ör. "noun", "verb") */
    @ColumnInfo(name = "part_of_speech")
    val partOfSpeech: String,

    /** Kelimenin Türkçe karşılığı (ML Kit ile çevrilir; çevrilemezse boş) */
    @ColumnInfo(name = "meaning_tr")
    val meaningTr: String = "",

    /** Tanımlar listesi — JSON string (List<String>) */
    @ColumnInfo(name = "definitions_json")
    val definitionsJson: String = "[]",

    /** Eşanlamlılar — JSON string (List<String>) */
    @ColumnInfo(name = "synonyms_json")
    val synonymsJson: String = "[]",

    /** Zıt anlamlılar — JSON string (List<String>) */
    @ColumnInfo(name = "antonyms_json")
    val antonymsJson: String = "[]",

    /**
     * Öğrenme seviyesi (basit SRS).
     * 0 = yeni/tekrar gerek, artar → öğrenildi. 2+ "biliniyor" sayılır.
     * Çalışma ekranında sağa kaydırınca artar, sola kaydırınca 0'a döner.
     */
    @ColumnInfo(name = "mastery_level")
    val masteryLevel: Int = 0,

    @ColumnInfo(name = "saved_at")
    val savedAt: Long = System.currentTimeMillis()
)
