package com.lingora.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lingora.app.data.model.Language
import com.lingora.app.data.model.SupportedLanguages
import com.lingora.app.ui.theme.AuroraNightElevated
import com.lingora.app.ui.theme.FieldShape
import com.lingora.app.ui.theme.GlassBorder
import com.lingora.app.ui.theme.GlassBorderFaint
import com.lingora.app.ui.theme.GlassFill
import com.lingora.app.ui.theme.PillShape
import com.lingora.app.ui.theme.TextMuted
import com.lingora.app.ui.theme.TextPrimary
import com.lingora.app.ui.theme.TextSecondary

/** One of the two mandatory language boxes at the top of the home screen. */
@Composable
fun LanguageChip(
    label: String,
    language: Language,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(PillShape)
            .background(GlassFill)
            .border(1.dp, GlassBorder, PillShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextMuted)
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                language.englishName,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.width(6.dp))
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/** A searchable full list of every supported language, opened by tapping
 *  either language box. Selecting a language and (re)confirming it here is
 *  what drives translation into that language. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePickerSheet(
    title: String,
    onDismiss: () -> Unit,
    onSelect: (Language) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        if (query.isBlank()) {
            SupportedLanguages.all
        } else {
            SupportedLanguages.all.filter {
                it.englishName.contains(query, ignoreCase = true) ||
                    it.nativeName.contains(query, ignoreCase = true)
            }
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = AuroraNightElevated) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search languages") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = FieldShape,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.heightIn(max = 420.dp)) {
                items(filtered, key = { it.code }) { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(language)
                                onDismiss()
                            }
                            .padding(vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.fillMaxWidth()) {
                            Text(language.englishName, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                            Text(language.nativeName, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                    HorizontalDivider(color = GlassBorderFaint)
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
