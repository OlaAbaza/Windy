package com.example.windy.database


import androidx.room.*
import com.example.windy.models.DatabaseWeatherConditions
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    //Queries with a Flow return type always run on the Room executors, so they are always main-safe.
    @Query("SELECT * FROM WeatherConditions")
    fun getWeatherConditions(): Flow<List<DatabaseWeatherConditions>>

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