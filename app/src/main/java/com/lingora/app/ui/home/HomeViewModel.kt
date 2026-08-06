package com.lingora.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingora.app.data.local.UserPreferencesRepository
import com.lingora.app.data.model.Language
import com.lingora.app.data.model.SupportedLanguages
import com.lingora.app.data.model.TranslationOutcome
import com.lingora.app.data.repository.TranslationRepository
import com.lingora.app.data.tts.TtsEvent
import com.lingora.app.data.tts.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val sourceLanguage: Language = SupportedLanguages.findByCode("en") ?: SupportedLanguages.all.first(),
    val targetLanguage: Language = SupportedLanguages.findByCode("es") ?: SupportedLanguages.all[1],
    val query: String = "",
    val isLoading: Boolean = false,
    val outcome: TranslationOutcome? = null,
    val errorMessage: String? = null,
    val speakingWord: String? = null,
    val voiceMissingLanguage: String? = null
)

/**
 * Drives the home screen: the two mandatory language boxes, the word
 * input, and the translated results. Selecting a language after typing a
 * word re-translates automatically, once a first search has happened.
 */
class HomeViewModel(
    private val repository: TranslationRepository,
    private val preferences: UserPreferencesRepository,
    private val ttsManager: TtsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val sourceCode = preferences.sourceLanguageCode.first()
            val targetCode = preferences.targetLanguageCode.first()
            _uiState.update { state ->
                state.copy(
                    sourceLanguage = sourceCode?.let(SupportedLanguages::findByCode) ?: state.sourceLanguage,
                    targetLanguage = targetCode?.let(SupportedLanguages::findByCode) ?: state.targetLanguage
                )
            }
        }
        viewModelScope.launch {
            ttsManager.event.collect { event ->
                when (event) {
                    is TtsEvent.Speaking -> _uiState.update { it.copy(speakingWord = event.word) }
                    is TtsEvent.Idle -> _uiState.update { it.copy(speakingWord = null) }
                    is TtsEvent.VoiceMissing -> _uiState.update { it.copy(voiceMissingLanguage = event.language) }
                    is TtsEvent.Unavailable -> Unit
                }
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        if (newQuery.length <= 50) _uiState.update { it.copy(query = newQuery) }
    }

    fun setSourceLanguage(language: Language) {
        _uiState.update { it.copy(sourceLanguage = language) }
        viewModelScope.launch { preferences.setSourceLanguage(language.code) }
        retranslateIfActive()
    }

    fun setTargetLanguage(language: Language) {
        _uiState.update { it.copy(targetLanguage = language) }
        viewModelScope.launch { preferences.setTargetLanguage(language.code) }
        retranslateIfActive()
    }

    fun swapLanguages() {
        val state = _uiState.value
        _uiState.update { it.copy(sourceLanguage = state.targetLanguage, targetLanguage = state.sourceLanguage) }
        viewModelScope.launch {
            preferences.setSourceLanguage(state.targetLanguage.code)
            preferences.setTargetLanguage(state.sourceLanguage.code)
        }
        retranslateIfActive()
    }

    fun translate() = performTranslation()

    private fun retranslateIfActive() {
        if (_uiState.value.outcome != null) performTranslation()
    }

    private fun performTranslation() {
        val state = _uiState.value
        val word = state.query.trim()
        if (word.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.translate(word, state.sourceLanguage, state.targetLanguage)
                .onSuccess { outcome ->
                    _uiState.update { it.copy(isLoading = false, outcome = outcome) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Couldn't reach the translation service."
                        )
                    }
                }
        }
    }

    fun speak(word: String) {
        val target = _uiState.value.targetLanguage
        ttsManager.speak(word, target.ttsLocale, target.englishName)
    }

    fun dismissVoiceMissingNotice() {
        _uiState.update { it.copy(voiceMissingLanguage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }
}
