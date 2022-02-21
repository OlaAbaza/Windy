package com.example.windy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.windy.models.Alarm
import com.example.windy.models.DatabaseWeatherConditions
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@Database(
    entities = [DatabaseWeatherConditions::class, Alarm::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(WeatherTypeConverters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun alarmDao(): AlarmDao
}
