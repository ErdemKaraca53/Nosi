package com.erdem.nosi.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Query("SELECT * FROM word")
    fun getAll() : Flow<List<Word>>

    @Insert
    fun insert(word: Word)

}