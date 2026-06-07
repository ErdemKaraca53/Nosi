package com.erdem.nosi.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdem.nosi.model.TranslatedSentenceData
import com.erdem.nosi.model.TranslationData
import com.erdem.nosi.model.WordData
import com.erdem.nosi.translate.SentenceTranslator
import com.erdem.nosi.translate.WordTranslator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Türkçe cümleyi İngilizceye çevirip kelime kelime Türkçe karşılıklarını üretir.
 *
 * Akış:
 *  1. Cümleyi TR→EN çevir (SentenceTranslator)
 *  2. İngilizce cümleden kelimeleri ayıkla
 *  3. Her kelimeyi EN→TR çevir (WordTranslator) → WordData
 */
class TranslationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<TranslationUiState>(TranslationUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private var job: Job? = null

    fun translate(turkish: String) {
        val text = turkish.trim()
        if (text.isBlank()) {
            _uiState.value = TranslationUiState.Idle
            return
        }

        job?.cancel()
        _uiState.value = TranslationUiState.Loading

        job = viewModelScope.launch {
            val english = SentenceTranslator.toEnglish(text)
            if (english.isNullOrBlank()) {
                _uiState.value = TranslationUiState.Error(
                    "Translation failed. Check your connection and try again."
                )
                return@launch
            }

            // İngilizce cümleden kelimeleri ayıkla ve her birini Türkçeye çevir
            val words = extractWords(english).map { w ->
                WordData(
                    word = w,
                    lemma = w,
                    pos = "",
                    meaningTr = WordTranslator.toTurkish(w).orEmpty()
                )
            }

            _uiState.value = TranslationUiState.Success(
                TranslationData(
                    translations = listOf(
                        TranslatedSentenceData(translatedSentence = english, words = words)
                    )
                )
            )
        }
    }

    /** Giriş temizlenince başlangıç durumuna döner. */
    fun reset() {
        job?.cancel()
        _uiState.value = TranslationUiState.Idle
    }

    /** Cümleyi kelimelere böler: noktalama temizlenir, küçük harfe çevrilir, tekrarlar atılır. */
    private fun extractWords(sentence: String): List<String> =
        sentence.split(Regex("[^A-Za-z']+"))
            .map { it.trim('\'').lowercase() }
            .filter { it.length > 1 }
            .distinct()
}

sealed class TranslationUiState {
    data object Idle : TranslationUiState()
    data object Loading : TranslationUiState()
    data class Success(val data: TranslationData) : TranslationUiState()
    data class Error(val message: String) : TranslationUiState()
}
