package com.example.windy.network

import com.example.windy.BuildConfig
import com.example.windy.models.NetworkWeatherConditions
import retrofit2.http.GET
import retrofit2.http.Query


enum class WeatherApiFilter(val value: String) {
    ARIBIC("ar"),
    ENGLISH("en"),
    METRIC("metric"),
    IMPERIAL("imperial"),
    STANDARD("standard")
}

interface WeatherApiService {
    @GET("data/2.5/onecall")
    suspend fun getCurrentWeatherByLatLng(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("exclude") exclude: String = "minutely",
        @Query("appid") appid: String = BuildConfig.OPENWEATHER_API_KEY
    ): NetworkWeatherConditions
}