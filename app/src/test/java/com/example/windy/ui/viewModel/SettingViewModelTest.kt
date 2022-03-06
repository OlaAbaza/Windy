package com.example.windy.ui.viewModel

import app.cash.turbine.test
import com.example.windy.MainCoroutineRule
import com.example.windy.network.WeatherApiFilter
import com.example.windy.repository.FakeWeatherRepositoryTest
import com.example.windy.util.Resource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingViewModelTest {
    // Subject under test
    private lateinit var settingViewModel: SettingViewModel

    // Use a fake repository to be injected into the viewModel
    private lateinit var weatherRepository: FakeWeatherRepositoryTest

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Before
    fun setup() {
        weatherRepository = FakeWeatherRepositoryTest()

        settingViewModel = SettingViewModel(weatherRepository)
    }

    @Test
    fun fetchWeatherDataWhenApiCallSuccess_updateWeatherList() = mainCoroutineRule.runBlockingTest {
        settingViewModel.getSelectedTimeZone.test {
            settingViewModel.getWeatherData(
                33.448,
                -94.0377,
                WeatherApiFilter.ENGLISH.value,
                WeatherApiFilter.IMPERIAL.value
            )
            assertThat(awaitItem()).isInstanceOf(Resource.Success::class.java)
            cancelAndConsumeRemainingEvents()
        }

    }

    @Test
    fun fetchWeatherDataWhenApiCallFailed_updateWeatherList() = mainCoroutineRule.runBlockingTest {
        weatherRepository.setReturnError(true)

        val job = launch {
            settingViewModel.getSelectedTimeZone.test {
                assertThat(awaitItem()).isInstanceOf(Resource.Error::class.java)
                awaitComplete()
            }
        }
        settingViewModel.getWeatherData(
            33.448,
            -94.0377,
            WeatherApiFilter.ENGLISH.value,
            WeatherApiFilter.IMPERIAL.value
        )
        job.join()
        job.cancel()
    }
}