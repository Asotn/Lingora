package com.lingora.app.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response shapes for the MyMemory Translation API
 * (https://mymemory.translated.net/doc/spec.php), Lingora's default,
 * no-API-key translation backend. [matches] is the interesting part: it
 * returns several real translation-memory entries for the same query,
 * each with a quality score and a usage count, which is exactly what
 * powers Lingora's "several boxes, from common to less common" results.
 */
@Serializable
data class MyMemoryResponse(
    @SerialName("responseData") val responseData: ResponseData? = null,
    @SerialName("responseStatus") val responseStatus: Int? = null,
    @SerialName("matches") val matches: List<MatchDto> = emptyList()
)

@Serializable
data class ResponseData(
    @SerialName("translatedText") val translatedText: String = ""
)

@Serializable
data class MatchDto(
    @SerialName("segment") val segment: String = "",
    @SerialName("translation") val translation: String = "",
    @SerialName("quality") val quality: String? = null,
    @SerialName("usage-count") val usageCount: Int = 0,
    @SerialName("match") val matchScore: Double = 0.0
)
