package com.erdem.nosi.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WordListDao {

    /** Tüm listeleri oluşturulma tarihine göre (yeniden eskiye) döndürür */
    @Query("SELECT * FROM word_lists ORDER BY created_at DESC")
    fun getAllLists(): Flow<List<WordListEntity>>

    /** Belirli bir listeyi id ile getirir */
    @Query("SELECT * FROM word_lists WHERE id = :listId")
    suspend fun getListById(listId: Long): WordListEntity?

    /** Yeni liste ekler, id'yi döndürür */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: WordListEntity): Long

    /** Mevcut listeyi günceller (isim, emoji, renk vb.) */
    @Update
    suspend fun updateList(list: WordListEntity)

    /** Listeyi ve (CASCADE sayesinde) içindeki tüm kelimeleri siler */
    @Delete
    suspend fun deleteList(list: WordListEntity)

    /** Bir listedeki kelime sayısını döndürür */
    @Query("SELECT COUNT(*) FROM saved_words WHERE list_id = :listId")
    fun getWordCountForList(listId: Long): Flow<Int>
}
