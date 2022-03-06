package com.example.windy.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SharedPreferenceUtilTest {

    private lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        sharedPreferenceUtil = SharedPreferenceUtil(context)

    }

    @Test
    fun `Saving Is Location Need Update pref `() {

        sharedPreferenceUtil.saveIsLocationNeedUpdate(true)

        assertTrue(sharedPreferenceUtil.getIsLocationNeedUpdate())
    }
}