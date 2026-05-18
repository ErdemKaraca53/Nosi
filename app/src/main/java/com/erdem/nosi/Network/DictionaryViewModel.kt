package com.erdem.nosi.Network

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class DictionaryViewModel: ViewModel() {

    init {
        getDictionary()
    }

    private fun getDictionary() {
        viewModelScope.launch {
            val startTime = System.nanoTime()
            val result = DictionaryApi.retrofitService.getWordDefinition("like")
            Log.e("Deneme", result.toString())
            val endTime = System.nanoTime()
            // Calculate the elapsed time in nanoseconds
            val elapsedTime = endTime - startTime

            // Convert the elapsed time to milliseconds for better readability
            val elapsedTimeMillis = elapsedTime / 1_000_000
            Log.e("Deneme", elapsedTimeMillis.toString())
        }
    }
}