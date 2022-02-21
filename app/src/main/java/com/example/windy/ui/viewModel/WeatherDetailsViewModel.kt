package com.example.windy.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.windy.repository.WeatherRepository
import com.example.windy.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherDetailsViewModel @Inject constructor(
    private val repository: WeatherRepository,
) : ViewModel() {

    private val _getDefaultWeatherCondition: MutableStateFlow<Resource> =
        MutableStateFlow(Resource.Loading)

    val getDefaultWeatherCondition = _getDefaultWeatherCondition

    fun getWeatherData(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ) {
        viewModelScope.launch {
            _getDefaultWeatherCondition.value = repository.fetchWeatherData(lat, lon, lang, unit)
        }
    }

    fun updateAllWeatherData(
        lang: String,
        unit: String
    ) {
        viewModelScope.launch {
            repository.updateAllWeatherData(lang, unit)
        }
    }

    fun getObjByTimezone(timeZone: String) {

        viewModelScope.launch {
            _getDefaultWeatherCondition.value = repository.getObjByTimezone(timeZone)
        }

    }

}
