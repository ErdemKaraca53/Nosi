package com.erdem.nosi.request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdem.nosi.data.Content
import com.erdem.nosi.data.GeminiRequest
import com.erdem.nosi.data.GeminiResponse
import com.erdem.nosi.data.Part
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GeminiViewModel() : ViewModel() {

    private val _newState = MutableStateFlow(GeminiResponse())
    val newState : StateFlow<GeminiResponse> = _newState

    private val apiInterface = RetrofitInstance.api
    private val repository = GeminiApiRepository(apiInterface)
    init {
        //fetchResponse()
    }

    private fun CreateRequest(text: String) : GeminiRequest {

        val prompt = GeminiApiService().CreatePrompt(text)

        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = prompt)
                    )
                )
            )
        )
        return request
    }

    fun fetchResponse(text: String) {
        viewModelScope.launch {
            try {
                val request = CreateRequest(text)
                val result = repository.getData(request)
                _newState.value = result
            } catch (e: Exception) {
                println("Hata oluştu: ${e.message}")
            }
        }
    }

}