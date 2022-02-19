package com.example.windy.network

import com.example.windy.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//database migration
//diffutlis
//applection vs context
//  flags = FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//3094a4552f97d08e731f30d924532c3
//https://api.openweathermap.org/data/2.5/onecall?lat=33.441792&lon=-94.037689&lang=ar&units=standard&exclude=minutely&appid=31cceaa80d19afe5ea2ec0f5b270311b

private const val BASE_URL = "https://api.openweathermap.org/"

enum class WeatherApiFilter(val value: String) {
    ARIBIC("ar"),
    ENGLISH("en"),
    METRIC("metric"),
    IMPERIAL("imperial"),
    STANDARD("standard")
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL).build()

object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
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