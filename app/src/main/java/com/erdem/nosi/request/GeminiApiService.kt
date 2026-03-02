package com.erdem.nosi.request

/**
 * Gemini API için prompt oluşturma servisi.
 * Object (singleton) olarak tanımlandı — her çağrıda yeni instance oluşturmaya gerek yok.
 */
object GeminiApiService {

    fun CreatePrompt(inputSentence: String): String {
        val safeInput = inputSentence
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", " ")
            .trim()

        return """
        Sana bir türkçe cümle vereceğim. Bu cümleyi ingilizceye çevir. İngilizce karşılığı olan
        cümlenin her kelimesini aşağıdaki json formatına uygun şekilde response olarak döndür.
        Sadece JSON döndür, başka bir şey yazma.

        JSON Schema:
        {
          "sourceLanguage": "tr",
          "targetLanguage": "en",
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

        Input sentence (Turkish):
        "$safeInput"
        """.trimIndent()
    }
}