package com.lingora.app.data.tts

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/** Events the rest of the app reacts to as the voice engine changes state. */
sealed class TtsEvent {
    data object Idle : TtsEvent()
    data object Unavailable : TtsEvent()
    data class Speaking(val word: String) : TtsEvent()
    data class VoiceMissing(val language: String) : TtsEvent()
}

/**
 * Wraps the text-to-speech engine already installed on the device. Lingora
 * never ships or downloads its own voices: every pronunciation is spoken
 * by whichever engine the learner has chosen in their Android settings,
 * always in the language the word was translated *into* — never the
 * learner's native language.
 *
 * If a language has no voice installed, [speak] does not fail silently:
 * it emits [TtsEvent.VoiceMissing] so the UI can offer to open the
 * device's voice settings via [openSystemVoiceSettings].
 */
class TtsManager(private val appContext: Context) {

    private var engine: TextToSpeech? = null

    private val _event = MutableStateFlow<TtsEvent>(TtsEvent.Idle)
    val event: StateFlow<TtsEvent> = _event.asStateFlow()

    fun initialize() {
        engine = TextToSpeech(appContext) { status ->
            if (status != TextToSpeech.SUCCESS) {
                _event.value = TtsEvent.Unavailable
            }
        }
        engine?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _event.value = TtsEvent.Speaking(utteranceId.orEmpty())
            }

            override fun onDone(utteranceId: String?) {
                _event.value = TtsEvent.Idle
            }

            @Deprecated("Deprecated in the platform interface; still required to override.")
            override fun onError(utteranceId: String?) {
                _event.value = TtsEvent.Idle
            }
        })
    }

    fun speak(word: String, locale: Locale, languageLabel: String) {
        val tts = engine
        if (word.isBlank() || tts == null) return

        when (tts.isLanguageAvailable(locale)) {
            TextToSpeech.LANG_MISSING_DATA, TextToSpeech.LANG_NOT_SUPPORTED -> {
                _event.value = TtsEvent.VoiceMissing(languageLabel)
            }
            else -> {
                tts.language = locale
                tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, word)
            }
        }
    }

    fun setSpeechRate(rate: Float) {
        engine?.setSpeechRate(rate)
    }

    fun setPitch(pitch: Float) {
        engine?.setPitch(pitch)
    }

    fun stop() {
        engine?.stop()
        _event.value = TtsEvent.Idle
    }

    fun release() {
        engine?.stop()
        engine?.shutdown()
        engine = null
    }

    companion object {
        /** Opens the device's own text-to-speech settings, where the
         *  learner can choose an engine and download more voices/languages —
         *  the same screen Lingora deep-links to when a voice is missing. */
        fun openSystemVoiceSettings(context: Context) {
            val intent = Intent(Settings.ACTION_TEXT_TO_SPEECH_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
