package com.erdem.nosi.translate

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

class Translator {

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.TURKISH)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()

        val translator = Translation.getClient(options)

        suspend fun transleteText(text: String): Result<String> {
            return try {
                val conditions = DownloadConditions.Builder()
                    .requireWifi()
                    .build()

                // Model indirme işlemini bekle
                translator.downloadModelIfNeeded(conditions).await()

                // Çeviri işlemini bekle ve sonucu döndür
                val result = translator.translate(text).await()
                Result.success(result)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}