package com.example.windy.models.domain

import com.example.windy.util.dateFormat
import com.example.windy.util.timeFormat

data class WeatherConditions(
    val alerts: List<Alert>?,
    val current: Current?,
    val daily: List<Daily>?,
    val hourly: List<Hourly>?,
    val lat: Double,
    val lon: Double,
    val timezone: String
)

data class Alert(
    val description: String?,
    val end: Int?,
    val event: String?,
    val senderName: String?,
    val start: Int?,
    val tags: List<String>?
) {
    val startTime: String?
        get() = start?.let { timeFormat(it) }
    val endTime: String?
        get() = end?.let { timeFormat(it) }
}

data class Current(
    val clouds: Int?,
    val feelsLike: Double?,
    val dt: Int?,
    val humidity: Int?,
    val pressure: Int?,
    val sunrise: Int?,
    val sunset: Int?,
    val temp: Double?,
    val uvi: Double?,
    val visibility: Int?,
    val weather: List<Weather>?,
    val windSpeed: Double?
) {
    val tempStr: String
        get() = temp?.toInt().toString()
    val date: String?
        get() = dt?.let { dateFormat(it) }
    val time: String?
        get() = dt?.let { timeFormat(it) }
    val sunriseTime: String?
        get() = dt?.let { timeFormat(it) }
    val sunsetTime: String?
        get() = dt?.let { timeFormat(it) }
}

data class Daily(
    val clouds: Int?,
    val feelsLike: FeelsLike?,
    val humidity: Int?,
    val pressure: Int?,
    val rain: Double?,
    val sunrise: Int?,
    val dt: Int?,
    val sunset: Int?,
    val temp: Temp?,
    val uvi: Double?,
    val weather: List<Weather>?,
    val windSpeed: Double?
) {
    val date: String?
        get() = dt?.let { dateFormat(it) }
}

data class FeelsLike(
    val day: Double?,
    val eve: Double?,
    val morn: Double?,
    val night: Double?
)

data class Hourly(
    val clouds: Int?,
    val feelsLike: Double?,
    val humidity: Int?,
    val pressure: Int?,
    val dt: Int?,
    val temp: Double?,
    val uvi: Double?,
    val visibility: Int?,
    val weather: List<Weather>?,
    val windSpeed: Double?
) {
    val tempStr: String
        get() = temp?.toInt().toString()

    val time: String?
        get() = dt?.let { timeFormat(it) }
}

data class Temp(
    val day: Double?,
    val eve: Double?,
    val max: Double?,
    val min: Double?,
    val morn: Double?,
    val night: Double?
)

data class Weather(
    val description: String?,
    val icon: String?,
    val id: Int?,
    val main: String?
)
