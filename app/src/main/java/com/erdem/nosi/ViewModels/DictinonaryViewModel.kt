package com.erdem.nosi.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdem.nosi.Network.DictionaryApi
import com.erdem.nosi.data.DictionaryApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DictionaryViewModel: ViewModel() {

    private val _uiState = MutableStateFlow<DictionaryUiState>(DictionaryUiState.Loading)
    val uiState = _uiState.asStateFlow()

    /* Bu fonksiyon gelen kelimeyi alır
       Bu kelimeyi api'ye gönderir ve gelen değerleri alır.
       Gelen değerleri uiState üzerinde gösterir.
    */
    fun getDictionary(word: String) {
        _uiState.value = DictionaryUiState.Loading

        viewModelScope.launch {
            try {
                val response = DictionaryApi.retrofitService.getWordDefinition(word)

                if (response.isEmpty()) {
                    Log.e("deneme", "bos geldi")
                } else {
                    Log.e("deneme", response.toString())
                    _uiState.value = DictionaryUiState.Success(response)
                }
            } catch (e : Exception) {
                Log.e("deneme", "bos geldi")
            }
        }

    }

}

/*uiState ekranda birden fazla durum oluştuğunda kullanılıyor.
  Örnek vermek gerekirse bir ekranın olası 3 durumu var.
  Mesela loading ve succes durumları var.
  Bu 2 durum içinde ekran farklı şekillenecek ve farklı veriler aktarılacak
  Bu durumları 'when' bloğu ile yapıyoruz.
  Bu when bloklarında her 'state için farklı bir composable oluşturabiliriz.
*/

sealed class DictionaryUiState {

    data object Loading : DictionaryUiState()
    data class Success (
        val response : List<DictionaryApiResponse>
    ) : DictionaryUiState()

}