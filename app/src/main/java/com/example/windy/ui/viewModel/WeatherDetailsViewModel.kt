package com.example.windy.ui.viewModel

import androidx.lifecycle.*
import com.example.windy.database.WeatherDatabase
import com.example.windy.domain.WeatherConditions
import com.example.windy.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherDetailsViewModel(
    private val weatherDatabase: WeatherDatabase,
) : ViewModel() {

    private val _getDefaultWeatherCondition = MutableLiveData<WeatherConditions?>()

    val getDefaultWeatherCondition: LiveData<WeatherConditions?>
        get() = _getDefaultWeatherCondition

    private val repository by lazy {
        WeatherRepository(weatherDatabase)
    }

    fun getWeatherData(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ) {
        var weatherConditions: WeatherConditions? = null
        val job = viewModelScope.launch {
            weatherConditions = repository.fetchWeatherData(lat, lon, lang, unit)
        }
        job.invokeOnCompletion {
            _getDefaultWeatherCondition.value = weatherConditions
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
        var weatherConditions: WeatherConditions? = null

        val job = viewModelScope.launch {
            weatherConditions = repository.getObjByTimezone(timeZone)
        }
        job.invokeOnCompletion {
            _getDefaultWeatherCondition.value = weatherConditions
        }
    }


    class Factory(private val weatherDatabase: WeatherDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WeatherDetailsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WeatherDetailsViewModel(weatherDatabase) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }
}