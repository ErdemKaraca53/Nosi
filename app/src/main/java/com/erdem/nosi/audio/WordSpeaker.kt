package com.erdem.nosi.audio

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Flashcard'larda İngilizce kelimeyi sesli okumak için Android TextToSpeech sarmalayıcısı.
 *
 * Kayıtlı bir ses dosyası gerektirmez; cihazın TTS motoru kullanılır (çevrimdışı da çalışır).
 * Composable yaşam döngüsüne bağlıdır: ekran kapanınca motor serbest bırakılır.
 *
 * @return verilen metni seslendiren fonksiyon
 */
@Composable
fun rememberWordSpeaker(): (String) -> Unit {
    val context = LocalContext.current
    val ttsState = remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        val engine = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsState.value?.language = Locale.US
            }
        }
        ttsState.value = engine
        onDispose {
            engine.stop()
            engine.shutdown()
            ttsState.value = null
        }
    }

    return remember {
        { text: String ->
            val tts = ttsState.value
            if (tts != null && text.isNotBlank()) {
                tts.language = Locale.US
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "study_word")
            }
        }
    }
}
