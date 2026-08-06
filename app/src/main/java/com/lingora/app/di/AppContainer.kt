package com.lingora.app.di

import android.content.Context
import com.lingora.app.data.local.UserPreferencesRepository
import com.lingora.app.data.repository.TranslationRepository
import com.lingora.app.data.tts.TtsManager

/**
 * Lingora's dependencies are few enough that a small hand-written
 * container is clearer than pulling in a DI framework. One instance lives
 * on [com.lingora.app.LingoraApplication] for the whole process lifetime.
 */
class AppContainer(context: Context) {
    val preferences = UserPreferencesRepository(context.applicationContext)
    val translationRepository = TranslationRepository()
    val ttsManager = TtsManager(context.applicationContext).apply { initialize() }
}
