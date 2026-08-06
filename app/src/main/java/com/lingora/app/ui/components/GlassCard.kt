package com.lingora.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.lingora.app.ui.theme.CardShape
import com.lingora.app.ui.theme.GlassBorder
import com.lingora.app.ui.theme.GlassBorderFaint
import com.lingora.app.ui.theme.GlassFill

/** The one visual building block behind almost every surface in Lingora:
 *  a translucent, soft-bordered "pane of glass" floating over the aurora
 *  background. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = CardShape,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(GlassFill)
            .border(1.dp, Brush.linearGradient(listOf(GlassBorder, GlassBorderFaint)), shape)
            .padding(contentPadding)
    ) {
        content()
    }
}
