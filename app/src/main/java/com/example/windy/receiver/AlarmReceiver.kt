package com.example.windy.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.windy.R
import com.example.windy.models.domain.WeatherConditions
import com.example.windy.repository.WeatherRepository
import com.example.windy.util.Constant.ALARM_END_TIME
import com.example.windy.util.Constant.ALARM_ID
import com.example.windy.util.Constant.EVENT
import com.example.windy.util.Constant.SOUND
import com.example.windy.util.NotificationUtils
import com.example.windy.util.SharedPreferenceUtil
import com.example.windy.util.cancelAlarm
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var sharedPreferences: SharedPreferenceUtil

    @Inject
    lateinit var repository: WeatherRepository

    @Inject
    lateinit var notificationUtilsUtils: NotificationUtils

    override fun onReceive(context: Context, intent: Intent) {

        val alarmEndTime = intent.getLongExtra(ALARM_END_TIME, 0)
        val alarmId = intent.getIntExtra(ALARM_ID, 0)
        val isAlarmSound = intent.getBooleanExtra(SOUND, false)
        val event = intent.getStringExtra(EVENT)


        if (alarmEndTime < Calendar.getInstance().timeInMillis) {
            notificationUtilsUtils.cancelAllNotifications()
            cancelAlarm(alarmId, context)
            CoroutineScope(Dispatchers.IO).launch {
                repository.deleteAlarmObj(alarmId)
            }
        } else {
            val timeZone = sharedPreferences.getTimeZone()
            var weatherConditionsItem: WeatherConditions? = null
            val jop = CoroutineScope(Dispatchers.IO).launch {
                weatherConditionsItem = timeZone?.let { repository.getObjByTimezone(it).data }
            }
            jop.invokeOnCompletion {
                val weatherDesc = weatherConditionsItem?.current?.weather?.get(0)?.description

                if (weatherDesc?.contains(event + "", ignoreCase = true) == true) {
                    notificationUtilsUtils.sendNotification(
                        context.getString(R.string.notification_title) + event,
                        context.getString(R.string.notification_body) + weatherDesc,
                        isAlarmSound,
                        false,
                        alarmId
                    )
                }
            }

        }

    }
}

