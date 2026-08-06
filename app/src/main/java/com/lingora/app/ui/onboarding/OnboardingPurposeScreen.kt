package com.lingora.app.ui.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.lingora.app.data.model.LearningPurpose
import com.lingora.app.ui.components.AuroraBackground
import com.lingora.app.ui.components.GlassCard
import com.lingora.app.ui.theme.AuroraNight
import com.lingora.app.ui.theme.AuroraTeal
import com.lingora.app.ui.theme.PillShape
import com.lingora.app.ui.theme.TextPrimary
import com.lingora.app.ui.theme.TextSecondary

/** The very first screen: why is the learner here? Answering this shapes
 *  nothing about the UI itself, but is stored so a future version of
 *  Lingora can tailor which words and examples it surfaces first. */
@Composable
fun OnboardingPurposeScreen(
    viewModel: OnboardingViewModel,
    onNext: () -> Unit
) {
    val selected by viewModel.selectedPurpose.collectAsState()

    Box(Modifier.fillMaxSize()) {
        AuroraBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 72.dp, bottom = 24.dp)
        ) {
            Text("Welcome to Lingora", style = MaterialTheme.typography.displayLarge, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            Text(
                "What brings you here? This helps us shape the words we surface for you.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(Modifier.height(28.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(LearningPurpose.entries) { purpose ->
                    val scale by animateFloatAsState(if (purpose == selected) 1.02f else 1f, label = "purposeScale")
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(scale)
                            .clickable { viewModel.selectPurpose(purpose) }
                    ) {
                        Column {
                            Text(
                                purpose.displayName,
                                style = MaterialTheme.typography.titleLarge,
                                color = if (purpose == selected) AuroraTeal else TextPrimary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(purpose.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onNext,
                enabled = selected != null,
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(containerColor = AuroraTeal, contentColor = AuroraNight),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Text("Continue", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
