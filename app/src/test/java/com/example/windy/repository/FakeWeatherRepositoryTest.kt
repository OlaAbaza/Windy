package com.example.windy.repository

import com.example.windy.models.Alarm
import com.example.windy.models.DatabaseCurrent
import com.example.windy.models.DatabaseWeatherConditions
import com.example.windy.models.asDomainModel
import com.example.windy.models.domain.WeatherConditions
import com.example.windy.util.Resource
import kotlinx.coroutines.flow.Flow


class FakeWeatherRepositoryTest : WeatherRepository {
    private var shouldReturnError = false
    private var weatherConditions =
        DatabaseWeatherConditions(
            DatabaseCurrent(
                1646338897,
                1631.1327,
                1646352860,
                297,
                89,
                1020, 24,
                275.86,
                5.13,
                0,
                emptyList(), 2.238
            ), emptyList(), emptyList(),
            emptyList(), 12.3, .12, ""
        ).asDomainModel()

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override fun getWeatherConditions(): Flow<List<WeatherConditions>> {
        TODO("Not yet implemented")
    }

    override fun getAlarmList(): Flow<List<Alarm>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchWeatherData(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): Resource {
        return if (shouldReturnError) {
            Resource.Error("Test exception")
        } else {
            Resource.Success(weatherConditions)
        }
    }

    override suspend fun updateAllWeatherData(lang: String, unit: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWeatherItem(timeZone: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarmObj(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlarm(alarmObj: Alarm): Long {
        TODO("Not yet implemented")
    }

    override suspend fun getObjByTimezone(timeZone: String): Resource {
        return if (shouldReturnError) {
            Resource.Error("Test exception")
        } else {
            Resource.Success(weatherConditions)
        }
    }

}