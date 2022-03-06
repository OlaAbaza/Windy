package com.example.windy.ui.viewModel

import app.cash.turbine.test
import com.example.windy.MainCoroutineRule
import com.example.windy.models.Alarm
import com.example.windy.repository.FakeWeatherRepositoryTest
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AlarmViewModelTest {

    // Subject under test
    private lateinit var alarmViewModel: AlarmViewModel

    // Use a fake repository to be injected into the viewModel
    private lateinit var weatherRepository: FakeWeatherRepositoryTest

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private var alarm = Alarm(
        "",
        "",
        "",
        "",
        true,
        ""
    )

    @Before
    fun setup() {
        weatherRepository = FakeWeatherRepositoryTest()

        alarmViewModel = AlarmViewModel(weatherRepository)
    }

    @Test
    fun updateAlarmDetails_updateNavigateToSelectedPropertyFlow() =
        mainCoroutineRule.runBlockingTest {
            alarmViewModel.navigateToSelectedProperty.test {
                alarmViewModel.updateAlarmDetails(alarm)
                Truth.assertThat(awaitItem()).isInstanceOf(Alarm::class.java)
                cancelAndConsumeRemainingEvents()
            }

        }

    @Test
    fun insertAlarmItem_updateAlarmList() = mainCoroutineRule.runBlockingTest {

        val job = launch {
            alarmViewModel.getAlarmItem.test {
                Truth.assertThat(awaitItem()).isInstanceOf(Alarm::class.java)
                awaitComplete()
            }
        }
        alarmViewModel.insertAlarmItem(alarm)
        job.join()
        job.cancel()
    }
}
