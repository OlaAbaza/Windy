package com.example.windy.repository

import com.example.windy.models.Alarm
import com.example.windy.models.domain.WeatherConditions
import com.example.windy.util.Resource
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getWeatherConditions(): Flow<List<WeatherConditions>>

    fun getAlarmList(): Flow<List<Alarm>>

    suspend fun fetchWeatherData(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): Resource

    suspend fun updateAllWeatherData(lang: String, unit: String)

    suspend fun deleteWeatherItem(timeZone: String)

    suspend fun deleteAlarmObj(id: Int)

    suspend fun insertAlarm(alarmObj: Alarm): Long

    suspend fun getObjByTimezone(timeZone: String): Resource
}