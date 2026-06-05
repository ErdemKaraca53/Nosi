package com.erdem.nosi.repository

import com.erdem.nosi.database.SavedWordDao
import com.erdem.nosi.database.SavedWordEntity
import com.erdem.nosi.database.WordListDao
import com.erdem.nosi.database.WordListEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow

/**
 * Kelime listesi ve kayıtlı kelime işlemlerini yöneten repository katmanı.
 * ViewModel'lar doğrudan DAO'larla değil bu sınıfla konuşur.
 */
class WordRepository(
    private val wordListDao: WordListDao,
    private val savedWordDao: SavedWordDao
) {
    private val gson = Gson()

    // ── Liste İşlemleri ──────────────────────────────────────────────────────

    /** Tüm listeleri Flow olarak döndürür */
    fun getAllLists(): Flow<List<WordListEntity>> = wordListDao.getAllLists()

    /** Yeni bir liste oluşturur, oluşturulan listenin id'sini döndürür */
    suspend fun createList(name: String, emoji: String, color: String): Long {
        val list = WordListEntity(name = name, emoji = emoji, color = color)
        return wordListDao.insertList(list)
    }

    /** Listeyi günceller */
    suspend fun updateList(list: WordListEntity) = wordListDao.updateList(list)

    /** Listeyi (ve içindeki kelimeleri) siler */
    suspend fun deleteList(list: WordListEntity) = wordListDao.deleteList(list)

    /** Bir listedeki kelime sayısını Flow olarak döndürür */
    fun getWordCountForList(listId: Long): Flow<Int> =
        wordListDao.getWordCountForList(listId)

    // ── Kelime İşlemleri ─────────────────────────────────────────────────────

    /** Belirli bir listedeki tüm kelimeleri Flow olarak döndürür */
    fun getWordsForList(listId: Long): Flow<List<SavedWordEntity>> =
        savedWordDao.getWordsForList(listId)

    /**
     * Kelimeyi belirtilen listeye kaydeder.
     * @return true → kaydedildi, false → zaten vardı (duplicate)
     */
    suspend fun saveWord(
        listId: Long,
        word: String,
        partOfSpeech: String,
        definitions: List<String>,
        synonyms: List<String>,
        antonyms: List<String>
    ): Boolean {
        val entity = SavedWordEntity(
            listId = listId,
            word = word,
            partOfSpeech = partOfSpeech,
            definitionsJson = gson.toJson(definitions),
            synonymsJson = gson.toJson(synonyms),
            antonymsJson = gson.toJson(antonyms)
        )
        val insertedId = savedWordDao.insertWord(entity)
        return insertedId != -1L  // -1 → IGNORE stratejisi ile çakışma oldu
    }

    /** Kelimeyi id ile siler */
    suspend fun deleteWordById(wordId: Long) = savedWordDao.deleteWordById(wordId)

    /** Bu kelime + POS kombinasyonu belirtilen listede var mı? */
    suspend fun isWordSaved(listId: Long, word: String, partOfSpeech: String): Boolean =
        savedWordDao.isWordSaved(listId, word, partOfSpeech) > 0

    /** Bu kelimenin kayıtlı olduğu tüm liste id'lerini Flow olarak döndürür */
    fun getListIdsForWord(word: String, partOfSpeech: String): Flow<List<Long>> =
        savedWordDao.getListIdsForWord(word, partOfSpeech)

    // ── Yardımcı: JSON'dan geri çözme ────────────────────────────────────────

    fun parseJsonList(json: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}