package com.lingora.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingora.app.BuildConfig
import com.lingora.app.data.local.UserPreferencesRepository
import com.lingora.app.data.tts.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class SettingsUiState(
    val speechRate: Float = 1.0f,
    val speechPitch: Float = 1.0f,
    val appVersion: String = BuildConfig.VERSION_NAME
)

class SettingsViewModel(
    private val preferences: UserPreferencesRepository,
    private val ttsManager: TtsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.speechRate.collect { rate ->
                _uiState.update { it.copy(speechRate = rate) }
                ttsManager.setSpeechRate(rate)
            }
        }
    }

    fun setSpeechRate(rate: Float) {
        _uiState.update { it.copy(speechRate = rate) }
        ttsManager.setSpeechRate(rate)
        viewModelScope.launch { preferences.setSpeechRate(rate) }
    }

    fun setSpeechPitch(pitch: Float) {
        _uiState.update { it.copy(speechPitch = pitch) }
        ttsManager.setPitch(pitch)
    }

    fun testVoice() {
        ttsManager.speak("Hello, this is how your voice will sound.", Locale.US, "English")
    }

    fun resetOnboarding(onDone: () -> Unit) {
        viewModelScope.launch {
            preferences.resetOnboarding()
            onDone()
        }
    }
}
