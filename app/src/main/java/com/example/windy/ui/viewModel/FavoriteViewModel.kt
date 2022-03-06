package com.example.windy.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.windy.models.domain.WeatherConditions
import com.example.windy.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: WeatherRepository,
) : ViewModel() {

    private val _navigateToSelectedProperty = MutableSharedFlow<WeatherConditions?>()

    val navigateToSelectedProperty = _navigateToSelectedProperty.asSharedFlow()


    val weatherConditionsList by lazy {
        repository.getWeatherConditions().asLiveData()
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
        viewModelScope.launch {
            _navigateToSelectedProperty.emit(weatherConditions)
        }
    }

    fun doneNavigating() {
        viewModelScope.launch {
            _navigateToSelectedProperty.emit(null)
        }
    }

}