package com.example.windy.ui.viewModel

import com.example.windy.MainCoroutineRule
import com.example.windy.network.WeatherApiFilter
import com.example.windy.repository.FakeWeatherRepositoryTest
import com.example.windy.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherDetailsViewModelTest {

    // Subject under test
    private lateinit var weatherDetailsViewModel: WeatherDetailsViewModel

    // Use a fake repository to be injected into the viewModel
    private lateinit var weatherRepository: FakeWeatherRepositoryTest

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Before
    fun setup() {
        weatherRepository = FakeWeatherRepositoryTest()

        weatherDetailsViewModel = WeatherDetailsViewModel(weatherRepository)
    }

    @Test
    fun fetchWeatherDataWhenApiCallSuccess_updateWeatherList() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        weatherDetailsViewModel.getWeatherData(
            33.4418,
            -94.0377,
            WeatherApiFilter.ENGLISH.value,
            WeatherApiFilter.IMPERIAL.value
        )

        assertTrue(
            weatherDetailsViewModel.getDefaultWeatherCondition.value is (Resource.Loading)
        )

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        assertTrue(
            weatherDetailsViewModel.getDefaultWeatherCondition.value is (Resource.Success)
        )
    }

    @Test
    fun fetchWeatherDataWhenApiCallFailed_updateWeatherList() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        weatherRepository.setReturnError(true)

        weatherDetailsViewModel.getWeatherData(
            33.4418,
            -94.0377,
            WeatherApiFilter.ENGLISH.value,
            WeatherApiFilter.IMPERIAL.value
        )

        assertTrue(
            weatherDetailsViewModel.getDefaultWeatherCondition.value is (Resource.Loading)
        )

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        assertTrue(
            weatherDetailsViewModel.getDefaultWeatherCondition.value is (Resource.Error)
        )
    }

    @Test
    fun getObjByTimezoneWhenFound_updateWeatherList() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        weatherDetailsViewModel.getObjByTimezone(
            "America/Chicago"
        )

        assertTrue(
            weatherDetailsViewModel.getDefaultWeatherCondition.value is (Resource.Loading)
        )

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        assertTrue(
            weatherDetailsViewModel.getDefaultWeatherCondition.value is (Resource.Success)
        )
    }

    @Test
    fun getObjByTimezoneWhenNotFound_updateWeatherList() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        weatherRepository.setReturnError(true)

        weatherDetailsViewModel.getObjByTimezone(
            "America/Chicago"
        )

        assertTrue(
            weatherDetailsViewModel.getDefaultWeatherCondition.value is (Resource.Loading)
        )

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        assertTrue(
            weatherDetailsViewModel.getDefaultWeatherCondition.value is (Resource.Error)
        )
    }
}