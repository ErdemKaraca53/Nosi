package com.erdem.nosi.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdem.nosi.Network.AutoCompleteApi
import com.erdem.nosi.data.AutoCompleteResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class AutoCompleteViewModel : ViewModel() {
    var count = 0
    private val _uiState = MutableStateFlow<AutoCompleteUiState>(AutoCompleteUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var currenSuggestion : List<AutoCompleteResponse> = emptyList()

    // Kullanıcı her yazdığında burası çağrılacak
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query   // ← TextField ile senkronizasyon için

        if (query.length < 2) {
            _uiState.value = AutoCompleteUiState.Idle
            return
        }

        getAutoCompleteSuggestions(query)
    }

    fun getAutoCompleteSuggestions(query: String) {
        /*
        Burada yaptığımız şey sealed class ile durumları genelleştirmek
        Burada her durum için sealed class'ta bir karşılık var.
        Bu karşılıkların içi sealed class kullanımında devreye giriyor.
         */
        count++
        viewModelScope.launch {
            _uiState.value = AutoCompleteUiState.Loading(currenSuggestion)
            //delay(300)
            try {
                val response = AutoCompleteApi.retrofitService.GetSuggestions(query)
                currenSuggestion = response

                Log.e("sayac", "total count: $count")
                if (response.isEmpty()) {
                    _uiState.value = AutoCompleteUiState.Error("No suggestions found")
                } else {
                    _uiState.value = AutoCompleteUiState.Success(response)
                }

            } catch (e: kotlin.Exception) {
                _uiState.value = AutoCompleteUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class AutoCompleteUiState {

    //Parametresiz durumlar için bellek ve performans
    /*
    Idle'ın İşlevi:
    Idle durumu, kullanıcının bir eylem (örneğin metin girip arama butonuna basma)
    yapmasını bekleyen pasif ekranlar için kullanılır.
     */
    data object Idle : AutoCompleteUiState()
    data class Loading(
        val oldSuggestion : List<AutoCompleteResponse> = emptyList()
    ) : AutoCompleteUiState()

    //Veri taşıyan durumlar için
    data class Success(
        val suggestions: List<AutoCompleteResponse>
    ) : AutoCompleteUiState()

    data class Error(
        val message: String
    ) : AutoCompleteUiState()
}