package com.example.windy.ui.viewModel

import app.cash.turbine.test
import com.example.windy.MainCoroutineRule
import com.example.windy.models.DatabaseCurrent
import com.example.windy.models.DatabaseWeatherConditions
import com.example.windy.models.asDomainModel
import com.example.windy.models.domain.WeatherConditions
import com.example.windy.repository.FakeWeatherRepositoryTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FavoriteViewModelTest {

    // Subject under test
    private lateinit var favoriteViewModel: FavoriteViewModel

    // Use a fake repository to be injected into the viewModel
    private lateinit var weatherRepository: FakeWeatherRepositoryTest

    // Set the main coroutines dispatcher for unit testing.

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private var weatherConditions =
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
        ).asDomainModel()

    @Before
    fun setup() {
        weatherRepository = FakeWeatherRepositoryTest()

        favoriteViewModel = FavoriteViewModel(weatherRepository)
    }

    @Test
    fun displayWeatherDetails_updateNavigateToSelectedPropertyFlow() =
        mainCoroutineRule.runBlockingTest {
            favoriteViewModel.navigateToSelectedProperty.test {
                favoriteViewModel.displayWeatherDetails(weatherConditions)
                assertThat(awaitItem()).isInstanceOf(WeatherConditions::class.java)
                cancelAndConsumeRemainingEvents()
            }

        }

    @Test
    fun doneNavigating_updateNavigateToSelectedPropertyFlow() = mainCoroutineRule.runBlockingTest {

        val job = launch {
            favoriteViewModel.navigateToSelectedProperty.test {
                assertThat(awaitItem()).isNull()
                awaitComplete()
            }
        }
        favoriteViewModel.doneNavigating()
        job.join()
        job.cancel()
    }
}
