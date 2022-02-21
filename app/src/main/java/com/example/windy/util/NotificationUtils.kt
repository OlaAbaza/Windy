package com.example.windy.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.windy.R
import com.example.windy.ui.fragment.WeatherDetailsFragment
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


private const val WEATHER_NOTIFICATION_CHANNEL_ID = "weather_channel"
private const val WEATHER_NOTIFICATION_CHANNEL_NAME = "Weather"
private const val NOTIFICATION_ID = 1


class NotificationUtils @Inject constructor(@ApplicationContext val context: Context) {
    private var notificationManager: NotificationManager? = null

    init {
        createChannel()
    }

    fun sendNotification(
        title: String,
        messageBody: String, isAlarmSound: Boolean,
        isCancelable: Boolean,
        notificationId: Int
    ) {
        // Create the content intent for the notification, which launches
        // this activity
        val contentIntent = Intent(context, WeatherDetailsFragment::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val builder = NotificationCompat.Builder(
            context,
            WEATHER_NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
            .setContentTitle(title)
            .setSound(
                RingtoneManager.getDefaultUri(
                    if (isAlarmSound) RingtoneManager.TYPE_ALARM else
                        RingtoneManager.TYPE_NOTIFICATION
                )
            )
            .setContentText(messageBody)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(context.getColor(R.color.purple_504))
            .setOngoing(isCancelable)

        getNotificationManager()?.notify(notificationId, builder.build().apply {
            if (isAlarmSound) {
                flags = Notification.FLAG_INSISTENT
            }
        })
    }


    private fun createChannel() {
        val notificationChannel = NotificationChannel(
            WEATHER_NOTIFICATION_CHANNEL_ID,
            WEATHER_NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
            .apply {
                setShowBadge(false)
            }

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.enableVibration(true)
        notificationChannel.description =
            context.getString(R.string.weather_notification_channel_description)
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        getNotificationManager()?.createNotificationChannel(notificationChannel)

    }

    private fun getNotificationManager(): NotificationManager? {
        if (notificationManager == null) {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        }
        return notificationManager
    }

    fun cancelAllNotifications() {
        getNotificationManager()?.cancelAll()
    }


}