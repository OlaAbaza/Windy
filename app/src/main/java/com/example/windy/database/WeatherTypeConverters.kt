package com.example.windy.database

import androidx.room.TypeConverter
import com.example.windy.models.DatabaseAlert
import com.example.windy.models.DatabaseDaily
import com.example.windy.models.DatabaseHourly
import com.example.windy.models.DatabaseWeather
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@ExperimentalSerializationApi
private val json = Json { explicitNulls = false }

@ExperimentalSerializationApi
class WeatherTypeConverters {

    @TypeConverter
    fun listHourlyToJson(value: List<DatabaseHourly>) = json.encodeToString(value)

    @TypeConverter
    fun listDailyToJson(value: List<DatabaseDaily>) = json.encodeToString(value)

    @TypeConverter
    fun listAlertToJson(value: List<DatabaseAlert>?) = json.encodeToString(value)

    @TypeConverter
    fun listWeatherToJson(value: List<DatabaseWeather>) = json.encodeToString(value)

    @TypeConverter
    fun jsonToWeatherList(value: String): List<DatabaseWeather> = json.decodeFromString(value)

    @TypeConverter
    fun jsonToHourlyList(value: String): List<DatabaseHourly> = json.decodeFromString(value)

    @TypeConverter
    fun jsonToDailyList(value: String): List<DatabaseDaily> = json.decodeFromString(value)

    @TypeConverter
    fun jsonToAlertList(value: String?): List<DatabaseAlert?>? {
        value?.let {
            return json.decodeFromString(value)
        }
        return null
    }
}