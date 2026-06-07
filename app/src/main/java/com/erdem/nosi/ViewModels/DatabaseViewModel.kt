package com.erdem.nosi.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.erdem.nosi.database.AppDatabase
import com.erdem.nosi.database.SavedSentenceEntity
import com.erdem.nosi.database.WordListEntity
import com.erdem.nosi.model.StudyWord
import com.erdem.nosi.repository.WordRepository
import com.erdem.nosi.translate.WordTranslator
import kotlinx.coroutines.flow.Flow
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
    private val repository = WordRepository(db.wordListDao(), db.savedWordDao(), db.savedSentenceDao())

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

    // ── Listedeki Kelimeler (Koleksiyon Detay + Çalışma) ─────────────────────

    /** Listedeki kelimeleri (JSON çözülmüş) Flow olarak döndürür */
    fun getStudyWordsForList(listId: Long): Flow<List<StudyWord>> =
        repository.getStudyWordsForList(listId)

    /** Bir listedeki kelime sayısını Flow olarak döndürür */
    fun getWordCountForList(listId: Long): Flow<Int> =
        repository.getWordCountForList(listId)

    /** Kaydedilmiş bir kelimeyi listeden siler */
    fun deleteWord(wordId: Long) {
        viewModelScope.launch {
            repository.deleteWordById(wordId)
        }
    }

    /** Çalışma ekranından öğrenme seviyesini günceller (SRS) */
    fun setMastery(wordId: Long, level: Int) {
        viewModelScope.launch {
            repository.setMastery(wordId, level)
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
            // Kelimeyi Türkçeye çevir (başarısız olursa boş geçeriz, akış bozulmaz)
            val meaningTr = WordTranslator.toTurkish(word).orEmpty()
            val success = repository.saveWord(
                listId = listId,
                word = word,
                partOfSpeech = partOfSpeech,
                meaningTr = meaningTr,
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

    // ── Cümle İşlemleri (çeviri ekranı) ──────────────────────────────────────

    /** Çeviriden gelen cümleyi (kaynak + çeviri) belirtilen listeye kaydeder */
    fun saveSentence(listId: Long, sourceText: String, translatedText: String) {
        viewModelScope.launch {
            repository.saveSentence(listId, sourceText, translatedText)
        }
    }

    /** Bir listedeki kayıtlı cümleleri Flow olarak döndürür */
    fun getSentencesForList(listId: Long): Flow<List<SavedSentenceEntity>> =
        repository.getSentencesForList(listId)

    /** Bir listedeki cümle sayısını Flow olarak döndürür */
    fun getSentenceCountForList(listId: Long): Flow<Int> =
        repository.getSentenceCountForList(listId)

    /** Kayıtlı bir cümleyi siler */
    fun deleteSentence(sentenceId: Long) {
        viewModelScope.launch {
            repository.deleteSentenceById(sentenceId)
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