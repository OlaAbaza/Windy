package com.example.windy.weatherService

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.windy.network.WeatherApiFilter
import com.example.windy.network.WeatherApiService
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@RunWith(JUnit4::class)
class WeatherServiceTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: WeatherApiService

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            )
            .build()
            .create(WeatherApiService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun getWeatherConditionsTest() = runBlocking {
        enqueueResponse("weather.json")
        val networkWeatherConditions = service.getCurrentWeatherByLatLng(
            33.4418,
            -94.0377,
            WeatherApiFilter.ENGLISH.value,
            WeatherApiFilter.IMPERIAL.value
        )

        assertThat(networkWeatherConditions).isNotNull()
        assertThat(networkWeatherConditions.daily?.size).isEqualTo(8)
        assertThat(networkWeatherConditions.timezone).isEqualTo("America/Chicago")
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader?.getResourceAsStream("api-response/$fileName")
        val source = inputStream?.source()?.buffer()
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
            mockResponse
                .setBody(source?.readString(Charsets.UTF_8) ?: "")
        )
    }
}
