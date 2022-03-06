package com.example.windy.ui

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.windy.R
import com.example.windy.network.WeatherApiFilter
import com.example.windy.util.SharedPreferenceUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    private var currentLocal = WeatherApiFilter.ENGLISH.value
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferenceUtil.getLanguage()?.let {
            currentLocal = it
        }
        checkCurrentLocal()
    }

    private fun checkCurrentLocal() {
        val mCurrentLocale = resources.configuration.locales.get(0)

        val locale = Locale(currentLocal)
        if (locale != mCurrentLocale) {
            setLocale()
        }
    }

    override fun onRestart() {
        super.onRestart()
        checkCurrentLocal()
    }

    private fun setLocale() {
        val locale = Locale(currentLocal)
        Locale.setDefault(locale)
        val resources: Resources = this.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        //applicationContext.createConfigurationContext(config)
        // onConfigurationChanged(config)
        resources.updateConfiguration(config, resources.displayMetrics)


    }
}

