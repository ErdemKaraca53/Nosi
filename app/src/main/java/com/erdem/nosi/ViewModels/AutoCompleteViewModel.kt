package com.erdem.nosi.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdem.nosi.Network.AutoCompleteApi
import com.erdem.nosi.data.AutoCompleteResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AutoCompleteViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AutoCompleteUiState>(AutoCompleteUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var currentSuggestions: List<AutoCompleteResponse> = emptyList()

    // Her tuş vuruşunda yeni istek atmamak için debounce job'ı
    private var searchJob: Job? = null

    /**
     * Kullanıcı her yazdığında çağrılır.
     * Önceki bekleyen aramayı iptal eder, 300ms sessizlik sonrası API'ye gider (debounce).
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query   // TextField ile senkronizasyon

        searchJob?.cancel()

        if (query.length < 2) {
            _uiState.value = AutoCompleteUiState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)  // debounce: kullanıcı yazmayı bırakana kadar bekle
            _uiState.value = AutoCompleteUiState.Loading(currentSuggestions)
            try {
                val response = AutoCompleteApi.retrofitService.GetSuggestions(query)
                currentSuggestions = response
                _uiState.value = if (response.isEmpty()) {
                    AutoCompleteUiState.Error("No suggestions found")
                } else {
                    AutoCompleteUiState.Success(response)
                }
            } catch (e: Exception) {
                _uiState.value = AutoCompleteUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class AutoCompleteUiState {

    /** Kullanıcının yazmasını bekleyen pasif durum. */
    data object Idle : AutoCompleteUiState()

    /** Yeni sonuç beklenirken eski önerileri tutar (titremeyi önler). */
    data class Loading(
        val oldSuggestion: List<AutoCompleteResponse> = emptyList()
    ) : AutoCompleteUiState()

    data class Success(
        val suggestions: List<AutoCompleteResponse>
    ) : AutoCompleteUiState()

    data class Error(
        val message: String
    ) : AutoCompleteUiState()
}
