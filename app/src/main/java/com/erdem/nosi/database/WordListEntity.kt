package com.erdem.nosi.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Kullanıcının oluşturduğu kelime listelerini temsil eder.
 * Her liste birden fazla SavedWordEntity içerebilir.
 *
 * İleride eklenebilecekler:
 * - isPublic: Boolean  (community sharing)
 * - targetLanguage: String
 * - ownerId: String   (kullanıcı hesabı entegrasyonu)
 */
@Entity(tableName = "word_lists")
data class WordListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    /** Liste için emoji ikon (ör. "📚", "✈️", "💼") */
    @ColumnInfo(name = "emoji")
    val emoji: String = "📚",

    /** Kullanıcının isteğe bağlı ekleyeceği açıklama */
    @ColumnInfo(name = "description")
    val description: String = "",

    /** Listenin teması için hex renk kodu (ör. "#0D9488") */
    @ColumnInfo(name = "color")
    val color: String = "#0D9488",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
