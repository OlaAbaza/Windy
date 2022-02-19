package com.example.windy.ui.viewModel

import androidx.lifecycle.*
import com.example.windy.database.WeatherDatabase
import com.example.windy.domain.WeatherConditions
import com.example.windy.repository.WeatherRepository
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val weatherDatabase: WeatherDatabase
) : ViewModel() {

    private val _navigateToSelectedProperty = MutableLiveData<WeatherConditions?>()

    val navigateToSelectedProperty: LiveData<WeatherConditions?>
        get() = _navigateToSelectedProperty

    private val repository by lazy {
        WeatherRepository(weatherDatabase)
    }

    val weatherConditionsList by lazy {
        repository.weatherConditions
    }

    fun getWeatherData(
        lat: Float,
        lon: Float,
        lang: String,
        unit: String
    ) {
        viewModelScope.launch {
            repository.fetchWeatherData(lat.toDouble(), lon.toDouble(), lang, unit)
        }
    }

    fun deleteWeatherItem(timeZone: String) {
        viewModelScope.launch {
            repository.deleteWeatherItem(timeZone)
        }
    }

    fun displayWeatherDetails(weatherConditions: WeatherConditions) {
        _navigateToSelectedProperty.value = weatherConditions
    }

    fun doneNavigating() {
        _navigateToSelectedProperty.value = null
    }

    class Factory(private val weatherDatabase: WeatherDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FavoriteViewModel(weatherDatabase) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }
}