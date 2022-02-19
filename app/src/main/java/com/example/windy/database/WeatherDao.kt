package com.example.windy.database


import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WeatherDao {
    @Query("SELECT * FROM WeatherConditions")
    fun getWeatherConditions(): LiveData<List<DatabaseWeatherConditions>>

    @Query("SELECT * FROM WeatherConditions")
    suspend fun getAllWeatherConditions(): List<DatabaseWeatherConditions>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherData: DatabaseWeatherConditions)

    @Delete
    suspend fun delete(weatherData: DatabaseWeatherConditions): Void

    @Query("DELETE FROM WeatherConditions")
    suspend fun deleteAll()

    @Query("select * From WeatherConditions where timezone =:mTimeZone")
    suspend fun getObjByTimezone(mTimeZone: String): DatabaseWeatherConditions

    @Query("select * From WeatherConditions where lat =:mLat and lon =:mLon")
    fun getWeatherByCoordinate(mLat: Double, mLon: Double): DatabaseWeatherConditions

    @Query("DELETE FROM WeatherConditions WHERE timezone = :timezone")
    suspend fun deleteWeatherByTimezone(timezone: String)

}