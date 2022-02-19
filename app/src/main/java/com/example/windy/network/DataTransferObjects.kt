package com.example.windy.network

import com.example.windy.database.*
import com.squareup.moshi.Json

data class NetworkWeatherConditions(
    val alerts: List<NetworkAlert>?,
    val current: NetworkCurrent?,
    val daily: List<NetworkDaily>?,
    val hourly: List<NetworkHourly>?,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    @Json(name = "timezone_offset")
    val timezoneOffset: Int?
)

data class NetworkAlert(
    val description: String?,
    val end: Int?,
    val event: String?,
    @Json(name = "sender_name")
    val senderName: String?,
    val start: Int?,
    val tags: List<String>?
)

data class NetworkCurrent(
    val clouds: Int?,
    @Json(name = "dew_point")
    val dewPoint: Double?,
    val dt: Int?,
    @Json(name = "feels_like")
    val feelsLike: Double?,
    val humidity: Int?,
    val pressure: Int?,
    val sunrise: Int?,
    val sunset: Int?,
    val temp: Double?,
    val uvi: Double?,
    val visibility: Int?,
    val weather: List<NetworkWeather>?,
    @Json(name = "wind_deg")
    val windDeg: Int?,
    @Json(name = "wind_gust")
    val windGust: Double?,
    @Json(name = "wind_speed")
    val windSpeed: Double?
)

data class NetworkDaily(
    val clouds: Int?,
    @Json(name = "dew_point")
    val dewPoint: Double?,
    val dt: Int?,
    @Json(name = "feels_like")
    val feelsLike: NetworkFeelsLike?,
    val humidity: Int?,
    @Json(name = "moon_phase")
    val moonPhase: Double?,
    val moonrise: Int?,
    val moonset: Int?,
    val pop: Double?,
    val pressure: Int?,
    val rain: Double?,
    val sunrise: Int?,
    val sunset: Int?,
    val temp: NetworkTemp?,
    val uvi: Double?,
    val weather: List<NetworkWeather>?,
    @Json(name = "wind_deg")
    val windDeg: Int?,
    @Json(name = "wind_gust")
    val windGust: Double?,
    @Json(name = "wind_speed")
    val windSpeed: Double?
)

data class NetworkFeelsLike(
    val day: Double?,
    val eve: Double?,
    val morn: Double?,
    val night: Double?
)

data class NetworkHourly(
    val clouds: Int?,
    @Json(name = "dew_point")
    val dewPoint: Double?,
    val dt: Int?,
    @Json(name = "feels_like")
    val feelsLike: Double?,
    val humidity: Int?,
    val pop: Double?,
    val pressure: Int?,
    val temp: Double?,
    val uvi: Double?,
    val visibility: Int?,
    val weather: List<NetworkWeather>?,
    @Json(name = "wind_deg")
    val windDeg: Int?,
    @Json(name = "wind_gust")
    val windGust: Double?,
    @Json(name = "wind_speed")
    val windSpeed: Double?
)

data class NetworkTemp(
    val day: Double?,
    val eve: Double?,
    val max: Double?,
    val min: Double?,
    val morn: Double?,
    val night: Double?
)

data class NetworkWeather(
    val description: String?,
    val icon: String?,
    val id: Int?,
    val main: String?
)

fun NetworkWeatherConditions.asDatabaseModel() = DatabaseWeatherConditions(
    alerts = alerts?.map { it.asDatabaseModel() },
    current = current?.asDatabaseModel(),
    daily = daily?.map { it.asDatabaseModel() },
    hourly = hourly?.map { it.asDatabaseModel() },
    lat = lat,
    lon = lon,
    timezone = timezone

)

fun NetworkAlert.asDatabaseModel() = DatabaseAlert(
    description, end, event, senderName, start, tags
)

fun NetworkCurrent.asDatabaseModel() = DatabaseCurrent(
    clouds = clouds,
    feelsLike = feelsLike,
    humidity = humidity,
    pressure = pressure,
    sunrise = sunrise,
    sunset = sunset,
    temp = temp,
    uvi = uvi,
    visibility = visibility,
    weather = weather?.map { it.asDataBaseModel() },
    windSpeed = windSpeed,
    dt = dt
)

fun NetworkHourly.asDatabaseModel() = DatabaseHourly(
    clouds = clouds,
    feelsLike = feelsLike,
    humidity = humidity,
    pressure = pressure,
    temp = temp,
    uvi = uvi,
    visibility = visibility,
    weather = weather?.map { it.asDataBaseModel() },
    windSpeed = windSpeed,
    dt = dt

)

fun NetworkDaily.asDatabaseModel() = DatabaseDaily(
    clouds = clouds,
    humidity = humidity,
    pressure = pressure,
    sunrise = sunrise,
    sunset = sunset,
    feelsLike = feelsLike?.asDataBaseModel(),
    temp = temp?.asDataBaseModel(),
    rain = rain,
    uvi = uvi,
    weather = weather?.map { it.asDataBaseModel() },
    windSpeed = windSpeed,
    dt = dt
)

fun NetworkTemp.asDataBaseModel() = DatabaseTemp(
    day, eve, max, min, morn, night
)

fun NetworkFeelsLike.asDataBaseModel() = DatabaseFeelsLike(
    day, eve, morn, night
)

fun NetworkWeather.asDataBaseModel() = DatabaseWeather(
    description, icon, id, main
)
