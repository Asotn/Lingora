package com.lingora.app.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Wires up the HTTP client Lingora uses to reach its translation backend.
 *
 * MyMemory is free and needs no API key, which makes it a good default for
 * an open-source project, but its anonymous quota is limited (roughly a
 * few thousand words per day per IP address). To raise that quota, or to
 * swap in a different provider entirely (Google Cloud Translation, DeepL,
 * Azure Translator...), this is the only file that needs to change —
 * [TranslationRepository] talks to [MyMemoryApiService] as an interface
 * and does not know or care which backend answers it.
 */
object NetworkModule {

    private const val BASE_URL = "https://api.mymemory.translated.net/"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val myMemoryApi: MyMemoryApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MyMemoryApiService::class.java)
    }
}
