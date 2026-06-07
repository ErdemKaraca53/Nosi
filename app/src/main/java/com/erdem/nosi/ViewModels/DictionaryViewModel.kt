package com.erdem.nosi.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdem.nosi.Network.DictionaryApi
import com.erdem.nosi.data.DictionaryApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DictionaryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DictionaryUiState>(DictionaryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    /**
     * Verilen kelimeyi API'ye gönderir ve sonucu uiState üzerinden yayınlar.
     * Zaten başarılı bir sonuç varsa (recomposition) tekrar çekmez.
     */
    fun getDictionary(word: String) {
        if (_uiState.value is DictionaryUiState.Success) return
        fetch(word)
    }

    /** Hata ekranındaki "tekrar dene" için — durumdan bağımsız yeniden çeker. */
    fun retry(word: String) = fetch(word)

    private fun fetch(word: String) {
        _uiState.value = DictionaryUiState.Loading

        viewModelScope.launch {
            try {
                val response = DictionaryApi.retrofitService.getWordDefinition(word)
                _uiState.value = if (response.isEmpty()) {
                    DictionaryUiState.Error("No definition found for \"$word\".")
                } else {
                    DictionaryUiState.Success(response)
                }
            } catch (e: HttpException) {
                // 404 → kelime sözlükte yok; diğerleri → genel sunucu hatası
                _uiState.value = if (e.code() == 404) {
                    DictionaryUiState.Error("No definition found for \"$word\".")
                } else {
                    DictionaryUiState.Error("Something went wrong. Please try again.")
                }
            } catch (e: Exception) {
                _uiState.value = DictionaryUiState.Error("Check your connection and try again.")
            }
        }
    }
}

/*
  uiState ekranın olası durumlarını temsil eder.
  Loading → veri çekiliyor, Success → sonuç hazır, Error → bir sorun oluştu.
  Ekran 'when' bloğuyla her durum için farklı bir composable gösterir.
*/
sealed class DictionaryUiState {

    data object Loading : DictionaryUiState()

    data class Success(
        val response: List<DictionaryApiResponse>
    ) : DictionaryUiState()

    data class Error(
        val message: String
    ) : DictionaryUiState()
}
