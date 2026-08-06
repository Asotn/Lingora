package com.lingora.app.data.model

import java.util.Locale

/**
 * A single language Lingora can translate into and, where the device has a
 * voice installed for it, pronounce aloud.
 *
 * @param code the code sent to the translation backend (mostly ISO 639-1,
 *   with a couple of regional exceptions such as "zh-CN").
 * @param englishName the language's name, shown in the language pickers.
 * @param nativeName the language's name written in itself.
 * @param ttsLocale the [Locale] requested from the device's text-to-speech
 *   engine. This is intentionally separate from [code]: some languages use
 *   a different tag for translation than for the voice engine (Filipino is
 *   "tl" for translation but "fil" for speech, for example).
 */
data class Language(
    val code: String,
    val englishName: String,
    val nativeName: String,
    val ttsLocale: Locale
)
