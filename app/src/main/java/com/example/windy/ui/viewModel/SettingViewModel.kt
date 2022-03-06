package com.example.windy.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.windy.repository.WeatherRepository
import com.example.windy.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {
    private val _getSelectedTimeZone = MutableSharedFlow<Resource>()

    val getSelectedTimeZone = _getSelectedTimeZone.asSharedFlow()

    fun getWeatherData(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ) {
        viewModelScope.launch {
            _getSelectedTimeZone.emit(
                repository.fetchWeatherData(lat, lon, lang, unit)
            )
        }
    }

}

