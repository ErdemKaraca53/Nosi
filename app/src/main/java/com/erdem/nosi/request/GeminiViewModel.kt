package com.erdem.nosi.request

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erdem.nosi.data.Content
import com.erdem.nosi.data.GeminiRequest
import com.erdem.nosi.data.Part
import com.erdem.nosi.data.TranslationData
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GeminiViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Eski state uyumluluk için korunuyor (gerekirse kaldırabilirsiniz)
    private val _newState = MutableStateFlow(com.erdem.nosi.data.GeminiResponse())
    val newState: StateFlow<com.erdem.nosi.data.GeminiResponse> = _newState

    private val apiInterface = RetrofitInstance.api
    private val repository = GeminiApiRepository(apiInterface)
    private val gson = Gson()

    /** Aktif istek Job'u — yeni istek gelince iptal edilir */
    private var currentJob: Job? = null

    private fun createRequest(text: String): GeminiRequest {
        // GeminiApiService artık object (singleton), new yapmıyoruz
        val prompt = GeminiApiService.CreatePrompt(text)

        return GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = prompt)
                    )
                )
            )
        )
    }

    /**
     * Gemini API yanıtından JSON text'i çıkarıp TranslationData'ya parse eder.
     * Markdown code fence (```json ... ```) varsa temizler.
     */
    private fun parseTranslationData(rawText: String): TranslationData {
        // Markdown code fence temizleme
        val cleanedJson = rawText
            .replace(Regex("```json\\s*"), "")
            .replace(Regex("```\\s*"), "")
            .trim()

        return try {
            gson.fromJson(cleanedJson, TranslationData::class.java)
        } catch (e: Exception) {
            Log.e("GeminiViewModel", "JSON parse hatası: ${e.message}", e)
            TranslationData()
        }
    }

    /**
     * Gemini API'ye istek gönderir.
     *
     * Güvenlik önlemleri:
     *  - Boş metin gönderilmez
     *  - Zaten yükleme devam ediyorsa önceki istek iptal edilir (çoklu istek önleme)
     *  - Loading/Success/Error durumları StateFlow ile takip edilir
     */
    fun fetchResponse(text: String) {
        // Boş metin kontrolü
        if (text.isBlank()) {
            Log.w("GeminiViewModel", "Boş metin gönderilmeye çalışıldı, istek atılmadı.")
            return
        }

        // Önceki isteği iptal et (çoklu istek önleme)
        currentJob?.cancel()

        currentJob = viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val request = createRequest(text)
                Log.d("GeminiViewModel", "API isteği gönderiliyor: ${text.take(50)}...")

                val result = repository.getData(request)
                _newState.value = result

                // API yanıtından JSON text'i çıkar ve parse et
                val rawText = result.candidates
                    .firstOrNull()?.content?.parts
                    ?.firstOrNull()?.text.orEmpty()

                Log.d("GeminiViewModel", "API ham yanıt: ${rawText.take(200)}...")

                val translationData = parseTranslationData(rawText)
                _uiState.value = UiState.Success(result, translationData)

                Log.d("GeminiViewModel", "API yanıtı başarılı. ${translationData.translations.size} çeviri bulundu.")
            } catch (e: kotlinx.coroutines.CancellationException) {
                // Coroutine iptal edildiyse (yeni istek geldi), bir şey yapma
                Log.d("GeminiViewModel", "İstek iptal edildi (yeni istek geldi).")
                throw e // CancellationException yeniden fırlatılmalı
            } catch (e: Exception) {
                Log.e("GeminiViewModel", "API hatası: ${e.message}", e)
                _uiState.value = UiState.Error(
                    e.message ?: "Beklenmeyen bir hata oluştu"
                )
            }
        }
    }

    /**
     * UI durumunu Idle'a sıfırlar (input ekranına döner).
     */
    fun resetToIdle() {
        currentJob?.cancel()
        _uiState.value = UiState.Idle
    }
}
