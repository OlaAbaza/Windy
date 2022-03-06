package com.example.windy.ui.fragment

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.windy.R
import com.example.windy.R.id
import com.example.windy.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@HiltAndroidTest
class WeatherDetailsFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }


    @Test
    fun clickMapIcon_navigateToMapFragment() {
        // GIVEN - On the home screen
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<WeatherDetailsFragment>(Bundle(), R.style.AppTheme) {
            navController.setGraph(R.navigation.navigation)
            navController.setCurrentDestination(R.id.weatherDetailsFragment)
            Navigation.setViewNavController(requireView(), navController)
        }

        // WHEN - Click on the Map icon
        onView(withId(id.menu_map)).perform(ViewActions.click())

        // THEN - Verify that we navigate to the map screen
        assertEquals(navController.currentDestination?.id, id.mapsFragment)
    }

    @Test
    fun clickAlarmIcon_navigateToAlarmFragment() {
        // GIVEN - On the home screen
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<WeatherDetailsFragment>(Bundle(), R.style.AppTheme) {
            navController.setGraph(R.navigation.navigation)
            navController.setCurrentDestination(R.id.weatherDetailsFragment)
            Navigation.setViewNavController(requireView(), navController)
        }

        // WHEN - Click on the alarm icon
        onView(withId(id.menu_alarm)).perform(ViewActions.click())

        // THEN - Verify that we navigate to the alarm screen
        assertEquals(navController.currentDestination?.id, id.alarmFragment)
    }


    @Test
    fun clickSettingButton_navigateToSettingFragment() {
        // GIVEN - On the home screen
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<WeatherDetailsFragment>(Bundle(), R.style.AppTheme) {
            navController.setGraph(R.navigation.navigation)
            navController.setCurrentDestination(R.id.weatherDetailsFragment)
            Navigation.setViewNavController(requireView(), navController)
        }

        // WHEN - Click on the setting icon
        onView(withId(id.menu_setting)).perform(ViewActions.click())

        // THEN - Verify that we navigate to the setting screen
        assertEquals(navController.currentDestination?.id, id.settingsFragment)
    }

    @Test
    fun clickFavoriteIcon_navigateToFavoriteFragment() {
        // GIVEN - On the home screen
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<WeatherDetailsFragment>(Bundle(), R.style.AppTheme) {
            navController.setGraph(R.navigation.navigation)
            navController.setCurrentDestination(R.id.weatherDetailsFragment)
            Navigation.setViewNavController(requireView(), navController)
        }

        // WHEN - Click on the Fav icon
        onView(withId(id.menu_fav)).perform(ViewActions.click())

        // THEN - Verify that we navigate to the Fav screen
        assertEquals(navController.currentDestination?.id, id.favoriteFragment)
    }


}
