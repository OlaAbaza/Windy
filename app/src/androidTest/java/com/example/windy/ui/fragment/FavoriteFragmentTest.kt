package com.example.windy.ui.fragment

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.windy.R
import com.example.windy.launchFragmentInHiltContainer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class FavoriteFragmentTest {

    @Test
    fun clickAddButton_navigateToMapFragment() {
        // GIVEN - On the home screen
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<WeatherDetailsFragment>(Bundle(), R.style.AppTheme) {
            navController.setGraph(R.navigation.navigation)
            navController.setCurrentDestination(R.id.weatherDetailsFragment)
            Navigation.setViewNavController(requireView(), navController)
        }

        // WHEN - Click on the Map icon
        Espresso.onView(ViewMatchers.withId(R.id.addBtn)).perform(ViewActions.click())

        // THEN - Verify that we navigate to the map screen
        Assert.assertEquals(navController.currentDestination?.id, R.id.mapsFragment)
    }
}