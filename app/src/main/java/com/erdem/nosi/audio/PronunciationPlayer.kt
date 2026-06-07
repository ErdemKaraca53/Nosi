package com.erdem.nosi.audio

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

/**
 * Sözlük API'sinden gelen telaffuz (mp3) URL'lerini çalan basit oynatıcı.
 *
 * Tek bir MediaPlayer örneği tutar; yeni bir ses başlatılınca öncekini serbest bırakır.
 * Ağ/format hatalarında sessizce başarısız olur (uygulama çökmez).
 */
object PronunciationPlayer {

    private var player: MediaPlayer? = null

    fun play(url: String) {
        if (url.isBlank()) return
        stop()
        try {
            val mp = MediaPlayer()
            player = mp
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            mp.setDataSource(url)
            mp.setOnPreparedListener { it.start() }
            mp.setOnCompletionListener {
                it.release()
                if (player === it) player = null
            }
            mp.setOnErrorListener { m, _, _ ->
                m.release()
                if (player === m) player = null
                true
            }
            mp.prepareAsync()
        } catch (e: Exception) {
            Log.w("PronunciationPlayer", "Playback failed: ${e.message}")
            stop()
        }
    }

    fun stop() {
        player?.let {
            try {
                it.release()
            } catch (_: Exception) {
            }
        }
        player = null
    }
}
