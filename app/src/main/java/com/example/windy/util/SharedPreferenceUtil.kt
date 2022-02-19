package com.example.windy.util

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.windy.network.WeatherApiFilter

class SharedPreferenceUtil(context: Context) {
    private val sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    private val editor = sharedPreferences.edit()

    fun saveTimeZone(timeZone: String) {
        with(editor) {
            putString(Constant.TIMEZONE, timeZone)
            apply()
        }
    }

    fun saveIsFirstTimeLunch(isFirstTimeLunch: Boolean) {
        with(editor) {
            putBoolean(Constant.IS_FIRST_TIME_LUNCH, isFirstTimeLunch)
            apply()
        }
    }

    fun saveIsDataNeedUpdate(isUpdate: Boolean) {
        with(editor) {
            putBoolean(Constant.IS_DATA_NEED_UPDATE, isUpdate)
            apply()
        }
    }

    fun saveIsLocationNeedUpdate(isUpdate: Boolean) {
        with(editor) {
            putBoolean(Constant.IS_LOCATION_NEED_UPDATE, isUpdate)
            apply()
        }
    }


    fun getTimeZone() = sharedPreferences.getString(Constant.TIMEZONE, "")

    fun getTempUnit() = sharedPreferences.getString(
        "UNIT_SYSTEM",
        WeatherApiFilter.IMPERIAL.value
    )

    fun getLanguage(): String? {
        return sharedPreferences.getString(
            "APP_LANG",
            WeatherApiFilter.ENGLISH.value
        )
    }

    fun getIsUseDeviceLocation() = sharedPreferences.getBoolean(
        "USE_DEVICE_LOCATION",
        true
    )


    fun getIsDataNeedUpdate() = sharedPreferences.getBoolean(
        Constant.IS_DATA_NEED_UPDATE,
        false
    )

    fun getIsFirstTimeLunch() = sharedPreferences.getBoolean(
        Constant.IS_FIRST_TIME_LUNCH,
        true
    )

    fun getIsLocationNeedUpdate() = sharedPreferences.getBoolean(
        Constant.IS_LOCATION_NEED_UPDATE,
        false
    )
}