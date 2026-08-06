package com.lingora.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "lingora_preferences")

/**
 * Everything Lingora remembers between sessions: whether onboarding is
 * done, why and at what level the learner is studying, their last-used
 * language pair, and their preferred speech rate. All of it is small,
 * non-sensitive app preferences — never words the learner has looked up.
 */
class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val PURPOSE = stringPreferencesKey("purpose")
        val LEVEL = stringPreferencesKey("level")
        val SOURCE_LANGUAGE = stringPreferencesKey("source_language")
        val TARGET_LANGUAGE = stringPreferencesKey("target_language")
        val SPEECH_RATE = stringPreferencesKey("speech_rate")
    }

    val isOnboardingComplete: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.ONBOARDING_DONE] ?: false }

    val sourceLanguageCode: Flow<String?> =
        context.dataStore.data.map { it[Keys.SOURCE_LANGUAGE] }

    val targetLanguageCode: Flow<String?> =
        context.dataStore.data.map { it[Keys.TARGET_LANGUAGE] }

    val speechRate: Flow<Float> = context.dataStore.data.map {
        it[Keys.SPEECH_RATE]?.toFloatOrNull() ?: 1.0f
    }

    suspend fun setPurpose(purpose: String) {
        context.dataStore.edit { it[Keys.PURPOSE] = purpose }
    }

    suspend fun setLevel(level: String) {
        context.dataStore.edit { it[Keys.LEVEL] = level }
    }

    suspend fun completeOnboarding() {
        context.dataStore.edit { it[Keys.ONBOARDING_DONE] = true }
    }

    suspend fun resetOnboarding() {
        context.dataStore.edit { it[Keys.ONBOARDING_DONE] = false }
    }

    suspend fun setSourceLanguage(code: String) {
        context.dataStore.edit { it[Keys.SOURCE_LANGUAGE] = code }
    }

    suspend fun setTargetLanguage(code: String) {
        context.dataStore.edit { it[Keys.TARGET_LANGUAGE] = code }
    }

    suspend fun setSpeechRate(rate: Float) {
        context.dataStore.edit { it[Keys.SPEECH_RATE] = rate.toString() }
    }
}
