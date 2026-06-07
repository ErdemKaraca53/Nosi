package com.erdem.nosi.translate

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Türkçe bir cümleyi İngilizceye çeviren ML Kit sarmalayıcısı.
 * Çeviri ekranı kullanır. Başarısız olursa null döner (akış bozulmaz).
 */
object SentenceTranslator {

    private val translator by lazy {
        Translation.getClient(
            TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.TURKISH)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build()
        )
    }

    private val downloadConditions = DownloadConditions.Builder().build()

    /** Türkçe metni İngilizceye çevirir; başarısız olursa null. */
    suspend fun toEnglish(text: String): String? = suspendCancellableCoroutine { cont ->
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
