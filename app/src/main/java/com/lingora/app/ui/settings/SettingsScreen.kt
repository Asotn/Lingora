package com.lingora.app.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lingora.app.data.model.SupportedLanguages
import com.lingora.app.data.tts.TtsManager
import com.lingora.app.ui.components.AuroraBackground
import com.lingora.app.ui.components.GlassCard
import com.lingora.app.ui.theme.AuroraTeal
import com.lingora.app.ui.theme.AuroraViolet
import com.lingora.app.ui.theme.PillShape
import com.lingora.app.ui.theme.TextMuted
import com.lingora.app.ui.theme.TextPrimary
import com.lingora.app.ui.theme.TextSecondary

/**
 * Voice/audio options, the supported-language list, the learner's goal,
 * and the about/version section. Everything Lingora lets you configure
 * outside of the two language boxes on the home screen lives here.
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onRestartOnboarding: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box(Modifier.fillMaxSize()) {
        AuroraBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 32.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(Modifier.width(4.dp))
                Text("Settings", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
            }

            Spacer(Modifier.height(24.dp))

            SettingsSection(title = "Voice & Pronunciation", icon = Icons.Filled.GraphicEq) {
                Text("Speech rate", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                Slider(
                    value = state.speechRate,
                    onValueChange = viewModel::setSpeechRate,
                    valueRange = 0.5f..2f,
                    colors = SliderDefaults.colors(thumbColor = AuroraTeal, activeTrackColor = AuroraTeal)
                )
                Spacer(Modifier.height(8.dp))
                Text("Pitch", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                Slider(
                    value = state.speechPitch,
                    onValueChange = viewModel::setSpeechPitch,
                    valueRange = 0.5f..2f,
                    colors = SliderDefaults.colors(thumbColor = AuroraViolet, activeTrackColor = AuroraViolet)
                )
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = viewModel::testVoice,
                    shape = PillShape,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Test voice")
                }
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = { TtsManager.openSystemVoiceSettings(context) }) {
                    Text("Manage voices on this device")
                }
                Text(
                    "Opens your device's text-to-speech settings, where you can pick an engine and download more languages. If a word's voice isn't installed yet, Lingora offers this same shortcut right when you tap play.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }

            Spacer(Modifier.height(16.dp))

            SettingsSection(title = "Languages", icon = Icons.Filled.Language) {
                Text(
                    "Lingora currently translates between ${SupportedLanguages.all.size} languages.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Pick your two languages any time from the two boxes on the home screen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }

            Spacer(Modifier.height(16.dp))

            SettingsSection(title = "Learning goal", icon = Icons.Filled.Flag) {
                Text(
                    "Change why you're learning or update your level any time.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { viewModel.resetOnboarding(onRestartOnboarding) },
                    shape = PillShape,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Redo setup")
                }
            }

            Spacer(Modifier.height(16.dp))

            SettingsSection(title = "About", icon = Icons.Filled.Info) {
                Text("Version ${state.appVersion}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Spacer(Modifier.height(4.dp))
                Text("Lingora is open source.", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = AuroraTeal, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}
