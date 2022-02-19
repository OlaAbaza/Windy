package com.example.windy

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.windy.util.SharedPreferenceUtil
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferenceUtil: SharedPreferenceUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        sharedPreferenceUtil = SharedPreferenceUtil(this)
        checkCurrentLocal()
    }

    private fun checkCurrentLocal() {
        val mCurrentLocale = resources.configuration.locale

        val locale = Locale(sharedPreferenceUtil.getLanguage())
        if (locale != mCurrentLocale) {
            setLocale()
        }
    }

    override fun onRestart() {
        super.onRestart()
        checkCurrentLocal()
    }

    private fun setLocale() {
        val locale = Locale(sharedPreferenceUtil.getLanguage())
        Locale.setDefault(locale)
        val resources: Resources = this.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
    //  applicationContext.createConfigurationContext(config)
        //onConfigurationChanged(config)
        resources.updateConfiguration(config, resources.displayMetrics)


    }
}

