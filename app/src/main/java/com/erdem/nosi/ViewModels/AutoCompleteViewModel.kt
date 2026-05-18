package com.erdem.nosi.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdem.nosi.Network.AutoCompleteApi
import kotlinx.coroutines.launch

class AutoCompleteViewModel: ViewModel() {

    init {
        getAutoComplete()
    }

    private fun getAutoComplete() {
        viewModelScope.launch {
           val result = AutoCompleteApi.retrofitService.GetSuggestions("like")
            Log.e("Deneme", result.toString())
        }
    }
}