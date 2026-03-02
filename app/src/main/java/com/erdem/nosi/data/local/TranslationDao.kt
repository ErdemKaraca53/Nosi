package com.erdem.nosi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Koleksiyonlar, çeviriler ve kelimeler için DAO.
 */
@Dao
interface TranslationDao {

    // ── Collection ──

    @Insert
    suspend fun insertCollection(collection: CollectionEntity): Long

    @Query("SELECT * FROM collections ORDER BY createdAt DESC")
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections ORDER BY createdAt ASC")
    suspend fun getAllCollectionsOnce(): List<CollectionEntity>

    @Query("SELECT COUNT(*) FROM collections WHERE name = :name")
    suspend fun collectionExistsByName(name: String): Int

    /**
     * Koleksiyon özet bilgilerini getir.
     */
    @Query("""
        SELECT 
            c.id,
            c.name,
            c.createdAt,
            (SELECT COUNT(*) FROM saved_translations WHERE collectionId = c.id) AS sentenceCount,
            (SELECT COUNT(*) FROM saved_words sw 
             INNER JOIN saved_translations st ON sw.translationId = st.id 
             WHERE st.collectionId = c.id) AS wordCount
        FROM collections c
        ORDER BY c.createdAt DESC
    """)
    fun getCollectionSummaries(): Flow<List<CollectionSummary>>

    // ── Translation ──

    @Insert
    suspend fun insertTranslation(translation: SavedTranslationEntity): Long

    @Insert
    suspend fun insertWords(words: List<SavedWordEntity>)

    /**
     * Bir çeviriyi kelimeleriyle birlikte kaydet.
     */
    @Transaction
    suspend fun insertTranslationWithWords(
        translation: SavedTranslationEntity,
        words: List<SavedWordEntity>
    ): Long {
        val translationId = insertTranslation(translation)
        val wordsWithId = words.map { it.copy(translationId = translationId) }
        insertWords(wordsWithId)
        return translationId
    }

    /**
     * Bir koleksiyondaki çevirileri getir.
     */
    @Query("SELECT * FROM saved_translations WHERE collectionId = :collectionId ORDER BY timestamp DESC")
    fun getTranslationsForCollection(collectionId: Long): Flow<List<SavedTranslationEntity>>

    @Query("SELECT * FROM saved_translations WHERE collectionId = :collectionId ORDER BY timestamp DESC")
    suspend fun getTranslationsForCollectionOnce(collectionId: Long): List<SavedTranslationEntity>

    /**
     * Bir çevirinin kelimelerini getir.
     */
    @Query("SELECT * FROM saved_words WHERE translationId = :translationId")
    suspend fun getWordsForTranslation(translationId: Long): List<SavedWordEntity>

    /**
     * Tüm kayıtlı çevirileri tarih sırasına göre getir.
     */
    @Query("SELECT * FROM saved_translations ORDER BY timestamp DESC")
    fun getAllTranslations(): Flow<List<SavedTranslationEntity>>

    /**
     * Kayıtlı çeviri sayısını getir.
     */
    @Query("SELECT COUNT(*) FROM saved_translations")
    fun getTranslationCount(): Flow<Int>

    /**
     * Bir çeviriyi sil (CASCADE ile kelimeleri de siler).
     */
    @Query("DELETE FROM saved_translations WHERE id = :id")
    suspend fun deleteTranslation(id: Long)

    /**
     * Koleksiyon adını getir.
     */
    @Query("SELECT name FROM collections WHERE id = :collectionId")
    suspend fun getCollectionName(collectionId: Long): String?
}
