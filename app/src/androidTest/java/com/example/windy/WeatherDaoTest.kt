package com.example.windy

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.windy.database.WeatherDatabase
import com.example.windy.models.DatabaseCurrent
import com.example.windy.models.DatabaseWeatherConditions
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalSerializationApi
@RunWith(AndroidJUnit4::class)
class WeatherDaoTest {

    private lateinit var mDatabase: WeatherDatabase
    private lateinit var weatherConditions: DatabaseWeatherConditions

    @Before
    fun init() {
        mDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).build()
        weatherConditions =
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
            )
    }

    @Test
    @Throws(InterruptedException::class)
    fun insert_and_select_weatherConditions() = runBlocking {

        mDatabase.weatherDao().insert(weatherConditions)

        val databaseWeatherConditions = mDatabase.weatherDao().getAllWeatherConditions()

        assertThat(databaseWeatherConditions[0], equalTo(weatherConditions))
    }

    @Test
    @Throws(InterruptedException::class)
    fun select_weatherConditions_by_timeZone() = runBlocking {

        mDatabase.weatherDao().insert(weatherConditions)

        val databaseWeatherConditions = mDatabase.weatherDao().getObjByTimezone("")

        assertThat(databaseWeatherConditions, equalTo(weatherConditions))

    }

    @After
    fun cleanup() {
        mDatabase.close()
    }
}
