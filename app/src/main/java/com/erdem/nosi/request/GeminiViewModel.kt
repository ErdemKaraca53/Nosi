package com.erdem.nosi.request

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.erdem.nosi.data.Content
import com.erdem.nosi.data.GeminiRequest
import com.erdem.nosi.data.Part
import com.erdem.nosi.data.TranslationData
import com.erdem.nosi.data.local.NosiDatabase
import com.erdem.nosi.data.local.SavedTranslationEntity
import com.erdem.nosi.data.local.SavedWordEntity
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GeminiViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Kaydetme durumu
    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    // Eski state uyumluluk için korunuyor
    private val _newState = MutableStateFlow(com.erdem.nosi.data.GeminiResponse())
    val newState: StateFlow<com.erdem.nosi.data.GeminiResponse> = _newState

    private val apiInterface = RetrofitInstance.api
    private val repository = GeminiApiRepository(apiInterface)
    private val gson = Gson()

    // Room DAO
    private val dao = NosiDatabase.getInstance(application).translationDao()

    // Girilen kaynak cümle (kaydetme için saklanır)
    private var currentSourceSentence: String = ""

    /** Aktif istek Job'u — yeni istek gelince iptal edilir */
    private var currentJob: Job? = null

    private fun createRequest(text: String): GeminiRequest {
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
     */
    fun fetchResponse(text: String) {
        if (text.isBlank()) {
            Log.w("GeminiViewModel", "Boş metin gönderilmeye çalışıldı, istek atılmadı.")
            return
        }

        currentSourceSentence = text
        currentJob?.cancel()

        currentJob = viewModelScope.launch {
            _uiState.value = UiState.Loading
            _saveState.value = SaveState.Idle

            try {
                val request = createRequest(text)
                Log.d("GeminiViewModel", "API isteği gönderiliyor: ${text.take(50)}...")

                val result = repository.getData(request)
                _newState.value = result

                val rawText = result.candidates
                    .firstOrNull()?.content?.parts
                    ?.firstOrNull()?.text.orEmpty()

                Log.d("GeminiViewModel", "API ham yanıt: ${rawText.take(200)}...")

                val translationData = parseTranslationData(rawText)
                _uiState.value = UiState.Success(result, translationData)

                Log.d("GeminiViewModel", "API yanıtı başarılı. ${translationData.translations.size} çeviri bulundu.")
            } catch (e: kotlinx.coroutines.CancellationException) {
                Log.d("GeminiViewModel", "İstek iptal edildi (yeni istek geldi).")
                throw e
            } catch (e: Exception) {
                Log.e("GeminiViewModel", "API hatası: ${e.message}", e)
                _uiState.value = UiState.Error(
                    e.message ?: "Beklenmeyen bir hata oluştu"
                )
            }
        }
    }

    /**
     * Mevcut çeviriyi Room Database'e kaydeder (varsayılan koleksiyona).
     */
    fun saveTranslation() {
        val state = _uiState.value
        if (state !is UiState.Success) return

        val data = state.translationData
        val firstTranslation = data.translations.firstOrNull() ?: return

        viewModelScope.launch {
            _saveState.value = SaveState.Saving
            try {
                // Varsayılan koleksiyon yoksa oluştur
                if (dao.collectionExistsByName("My Words") == 0) {
                    dao.insertCollection(
                        com.erdem.nosi.data.local.CollectionEntity(name = "My Words")
                    )
                }
                // Varsayılan koleksiyon ID'sini bul
                val collections = dao.getAllCollectionsOnce()
                val defaultCollectionId = collections.firstOrNull()?.id ?: 1L

                val entity = SavedTranslationEntity(
                    collectionId = defaultCollectionId,
                    sourceSentence = currentSourceSentence,
                    translatedSentence = firstTranslation.translatedSentence,
                    sourceLanguage = data.sourceLanguage,
                    targetLanguage = data.targetLanguage
                )

                val wordEntities = firstTranslation.words.map { word ->
                    SavedWordEntity(
                        translationId = 0,
                        word = word.word,
                        lemma = word.lemma,
                        pos = word.pos,
                        meaningTr = word.meaningTr
                    )
                }

                dao.insertTranslationWithWords(entity, wordEntities)
                _saveState.value = SaveState.Saved
                Log.d("GeminiViewModel", "Çeviri kaydedildi: ${firstTranslation.translatedSentence}")
            } catch (e: Exception) {
                Log.e("GeminiViewModel", "Kaydetme hatası: ${e.message}", e)
                _saveState.value = SaveState.Error(e.message ?: "Kaydetme hatası")
            }
        }
    }

    /**
     * UI durumunu Idle'a sıfırlar (input ekranına döner).
     */
    fun resetToIdle() {
        currentJob?.cancel()
        _uiState.value = UiState.Idle
        _saveState.value = SaveState.Idle
    }
}

/**
 * Kaydetme durumunu temsil eden sealed class.
 */
sealed class SaveState {
    data object Idle : SaveState()
    data object Saving : SaveState()
    data object Saved : SaveState()
    data class Error(val message: String) : SaveState()
}
