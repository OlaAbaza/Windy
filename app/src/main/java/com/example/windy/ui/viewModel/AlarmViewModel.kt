package com.example.windy.ui.viewModel

import androidx.lifecycle.*
import com.example.windy.database.Alarm
import com.example.windy.database.WeatherDatabase
import com.example.windy.repository.WeatherRepository
import kotlinx.coroutines.launch

class AlarmViewModel(private val weatherDatabase: WeatherDatabase) : ViewModel() {

    private val _navigateToSelectedProperty = MutableLiveData<Alarm?>()

    val navigateToSelectedProperty: LiveData<Alarm?>
        get() = _navigateToSelectedProperty

    private val _getAlarmItem = MutableLiveData<Alarm>()

    val getAlarmItem: LiveData<Alarm>
        get() = _getAlarmItem

    private val repository by lazy {
        WeatherRepository(weatherDatabase)
    }

    val alarmList by lazy {
        repository.alarmList
    }


    fun deleteAlarmItem(id: Int) {
        viewModelScope.launch {
            repository.deleteAlarmObj(id)
        }
    }

    fun insertAlarmItem(item: Alarm) {
        var id = 0L
        viewModelScope.launch {
            id = repository.insertAlarm(item)
        }.invokeOnCompletion {
            item.id = id.toInt()
            _getAlarmItem.value = item
        }

    }

    fun updateAlarmDetails(alarm: Alarm) {
        _navigateToSelectedProperty.value = alarm
    }

    class Factory(private val weatherDatabase: WeatherDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AlarmViewModel(weatherDatabase) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }
}