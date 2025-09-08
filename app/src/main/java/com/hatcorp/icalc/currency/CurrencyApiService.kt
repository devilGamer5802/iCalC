package com.hatcorp.icalc.currency

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

private const val BASE_URL = "https://api.frankfurter.app/"

// A lenient Json configuration is helpful for APIs
private val json = Json { ignoreUnknownKeys = true }

private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface CurrencyApiService {
    // Fetches the latest rates with EUR as the base currency
    @GET("latest")
    suspend fun getLatestRates(): CurrencyResponse
}

// Singleton object to provide an instance of our service
object CurrencyApi {
    val retrofitService: CurrencyApiService by lazy {
        retrofit.create(CurrencyApiService::class.java)
    }
}