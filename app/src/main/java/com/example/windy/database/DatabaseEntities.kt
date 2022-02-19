package com.example.windy.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.windy.domain.*
import kotlinx.serialization.Serializable


@Entity(tableName = "WeatherConditions")
data class DatabaseWeatherConditions(
    @Embedded
    val current: DatabaseCurrent?,
    val daily: List<DatabaseDaily>?,
    val hourly: List<DatabaseHourly>?,
    val alerts: List<DatabaseAlert>?,
    val lat: Double,
    val lon: Double,
    @PrimaryKey
    val timezone: String
)

@Serializable
data class DatabaseAlert(
    val description: String?,
    val end: Int?,
    val event: String?,
    val senderName: String?,
    val start: Int?,
    val tags: List<String>?
)

data class DatabaseCurrent(
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
    val weather: List<DatabaseWeather>?,
    val windSpeed: Double?
)

@Serializable
data class DatabaseDaily(
    val clouds: Int?,
    val feelsLike: DatabaseFeelsLike?,
    val humidity: Int?,
    val pressure: Int?,
    val dt: Int?,
    val rain: Double?,
    val sunrise: Int?,
    val sunset: Int?,
    val temp: DatabaseTemp?,
    val uvi: Double?,
    val weather: List<DatabaseWeather>?,
    val windSpeed: Double?
)

@Serializable
data class DatabaseFeelsLike(
    val day: Double?,
    val eve: Double?,
    val morn: Double?,
    val night: Double?
)

@Serializable
data class DatabaseHourly(
    val clouds: Int?,
    val feelsLike: Double?,
    val humidity: Int?,
    val pressure: Int?,
    val dt: Int?,
    val temp: Double?,
    val uvi: Double?,
    val visibility: Int?,
    val weather: List<DatabaseWeather>?,
    val windSpeed: Double?
)

@Serializable
data class DatabaseTemp(
    val day: Double?,
    val eve: Double?,
    val max: Double?,
    val min: Double?,
    val morn: Double?,
    val night: Double?
)

@Serializable
data class DatabaseWeather(
    val description: String?,
    val icon: String?,
    val id: Int?,
    val main: String?
)

fun List<DatabaseWeatherConditions>.asDomainModel() =
    map { it ->
        WeatherConditions(
            alerts = it.alerts?.map { it.asDomainModel() },
            current = it.current?.asDomainModel(),
            daily = it.daily?.map { it.asDomainModel() },
            hourly = it.hourly?.map { it.asDomainModel() },
            lat = it.lat,
            lon = it.lon,
            timezone = it.timezone
        )
    }

fun DatabaseWeatherConditions.asDomainModel() =
    WeatherConditions(
        current = current?.asDomainModel(),
        daily = daily?.map { it.asDomainModel() },
        hourly = hourly?.map { it.asDomainModel() },
        alerts = alerts?.map { it.asDomainModel() },
        lat = lat,
        lon = lon,
        timezone = timezone
    )


fun DatabaseAlert.asDomainModel() = Alert(
    description, end, event, senderName, start, tags
)

fun DatabaseCurrent.asDomainModel() = Current(
    clouds = clouds,
    feelsLike = feelsLike,
    humidity = humidity,
    pressure = pressure,
    sunrise = sunrise,
    sunset = sunset,
    temp = temp,
    uvi = uvi,
    visibility = visibility,
    weather = weather?.map { it.asDomainModel() },
    windSpeed = windSpeed,
    dt = dt
)

fun DatabaseHourly.asDomainModel() = Hourly(
    clouds = clouds,
    feelsLike = feelsLike,
    humidity = humidity,
    pressure = pressure,
    temp = temp,
    uvi = uvi,
    visibility = visibility,
    weather = weather?.map { it.asDomainModel() },
    windSpeed = windSpeed,
    dt = dt
)

fun DatabaseDaily.asDomainModel() = Daily(
    clouds = clouds,
    humidity = humidity,
    pressure = pressure,
    sunrise = sunrise,
    sunset = sunset,
    feelsLike = feelsLike?.asDomainModel(),
    temp = temp?.asDomainModel(),
    rain = rain,
    uvi = uvi,
    weather = weather?.map { it.asDomainModel() },
    windSpeed = windSpeed,
    dt = dt
)

fun DatabaseTemp.asDomainModel() = Temp(
    day, eve, max, min, morn, night
)

fun DatabaseFeelsLike.asDomainModel() = FeelsLike(
    day, eve, morn, night
)

fun DatabaseWeather.asDomainModel() = Weather(
    description, icon, id, main
)
