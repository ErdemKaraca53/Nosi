package com.erdem.nosi.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedWordDao {

    /** Belirli bir listedeki tüm kelimeleri (yeniden eskiye) döndürür */
    @Query("SELECT * FROM saved_words WHERE list_id = :listId ORDER BY saved_at DESC")
    fun getWordsForList(listId: Long): Flow<List<SavedWordEntity>>

    /** Kelime aynı listede zaten kayıtlı mı? */
    @Query("""
        SELECT COUNT(*) FROM saved_words 
        WHERE list_id = :listId AND word = :word AND part_of_speech = :partOfSpeech
    """)
    suspend fun isWordSaved(listId: Long, word: String, partOfSpeech: String): Int

    /**
     * Kelimeyi kaydeder.
     * IGNORE stratejisi: unique index çakışırsa sessizce atlar (duplicate önleme).
     * Başarıyla eklenirse row id döner, çakışma olursa -1 döner.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWord(word: SavedWordEntity): Long

    /** Kaydedilen kelimeyi siler */
    @Delete
    suspend fun deleteWord(word: SavedWordEntity)

    /** Id ile sil */
    @Query("DELETE FROM saved_words WHERE id = :wordId")
    suspend fun deleteWordById(wordId: Long)

    /** Kelimenin öğrenme seviyesini günceller (SRS) */
    @Query("UPDATE saved_words SET mastery_level = :level WHERE id = :wordId")
    suspend fun updateMastery(wordId: Long, level: Int)

    /** Tüm listelerdeki toplam kelime sayısı */
    @Query("SELECT COUNT(*) FROM saved_words")
    fun getTotalWordCount(): Flow<Int>

    /** Belirli bir kelimenin hangi listelerde kayıtlı olduğunu getirir */
    @Query("SELECT list_id FROM saved_words WHERE word = :word AND part_of_speech = :partOfSpeech")
    fun getListIdsForWord(word: String, partOfSpeech: String): Flow<List<Long>>
}
