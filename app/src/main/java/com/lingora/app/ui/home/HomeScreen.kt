package com.lingora.app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lingora.app.data.tts.TtsManager
import com.lingora.app.ui.components.AuroraBackground
import com.lingora.app.ui.components.LanguageChip
import com.lingora.app.ui.components.LanguagePickerSheet
import com.lingora.app.ui.components.TranslationResultCard
import com.lingora.app.ui.components.WordInputField
import com.lingora.app.ui.theme.AuroraMagenta
import com.lingora.app.ui.theme.AuroraNight
import com.lingora.app.ui.theme.AuroraTeal
import com.lingora.app.ui.theme.PillShape
import com.lingora.app.ui.theme.TextMuted
import com.lingora.app.ui.theme.TextPrimary
import com.lingora.app.ui.theme.TextSecondary

private enum class PickerTarget { SOURCE, TARGET }

/**
 * The main screen. Two mandatory language boxes at the top (with a swap
 * shortcut between them), the word box below, and every distinct
 * translation shown in its own card underneath — each with a speaker icon
 * that pronounces it in the target language.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var pickerTarget by remember { mutableStateOf<PickerTarget?>(null) }

    Box(Modifier.fillMaxSize()) {
        AuroraBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lingora", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = TextSecondary)
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LanguageChip(
                    label = "I speak",
                    language = state.sourceLanguage,
                    onClick = { pickerTarget = PickerTarget.SOURCE },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { viewModel.swapLanguages() }) {
                    Icon(Icons.Filled.SwapHoriz, contentDescription = "Swap languages", tint = AuroraTeal)
                }
                LanguageChip(
                    label = "I'm learning",
                    language = state.targetLanguage,
                    onClick = { pickerTarget = PickerTarget.TARGET },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            WordInputField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                onSubmit = viewModel::translate
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = viewModel::translate,
                enabled = state.query.isNotBlank() && !state.isLoading,
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(containerColor = AuroraTeal, contentColor = AuroraNight),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(if (state.isLoading) "Translating…" else "Translate", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(20.dp))

            state.errorMessage?.let { message ->
                Text(message, color = AuroraMagenta, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))
            }

            val outcome = state.outcome
            if (outcome == null && !state.isLoading) {
                Text(
                    "Type a word above and translate it into ${state.targetLanguage.englishName} to see every way to use it.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            } else if (outcome != null) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(outcome.entries, key = { it.text }) { entry ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
                        ) {
                            TranslationResultCard(
                                entry = entry,
                                isSpeaking = state.speakingWord == entry.text,
                                onSpeak = { viewModel.speak(entry.text) }
                            )
                        }
                    }
                }
            }
        }
    }

    pickerTarget?.let { target ->
        LanguagePickerSheet(
            title = if (target == PickerTarget.SOURCE) "I speak" else "I'm learning",
            onDismiss = { pickerTarget = null },
            onSelect = { language ->
                if (target == PickerTarget.SOURCE) viewModel.setSourceLanguage(language) else viewModel.setTargetLanguage(language)
            }
        )
    }

    state.voiceMissingLanguage?.let { language ->
        VoiceMissingDialog(language = language, onDismiss = viewModel::dismissVoiceMissingNotice)
    }
}

@Composable
private fun VoiceMissingDialog(language: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Voice not installed") },
        text = { Text("Your device doesn't have a $language voice installed yet. Open your device's voice settings to add one.") },
        confirmButton = {
            TextButton(onClick = {
                TtsManager.openSystemVoiceSettings(context)
                onDismiss()
            }) { Text("Open Voice Settings") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Not Now") }
        }
    )
}
