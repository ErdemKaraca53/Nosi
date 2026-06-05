package com.erdem.nosi.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdem.nosi.database.AppDatabase
import com.erdem.nosi.database.Word
import com.erdem.nosi.repository.WordRepository
import kotlinx.coroutines.launch

class DatabaseViewModel(application: Application): AndroidViewModel(application) {

    //Normal viewModelde  contenxt yok.
    //AndroidViewModelde Applicaton Context var.

    //Veri tabanının objesini oluşturuyoruz. Companion object olduğu için sınıftan obje üretmemize gerke yok
    private val db = AppDatabase.getDatabase(application)

    private val repository = WordRepository(db.wordDao())

    fun addWord(word: Word) {
        viewModelScope.launch {
            repository.insertWord(word)
        }
    }

}