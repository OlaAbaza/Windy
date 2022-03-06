package com.example.windy.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.windy.models.Alarm
import com.example.windy.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val repository: WeatherRepository,
) : ViewModel() {

    private val _navigateToSelectedProperty = MutableSharedFlow<Alarm?>()

    val navigateToSelectedProperty = _navigateToSelectedProperty.asSharedFlow()

    private val _getAlarmItem = MutableSharedFlow<Alarm>()

    val getAlarmItem = _getAlarmItem.asSharedFlow()

    val alarmList by lazy {
        repository.getAlarmList().asLiveData()
    }


    fun deleteAlarmItem(id: Int) {
        viewModelScope.launch {
            repository.deleteAlarmObj(id)
        }
    }

    fun insertAlarmItem(item: Alarm) {
        viewModelScope.launch {
            item.id = repository.insertAlarm(item).toInt()
            _getAlarmItem.emit(item)
        }
    }

    fun updateAlarmDetails(alarm: Alarm) {
        viewModelScope.launch {
            _navigateToSelectedProperty.emit(alarm)
        }
    }
}