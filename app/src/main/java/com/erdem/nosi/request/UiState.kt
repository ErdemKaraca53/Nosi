package com.erdem.nosi.request

import com.erdem.nosi.data.GeminiResponse
import com.erdem.nosi.data.TranslationData

/**
 * UI durumunu temsil eden sealed class.
 * Ekranın hangi durumda olduğunu belirler.
 */
sealed class UiState {
    /** Henüz istek atılmadı — input ekranı gösterilir */
    data object Idle : UiState()

    /** İstek atıldı, yanıt bekleniyor */
    data object Loading : UiState()

    /** Başarılı yanıt alındı */
    data class Success(
        val data: GeminiResponse,
        val translationData: TranslationData
    ) : UiState()

    /** Hata oluştu */
    data class Error(val message: String) : UiState()
}
