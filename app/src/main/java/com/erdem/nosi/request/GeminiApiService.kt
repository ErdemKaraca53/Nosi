package com.erdem.nosi.request

import android.util.Log
import com.erdem.nosi.BuildConfig
import com.erdem.nosi.data.Content
import com.erdem.nosi.data.GeminiRequest
import com.erdem.nosi.data.Part

class GeminiApiService() {

    fun CreatePrompt(inputSentence: String): String {
        val safeInput = inputSentence.replace("\"", "\\\"")

        return """
        
        Sana bir türkçe cümle vereceğim. Bu cümleyi ingilizceye çevir. İngilizce karşılığı olan
        cümlenin her kelimesini aşağıdaki json formatına uygun şekilde response olarak döndür

        JSON Schema:
        {
          "sourceLanguage": "tr",
          "targetLanguage": "en",
          {
            "translations": [
              {
                "translatedSentence": "string",
                "words": [
                  {
                    "word": "string",
                    "pos": "string",
                    "meaningTr": "string"
                  }
                ]
              }
            ]
          }
        }


        Input sentence (Turkish):
        "$safeInput"
    """.trimIndent()
    }

}