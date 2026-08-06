package com.lingora.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.lingora.app.data.model.TranslationEntry
import com.lingora.app.data.model.TranslationRelevance
import com.lingora.app.ui.theme.AuroraTeal
import com.lingora.app.ui.theme.AuroraViolet
import com.lingora.app.ui.theme.TextPrimary
import com.lingora.app.ui.theme.TextSecondary

/** One "box" for one distinct translation: the word itself, how common it
 *  is, when it shows up in real usage, and a button to hear it spoken in
 *  the target language. */
@Composable
fun TranslationResultCard(
    entry: TranslationEntry,
    isSpeaking: Boolean,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
    val speakerScale by animateFloatAsState(if (isSpeaking) 1.15f else 1f, label = "speakerScale")

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.weight(1f)) {
                    Text(entry.text, style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                    Spacer(Modifier.height(4.dp))
                    RelevanceBadge(entry.relevance)
                }
                IconButton(onClick = onSpeak, modifier = Modifier.scale(speakerScale)) {
                    Icon(Icons.Filled.VolumeUp, contentDescription = "Play pronunciation", tint = AuroraTeal)
                }
            }
            entry.exampleSource?.let { example ->
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Used in: \u201C$example\u201D",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun RelevanceBadge(relevance: TranslationRelevance) {
    val (label, color) = when (relevance) {
        TranslationRelevance.COMMON -> "Common usage" to AuroraTeal
        TranslationRelevance.LESS_COMMON -> "Less common" to AuroraViolet
    }
    Text(text = label, style = MaterialTheme.typography.labelMedium, color = color)
}
