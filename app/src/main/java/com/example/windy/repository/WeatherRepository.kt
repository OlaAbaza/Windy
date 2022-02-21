package com.example.windy.repository

import com.example.windy.database.WeatherDatabase
import com.example.windy.models.Alarm
import com.example.windy.models.asDatabaseModel
import com.example.windy.models.asDomainModel
import com.example.windy.network.WeatherApiService
import com.example.windy.util.Resource
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherDatabase: WeatherDatabase,
    private val weatherApiService: WeatherApiService
) {

    val weatherConditions by lazy {
        weatherDatabase.weatherDao().getWeatherConditions().map {
            it.asDomainModel()
        }
    }

    val alarmList by lazy {
        weatherDatabase.alarmDao().getAllAlarms()
    }

    suspend fun fetchWeatherData(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): Resource {
        return try {
            val result = weatherApiService.getCurrentWeatherByLatLng(
                lat,
                lon,
                lang,
                unit
            ).asDatabaseModel()
            weatherDatabase.weatherDao().insert(result)

            Resource.Success(result.asDomainModel())

        } catch (cause: Throwable) {
            cause.printStackTrace()
            Resource.Error(cause.message.toString())
        }
    }

    suspend fun updateAllWeatherData(lang: String, unit: String) {
        val items = weatherDatabase.weatherDao().getAllWeatherConditions()
        for (item in items) {
            try {
                fetchWeatherData(
                    item.lat,
                    item.lon,
                    lang,
                    unit
                )
            } catch (cause: Throwable) {
                Timber.i("failed %s", cause.message)
            }
        }
    }

    suspend fun deleteWeatherItem(timeZone: String) {
        weatherDatabase.weatherDao().deleteWeatherByTimezone(timeZone)
    }

    suspend fun deleteAlarmObj(id: Int) {
        weatherDatabase.alarmDao().deleteAlarmObj(id)
    }

    suspend fun insertAlarm(alarmObj: Alarm): Long {
        return weatherDatabase.alarmDao().insertAlarm(alarmObj)
    }

    suspend fun getObjByTimezone(timeZone: String): Resource.Success {
        return Resource.Success(
            weatherDatabase.weatherDao().getObjByTimezone(timeZone)
                .asDomainModel()
        )
    }
}