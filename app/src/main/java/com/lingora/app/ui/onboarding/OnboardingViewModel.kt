package com.lingora.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingora.app.data.local.UserPreferencesRepository
import com.lingora.app.data.model.LearningPurpose
import com.lingora.app.data.model.ProficiencyLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val preferences: UserPreferencesRepository
) : ViewModel() {

    private val _selectedPurpose = MutableStateFlow<LearningPurpose?>(null)
    val selectedPurpose: StateFlow<LearningPurpose?> = _selectedPurpose.asStateFlow()

    private val _selectedLevel = MutableStateFlow<ProficiencyLevel?>(null)
    val selectedLevel: StateFlow<ProficiencyLevel?> = _selectedLevel.asStateFlow()

    fun selectPurpose(purpose: LearningPurpose) {
        _selectedPurpose.value = purpose
    }

    fun selectLevel(level: ProficiencyLevel) {
        _selectedLevel.value = level
    }

    fun finishOnboarding(onDone: () -> Unit) {
        val purpose = _selectedPurpose.value ?: return
        val level = _selectedLevel.value ?: return
        viewModelScope.launch {
            preferences.setPurpose(purpose.name)
            preferences.setLevel(level.name)
            preferences.completeOnboarding()
            onDone()
        }
    }
}
