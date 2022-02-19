package com.example.windy.repository

import androidx.lifecycle.Transformations
import com.example.windy.database.Alarm
import com.example.windy.database.WeatherDatabase
import com.example.windy.database.asDomainModel
import com.example.windy.domain.WeatherConditions
import com.example.windy.network.WeatherApi
import com.example.windy.network.asDatabaseModel
import timber.log.Timber


class WeatherRepository(private val weatherDatabase: WeatherDatabase) {

    val weatherConditions by lazy {
        Transformations.map(weatherDatabase.weatherDao().getWeatherConditions()) {
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
    ): WeatherConditions? {
        try {
            val result = WeatherApi.retrofitService.getCurrentWeatherByLatLng(
                lat,
                lon,
                lang,
                unit
            )

            weatherDatabase.weatherDao().insert(result.asDatabaseModel())

            return result.asDatabaseModel().asDomainModel()

        } catch (cause: Throwable) {
            Timber.i("failed %s", cause.message)
            //throw TitleRefreshError("Unable to refresh title", cause)
        }
        return null
    }

    suspend fun updateAllWeatherData(lang: String, unit: String) {
        val items = weatherDatabase.weatherDao().getAllWeatherConditions()
        for (item in items) {
            try {
                val result = WeatherApi.retrofitService.getCurrentWeatherByLatLng(
                    item.lat,
                    item.lon,
                    lang,
                    unit
                )
                weatherDatabase.weatherDao().insert(result.asDatabaseModel())
            } catch (cause: Throwable) {
                Timber.i("failed %s", cause.message)
                //throw TitleRefreshError("Unable to refresh title", cause)
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

    suspend fun getObjByTimezone(timeZone: String): WeatherConditions {
        return (weatherDatabase.weatherDao().getObjByTimezone(timeZone)).asDomainModel()
    }
}