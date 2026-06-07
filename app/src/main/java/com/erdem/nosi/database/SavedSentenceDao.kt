package com.erdem.nosi.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedSentenceDao {

    /** Bir listedeki cümleleri (yeniden eskiye) döndürür */
    @Query("SELECT * FROM saved_sentences WHERE list_id = :listId ORDER BY saved_at DESC")
    fun getSentencesForList(listId: Long): Flow<List<SavedSentenceEntity>>

    /** Cümle ekler, satır id'sini döndürür */
    @Insert
    suspend fun insertSentence(sentence: SavedSentenceEntity): Long

    /** Cümleyi id ile siler */
    @Query("DELETE FROM saved_sentences WHERE id = :sentenceId")
    suspend fun deleteSentenceById(sentenceId: Long)

    /** Bir listedeki cümle sayısını döndürür */
    @Query("SELECT COUNT(*) FROM saved_sentences WHERE list_id = :listId")
    fun getSentenceCountForList(listId: Long): Flow<Int>
}
