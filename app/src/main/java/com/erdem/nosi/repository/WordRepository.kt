package com.erdem.nosi.repository

import com.erdem.nosi.database.Word
import com.erdem.nosi.database.WordDao
import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao: WordDao) {

    fun getAllWord() : Flow<List<Word>> = wordDao.getAll()

    suspend fun insertWord(word: Word) = wordDao.insert(word)
}