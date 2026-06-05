package com.erdem.nosi.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.erdem.nosi.database.AppDatabase
import com.erdem.nosi.database.WordListEntity
import com.erdem.nosi.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Kelime listesi ve kaydetme işlemlerini yöneten ViewModel.
 * AndroidViewModel kullandık çünkü Room için Application Context gerekiyor.
 */
class DatabaseViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = WordRepository(db.wordListDao(), db.savedWordDao())

    // ── Tüm listeler (her yerden erişilebilir StateFlow) ────────────────────
    val allLists: StateFlow<List<WordListEntity>> = repository.getAllLists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // ── Kelime Kaydetme UI State ──────────────────────────────────────────────
    private val _saveState = MutableStateFlow<SaveWordState>(SaveWordState.Idle)
    val saveState: StateFlow<SaveWordState> = _saveState.asStateFlow()

    // ── Liste İşlemleri ──────────────────────────────────────────────────────

    /**
     * Yeni bir kelime listesi oluşturur.
     * @return oluşturulan listenin id'si
     */
    fun createList(
        name: String,
        emoji: String = "📚",
        color: String = "#0D9488",
        onCreated: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val id = repository.createList(name, emoji, color)
            onCreated(id)
        }
    }

    fun deleteList(list: WordListEntity) {
        viewModelScope.launch {
            repository.deleteList(list)
        }
    }

    // ── Kelime Kaydetme ───────────────────────────────────────────────────────

    /**
     * Seçili anlamı (POS) belirtilen listeye kaydeder.
     *
     * @param listId      hedef liste
     * @param word        aranılan kelime
     * @param partOfSpeech seçili POS (noun, verb, ...)
     * @param definitions  tanım metinleri
     * @param synonyms     eşanlamlılar
     * @param antonyms     zıt anlamlılar
     */
    fun saveWord(
        listId: Long,
        word: String,
        partOfSpeech: String,
        definitions: List<String>,
        synonyms: List<String>,
        antonyms: List<String>
    ) {
        _saveState.value = SaveWordState.Saving
        viewModelScope.launch {
            val success = repository.saveWord(
                listId = listId,
                word = word,
                partOfSpeech = partOfSpeech,
                definitions = definitions,
                synonyms = synonyms,
                antonyms = antonyms
            )
            _saveState.value = if (success) {
                SaveWordState.Saved
            } else {
                SaveWordState.AlreadyExists
            }
        }
    }

    /** Save state'i sıfırla (Bottom Sheet kapandığında çağrılır) */
    fun resetSaveState() {
        _saveState.value = SaveWordState.Idle
    }

    /** Belirli bir kelime + POS'un belirtilen listede kayıtlı olup olmadığını kontrol eder */
    fun getListIdsForWord(word: String, partOfSpeech: String) =
        repository.getListIdsForWord(word, partOfSpeech)
}

// ── Save State ────────────────────────────────────────────────────────────────
sealed class SaveWordState {
    data object Idle : SaveWordState()
    data object Saving : SaveWordState()
    data object Saved : SaveWordState()
    data object AlreadyExists : SaveWordState()  // Duplicate — zaten kayıtlı
}