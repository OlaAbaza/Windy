package com.example.windy.ui.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.windy.MainActivity
import com.example.windy.R
import com.example.windy.database.WeatherDatabase
import com.example.windy.network.WeatherApiFilter
import com.example.windy.ui.viewModel.SettingViewModel
import com.example.windy.util.Constant
import com.example.windy.util.SharedPreferenceUtil
import com.example.windy.util.isConnected


class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val viewModel by lazy {
        val application = requireNotNull(activity).application
        val weatherDatabase = WeatherDatabase.getInstance(application)
        ViewModelProvider(this, SettingViewModel.Factory(weatherDatabase)).get(
            SettingViewModel::class.java
        )
    }
    private val sharedPreferenceUtil by lazy {
        context?.run { SharedPreferenceUtil(this) }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val arguments = SettingsFragmentArgs.fromBundle(requireArguments())

        if (arguments.lat != 0f) {
            if (context?.let { context -> isConnected(context) } == true) {
                viewModel.getWeatherData(
                    arguments.lat,
                    arguments.lon,
                    sharedPreferenceUtil?.getLanguage()
                        ?: WeatherApiFilter.ENGLISH.value,
                    sharedPreferenceUtil?.getTempUnit()
                        ?: WeatherApiFilter.IMPERIAL.value
                )
            }
        }

        val goToLocationSettings: Preference? =
            findPreference(getString(R.string.custom_location_key))
        goToLocationSettings?.let {
            Preference.OnPreferenceClickListener {
                navigateToMapScreen()
                true
            }.also { goToLocationSettings.onPreferenceClickListener = it }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSelectedTimeZone.observe(viewLifecycleOwner, { timeZone ->
            sharedPreferenceUtil?.saveTimeZone(timeZone ?: "")
            sharedPreferenceUtil?.saveIsLocationNeedUpdate(true)

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

        if (key == getString(R.string.device_location_key)) {
            sharedPreferenceUtil?.saveIsLocationNeedUpdate(true)
        }
        if (key == getString(R.string.attachment_language_key)) {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
        val preference: Preference? = key?.let { findPreference(it) }
        preference?.let {
            if (preference is ListPreference)
                sharedPreferenceUtil?.saveIsDataNeedUpdate(true)
        }
    }

    private fun navigateToMapScreen() {
        this.findNavController()
            .navigate(
                SettingsFragmentDirections.actionSettingsFragmentToMapsFragment(
                    Constant.SETTINGS
                )
            )
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }

}