package com.erdem.nosi.translate

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * İngilizce bir kelimeyi Türkçeye çeviren ML Kit sarmalayıcısı.
 *
 * Kelime kaydedilirken çağrılır; sonucu flashcard'ın arka yüzünde gösterilir.
 * Model indirilemezse veya çeviri başarısız olursa null döner — bu durumda
 * ekran İngilizce tanıma geri düşer (uygulama çökmez, akış bozulmaz).
 */
object WordTranslator {

    private val translator by lazy {
        Translation.getClient(
            TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.TURKISH)
                .build()
        )
    }

    // Wifi şartı koymuyoruz ki mobil veride de model inebilsin (~30MB, tek seferlik)
    private val downloadConditions = DownloadConditions.Builder().build()

    /** Metni Türkçeye çevirir; başarısız olursa null. */
    suspend fun toTurkish(text: String): String? = suspendCancellableCoroutine { cont ->
        translator.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener {
                translator.translate(text)
                    .addOnSuccessListener { translated ->
                        if (cont.isActive) cont.resume(translated)
                    }
                    .addOnFailureListener {
                        if (cont.isActive) cont.resume(null)
                    }
            }
            .addOnFailureListener {
                if (cont.isActive) cont.resume(null)
            }
    }
}
