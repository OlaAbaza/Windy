package com.example.windy.ui.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.windy.R
import com.example.windy.extensions.collectLatestLifeCycleFlow
import com.example.windy.network.WeatherApiFilter
import com.example.windy.ui.MainActivity
import com.example.windy.ui.viewModel.SettingViewModel
import com.example.windy.util.Constant
import com.example.windy.util.Resource
import com.example.windy.util.SharedPreferenceUtil
import com.example.windy.util.isConnected
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    private val viewModel: SettingViewModel by viewModels()

    @Inject
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val arguments = SettingsFragmentArgs.fromBundle(requireArguments())

        if (arguments.lat != 0f) {
            if (context?.let { context -> isConnected(context) } == true) {
                viewModel.getWeatherData(
                    arguments.lat.toDouble(),
                    arguments.lon.toDouble(),
                    sharedPreferenceUtil.getLanguage()
                        ?: WeatherApiFilter.ENGLISH.value,
                    sharedPreferenceUtil.getTempUnit()
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
        collectLatestLifeCycleFlow(viewModel.getSelectedTimeZone) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data.apply {
                        sharedPreferenceUtil.saveTimeZone(this.timezone)
                        sharedPreferenceUtil.saveIsLocationNeedUpdate(true)
                    }
                }
                else -> {
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

        if (key == getString(R.string.device_location_key)) {
            sharedPreferenceUtil.saveIsLocationNeedUpdate(true)
        }
        if (key == getString(R.string.attachment_language_key)) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        val preference: Preference? = key?.let { findPreference(it) }
        preference?.let {
            if (preference is ListPreference)
                sharedPreferenceUtil.saveIsDataNeedUpdate(true)
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