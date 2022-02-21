package com.example.windy.util

import com.example.windy.models.domain.WeatherConditions

sealed class Resource {
    class Success(var data: WeatherConditions) : Resource()

    class Error(var msg: String) : Resource()

    object Loading : Resource()
}