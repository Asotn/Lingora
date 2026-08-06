package com.lingora.app.data.model

/** How often this particular translation shows up in real usage, derived
 *  from the backend's own match quality and usage statistics — never
 *  guessed or invented locally. */
enum class TranslationRelevance { COMMON, LESS_COMMON }

/** One translated word or phrase, shown in its own card in the results
 *  list so learners can see every distinct way to say something. */
data class TranslationEntry(
    val text: String,
    val exampleSource: String?,
    val relevance: TranslationRelevance
)

/** The full result of translating [query] from one language into another:
 *  the primary translation plus every alternative worth showing. */
data class TranslationOutcome(
    val query: String,
    val primary: String,
    val entries: List<TranslationEntry>
)
