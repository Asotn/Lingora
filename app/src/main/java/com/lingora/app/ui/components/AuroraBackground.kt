package com.lingora.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lingora.app.ui.theme.AuroraBlue
import com.lingora.app.ui.theme.AuroraMagenta
import com.lingora.app.ui.theme.AuroraNight
import com.lingora.app.ui.theme.AuroraTeal
import com.lingora.app.ui.theme.AuroraViolet

/**
 * A slowly drifting field of soft aurora-colored light behind the app's
 * content. Built from layered, animated radial gradients rather than a
 * real Gaussian blur, so it renders identically — no minimum API level —
 * on every device Lingora supports.
 */
@Composable
fun AuroraBackground(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "aurora")

    val driftOne by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(26000, easing = LinearEasing), RepeatMode.Reverse),
        label = "driftOne"
    )
    val driftTwo by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(34000, easing = LinearEasing), RepeatMode.Reverse),
        label = "driftTwo"
    )
    val driftThree by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(21000, easing = LinearEasing), RepeatMode.Reverse),
        label = "driftThree"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AuroraNight)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawAuroraBlob(Offset(0.15f + 0.15f * driftOne, 0.18f + 0.08f * driftTwo), AuroraViolet, 0.55f)
            drawAuroraBlob(Offset(0.85f - 0.15f * driftTwo, 0.28f + 0.10f * driftThree), AuroraTeal, 0.50f)
            drawAuroraBlob(Offset(0.30f + 0.20f * driftThree, 0.85f - 0.10f * driftOne), AuroraMagenta, 0.50f)
            drawAuroraBlob(Offset(0.75f - 0.10f * driftOne, 0.80f + 0.05f * driftTwo), AuroraBlue, 0.45f)
        }
    }
}

private fun DrawScope.drawAuroraBlob(centerFraction: Offset, color: Color, radiusFraction: Float) {
    val center = Offset(size.width * centerFraction.x, size.height * centerFraction.y)
    val radius = size.maxDimension * radiusFraction
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.35f), color.copy(alpha = 0f)),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
}
