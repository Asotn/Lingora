package com.lingora.app.data.repository

import com.lingora.app.data.model.Language
import com.lingora.app.data.model.TranslationEntry
import com.lingora.app.data.model.TranslationOutcome
import com.lingora.app.data.model.TranslationRelevance
import com.lingora.app.data.remote.MyMemoryApiService
import com.lingora.app.data.remote.NetworkModule

/**
 * Turns one word or phrase into a [TranslationOutcome]: a primary
 * translation plus every distinct alternative worth its own card, ranked
 * by how often real translators have used them. Nothing here is guessed —
 * every entry, quality label, and example sentence comes straight from the
 * backend's own translation-memory data.
 */
class TranslationRepository(
    private val api: MyMemoryApiService = NetworkModule.myMemoryApi
) {

    suspend fun translate(
        word: String,
        source: Language,
        target: Language
    ): Result<TranslationOutcome> = runCatching {
        val response = api.translate(
            text = word,
            langPair = "${source.code}|${target.code}"
        )

        check(response.responseStatus == null || response.responseStatus == 200) {
            "Translation service returned status ${response.responseStatus}."
        }

        val primary = response.responseData?.translatedText
            ?.trim()
            ?.ifBlank { null }
            ?: word

        val alternatives = response.matches
            .filter { it.translation.isNotBlank() }
            .distinctBy { it.translation.trim().lowercase() }
            .sortedByDescending { it.usageCount + (it.matchScore * 100).toInt() }
            .take(6)
            .map { match ->
                val segment = match.segment.trim()
                val looksLikeExampleSentence =
                    segment.split(" ").size > 3 && !segment.equals(word, ignoreCase = true)

                TranslationEntry(
                    text = match.translation.trim(),
                    exampleSource = segment.takeIf { looksLikeExampleSentence },
                    relevance = if (match.usageCount >= 3 || match.matchScore >= 0.85) {
                        TranslationRelevance.COMMON
                    } else {
                        TranslationRelevance.LESS_COMMON
                    }
                )
            }

        val hasPrimary = alternatives.any { it.text.equals(primary, ignoreCase = true) }
        val entries = if (hasPrimary) {
            alternatives
        } else {
            listOf(TranslationEntry(primary, null, TranslationRelevance.COMMON)) + alternatives
        }

        TranslationOutcome(
            query = word,
            primary = primary,
            entries = entries.ifEmpty {
                listOf(TranslationEntry(primary, null, TranslationRelevance.COMMON))
            }
        )
    }
}
