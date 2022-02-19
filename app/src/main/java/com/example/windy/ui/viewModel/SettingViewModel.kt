package com.example.windy.ui.viewModel

import androidx.lifecycle.*
import com.example.windy.database.WeatherDatabase
import com.example.windy.domain.WeatherConditions
import com.example.windy.repository.WeatherRepository
import kotlinx.coroutines.launch

class SettingViewModel(
    private val weatherDatabase: WeatherDatabase
) : ViewModel() {
    private val _getSelectedTimeZone = MutableLiveData<String?>()

    val getSelectedTimeZone: LiveData<String?>
        get() = _getSelectedTimeZone

    //
//    fun notifyUser(timeZone: WeatherConditions) {
//        lateinit var apiObj: WeatherConditions
//        val jop = CoroutineScope(Dispatchers.IO).launch {
//            apiObj = localDataSource.getApiObj(timeZone)
//        }
//        jop.invokeOnCompletion {
//            val notificationUtils = WeatherNotification(getApplication())
//            val nb: NotificationCompat.Builder =
//                notificationUtils.getAndroidChannelNotification(
//                    "" + apiObj.currentWether.temp.toInt()
//                        .toString() + "°" +" "+ apiObj.timezone,
//                    "" + apiObj.currentWether.weather.get(0).description,
//                    true,
//                    true
//                )
//            notificationUtils.getManager()?.notify(4, nb.build())
//
//        }
//    }
    private val repository by lazy {
        WeatherRepository(weatherDatabase)
    }

    fun getWeatherData(
        lat: Float,
        lon: Float,
        lang: String,
        unit: String
    ) {
        var weatherConditions: WeatherConditions? = null
        val job = viewModelScope.launch {
            weatherConditions = repository.fetchWeatherData(lat.toDouble(), lon.toDouble(), lang, unit)
        }
        job.invokeOnCompletion {
            _getSelectedTimeZone.value = weatherConditions?.timezone
        }
    }

    class Factory(private val weatherDatabase: WeatherDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingViewModel(weatherDatabase) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }
}

